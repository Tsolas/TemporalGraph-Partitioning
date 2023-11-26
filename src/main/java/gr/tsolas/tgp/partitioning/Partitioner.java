package gr.tsolas.tgp.partitioning;

import gr.tsolas.tgp.graph.Dianode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

/**
 *
 * @author giorgos
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Partitioner {

  private PartitioningStrategy strategy;

  public void partitionNode(Dianode node) {
    if (strategy != null) {
      strategy.partition(node);
    }
  }

}
