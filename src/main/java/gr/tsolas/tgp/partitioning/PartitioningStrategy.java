package gr.tsolas.tgp.partitioning;

import gr.tsolas.tgp.graph.Dianode;

/**
 *
 * @author giorgos
 */
public interface PartitioningStrategy {

  /**
   * Method that partitions the nodes placing them in to the appropriate workers
   *
   * @param node
   */
  void partition(Dianode node);
}
