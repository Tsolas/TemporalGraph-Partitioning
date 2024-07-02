package gr.tsolas.tgp.graph;

import gr.tsolas.tgp.partitioning.Scoring;
import java.util.*;

public class Worker {

    private int id;
    private int nodeCount;
    private int memory;
    private Map<Integer, Dianode> nodes;
    private Set<Dianode> neighborNodes;
    private Scoring scoring;

    public Worker(int id, Scoring scoring) {
        this.id = id;
        this.nodeCount = 0;
        this.memory = 0;
        this.nodes = new HashMap<>();
        this.neighborNodes = new HashSet<>();
        this.scoring = scoring;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public Map<Integer, Dianode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<Integer, Dianode> nodes) {
        this.nodes = nodes;
    }

    public Set<Dianode> getNeighborNodes() {
        return neighborNodes;
    }

    public List<Dianode> getSortedNeighborNodes() {
        List<Dianode> sortedNeighbors = new ArrayList<>(neighborNodes);
        sortedNeighbors.sort(Comparator.comparing((Dianode node) -> scoring.calculateWeightedEdgeCutScore(id, node)));
        return sortedNeighbors;
    }

    public Dianode getTopNeighbor() {
        List<Dianode> sortedNeighbors = getSortedNeighborNodes();
        return sortedNeighbors.isEmpty() ? null : sortedNeighbors.get(0);
    }

    public void addNode(Dianode node) {
        nodes.put(node.getId(), node);
        node.setWorkerId(this.id); // Update the workerId of the node
        nodeCount++;
        memory += node.getMemory();
        addNeighbors(node);
    }

    public void removeNode(Dianode node) {
        if (nodes.containsKey(node.getId())) {
            nodes.remove(node.getId());
            nodeCount--;
            memory -= node.getMemory();
            // Additional logic can be added here to remove neighbors if necessary
        }
    }

    private void addNeighbors(Dianode node) {
        for (Dianode neighbor : node.getNeighbors()) {
            if (!nodes.containsKey(neighbor.getId())) {
                neighborNodes.add(neighbor);
            }
        }
    }

    public Set<Dianode> getNeighbors() {
        return neighborNodes;
    }
}
