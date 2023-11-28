package gr.tsolas.tgp.graph;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author giorgos
 */
@Data
@AllArgsConstructor
public class Edge {

    private int timeStart;
    private int timeEnd;
    private int dianodeIdStart;
    private int dianodeIdEnd;
    private int weight;

    public Edge(int timeStart, int timeEnd, int dianodeIdStart, int dianodeIdEnd) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.dianodeIdStart = dianodeIdStart;
        this.dianodeIdEnd = dianodeIdEnd;
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
                && dianodeIdStart == edge.dianodeIdStart
                && dianodeIdEnd == edge.dianodeIdEnd;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStart, timeEnd, dianodeIdStart, dianodeIdEnd);
    }

}
