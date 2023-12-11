package gr.tsolas.tgp;

import gr.tsolas.tgp.graph.Worker;
import gr.tsolas.tgp.graph.utils.GraphParser;
import gr.tsolas.tgp.partitioning.HashPartitioning;
import gr.tsolas.tgp.partitioning.MyPartitioning;
import gr.tsolas.tgp.partitioning.Partitioner;
import gr.tsolas.tgp.partitioning.PartitioningStrategy;
import gr.tsolas.tgp.partitioning.Scoring;
import gr.tsolas.tgp.repository.GraphRepository;

/**
 *
 * @author giorgos
 */
public class TemporalGraphPartitioning {

    public static void main(String[] args) {
        if (args.length != 3 || (!"1".equals(args[0]) && !"2".equals(args[0]))) {
            System.out.println("Usage: <1 for hash-based or 2 for myPartitioning> <number of Workers> <dataset-file-path>");
            return;
        }

        String partitioningMethodSelector = args[0];
        int numberOfWorkers = Integer.parseInt(args[1]);
        String datasetFilePath = args[2];

        // Initialize the repository
        GraphRepository repository = new GraphRepository();
        repository.createWorkers(numberOfWorkers); // create and add workers

        //Instantiate Scoring class
        Scoring score = new Scoring(repository);

        // Initialize the partitioner and set the strategy based on user selection
        Partitioner partitioner = new Partitioner(); // Assuming Partitioner can work without Scoring for now
        PartitioningStrategy strategy = choosePartitioningStrategy(partitioningMethodSelector, repository);
        partitioner.setStrategy(strategy);

        // Initialize the parser with the repository and partitioner
        GraphParser parser = new GraphParser(repository, partitioner); // Adjusted constructor

        // Parse the dataset
        parser.parseFile(datasetFilePath);

        // Display the results
        displayWorkers(repository);

        double finalScore = score.calculateWeightedEdgeCutScoreRatio(repository.getAllWorkers());
        System.out.println("Final Weighted Edge Cut Score Ratio: " + finalScore);
    }

    private static PartitioningStrategy choosePartitioningStrategy(String methodSelector, GraphRepository repository) {
        if ("1".equals(methodSelector)) {
            return new HashPartitioning(repository.getWorkers());
        } else if ("2".equals(methodSelector)) {
            return new MyPartitioning(repository.getWorkers(), repository);
        } else {
            throw new IllegalArgumentException("Invalid partitioning method selector");
        }
    }

    private static void displayWorkers(GraphRepository repository) {
        System.out.println("Workers and their assigned nodes:");
        for (Worker worker : repository.getAllWorkers().values()) {
            System.out.println("Worker ID: " + worker.getId() + ", Node Count: " + worker.getNodes().size());
            for (Integer nodeId : worker.getNodes().keySet()) {
                System.out.println("  Node ID: " + nodeId);
            }
        }

    }
}
