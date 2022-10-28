package main;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HashMapGraph {
    public List<Vertex> vertices = new ArrayList<>();
    public HashMap<Vertex, ArrayList<Vertex>> edges = new HashMap<>();

    public HashMapGraph(ArrayList<Edge> edges){
        this.vertices = getVerticesFromEdges(edges);
        this.edges = getHashmapFromList(this.vertices, edges);
    }

    public HashMapGraph(HashMap<Vertex, ArrayList<Vertex>> edges){
        this.vertices = new ArrayList<>(edges.keySet());
        this.edges = edges;
    }

    public HashMapGraph(HashMapGraph graphToCopy){
        this(copy(graphToCopy.getEdges()));
    }


    public Edge getRandomEdge(){
        Edge randomEdge = null;
        while (randomEdge == null){
            int rnd = new Random().nextInt(vertices.size());
            Vertex rndVertex = vertices.get(rnd);
            if (!edges.get(rndVertex).isEmpty()){
                randomEdge = new Edge(rndVertex, edges.get(rndVertex).get(0));
            }
        }
        return randomEdge;
    }

    public ArrayList<Vertex> getVerticesFromEdges(List<Edge> edges){
        ArrayList<Vertex> vertices = new ArrayList<>();
        for (Edge edge : edges){
            if (!vertices.contains(edge.getFirstVertex())) {
                vertices.add(edge.getFirstVertex());
            }
            if (!vertices.contains(edge.getSecondVertex())) {
                vertices.add(edge.getSecondVertex());
            }
        }
        return vertices;
    }

    public HashMap<Vertex, ArrayList<Vertex>> getHashmapFromList(List<Vertex> vertices, ArrayList<Edge> edges){
        HashMap<Vertex, ArrayList<Vertex>> mapEdges = new HashMap<>();

        for (Edge edge : edges){
            if (!mapEdges.containsKey(edge.getFirstVertex())){
                ArrayList<Vertex> adjacentList = new ArrayList<>();
                adjacentList.add(edge.getSecondVertex());
                mapEdges.put(edge.getFirstVertex(), adjacentList);
            }
            else{
                mapEdges.get(edge.getFirstVertex()).add(edge.getSecondVertex());
            }

            if (!mapEdges.containsKey(edge.getSecondVertex())){
                ArrayList<Vertex> adjacentList = new ArrayList<>();
                adjacentList.add(edge.getFirstVertex());
                mapEdges.put(edge.getSecondVertex(), adjacentList);
            }
            else{
                mapEdges.get(edge.getSecondVertex()).add(edge.getFirstVertex());
            }
        }

        return mapEdges;
    }

    public boolean hasEdges(){
        for (Vertex vertex : vertices){
            if (!edges.get(vertex).isEmpty()){
                return true;
            }
        }
        return false;
    }

    public HashMapGraph deleteVertex(Vertex vertexToDelete){
        vertices.remove(vertexToDelete);
        for (Vertex vertex : edges.get(vertexToDelete)){
            if (edges.get(vertex).contains(vertexToDelete)){
                edges.get(vertex).remove(vertexToDelete);
            }
        }
        edges.remove(vertexToDelete);
        return this;
    }

    public HashMap<Vertex, ArrayList<Vertex>> getEdges(){
        return edges;
    }

    public static HashMap<Vertex, ArrayList<Vertex>> copy(
            HashMap<Vertex, ArrayList<Vertex>> original)
    {
        HashMap<Vertex, ArrayList<Vertex>> copy = new HashMap<Vertex, ArrayList<Vertex>>();
        for (Map.Entry<Vertex, ArrayList<Vertex>> entry : original.entrySet())
        {
            copy.put(entry.getKey(),
                    // Or whatever List implementation you'd like here.
                    new ArrayList<Vertex>(entry.getValue()));
        }
        return copy;
    }

    public void addEdges(Vertex vertexToAdd, ArrayList<Vertex> edgesToAdd){
        if (!vertices.contains(vertexToAdd)){
            vertices.add(vertexToAdd);
        }
        edges.put(vertexToAdd, edgesToAdd);
        for (Vertex vertex : edgesToAdd){
            edges.get(vertex).add(vertexToAdd);
        }
    }

}
