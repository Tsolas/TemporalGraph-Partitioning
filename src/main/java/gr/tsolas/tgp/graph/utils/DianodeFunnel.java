package gr.tsolas.tgp.graph.utils;

import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import gr.tsolas.tgp.graph.Dianode;

/**
 *
 * @author ed-dev
 */
public class DianodeFunnel implements Funnel<Dianode>{
    @Override
    public void funnel(Dianode node, PrimitiveSink into) {
        into
            .putInt(node.getId())
            .putInt(node.getTimeStart())
            .putInt(node.getTimeEnd());
    }
}
