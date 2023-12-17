package gr.tsolas.tgp.graph.utils;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Edge;
import gr.tsolas.tgp.partitioning.HashPartitioning;
import gr.tsolas.tgp.partitioning.MyPartitioning;
import gr.tsolas.tgp.partitioning.Partitioner;
import gr.tsolas.tgp.partitioning.PartitioningStrategy;
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
        if (2 == parts.length) {
            int nodeId = Integer.parseInt(parts[1]);
            Dianode node = new Dianode(nodeId, currentTimeInstance, -1);
            repository.addNode(node);
        }
    }

    private void processEdge(String line) {
        String[] parts = line.split("\\s+");
        if (3 == parts.length) {
            int startNodeId = Integer.parseInt(parts[1]);
            int endNodeId = Integer.parseInt(parts[2]);
            //used 100 as a test value.
            Edge edge = new Edge(currentTimeInstance, -1, startNodeId, endNodeId);
            Dianode startNode = repository.getNodeById(startNodeId);
            Dianode endNode = repository.getNodeById(endNodeId);
            if (startNode != null && endNode != null) {
                startNode.addOutgoingEdge(edge);
                endNode.addIncomingEdge(edge);
            }
            partitioner.partitionNode(startNode);
            partitioner.partitionNode(endNode);
        }
    }

    private void processGraph() {
        currentTimeInstance++;
    }

    private PartitioningStrategy choosePartitioningStrategy(String partitioningMethodSelector) {
        switch (partitioningMethodSelector) {
            case "1":
                return new HashPartitioning(repository.getWorkers());
            case "2":
                return new MyPartitioning(repository.getWorkers(), repository);
            default:
                throw new IllegalArgumentException("Invalid partitioning method selector");
        }
    }
}
