package gr.tsolas.tgp.partitioning;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Worker;
import gr.tsolas.tgp.repository.GraphRepository;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author giorgos
 */
public class MyPartitioning implements PartitioningStrategy {

    private Map<Integer, Worker> workers;
    private Scoring scoring;
    private double loadImbalanceThreshold;

    public MyPartitioning(GraphRepository repository, double threshold) {
        this.workers = repository.getWorkers();
        this.scoring = new Scoring(repository);
        this.loadImbalanceThreshold = threshold;
    }

    @Override
    public void partition(Dianode node) {
        if (node.getWorkerId() != -1) {
            for (Worker worker : workers.values()) {
                if (worker.getId() == node.getWorkerId()) {
                    worker.removeNode(node);
                }
            }
        }
        if (isLoadImbalanced(loadImbalanceThreshold)) {
            assignToLeastFullWorker(node);
        } else {
            assignBasedOnCommunicationCostScore(node);
        }
    }

    /**
     * Assigns the given node to the worker with the least number of nodes. If
     * multiple workers have the same, lowest number of nodes, one of them is
     * chosen arbitrarily.
     *
     * @param node The node to be assigned to a worker.
     */
    private void assignToLeastFullWorker(Dianode node) {
        Worker leastFullWorker = workers.values().stream()
                .min(Comparator.comparingInt(w -> w.getNodes().size()))
                .orElse(null);
        if (leastFullWorker != null) {
            leastFullWorker.addNode(node);
        }
    }

    /**
     * Assigns the given node to the worker that results in the lowest
     * communication cost score. In case of a tie (multiple workers resulting in
     * the same score), one of them is chosen arbitrarily.
     *
     * @param node The node to be assigned based on communication cost
     * calculation.
     */
    private void assignBasedOnCommunicationCostScore(Dianode node) {
        Worker bestWorker = null;
        BigInteger bestScore = BigInteger.valueOf(Integer.MAX_VALUE);
        for (Worker worker : workers.values()) {
            BigInteger edgeCutScore = scoring.calculateWeightedEdgeCutScore(worker.getId(), node);
            if (edgeCutScore.compareTo(bestScore) < 0) {
                bestScore = edgeCutScore;
                bestWorker = worker;
            }
        }
        if (bestWorker != null) {
            bestWorker.addNode(node);
        }
    }

    /**
     * Determines if the current load across all workers is imbalanced beyond
     * the specified threshold.
     *
     * @param threshold The threshold ratio to determine load imbalance. A value
     * of 0.5, for example, means that the least full worker should have at
     * least 50% of the nodes of the most full worker for the load to be
     * considered balanced.
     * @return true if the load is imbalanced beyond the specified threshold,
     * false otherwise.
     */
    private boolean isLoadImbalanced(double threshold) {
        int maxNodes = workers.values().stream().mapToInt(worker -> worker.getNodes().size()).max().orElse(0);
        int minNodes = workers.values().stream().mapToInt(worker -> worker.getNodes().size()).min().orElse(0);
        // Check if the difference in node count is less than the threshold percentage of the maxNodes
        return maxNodes - minNodes > maxNodes * threshold;
    }
}
