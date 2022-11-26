#!/usr/bin/env python3

"""
    => Minimal Python Implementation.

    ### Implemented Features:
        - Simple List of Edges (bad!)
		- Creates a copy from Edge-List in each iteration (bad!)
        - Improved Branching
        - Reduction Rule
        - Understand + implement LP-Lower-Bound
    ### TODO:
        - Use Hashmap / Heap / other datastructure for storing edges
		- Use Mapping from Vertex-Label to Numeric-ID (String <=> Int)
		- Add/Remove vertices (more efficiently) instead of duplicating complete edge list
        - Understand + implement Clique-Lower-Bound
"""

from re import match
from signal import signal,SIGINT
from sys import stdin
from bipartite_graph import BipartiteGraph

# Global variables (used for benchmark.sh)
RECURSION = 0
LAST_K = 0

ENABLE_REDUCTION = True
ENABLE_LP_BOUND = True
ENABLE_KN_BOUND = True
ENABLE_DEGREE_LEQ2 = False

# Main method
def main():
    try: V,E = read_input()                       # Parse input from stdin
    except Exception as e: return print("[ERROR] Could not parse input graph: "+str(e))
    
    if ENABLE_REDUCTION:
        E,S0 = apply_reduction(E)                 # Apply reduction rules
        V -= set(S0)
    else: S0 = []

    lower_bound = 0
    if ENABLE_LP_BOUND:
        LP_bound = BipartiteGraph(V,E).lower_bound() if ENABLE_LP_BOUND else 0
        print("#LP-Lower-Bound: "+str(LP_bound))
        lower_bound = max(lower_bound,LP_bound)
    
    S = vertex_cover(V,E,lower_bound)             # Execute V.C. algorithm (on reduced graph if possible)
    for v in S0: S.append(v)
    if len(S) > 0: print("\n".join(S))            # Print result

# Parse input graph from stdin
def read_input():
    V,E = set(),list()
    for line in stdin:                                        			# Parse each following edge line
        line = line.strip()
        if line.startswith("#") or len(line.strip()) == 0: continue		# Skip comment lines and empty lines
        e = match(r"([a-zA-Z0-9_]+) ([a-zA-Z0-9_]+)",line).groups()
        E.append(e)
        for n in e: V.add(n)
    return V,E

# Try to apply reduction rule multiple times
def apply_reduction(E):
    S = []                                        # Store partial result of the VC-Solution
    D = node_degrees(E)
    D,v = reduction_rules(D)
    while not v is None:                          # Call the reduction-method repeatedly
        S.append(v)                               # Add removed vertex to partial solution of the VC-Solution
        D,v = reduction_rules(D)
    E2 = []
    for u,ne in D.items():
        for v in ne:
            if u < v:
                E2.append((u,v))
    return E2,S                                    # Return reduced graph and partial VC-Solution

# Single reduction rule implemented
def reduction_rules(D):                           
    for ne in D.values():
        if len(ne) == 1:                          # Find vertex v with degree = 1
            n = ne[0]                             # Remove the (single) neighbour N(v) from graph
            D,_ = remove_vertex(D,n)
            #En = [e for e in E if n not in e]
            return D,n                           # Add N(v) to the VC-Solution
    return D,None   # Reduction rule can't be applied anymore (=> there is no vertex v with degree = 1)

def remove_vertex(D,v):
    ne = D[v]
    for n in D[v]:
        D[n].remove(v)
    del D[v]
    return D,ne

# Find minimal vertex cover (method from lecture)
def vertex_cover(V,E,lower_bound=0):
    for k in range(lower_bound,len(V)):
        LAST_K = k
        S = vc_branch(E,k)
        if not S is None: return S
    return V

# Find vertex cover of size k (method from lecture + improved branching)
def vc_branch(E,k):
    global RECURSION
    RECURSION += 1

    if k < 0: return None
    elif len(E) == 0: return list()
        
    # Calculate node degree (for each node)
    D = node_degrees(E)

    # Lower Bound - Complete-Graph: VC(Kn) = n-1
    total_edges, total_nodes = len(E), len(D)
    if ENABLE_KN_BOUND:
        if total_nodes-1 <= k and total_edges == total_nodes*(total_nodes-1)/2:
            return list(D.keys())[1:]

    # Find max degree node
    max_v = max(D,key=lambda k: len(D[k]))
    max_ne = D[max_v]
    max_deg = len(max_ne)

    if False and ENABLE_DEGREE_LEQ2:    # Disabled, because not working correct at the moment ... TODO!
        if max_deg <= 2:
            print("#Max-Degree Bound: deg <= 2")
            S = solve_degree_leq2(E,k)
            if not S is None:
                return S

    # Try G\{v}
    En = [e for e in E if max_v not in e]
    S = vc_branch(En,k-1)
    if not S is None:
        S.append(max_v)
        return S
    
    # Try G\N(v)
    En = [e for e in E if sum([v in max_ne for v in e]) == 0]
    S = vc_branch(En,k-max_deg)
    if not S is None:
        for v in max_ne:
            S.append(v)
        return S
    
    # No solution
    return None

# Loop through list of edges and create adjacency-hashmap
def node_degrees(E):
    D = {}
    for u,v in E:
        if u in D: D[u].append(v)
        else: D[u] = [v]
        if v in D: D[v].append(u)
        else: D[v] = [u]
    return D

# Disabled, because not working correct at the moment ... TODO!
def solve_degree_leq2(E,k):
    visited = set()
    D = node_degrees(E)
    total_nodes = len(D)
    S = list()
    while len(visited) < total_nodes:
        min_v = min(D,key=lambda v:len(D[v]))
        path = dfs(D,min_v)
        visited |= set(path)
        # todo: add first node if circle
        S += path[1::2]
        if len(S) > k: return None
        if len(D[min_v]) == 2 and len(path) % 2 == 1:
            path.insert(0,None)
        for v in path[1::2]:
            ne = D[v]
            del D[v]
            for v2 in ne:
                D[v2].remove(v)
                if len(D[v2]) == 0:
                    del D[v2]
    return S if len(S) <= k else None

def dfs(D,start):
    visited = []
    stack = [start]
    while len(stack) > 0:
        tmp = stack.pop()
        visited.append(tmp)
        for v in D[tmp]:
            if v not in visited and v not in stack:
                stack.append(v)
    return visited

# print additional comment lines when SIGINT received
def debug_before_shutdown(sig,frame):
    print("#recursive steps: {}".format(RECURSION))
    print("#last-k: {}".format(LAST_K))
    exit(1)

# Call main method
if __name__ == "__main__":
    signal(SIGINT,debug_before_shutdown)
    main()
    print("#recursive steps: {}".format(RECURSION))
