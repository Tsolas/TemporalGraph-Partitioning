/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
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
    if (args.length != 2) {
      System.out.println("Please use: <number of Workers> <dataset-file-path>");
      return;
    }

    int numberOfWorkers = Integer.parseInt(args[0]);
    String datasetFilePath = args[1];

    //Parser and repository initialization
    GraphRepository repository = new GraphRepository();
    GraphParser parser = new GraphParser(repository);

    //create and add workers
    repository.createWorkers(numberOfWorkers);

    //Parse the dataset
    parser.parseFile(datasetFilePath);

    //display workers
    displayWorkers(repository);
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
}
