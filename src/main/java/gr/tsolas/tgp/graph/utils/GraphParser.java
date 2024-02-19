package gr.tsolas.tgp.graph.utils;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Edge;
import gr.tsolas.tgp.partitioning.Partitioner;
import gr.tsolas.tgp.repository.GraphRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

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
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(this::processLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sortDianodesByMemory();

    }

    private void processLine(String line) {
        if (line.startsWith("vertex")) {
            processVertex(line);
        } else if (line.startsWith("edge")) {
            processEdge(line);
        } else if (line.startsWith("graph")) {
            processGraph();
        }
    }

    private void processVertex(String line) {
        int firstSpace = line.indexOf(' ');
        if (firstSpace != -1) {
            int nodeId = Integer.parseInt(line.substring(firstSpace + 1));
            Dianode node = new Dianode(nodeId, currentTimeInstance, -1);
            repository.addNode(node);
            //partitioner.partitionNode(node);
        }
    }

    private void processEdge(String line) {
        int firstSpace = line.indexOf(' ');
        int secondSpace = line.indexOf(' ', firstSpace + 1);
        if (secondSpace != -1) {
            int startNodeId = Integer.parseInt(line.substring(firstSpace + 1, secondSpace));
            int endNodeId = Integer.parseInt(line.substring(secondSpace + 1));
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

    private void sortDianodesByMemory() {
        // Sorts dianodes based on their memory attribute
        repository.getNodes().sort(Comparator.comparingInt(Dianode::getMemory).reversed());
    }

}
