package gr.tsolas.tgp.graph.utils;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Edge;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author giorgos
 */
public class GraphParser {

  private Map<Integer, Dianode> nodesMap = new HashMap<>();
  private List<Edge> edgesList = new ArrayList<>();
  private int currentTimeInstance = 0;

  public GraphParser() {
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
      Dianode node = new Dianode(nodeId, currentTimeInstance, Integer.MAX_VALUE);
      nodesMap.put(nodeId, node);
    }
  }

  private void processEdge(String line) {
    String[] parts = line.split("\\s+");
    if (3 == parts.length) {
      int startNodeId = Integer.parseInt(parts[1]);
      int endNodeId = Integer.parseInt(parts[2]);
      Edge edge = new Edge(currentTimeInstance, Integer.MAX_VALUE, startNodeId, endNodeId);
    }
  }

  private void processGraph() {
    currentTimeInstance++;
  }

}
