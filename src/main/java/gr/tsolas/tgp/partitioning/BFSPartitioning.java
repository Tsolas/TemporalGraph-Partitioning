package gr.tsolas.tgp.partitioning;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Worker;
import java.util.*;

public class BFSPartitioning implements PartitioningStrategy {

    private Map<Integer, Worker> workers;
    private Set<Integer> visitedNodes;
    private List<Dianode> allNodes;
    private Scoring scoring;

    public BFSPartitioning(Map<Integer, Worker> workers, List<Dianode> allNodes, Scoring scoring) {
        this.workers = workers;
        this.visitedNodes = new HashSet<>();
        this.allNodes = new ArrayList<>(allNodes);
        this.scoring = scoring;
    }

    @Override
    public void partition(Dianode node) {
        // This method will be empty because we are going to call partitionAllNodes instead
    }

    public void partitionAllNodes() {
        // Step 1: Select random seed nodes and assign them to workers
        selectRandomSeedNodes();

        // Step 2: Perform BFS to partition remaining nodes
        partitionRemainingNodes();

        // Step 3: Score the final partitioning
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
                    visitedNodes.add(seedNode.getId());
                    allNodes.remove(seedNode); // Remove seed node from allNodes list
                    System.out.println("Assigned seed node " + seedNode.getId() + " to worker " + worker.getId());
                }
            }
        }
    }

    private void partitionRemainingNodes() {
        // Get a list of workers for round-robin assignment
        List<Worker> workerList = new ArrayList<>(workers.values());

        for (Worker worker : workers.values()) {
            for (Dianode seedNode : worker.getNodes().values()) {
                bfs(seedNode, workerList);
            }
        }
    }

    private void bfs(Dianode startNode, List<Worker> workerList) {
        Queue<Dianode> queue = new LinkedList<>();
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Dianode currentNode = queue.poll();

            // Track top neighbors for each worker
            Map<Dianode, List<Worker>> topNeighborsMap = new HashMap<>();
            for (Worker worker : workerList) {
                Dianode topNeighbor = worker.getTopNeighbor();
                if (topNeighbor != null) {
                    topNeighborsMap.computeIfAbsent(topNeighbor, k -> new ArrayList<>()).add(worker);
                }
            }

            // Process each neighbor of the current node
            for (Dianode neighbor : currentNode.getNeighbors()) {
                if (!visitedNodes.contains(neighbor.getId())) {
                    List<Worker> candidateWorkers = topNeighborsMap.getOrDefault(neighbor, new ArrayList<>());

                    if (candidateWorkers.size() > 1) {
                        // If the neighbor is in multiple workers' lists, choose the one with the least edge cut score
                        Worker bestWorker = candidateWorkers.stream()
                                .min(Comparator.comparingDouble(w -> scoring.calculateWeightedEdgeCutScore(w.getId(), neighbor)))
                                .orElse(candidateWorkers.get(0));
                        addNodeToWorker(neighbor, bestWorker, queue);
                    } else if (!candidateWorkers.isEmpty()) {
                        // If the neighbor is in only one worker's list, add it to that worker
                        addNodeToWorker(neighbor, candidateWorkers.get(0), queue);
                    }
                }
            }
        }
    }

    private void addNodeToWorker(Dianode node, Worker worker, Queue<Dianode> queue) {
        if (!visitedNodes.contains(node.getId())) {
            worker.addNode(node);
            queue.add(node);
            visitedNodes.add(node.getId());
            allNodes.remove(node); // Remove node from allNodes list
        }
    }

    private void scorePartitioning() {
        int totalEdgeCuts = scoring.calculateTotalEdgeCuts();
        double weightedEdgeCutScoreRatio = scoring.calculateWeightedEdgeCutScoreRatio(workers);
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
