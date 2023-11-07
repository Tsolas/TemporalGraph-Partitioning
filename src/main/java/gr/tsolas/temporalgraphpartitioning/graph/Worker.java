package gr.tsolas.temporalgraphpartitioning.graph;

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

  public Dianode getNodeById(int nodeId) {
    return nodes.get(nodeId);
  }

  public void addNode(Dianode node) {
    nodes.put(node.getId(), node);
    nodeCount++;
    weight += node.getWeight();
  }

}
