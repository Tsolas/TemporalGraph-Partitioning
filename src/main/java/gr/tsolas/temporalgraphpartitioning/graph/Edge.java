package gr.tsolas.temporalgraphpartitioning.graph;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author giorgos
 */
@Data
@NoArgsConstructor
public class Edge {

  private int timeStart;
  private int timeEnd;
  private int dianodeIdStart;
  private int dianodeIdEnd;

}
