package gr.tsolas.tgp.repository;

import gr.tsolas.tgp.graph.Dianode;
import gr.tsolas.tgp.graph.Worker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author giorgos
 */
public class GraphRepository {

    private List<Dianode> nodes = new ArrayList<>();
    private Map<Integer, Worker> workers;

    public List<Dianode> getNodes() {
        return nodes;
    }

    public void setNodes(List<Dianode> nodes) {
        this.nodes = nodes;
    }

    public Map<Integer, Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(Map<Integer, Worker> workers) {
        this.workers = workers;
    }

    public GraphRepository() {
        this.nodes = new ArrayList<>();
        this.workers = new HashMap<>();
    }

    //dianode methods
    public void addNode(Dianode node) {
        nodes.add(node);
    }

    public Dianode getNodeById(int nodeId) {
        return nodes.get(nodeId);
    }

    public List<Dianode> getAllNodes() {
        return nodes;
    }

    //worker methods
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
            addWorker(new Worker(i));
        }
    }

    // Method to clear existing dianodes
    public void clearDianodes() {
        this.nodes.clear();
    }

}
