package gr.tsolas.tgp.partitioning;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Worker;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class BFSPartitioning implements PartitioningStrategy {

    private Map<Integer, Worker> workers;
    private Set<Integer> visitedNodes;
    private List<Dianode> allNodes;
    private Scoring scoring;
    private int partitionedNodeCount = 1;
    private Map<Integer, Queue<Dianode>> workerQueues; // Queue for each worker

    public BFSPartitioning(Map<Integer, Worker> workers, List<Dianode> allNodes, Scoring scoring) {
        this.workers = workers;
        this.visitedNodes = new HashSet<>();
        this.allNodes = new ArrayList<>(allNodes);
        this.scoring = scoring;
        this.workerQueues = new HashMap<>();
    }

    @Override
    public void partition(Dianode node) {
        // This method will be empty because we are going to call partitionAllNodes instead
    }

    public void partitionAllNodes() {
        System.out.println("Starting BFS partitioning...");

        // Step 1: Select random seed nodes and assign them to workers
        selectRandomSeedNodes();

        // Step 2: Initialize queues for each worker with their neighbors
        initializeWorkerQueues();

        // Step 3: Perform BFS to partition remaining nodes
        partitionRemainingNodes();

        // Step 4: Score the final partitioning
        scorePartitioning();
    }

    private void selectRandomSeedNodes() {
        List<Dianode> shuffledNodes = new ArrayList<>(allNodes);
        Collections.shuffle(shuffledNodes);
        Iterator<Dianode> iterator = shuffledNodes.iterator();

        for (Worker worker : workers.values()) {
            if (iterator.hasNext()) {
                Dianode seedNode = iterator.next();
                if (!visitedNodes.contains(seedNode.getId())) {
                    worker.addNode(seedNode);
                    // System.out.println("Node Number " + partitionedNodeCount + " Partitioned!");
                    partitionedNodeCount++;
                    visitedNodes.add(seedNode.getId());
                    allNodes.remove(seedNode); // Remove seed node from allNodes list
                    // System.out.println("Assigned seed node " + seedNode.getId() + " to worker " + worker.getId());
                }
            }
        }
    }

    private void initializeWorkerQueues() {
        for (Worker worker : workers.values()) {
            Queue<Dianode> queue = new PriorityQueue<>(Comparator.comparing(node -> scoring.calculateWeightedEdgeCutScore(worker.getId(), node)));
            queue.addAll(worker.getNeighbors());
            workerQueues.put(worker.getId(), queue);
        }
    }

    private void partitionRemainingNodes() {
        boolean nodesRemaining = true;

        while (nodesRemaining) {
            nodesRemaining = false;

            for (Worker worker : workers.values()) {
                Queue<Dianode> queue = workerQueues.get(worker.getId());
                boolean isNotBestOption = false;
                do {
                    isNotBestOption = false;
                    if (!queue.isEmpty()) {
                        nodesRemaining = true;
                        Dianode topNeighbor = queue.poll();

                        if (topNeighbor != null && !visitedNodes.contains(topNeighbor.getId())) {
                            // Check if this neighbor is the top neighbor in other queues
                            Worker bestWorker = worker;
                            BigInteger bestScore = scoring.calculateWeightedEdgeCutScore(worker.getId(), topNeighbor);
                            for (Worker otherWorker : workers.values()) {
                                if (otherWorker.getId() != worker.getId()) {
                                    Queue<Dianode> otherQueue = workerQueues.get(otherWorker.getId());
                                    if (!otherQueue.isEmpty() && otherQueue.peek().equals(topNeighbor)) {
                                        BigInteger otherScore = scoring.calculateWeightedEdgeCutScore(otherWorker.getId(), topNeighbor);
                                        if (otherScore.compareTo(bestScore) < 0) {
                                            isNotBestOption = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!isNotBestOption) {
                                // Assign the neighbor to the best worker
                                addNodeToWorker(topNeighbor, bestWorker);

                                // Remove the neighbor from all queues and from the list of all nodes
                                allNodes.remove(topNeighbor);
                                for (Queue<Dianode> q : workerQueues.values()) {
                                    q.remove(topNeighbor);
                                }

                                // Mark this iteration as successful in placing a node
                                nodesRemaining = true;
                            }
                        } else {
                            continue;
                        }

                    }

                } while (isNotBestOption);

            }
        }
    }

    private void addNodeToWorker(Dianode node, Worker worker) {
        if (!visitedNodes.contains(node.getId())) {
            worker.addNode(node);
            workerQueues.get(worker.getId()).addAll(node.getNeighbors());
            visitedNodes.add(node.getId());
            partitionedNodeCount++;
        }
    }

    private void scorePartitioning() {
        int totalEdgeCuts = scoring.calculateTotalEdgeCuts();
        BigDecimal weightedEdgeCutScoreRatio = scoring.calculateWeightedEdgeCutScoreRatio(workers);
        double loadImbalanceRatio = scoring.calculateLoadImbalanceRatio();

        System.out.println("Total Edge Cuts: " + totalEdgeCuts);
        System.out.println("Weighted Edge Cut Score Ratio: " + weightedEdgeCutScoreRatio);
        System.out.println("Load Imbalance Ratio: " + loadImbalanceRatio);

        // Print worker node counts
        System.out.println("Worker node counts:");
        for (Worker worker : workers.values()) {
            System.out.println("Worker ID: " + worker.getId() + ", Node Count: " + worker.getNodeCount());
        }
    }
}
