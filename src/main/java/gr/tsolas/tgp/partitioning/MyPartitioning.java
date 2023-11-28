package gr.tsolas.tgp.partitioning;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Worker;
import java.util.Map;

/**
 *
 * @author giorgos
 */
public class MyPartitioning implements PartitioningStrategy {

    private Map<Integer, Worker> workers;
    private Scoring scoring;

    public MyPartitioning(Map<Integer, Worker> workers) {
        this.workers = workers;
        this.scoring = new Scoring();
    }

    @Override
    public void partition(Dianode node) {
        Worker bestWorker = null;
        double bestScore = Double.MAX_VALUE;
        for (Worker worker : workers.values()) {
            // Temporarily add the node to the worker for scoring
            worker.addNode(node);
            double score = scoring.calculateLoadImbalanceRatio(workers);
            // Check if this worker offers a better score
            if (score < bestScore) {
                bestScore = score;
                bestWorker = worker;
            }
            // Remove the node after scoring
            worker.removeNode(node);
        }
        // Finally, assign the node to the best worker
        if (bestWorker != null) {
            bestWorker.addNode(node);
        }
    }
}
