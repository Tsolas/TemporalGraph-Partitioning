package gr.tsolas.tgp.partitioning;

import com.google.common.hash.Hashing;
import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Worker;
import gr.tsolas.tgp.graph.utils.DianodeFunnel;
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
    return Hashing.goodFastHash(32)
                  .newHasher()
                  .putObject(node, new DianodeFunnel())
                  .hash()
                  .asInt();
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
