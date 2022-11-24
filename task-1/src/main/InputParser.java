package main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InputParser {
    public BufferedReader reader;

    public InputParser() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public InputParser(FileReader reader) {
        this.reader = new BufferedReader(reader);
    }

    public List<String> parseEdges() throws IOException {
        List<String> stringEdges = new ArrayList<>();
        String[] line;
        line = this.reader.readLine().substring(1).split(" ");
        int vertices_num = Integer.parseInt(line[0]);
        int edges_num = Integer.parseInt(line[1]);

        for (int i = 0; i < edges_num; i++) {
            stringEdges.add(this.reader.readLine());
        }
        return stringEdges;
    }
}
