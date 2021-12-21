package obp2.divine;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/*
* This class implements a linear scan set with the elements in the table itself,
* it works only for elements of identical size
* - It is fixed size for now, but we can implement regrowing
* */
public class CompactLinearScanSet implements Set<byte[]> {
    int m_capacity;
    int m_size;
    byte[] m_content;
    byte[] m_occupancy;
    int m_element_size;
    Function<byte[], Integer> m_hasher;

    public CompactLinearScanSet(int capacity, int element_size, Function<byte[], Integer> hasher) {
        this.m_capacity = capacity;
        this.m_element_size = element_size;
        this.m_size = 0;
        this.m_content = new byte[m_capacity * m_element_size];
        this.m_occupancy = new byte[m_capacity/8 + 1];
        this.m_hasher = hasher;
    }

    int byte_idx(int index) {
        return index >> 3;
    }
    int bit_mask(int index) {
        return 0x1 << ((index) % 8);
    }
    boolean is_occupied(byte[] table, int index) {
        return (table[byte_idx(index)] & bit_mask(index)) > 0;
    }

    @Override
    public int size() {
        return m_size;
    }

    @Override
    public boolean isEmpty() {
        return m_size == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof byte[])) {
            return false;
        }
        byte[] item = (byte[]) o;
        if (m_element_size != item.length) {
            return false;
        }
        int hash = m_hasher.apply(item);
        int index = Math.abs(hash) % m_capacity;

        int content_index = index * m_element_size;

        //slot is empty
        if (! is_occupied(m_occupancy, index)) {
            return false;
        }

        //slot not empty, check if the same item is stored
        if (isTheSame(content_index, item)) {
            return true;
        }

        //not found, check next slots
        int start = index;
        boolean is_set = false;
        do {
            index = (index + 1) % m_capacity;
            content_index = index * m_element_size;
        } while(
                (is_set = is_occupied(m_occupancy, index))
                        && !isTheSame(content_index, item)
                        && index != start
        );

        if (index == start) {
            return false;
        }
        return is_set;
    }

    @Override
    public java.util.Iterator<byte[]> iterator() {
        return new Iterator();
    }

    class Iterator implements java.util.Iterator<byte[]> {

        int currentIndex = 0;
        int alreadyRead = 0;
        boolean indexIsSet = false;

        @Override
        public boolean hasNext() {
            if (m_size == 0) return false;
            if (m_size == alreadyRead) return false;
            for (int i = currentIndex; i < m_capacity; i++) {
                if (is_occupied(m_occupancy, i)) {
                    currentIndex = i;
                    indexIsSet = true;
                    return true;
                }
            }
            return false;
        }

        @Override
        public byte[] next() {
            byte[] bytes = new byte[m_element_size];
            int content_index = currentIndex * m_element_size;
            System.arraycopy(m_content, content_index, bytes, 0, m_element_size);
            alreadyRead ++;
            return bytes;
        }
    }

    @Override
    public Object[] toArray() {
        byte[][] array = new byte[m_size][];
        java.util.Iterator<byte[]> iterator = iterator();
        for (int i = 0; i < m_size; i++) {
            if (iterator.hasNext()) {
                array[i] = iterator.next();
            }
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int size = m_size;
        T[] r = a.length >= size ? a :
                (T[])java.lang.reflect.Array
                        .newInstance(a.getClass().getComponentType(), size);
        java.util.Iterator<byte[]> it = iterator();
        for (int i = 0; i<r.length; i++) {
            if (!it.hasNext()) {// fewer elements than expected
                if (a == r) {
                    r[i] = null; //null-terminate
                } else if (a.length < i) {
                    return Arrays.copyOf(r, i);
                } else {
                    System.arraycopy(r, 0, a, 0, i);
                    if (a.length > i) {
                        a[i] = null;
                    }
                }
                return a;
            }
            r[i] = (T)it.next();
        }
        //more elements than expected
        return it.hasNext() ? finishToArray(r, it) : r;
    }
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private static <T> T[] finishToArray(T[] r, java.util.Iterator<?> it) {
        int i = r.length;
        while (it.hasNext()) {
            int cap = r.length;
            if (i == cap) {
                int newCap = cap + (cap >> 1) + 1;
                // overflow-conscious code
                if (newCap - MAX_ARRAY_SIZE > 0)
                    newCap = hugeCapacity(cap + 1);
                r = Arrays.copyOf(r, newCap);
            }
            r[i++] = (T)it.next();
        }
        // trim if overallocated
        return (i == r.length) ? r : Arrays.copyOf(r, i);
    }
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError
                    ("Required array size too large");
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    @Override
    public boolean add(byte[] item) {
        ByteBuffer object = addAndGet(item);
        return object != null;
    }

    public ByteBuffer addAndGet(byte[] item) {
        if (m_element_size != item.length) {
            throw new RuntimeException("The element length does not match the expected length");
        }
        int hash = m_hasher.apply(item);
        int index = Math.abs(hash) % m_capacity;

        int content_index = index * m_element_size;

        //slot is empty
        if (! is_occupied(m_occupancy, index)) {
            return raw_add(index, content_index, item);
        }

        //slot not empty, check if the same item is stored
        if (isTheSame(content_index, item)) {
            return null;
            //return ByteBuffer.wrap(m_content, content_index, m_element_size);
        }

        //not found, check next slots
        int start = index;
        boolean is_set = false;
        do {
            index = (index + 1) % m_capacity;
            content_index = index * m_element_size;
        } while(
                (is_set = is_occupied(m_occupancy, index))
                && !isTheSame(content_index, item)
                && index != start
        );

        if (index == start) {
            throw new RuntimeException("Table is full");
        }
        if (is_set) {
            return null;//ByteBuffer.wrap(m_content, content_index, m_element_size);
        }
        return raw_add(index, content_index, item);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("'remove' is not yet supported by the CompactLinearScanSet");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends byte[]> c) {
        boolean changed = false;
        for (Object o : c) {
            if (!(o instanceof byte[])) {
                throw new IllegalArgumentException("The object " + o + " is not instance of byte[]");
            }
            byte[] bytes = (byte[]) o;
            if (add(bytes)) changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("'retainAll' is not yet supported by the CompactLinearScanSet");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("'removeAll' is not yet supported by the CompactLinearScanSet");
    }

    @Override
    public void clear() {
        Arrays.fill(m_occupancy, (byte)0);
        Arrays.fill(m_content, (byte)0);
        m_size = 0;
    }

    ByteBuffer raw_add(int index, int content_index, byte[] item) {
        m_occupancy[byte_idx(index)] |= bit_mask(index);
        System.arraycopy(item, 0, m_content, content_index, m_element_size);
        m_size ++;
        return ByteBuffer.wrap(m_content, content_index, m_element_size);
    }

    boolean isTheSame(int content_index, byte[] item) {
        for (int i=0;i<m_element_size;i++) {
            if (m_content[content_index + i] != item[i]) {
                return false;
            }
        }
        return true;
    }
}

