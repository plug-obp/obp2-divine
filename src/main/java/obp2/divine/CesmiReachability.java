package obp2.divine;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import obp2.cesmi.CesmiSimpleTransitionRelation;

public class CesmiReachability {

    void reachability(CesmiSimpleTransitionRelation transitionRelation) {
        int configurationWidth = transitionRelation.getInstance().configurationWidth();
        CompactLinearScanSet known = new CompactLinearScanSet(5000, configurationWidth, Arrays::hashCode);
        Queue<ByteBuffer> frontier = new LinkedList<>();
        boolean atStart = true;

        Iterator<byte[]> neighbours;
        while (!frontier.isEmpty() || atStart) {
            if (atStart) {
                neighbours = transitionRelation.initialIterator();;
                atStart = false;
            } else {
                byte[] source = new byte[configurationWidth];
                frontier.remove().get(source);
                neighbours = transitionRelation.nextIterator(source);
            }

            for (byte[] neighbour: transitionRelation.iterable(neighbours)) {
                ByteBuffer buffer;
                if ((buffer = known.addAndGet(neighbour)) != null) {
                    frontier.add(buffer);
                }
            }
        }
        System.out.println( "state-space size: " + known.m_size);
    }

    public static void main(String[] args) {
        CesmiReachability o = new CesmiReachability();

        o.reachability(new CesmiSimpleTransitionRelation(args[0], false));
    }
}
