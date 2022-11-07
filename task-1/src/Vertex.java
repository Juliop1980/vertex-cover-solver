public class Vertex implements Comparable<Vertex>{
    int label;
    int degree;
    Vertex(int label) {
        this.label = label;
    }

    // equals and hashCode

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }


        if (!(o instanceof Vertex)) {
            return false;
        }



        return this.label == ((Vertex) o).label;
    }

    @Override
    public int hashCode() {

        return this.label;
    }


    @Override
    public String toString() {

        //sb.append("Vertices: ").append("\n");

        return "Vertex " + this.label +" degree " + this.degree;
    }


    @Override
    public int compareTo(Vertex o) {
        //System.out.println("im comparing Vertex: " + this.label + " with degree: "+ this.degree + " with Vertex: "+ o.label+ " and degree: "+ o.degree+ " and the result is: "+ Integer.compare(this.degree,  o.degree));

        return Integer.compare(this.degree,  o.degree);
    }


}
