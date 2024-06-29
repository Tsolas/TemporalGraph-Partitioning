package gr.tsolas.tgp;

import gr.tsolas.tgp.graph.Worker;
import gr.tsolas.tgp.graph.utils.GraphParser;
import gr.tsolas.tgp.partitioning.HashPartitioning;
import gr.tsolas.tgp.partitioning.MyPartitioning;
import gr.tsolas.tgp.partitioning.Partitioner;
import gr.tsolas.tgp.partitioning.PartitioningStrategy;
import gr.tsolas.tgp.partitioning.Scoring;
import gr.tsolas.tgp.repository.GraphRepository;
import java.math.BigDecimal;

/**
 *
 * @author giorgos
 */
public class TemporalGraphPartitioning {

    public static void main(String[] args) {
        if (args.length != 4 || (!"1".equals(args[0]) && !"2".equals(args[0]))) {
            System.out.println("Usage: <1 for hash-based or 2 for myPartitioning> <number of Workers> <dataset-file-path> <load imbalance threshold>");
            return;
        }

        String partitioningMethodSelector = args[0];
        int numberOfWorkers = Integer.parseInt(args[1]);
        String datasetFilePath = args[2];
        double loadImbalanceThreshold = Double.parseDouble(args[3]);

        // Initialize the repository and create workers
        GraphRepository repository = new GraphRepository();
        repository.createWorkers(numberOfWorkers);

        // Initialize the partitioner with the chosen strategy
        Partitioner partitioner = new Partitioner();
        PartitioningStrategy strategy = choosePartitioningStrategy(partitioningMethodSelector, repository, loadImbalanceThreshold);
        partitioner.setStrategy(strategy);

        // Initialize the parser with the repository and partitioner
        GraphParser parser = new GraphParser(repository, partitioner);

        // Parse the dataset
        parser.parseFile(datasetFilePath);

        // Display the partitioning results
        displayWorkers(repository);

        // Initialize Scoring and calculate scores
        Scoring scoring = new Scoring(repository);
        double loadImbalanceRatio = scoring.calculateLoadImbalanceRatio();
        System.out.println("Final Load Imbalance Ratio: " + loadImbalanceRatio);

        int totalEdgeCuts = scoring.calculateTotalEdgeCuts();
        System.out.println("Total Edge Cuts: " + totalEdgeCuts);

        BigDecimal weightedEdgeCutScoreRatio = scoring.calculateWeightedEdgeCutScoreRatio(repository.getAllWorkers());
        System.out.println("Weighted Edge Cut Score Ratio: " + weightedEdgeCutScoreRatio);
        System.out.println("Total Edges:  " + parser.getEdgeCount());
    }

    private static PartitioningStrategy choosePartitioningStrategy(String methodSelector, GraphRepository repository, double threshold) {
        if ("1".equals(methodSelector)) {
            return new HashPartitioning(repository.getWorkers());
        } else if ("2".equals(methodSelector)) {
            return new MyPartitioning(repository, threshold);
        } else {
            throw new IllegalArgumentException("Invalid partitioning method selector");
        }
    }

    private static void displayWorkers(GraphRepository repository) {
        System.out.println("Workers and their assigned nodes:");
        for (Worker worker : repository.getAllWorkers().values()) {
            System.out.println("Worker ID: " + worker.getId() + ", Node Count: " + worker.getNodes().size());
//            for (Integer nodeId : worker.getNodes().keySet()) {
//                System.out.println("  Node ID: " + nodeId);
//            }
        }
    }
}
