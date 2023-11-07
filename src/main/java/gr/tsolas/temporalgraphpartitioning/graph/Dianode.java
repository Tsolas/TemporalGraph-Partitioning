package gr.tsolas.temporalgraphpartitioning.graph;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author giorgos
 */
@Data
@NoArgsConstructor
public class Dianode {

  private int id;
  private int timeStart;
  private int timeEnd;
  private List<Edge> edgesIncoming;
  private List<Edge> edgesOutgoing;
  private int weight;

  public void addIncomingEdge(Edge edge) {
    this.edgesIncoming.add(edge);
    updateWeight();
  }

  public void addOutgoingEdge(Edge edge) {
    this.edgesOutgoing.add(edge);
    updateWeight();
  }

  private void updateWeight() {
    this.weight = this.edgesIncoming.size() + edgesOutgoing.size();
  }

}
