package obp2.divine;

import java.nio.ByteBuffer;
import java.util.*;

public class CesmiReachability {

    void reachability(CesmiSimpleTransitionRelation transitionRelation) {
        int configurationWidth = transitionRelation.getBinding().configuration_width;
        CompactLinearScanSet known = new CompactLinearScanSet(5000, configurationWidth, Arrays::hashCode);
        Queue<ByteBuffer> frontier = new LinkedList<>();
        boolean atStart = true;

        Iterator<byte[]> neighbours;
        while (!frontier.isEmpty() || atStart) {
            if (atStart) {
                neighbours = transitionRelation.initialConfigurations();;
                atStart = false;
            } else {
                byte[] source = new byte[configurationWidth];
                frontier.remove().get(source);
                neighbours = transitionRelation.next(source);
            }

            for (byte[] neighbour: transitionRelation.iterable(neighbours)) {
                ByteBuffer buffer;
                if ((buffer = known.add(neighbour)) != null) {
                    frontier.add(buffer);
                }
            }
        }
        System.out.println( "state-space size: " + known.m_size);
    }

    public static void main(String[] args) {
        CesmiReachability o = new CesmiReachability();

        o.reachability(new CesmiSimpleTransitionRelation(false));
    }
}
