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
     * Calculates the load imbalance ratio across all workers. The load
     * imbalance ratio is defined as the ratio of the number of nodes in the
     * largest partition (worker) to the number of nodes in the smallest
     * partition. A higher ratio indicates a greater imbalance. If there are no
     * workers, or if the smallest partition contains no nodes, the method
     * returns 1.0, suggesting perfect balance or an empty set of workers.
     *
     * @return The load imbalance ratio.
     */
    public double calculateLoadImbalanceRatio() {
        Map<Integer, Worker> workers = repository.getAllWorkers();
        if (workers.isEmpty()) {
            return 1.0;
        }
        int maxNodes = 0; // Maximum number of nodes in a worker
        int minNodes = Integer.MAX_VALUE; // Minimum number of nodes in a worker
        for (Worker worker : workers.values()) {
            int workerNodeCount = worker.getNodes().size();
            if (workerNodeCount > maxNodes) {
                maxNodes = workerNodeCount;
            }
            if (workerNodeCount < minNodes) {
                minNodes = workerNodeCount;
            }
        }
        if (minNodes == 0) {
            return 1.0;
        }
        return (double) minNodes / maxNodes;
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

    /**
     * Calculates the total number of edge cuts after the partitioning process.
     * An edge is considered cut if it connects nodes that are in different
     * workers.
     *
     * @return The total number of edge cuts.
     */
    public int calculateTotalEdgeCuts() {
        int edgeCuts = 0;
        for (Dianode node : repository.getAllNodes()) {
            int nodeWorkerId = findWorkerIdForNode(node);
            Set<Edge> outgoingEdges = node.getEdgesOutgoing();
            for (Edge edge : outgoingEdges) {
                Dianode targetNode = repository.getNodeById(edge.getDianodeIdTarget());
                if (targetNode != null && findWorkerIdForNode(targetNode) != nodeWorkerId) {
                    edgeCuts++;
                }
            }
        }
        return edgeCuts;
    }

    private int findWorkerIdForNode(Dianode node) {
        for (Map.Entry<Integer, Worker> entry : repository.getAllWorkers().entrySet()) {
            if (entry.getValue().getNodes().containsKey(node.getId())) {
                return entry.getKey();
            }
        }
        return -1; // Indicates that the worker was not found
    }

    private boolean isEdgeCut(Edge edge, int workerId) {
        return workerId != repository.getNodeById(edge.getDianodeIdTarget()).getWorkerId();
    }

}
