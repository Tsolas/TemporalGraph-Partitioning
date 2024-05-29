package gr.tsolas.tgp.graph;

import gr.tsolas.tgp.graph.utils.GraphParser;

/**
 *
 * @author giorgos
 */
public class Edge {

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

    public int getDianodeIdSource() {
        return dianodeIdSource;
    }

    public void setDianodeIdSource(int dianodeIdSource) {
        this.dianodeIdSource = dianodeIdSource;
    }

    public int getDianodeIdTarget() {
        return dianodeIdTarget;
    }

    public void setDianodeIdTarget(int dianodeIdTarget) {
        this.dianodeIdTarget = dianodeIdTarget;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Edge() {
    }

    public Edge(int timeStart, int timeEnd, int dianodeIdSource, int dianodeIdTarget, int weight) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.dianodeIdSource = dianodeIdSource;
        this.dianodeIdTarget = dianodeIdTarget;
        this.weight = weight;
    }

    private int timeStart;
    private int timeEnd;
    private int dianodeIdSource;
    private int dianodeIdTarget;
    private int weight;

    public Edge(int timeStart, int timeEnd, int dianodeIdStart, int dianodeIdEnd) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.dianodeIdSource = dianodeIdStart;
        this.dianodeIdTarget = dianodeIdEnd;
        this.weight = calculateWeight(timeStart, timeEnd); // Calculate the weight based on time instances
    }

    private int calculateWeight(int timeStart, int timeEnd) {
        return timeEnd - timeStart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Edge edge = (Edge) o;
        return timeStart == edge.timeStart
                && timeEnd == edge.timeEnd
                && dianodeIdSource == edge.dianodeIdSource
                && dianodeIdTarget == edge.dianodeIdTarget;
    }

}
