package gr.tsolas.tgp.graph;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author giorgos
 */
@Data
@NoArgsConstructor
public class Worker {

  private int id;
  private int nodeCount;
  private int weight;
  private Map<Integer, Dianode> nodes;

  public Worker(int id) {
    this.id = id;
    this.nodeCount = 0;
    this.weight = 0;
    this.nodes = new HashMap<>();
  }

  public Dianode getNodeById(int nodeId) {
    return nodes.get(nodeId);
  }

  public void addNode(Dianode node) {
    nodes.put(node.getId(), node);
    nodeCount++;
    weight += node.getWeight();
  }

}
