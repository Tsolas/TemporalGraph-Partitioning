package gr.tsolas.tgp.partitioning;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Worker;
import java.util.Map;

/**
 *
 * @author giorgos
 */
public class HashPartitioning implements PartitioningStrategy {

  private Map<Integer, Worker> workers;

  public HashPartitioning(Map<Integer, Worker> workers) {
    this.workers = workers;
  }

  private int hashFunction(Dianode node) {
    int prime = 31;
    int hash = 1;
    hash = prime * hash + node.getId();
    hash = prime * hash + node.getWeight();
    return hash;
  }

  @Override
  public void partition(Dianode node) {
    int hash = hashFunction(node);
    int workerId = Math.abs(hash) % workers.size();
    Worker assignedWorker = workers.get(workerId);
    if (assignedWorker != null) {
      assignedWorker.addNode(node);
    }
  }
}
