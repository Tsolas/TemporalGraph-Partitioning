package gr.tsolas.tgp.repository;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Worker;
import gr.tsolas.tgp.partitioning.Scoring;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GraphRepository {

    private Map<Integer, Dianode> nodes;
    private Map<Integer, Worker> workers;
    private Scoring scoring;

    public GraphRepository() {
        this.nodes = new HashMap<>();
        this.workers = new HashMap<>();
    }

    // Setter for Scoring
    public void setScoring(Scoring scoring) {
        this.scoring = scoring;
    }

    // Dianode methods
    public void addNode(Dianode node) {
        nodes.put(node.getId(), node);
    }

    public Dianode getNodeById(int nodeId) {
        return nodes.get(nodeId);
    }

    public Map<Integer, Dianode> getAllNodes() {
        return Collections.unmodifiableMap(nodes);
    }

    // Worker methods
    public void addWorker(Worker worker) {
        workers.put(worker.getId(), worker);
    }

    public Worker getWorkerById(int workerId) {
        return workers.get(workerId);
    }

    public Map<Integer, Worker> getAllWorkers() {
        return Collections.unmodifiableMap(workers);
    }

    public void createWorkers(int numberOfWorkers) {
        for (int i = 0; i < numberOfWorkers; i++) {
            addWorker(new Worker(i, scoring));
        }
    }

    // Getters and Setters
    public Map<Integer, Dianode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<Integer, Dianode> nodes) {
        this.nodes = nodes;
    }

    public Map<Integer, Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(Map<Integer, Worker> workers) {
        this.workers = workers;
    }
}
