package obp2.divine;

import obp2.runtime.core.ISimpleTransitionRelationIterator;

import java.util.Iterator;

public class CesmiSimpleTransitionRelation implements ISimpleTransitionRelationIterator<byte[]> {
    CesmiBindingJNI binding;

    public CesmiSimpleTransitionRelation(boolean hasLTL) {
        binding = new CesmiBindingJNI(hasLTL);
    }

    public CesmiBindingJNI getBinding() {
        return binding;
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
        return binding.isAccepting(configuration);
    }

    class TheIterator implements Iterator<byte[]> {
        byte[] source = null;
        byte[] target = new byte[binding.configuration_width];
        boolean nextHasNext = false;
        boolean currentHasNext = true;

        boolean consumed = true;

        public TheIterator(byte[] source) {
            this.source = source;
        }

        @Override
        public boolean hasNext() {
            if (consumed && currentHasNext) {
                int targetWidth = target.length;
                nextHasNext = source == null ? binding.initial(target) : binding.next(source, target, targetWidth);
                if (targetWidth == 0) {
                    //if targetWidth is set to zero then we have a deadlock, so hasNext is false
                    return false;
                }
                consumed = false;
            }
            return currentHasNext;
        }

        @Override
        public byte[] next() {
            currentHasNext = nextHasNext;
            consumed = true;
            return target;
        }
    }
}
