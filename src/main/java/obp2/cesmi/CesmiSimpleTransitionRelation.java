package obp2.cesmi;

import obp2.runtime.core.ISimpleTransitionRelationIterator;

import java.util.Iterator;

public class CesmiSimpleTransitionRelation implements ISimpleTransitionRelationIterator<byte[]> {
    public final String cesmiPath;
    public final boolean asBuchi;
    final CesmiInstance instance;

    public CesmiSimpleTransitionRelation(
            String cesmiPath,
            boolean asBuchi) {
        this.cesmiPath = cesmiPath;
        this.asBuchi = asBuchi;
        instance = new CesmiInstance(cesmiPath, asBuchi);
    }

    public CesmiInstance getInstance() {
        return instance;
    }

    @Override
    public Iterator<byte[]> initialIterator() {
        return new TheIterator(null);
    }

    @Override
    public Iterator<byte[]> nextIterator(byte[] source) {
        return new TheIterator(source);
    }

    @Override
    public boolean isAccepting(byte[] configuration) {

        long flags = instance.flags(configuration);
        boolean isAccepting = ((flags>>1) & 1) != 0;
        return isAccepting;
    }

    class TheIterator implements Iterator<byte[]> {
        byte[] source;
        byte[] target;
        int next = 1;
        boolean consumed = true;

        public TheIterator(byte[] source) {
            this.source = source;
        }

        @Override
        public boolean hasNext() {
            if (next == 0) return false;
            if (consumed) {
                target = new byte[instance.configurationWidth()];
                next = source == null ? instance.initial(next, target) : instance.successor(next, source, target);
                if (next == 0) {
                    target = null;
                    return false;
                }
                consumed = false;
            }
            return true;
        }

        @Override
        public byte[] next() {
            consumed = true;
            return target;
        }
    }
}
