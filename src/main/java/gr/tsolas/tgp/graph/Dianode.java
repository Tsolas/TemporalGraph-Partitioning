package gr.tsolas.tgp.graph;

import java.util.HashSet;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author giorgos
 */
@Data
@AllArgsConstructor
public class Dianode {

  public Dianode(int id, int timeStart, int timeEnd) {
    this.id = id;
    this.timeStart = timeStart;
    this.timeEnd = timeEnd;
    this.edgesIncoming = new HashSet<>();
    this.edgesOutgoing = new HashSet<>();
    this.weight = 0;

  }

  private int id;
  private int timeStart;
  private int timeEnd;
  private HashSet<Edge> edgesIncoming;
  private HashSet<Edge> edgesOutgoing;
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
