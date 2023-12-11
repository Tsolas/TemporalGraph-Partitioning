package gr.tsolas.tgp.graph;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author giorgos
 */

public class Worker {

    public Worker() {
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

    private int id;
    private int nodeCount;
    //to rename
    private int memory;
    private Map<Integer, Dianode> nodes;

    public Worker(int id) {
        this.id = id;
        this.nodeCount = 0;
        this.memory = 0;
        this.nodes = new HashMap<>();
    }

    public Dianode getNodeById(int nodeId) {
        return nodes.get(nodeId);
    }

    public void addNode(Dianode node) {
        nodes.put(node.getId(), node);
        nodeCount++;
        memory += node.getMemory();
        //add worker id 
    }

    public void removeNode(Dianode node) {
        if (nodes.containsKey(node.getId())) {
            nodes.remove(node.getId());
            nodeCount--;
            memory -= node.getMemory(); // Update the memory when a node is removed
        }
    }

}
