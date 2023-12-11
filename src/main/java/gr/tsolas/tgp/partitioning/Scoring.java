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

//    /**
//     * Calculates the normalized edge cut ratio for a given node to be added to
//     * the partitions.
//     *
//     * The normalized edge cut ratio is defined as the number of edge cuts
//     * divided by the total number of edges. An edge cut occurs when an edge
//     * connects nodes in different partitions.
//     *
//     * @param workers The map of worker IDs to Worker objects, representing the
//     * current partitions.
//     * @param nodeToAdd The DiaNode object that is considered for addition to a
//     * partition.
//     * @param allNodes The map of all DiaNode objects in the graph.
//     * @return The normalized edge cut ratio, where 0 indicates no edge cuts and
//     * values closer to 1 indicate higher numbers of edge cuts compared to total
//     * edges.
//     */
//    public double calculateNormalizedEdgeCutRatio(Map<Integer, Worker> workers, Dianode nodeToAdd, Map<Integer, Dianode> allNodes) {
//        int totalEdges = 0;
//        int edgeCuts = 0;
//
//        for (Worker worker : workers.values()) {
//            for (Dianode node : worker.getNodes().values()) {
//                Set<Edge> outgoingEdges = node.getEdgesOutgoing();
//                totalEdges += outgoingEdges.size();
//
//                for (Edge edge : outgoingEdges) {
//                    if (isEdgeCut(edge, nodeToAdd, workers, allNodes)) {
//                        edgeCuts++;
//                    }
//                }
//            }
//        }
//        //to avoid division by zero
//        return totalEdges == 0 ? 0 : (double) edgeCuts / totalEdges;
//    }
    public double calculateWeightedEdgeCutScore(Map<Integer, Worker> workers, Dianode nodeToAdd, Map<Integer, Dianode> allNodes) {
        double weightedEdgeCutScore = 0.0;
        for (Worker worker : workers.values()) {
            for (Dianode node : worker.getNodes().values()) {
                Set<Edge> outgoingEdges = node.getEdgesOutgoing();
                for (Edge edge : outgoingEdges) {
                    if (isEdgeCut(edge, nodeToAdd, workers, allNodes)) {
                        weightedEdgeCutScore += edge.getWeight();
                    }
                }
            }
        }
        return weightedEdgeCutScore;
    }

    public double calculateWeightedEdgeCutScoreRatio(Map<Integer, Worker> workers, Dianode nodeToAdd, Map<Integer, Dianode> allNodes) {
        double weightedEdgeCutScore = 0.0;
        //total edge weight of dataset
        int count = 0;
        for (Worker worker : workers.values()) {
            for (Dianode node : worker.getNodes().values()) {
                Set<Edge> outgoingEdges = node.getEdgesOutgoing();
                for (Edge edge : outgoingEdges) {
                    count += edge.getWeight();
                    if (isEdgeCut(edge, nodeToAdd, workers, allNodes)) {
                        weightedEdgeCutScore += edge.getWeight();
                    }
                }
            }
        }
        return weightedEdgeCutScore;
    }

    //checks edge cuts for possible worker.
    public double calculateWeightedEdgeCutScoreNew(int workerId, Dianode nodeToAdd) {
        double weightedEdgeCutScore = 0.0;
        Set<Edge> outgoingEdges = nodeToAdd.getEdgesOutgoing();
        for (Edge edge : outgoingEdges) {
            if (isEdgeCutNew(edge,workerId)) {
                weightedEdgeCutScore += edge.getWeight();
            }
        }
        Set<Edge> incomingEdges = nodeToAdd.getEdgesIncoming();
        for (Edge edge : incomingEdges) {
            if (isEdgeCutNew(edge,workerId)) {
                weightedEdgeCutScore += edge.getWeight();
            }
        }
        return weightedEdgeCutScore;
        //modify isEdgeCutNew
    }

//    /**
//     * Determines if adding a given node to a partition results in an edge cut.
//     *
//     * An edge cut occurs if the node to be added and the node on the other end
//     * of an edge are in different partitions.
//     *
//     * @param edge The Edge object to check for a potential cut.
//     * @param nodeToAdd The DiaNode object that is being considered for
//     * addition.
//     * @param workers The map of worker IDs to Worker objects, representing the
//     * current partitions.
//     * @param allNodes The map of all DiaNode objects in the graph.
//     * @return true if adding the node results in an edge cut, false otherwise.
//     */
//    private boolean isEdgeCut(Edge edge, Dianode nodeToAdd, Map<Integer, Worker> workers, Map<Integer, Dianode> allNodes) {
//        int otherNodeId = (edge.getDianodeIdSource() == nodeToAdd.getId()) ? edge.getDianodeIdTarget() : edge.getDianodeIdSource();
//        Dianode otherNode = allNodes.get(otherNodeId);
//        Worker nodeWorker = findWorkerForNode(nodeToAdd, workers);
//        Worker otherNodeWorker = findWorkerForNode(otherNode, workers);
//        return nodeWorker != null && otherNodeWorker != null && !nodeWorker.equals(otherNodeWorker);
//    }
    
    
    private boolean isEdgeCutNew(Edge edge, int workerId) {
        return workerId != repository.getNodeById(edge.getDianodeIdTarget()).getWorkerId();
    }

    /**
     * Finds the Worker object that currently contains a given DiaNode.
     *
     * @param node The DiaNode object to locate among the partitions.
     * @param workers The map of worker IDs to Worker objects, representing the
     * current partitions.
     * @return The Worker object that contains the given node, or null if the
     * node is not found in any partition.
     */
    private Worker findWorkerForNode(Dianode node, Map<Integer, Worker> workers) {
        for (Worker worker : workers.values()) {
            if (worker.getNodes().containsKey(node.getId())) {
                return worker;
            }
        }
        return null;
    }

    public double normalizeLoadImbalanceRatio(double lir, double maxLir) {
        if (lir <= 1) {
            return 0; // Perfect balance scenario
        }
        return (lir - 1) / (maxLir - 1);
    }

}
