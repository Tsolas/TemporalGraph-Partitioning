package gr.tsolas.tgp.graph;

import gr.tsolas.tgp.partitioning.Scoring;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author giorgos
 */
public class Worker {

    private int id;
    private int nodeCount;
    private int memory;
    private Map<Integer, Dianode> nodes;
    private Scoring scoring;

    public Worker(int id, Scoring scoring) {
        this.id = id;
        this.nodeCount = 0;
        this.memory = 0;
        this.nodes = new HashMap<>();
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

    public void addNode(Dianode node) {
        nodes.put(node.getId(), node);
        node.setWorkerId(this.id); // Update the workerId of the node
        nodeCount++;
        memory += node.getMemory();
    }

    public void removeNode(Dianode node) {
        if (nodes.containsKey(node.getId())) {
            nodes.remove(node.getId());
            nodeCount--;
            memory -= node.getMemory();
        }
    }

}
