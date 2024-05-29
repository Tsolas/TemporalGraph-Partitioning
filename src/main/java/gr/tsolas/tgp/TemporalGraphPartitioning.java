package gr.tsolas.tgp;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Worker;
import gr.tsolas.tgp.graph.utils.GraphParser;
import gr.tsolas.tgp.partitioning.BFSPartitioning;
import gr.tsolas.tgp.partitioning.HashPartitioning;
import gr.tsolas.tgp.partitioning.MyPartitioning;
import gr.tsolas.tgp.partitioning.Partitioner;
import gr.tsolas.tgp.partitioning.PartitioningStrategy;
import gr.tsolas.tgp.partitioning.Scoring;
import gr.tsolas.tgp.repository.GraphRepository;
import java.util.ArrayList;
import java.util.List;

public class TemporalGraphPartitioning {

    public static void main(String[] args) {
        if (args.length < 3 || args.length > 4 || (!"1".equals(args[0]) && !"2".equals(args[0]) && !"3".equals(args[0]))) {
            System.out.println("Usage: <1 for hash-based or 2 for myPartitioning or 3 for bfsPartitioning> <number of Workers> <dataset-file-path> [<load imbalance threshold>]");
            return;
        }

        String partitioningMethodSelector = args[0];
        int numberOfWorkers = Integer.parseInt(args[1]);
        String datasetFilePath = args[2];
        double loadImbalanceThreshold = 0.0;

        if (!"3".equals(partitioningMethodSelector) && args.length != 4) {
            System.out.println("Usage: <1 for hash-based or 2 for myPartitioning or 3 for bfsPartitioning> <number of Workers> <dataset-file-path> [<load imbalance threshold>]");
            return;
        }

        if (!"3".equals(partitioningMethodSelector)) {
            loadImbalanceThreshold = Double.parseDouble(args[3]);
        }

        // Initialize the repository without creating workers
        GraphRepository repository = new GraphRepository();

        // Initialize the scoring with the repository
        Scoring scoring = new Scoring(repository);

        // Create workers with the scoring instance
        repository.setScoring(scoring);
        repository.createWorkers(numberOfWorkers);

        // Initialize the parser with the repository and partitioner
        Partitioner partitioner = new Partitioner();
        GraphParser parser = new GraphParser(repository, partitioner);

        // Parse the dataset
        parser.parseFile(datasetFilePath);

        // Choose the partitioning strategy and set it in the partitioner
        PartitioningStrategy strategy = choosePartitioningStrategy(partitioningMethodSelector, repository, loadImbalanceThreshold, scoring);
        partitioner.setStrategy(strategy);

        // Perform partitioning and display results
        if ("3".equals(partitioningMethodSelector)) {
            // For BFS partitioning, the scoring is printed within the strategy
            for (Dianode node : repository.getAllNodes().values()) {
                partitioner.partitionNode(node);
            }
        } else {
            // Perform partitioning for the other methods
            for (Dianode node : repository.getAllNodes().values()) {
                partitioner.partitionNode(node);
            }

            // Display the partitioning results
            displayWorkers(repository);

            // Calculate and display scores
            double loadImbalanceRatio = scoring.calculateLoadImbalanceRatio();
            System.out.println("Final Load Imbalance Ratio: " + loadImbalanceRatio);

            int totalEdgeCuts = scoring.calculateTotalEdgeCuts();
            System.out.println("Total Edge Cuts: " + totalEdgeCuts);

            double weightedEdgeCutScoreRatio = scoring.calculateWeightedEdgeCutScoreRatio(repository.getAllWorkers());
            System.out.println("Weighted Edge Cut Score Ratio: " + weightedEdgeCutScoreRatio);
            System.out.println("Total Edges:  " + parser.getEdgeCount());
        }
    }

    private static PartitioningStrategy choosePartitioningStrategy(String methodSelector, GraphRepository repository, double threshold, Scoring scoring) {
        if ("1".equals(methodSelector)) {
            return new HashPartitioning(repository.getWorkers());
        } else if ("2".equals(methodSelector)) {
            return new MyPartitioning(repository, threshold);
        } else if ("3".equals(methodSelector)) {
            List<Dianode> allNodes = new ArrayList<>(repository.getAllNodes().values());
            return new BFSPartitioning(repository.getWorkers(), allNodes, scoring);
        } else {
            throw new IllegalArgumentException("Invalid partitioning method selector");
        }
    }

    private static void displayWorkers(GraphRepository repository) {
        System.out.println("Workers and their assigned nodes:");
        for (Worker worker : repository.getAllWorkers().values()) {
            System.out.println("Worker ID: " + worker.getId() + ", Node Count: " + worker.getNodes().size());
        }
    }
}
