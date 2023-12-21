package gr.tsolas.tgp.graph.utils;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Edge;
import gr.tsolas.tgp.partitioning.Partitioner;
import gr.tsolas.tgp.repository.GraphRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GraphParser {

    public static int getCurrentTimeInstance() {
        return currentTimeInstance;
    }

    private final GraphRepository repository;
    private final Partitioner partitioner;
    public static int currentTimeInstance = 0;
    private int edgeCount = 0;

    public GraphParser(GraphRepository repository, Partitioner partitioner) {
        this.repository = repository;
        this.partitioner = partitioner;
    }

    public void parseFile(String filepath) {
        Path path = Paths.get(filepath);
        try {
            List<String> allLines = Files.readAllLines(path);
            for (String line : allLines) {
                if (line.startsWith("vertex")) {
                    processVertex(line);
                } else if (line.startsWith("edge")) {
                    processEdge(line);
                } else if (line.startsWith("graph")) {
                    processGraph();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processVertex(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length == 2) {
            int nodeId = Integer.parseInt(parts[1]);
            Dianode node = new Dianode(nodeId, currentTimeInstance, -1);
            repository.addNode(node);
            partitioner.partitionNode(node);
        }
    }

    private void processEdge(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length == 3) {
            int startNodeId = Integer.parseInt(parts[1]);
            int endNodeId = Integer.parseInt(parts[2]);
            Edge edge = new Edge(currentTimeInstance, -1, startNodeId, endNodeId);
            edgeCount++;
            Dianode startNode = repository.getNodeById(startNodeId);
            Dianode endNode = repository.getNodeById(endNodeId);
            if (startNode != null && endNode != null) {
                startNode.addOutgoingEdge(edge);
                endNode.addIncomingEdge(edge);
            }
        }
    }

    private void processGraph() {
        currentTimeInstance++;
    }

    public int getEdgeCount() {
        return edgeCount;
    }
}
