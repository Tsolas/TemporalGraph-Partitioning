package gr.tsolas.tgp.graph;

import gr.tsolas.tgp.graph.utils.GraphParser;
import java.util.HashSet;
import java.util.Set;

public class Dianode {

    private int id;
    private int timeStart;
    private int timeEnd;
    private HashSet<Edge> edgesIncoming;
    private HashSet<Edge> edgesOutgoing;
    private int memory;
    private int workerId;
    private Set<Dianode> neighbors; // New field for neighbors

    public Dianode() {
        this.neighbors = new HashSet<>(); // Initialize neighbors set
    }

    public Dianode(int id, int timeStart, int timeEnd) {
        this.workerId = -1;
        this.id = id;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.edgesIncoming = new HashSet<>();
        this.edgesOutgoing = new HashSet<>();
        this.memory = 0;
        this.neighbors = new HashSet<>(); // Initialize neighbors set
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
    }

    public int getTimeEnd() {
        if (this.timeEnd == -1) {
            return GraphParser.getCurrentTimeInstance();
        }
        return this.timeEnd;
    }

    public void setTimeEnd(int timeEnd) {
        this.timeEnd = timeEnd;
    }

    public HashSet<Edge> getEdgesIncoming() {
        return edgesIncoming;
    }

    public void setEdgesIncoming(HashSet<Edge> edgesIncoming) {
        this.edgesIncoming = edgesIncoming;
    }

    public HashSet<Edge> getEdgesOutgoing() {
        return edgesOutgoing;
    }

    public void setEdgesOutgoing(HashSet<Edge> edgesOutgoing) {
        this.edgesOutgoing = edgesOutgoing;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public Set<Dianode> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Dianode neighbor) {
        neighbors.add(neighbor);
    }

    public void addIncomingEdge(Edge edge) {
        this.edgesIncoming.add(edge);
        updateWeight();
    }

    public void addOutgoingEdge(Edge edge) {
        this.edgesOutgoing.add(edge);
        updateWeight();
    }

    private void updateWeight() {
        this.memory = this.edgesIncoming.size() + edgesOutgoing.size();
    }

    @Override
    public String toString() {
        return "DiaNode{"
                + "id=" + id
                + ", timeStart=" + timeStart
                + ", timeEnd=" + timeEnd
                + '}';
    }
}
