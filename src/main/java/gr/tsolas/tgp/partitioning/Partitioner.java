package gr.tsolas.tgp.partitioning;

import gr.tsolas.tgp.graph.Dianode;

/**
 *
 * @author giorgos
 */
public class Partitioner {

    public Partitioner() {
    }

    public PartitioningStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(PartitioningStrategy strategy) {
        this.strategy = strategy;
    }

    public Partitioner(PartitioningStrategy strategy) {
        this.strategy = strategy;
    }

  private PartitioningStrategy strategy;

  public void partitionNode(Dianode node) {
    if (strategy != null) {
      strategy.partition(node);
    }
  }

}
