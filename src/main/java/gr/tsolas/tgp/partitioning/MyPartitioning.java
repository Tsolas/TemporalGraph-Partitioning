package gr.tsolas.tgp.partitioning;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Worker;
import gr.tsolas.tgp.repository.GraphRepository;
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
        this.loadImbalanceThreshold = threshold;  // Initialize the threshold
    }

    @Override
    public void partition(Dianode node) {
        if (isLoadImbalanced(0.5)) {
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
        double bestScore = Double.MAX_VALUE;
        for (Worker worker : workers.values()) {
            double edgeCutScore = scoring.calculateWeightedEdgeCutScore(worker.getId(), node);
            if (edgeCutScore < bestScore) {
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
        return maxNodes == 0 || (double) minNodes / maxNodes < threshold;
    }
}
