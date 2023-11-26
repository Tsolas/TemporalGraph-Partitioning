package gr.tsolas.tgp;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Worker;
import gr.tsolas.tgp.graph.utils.GraphParser;
import gr.tsolas.tgp.repository.GraphRepository;

/**
 *
 * @author giorgos
 */
public class TemporalGraphPartitioning {

  public static void main(String[] args) {
    if (args.length != 3 || (!"1".equals(args[0]) && !"2".equals(args[0]))) {
      System.out.println("Please use: <1 for hash-based or 2 for myPartitioning> <number of Workers> <dataset-file-path>");
      return;
    }

    String partitioningMethodSelector = args[0];
    int numberOfWorkers = Integer.parseInt(args[1]);
    String datasetFilePath = args[2];

    //Parser and repository initialization
    GraphRepository repository = new GraphRepository();
    GraphParser parser = new GraphParser(repository);

    //create and add workers
    repository.createWorkers(numberOfWorkers);

    //Parse the dataset
    parser.parseFile(partitioningMethodSelector, datasetFilePath);

    //display workers
    displayWorkers(repository);
    displayAllNodes(repository);
  }

  private static void displayWorkers(GraphRepository repository) {
    for (Worker worker : repository.getAllWorkers().values()) {
      System.out.println("Worker ID: " + worker.getId());
      for (Integer nodeId : worker.getNodes().keySet()) {
        Dianode node = repository.getNodeById(nodeId);
        if (node != null) {
          System.out.println("  Node ID: " + node.getId());
        }
      }
    }
  }

  private static void displayAllNodes(GraphRepository repository) {
    System.out.println("All Nodes:");
    for (Dianode node : repository.getAllNodes().values()) {
      System.out.println(node.toString());
    }
  }
}
