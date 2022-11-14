import java.util.HashMap;
import java.util.HashSet;

public class VertexDegreeOrder {
    private final HashMap<Integer, HashSet<Vertex>> degreeMap = new HashMap<>();
    private int maxDegree = 0;

    public  VertexDegreeOrder(){

    }
    public void addVertex(Vertex v){
        if (!degreeMap.containsKey(v.degree)){
            HashSet<Vertex> tmpSet = new HashSet<>();
            tmpSet.add(v);
            degreeMap.put(v.degree,tmpSet);
            if (maxDegree<v.degree) maxDegree = v.degree;
        }else{
            degreeMap.get(v.degree).add(v);
        }
    }






    @Override
    public String toString() {


        return degreeMap + " Max Degree: "+ maxDegree;
    }

    public void increaseDegreeOfVertex(Vertex v, int delta){
        if(degreeMap.containsKey(v.degree-delta)) degreeMap.get(v.degree-delta).remove(v);
        if (!degreeMap.containsKey(v.degree)){
            HashSet<Vertex> tmpSet = new HashSet<>();
            tmpSet.add(v);
            degreeMap.put(v.degree,tmpSet);
            if (maxDegree<v.degree) maxDegree = v.degree;
        }else{
            degreeMap.get(v.degree).add(v);
            if (maxDegree<v.degree) maxDegree = v.degree;
        }

    }

    public void decreaseDegreeOfVertex(Vertex v,int delta) {

        if (degreeMap.containsKey(v.degree + delta)){
            degreeMap.get(v.degree + delta).remove(v);
            if ((v.degree + delta==this.maxDegree) && degreeMap.get(v.degree + delta).isEmpty()){
                int degree = v.degree+delta;
                while(degree> 0 && this.degreeMap.containsKey(degree) && this.degreeMap.get(degree--).isEmpty()){
                    this.maxDegree--;
                }
            }
        }
        if (!degreeMap.containsKey(v.degree)) {
            HashSet<Vertex> tmpSet = new HashSet<>();
            tmpSet.add(v);
            degreeMap.put(v.degree, tmpSet);
        } else {
            degreeMap.get(v.degree).add(v);
        }
    }

    public void removeVertex(Vertex v){


        this.degreeMap.get(v.degree).remove(v);
        if(v.degree == this.maxDegree && this.degreeMap.get(v.degree).isEmpty()) {
            int degree = v.degree;
            while (degree >0) {
                if (this.degreeMap.containsKey(degree) && !this.degreeMap.get(degree).isEmpty()) {
                    break;
                }

                degree--;
            }
            this.maxDegree = degree;
        }



    }

    public Vertex getVertexWithMaxDegree(){

            return this.degreeMap.get(this.maxDegree).iterator().next();
    }
    public boolean isEmpty(){
        return this.degreeMap.isEmpty() || this.degreeMap.get(this.maxDegree).isEmpty();
    }

}