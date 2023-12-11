package gr.tsolas.tgp.partitioning;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Edge;
import gr.tsolas.tgp.graph.Worker;
import gr.tsolas.tgp.repository.GraphRepository;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author giorgos
 */
public class Scoring {

    public Scoring(GraphRepository repository) {
        this.repository = repository;
    }

    private final GraphRepository repository;

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

    /**
     * Used to calculate the ratio of weighted edge cut as a final evaluation of
     * the partitioning method.
     *
     * @param workers
     * @return
     */
    public double calculateWeightedEdgeCutScoreRatio(Map<Integer, Worker> workers) {
        double weightedEdgeCutScore = 0.0;
        int totalEdgeWeight = 0;

        // Calculate the total edge weight and weighted edge cut score
        for (Worker worker : workers.values()) {
            for (Dianode node : worker.getNodes().values()) {
                Set<Edge> outgoingEdges = node.getEdgesOutgoing();
                for (Edge edge : outgoingEdges) {
                    totalEdgeWeight += edge.getWeight();
                    if (isEdgeCut(edge, worker.getId())) {
                        weightedEdgeCutScore += edge.getWeight();
                    }
                }
            }
        }

        // Calculate the ratio
        return totalEdgeWeight == 0 ? 0 : weightedEdgeCutScore / (double) totalEdgeWeight;
    }

    public double calculateWeightedEdgeCutScore(int workerId, Dianode nodeToAdd) {
        double weightedEdgeCutScore = 0.0;
        //for all the edges of the node calculate the edge cuts considering the weight of each edge.
        Set<Edge> outgoingEdges = nodeToAdd.getEdgesOutgoing();
        for (Edge edge : outgoingEdges) {
            if (isEdgeCut(edge, workerId)) {
                weightedEdgeCutScore += edge.getWeight();
            }
        }
        Set<Edge> incomingEdges = nodeToAdd.getEdgesIncoming();
        for (Edge edge : incomingEdges) {
            if (isEdgeCut(edge, workerId)) {
                weightedEdgeCutScore += edge.getWeight();
            }
        }
        return weightedEdgeCutScore;
    }

    private boolean isEdgeCut(Edge edge, int workerId) {
        return workerId != repository.getNodeById(edge.getDianodeIdTarget()).getWorkerId();
    }

}
