package gr.tsolas.tgp.partitioning;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Edge;
import gr.tsolas.tgp.graph.Worker;
import gr.tsolas.tgp.repository.GraphRepository;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
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
            return 0.0;
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
    public BigDecimal calculateWeightedEdgeCutScoreRatio(Map<Integer, Worker> workers) {
        BigInteger weightedEdgeCutScore = BigInteger.ZERO;
        BigInteger totalEdgeWeight = BigInteger.ZERO;

        // Calculate the total edge weight and weighted edge cut score
        for (Worker worker : workers.values()) {
            for (Dianode node : worker.getNodes().values()) {
                Set<Edge> outgoingEdges = node.getEdgesOutgoing();
                for (Edge edge : outgoingEdges) {
                    BigInteger edgeWeight = edge.getWeight();
                    totalEdgeWeight = totalEdgeWeight.add(edgeWeight);
                    if (isEdgeCut(edge, worker.getId())) {
                        weightedEdgeCutScore = weightedEdgeCutScore.add(edgeWeight);
                    }
                }
            }
        }

        System.out.println("Total Weight EdgeCut Score: " + weightedEdgeCutScore);
        System.out.println("Total Edge Weight: " + totalEdgeWeight);

        // Calculate the ratio using BigDecimal for division
        if (totalEdgeWeight.equals(BigInteger.ZERO)) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(weightedEdgeCutScore).divide(new BigDecimal(totalEdgeWeight), MathContext.DECIMAL128);
    }

    public BigInteger calculateWeightedEdgeCutScore(int workerId, Dianode nodeToAdd) {
        BigInteger weightedEdgeCutScore = BigInteger.ZERO;

        // For all the edges of the node, calculate the edge cuts considering the weight of each edge.
        Set<Edge> outgoingEdges = nodeToAdd.getEdgesOutgoing();
        for (Edge edge : outgoingEdges) {
            if (repository.getNodeById(edge.getDianodeIdTarget()).getWorkerId() != -1 && isEdgeCut(edge, workerId)) {
                weightedEdgeCutScore = weightedEdgeCutScore.add(edge.getWeight());
            }
        }

        Set<Edge> incomingEdges = nodeToAdd.getEdgesIncoming();
        for (Edge edge : incomingEdges) {
            if (repository.getNodeById(edge.getDianodeIdSource()).getWorkerId() != -1 && isEdgeCutSource(edge, workerId)) {
                weightedEdgeCutScore = weightedEdgeCutScore.add(edge.getWeight());
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
        int edges = 0;
        for (Dianode node : repository.getAllNodes().values()) {
            int nodeWorkerId = node.getWorkerId();//findWorkerIdForNode(node);
            Set<Edge> outgoingEdges = node.getEdgesOutgoing();
            for (Edge edge : outgoingEdges) {
                if (isEdgeCut(edge, nodeWorkerId)) {
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

    private boolean isEdgeCutSource(Edge edge, int workerId) {
        return workerId != repository.getNodeById(edge.getDianodeIdSource()).getWorkerId();
    }

}
