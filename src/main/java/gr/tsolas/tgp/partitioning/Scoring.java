package gr.tsolas.tgp.partitioning;

import gr.tsolas.tgp.graph.Worker;
import java.util.Map;

/**
 *
 * @author giorgos
 */
public class Scoring {

    /**
     * This method computes the load imbalance ratio which is the ratio of the
     * size of the largest partition to the average partition size.
     *
     * @param workers
     * @return
     */
    public double calculateLoadImbalanceRatio(Map<Integer, Worker> workers) {
        if (workers == null || workers.isEmpty()) {
            // Returning 1 to indicate perfect balance when no workers are present
            return 1.0;
        }
        int totalNodes = workers.values().stream().mapToInt(worker -> worker.getNodes().size()).sum();
        double averagePartitionSize = (double) totalNodes / workers.size();
        int maxPartitionSize = workers.values().stream().mapToInt(worker -> worker.getNodes().size()).max().orElse(0);
        return maxPartitionSize / averagePartitionSize;
    }
}
