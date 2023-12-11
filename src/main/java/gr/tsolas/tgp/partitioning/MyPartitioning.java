package gr.tsolas.tgp.partitioning;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Worker;
import gr.tsolas.tgp.repository.GraphRepository;
import java.util.Map;

/**
 *
 * @author giorgos
 */
public class MyPartitioning implements PartitioningStrategy {

    private Map<Integer, Worker> workers;
    private Scoring scoring;

    public MyPartitioning(Map<Integer, Worker> workers,GraphRepository repository) {
        this.workers = workers;
        this.scoring = new Scoring(repository);
    }

    @Override
    public void partition(Dianode node) {
        Worker bestWorker = null;
        double bestScore = Double.MAX_VALUE;
        for (Worker worker : workers.values()) {
            double loadImbalanceScore = scoring.calculateLoadImbalanceRatio(workers);
            double edgeCutScore = scoring.calculateWeightedEdgeCutScore(worker.getId(), node);
            
            // Combine the scores for future implementation
            //double combinedScore = loadImbalanceScore + edgeCutScore; 

            // Check if this worker offers a better score for edge cuts
            if (edgeCutScore < bestScore) {
                bestScore = edgeCutScore;
                bestWorker = worker;
            }
        }
        // Finally, assign the node to the best worker
        if (bestWorker != null) {
            bestWorker.addNode(node);
        }
    }
}
