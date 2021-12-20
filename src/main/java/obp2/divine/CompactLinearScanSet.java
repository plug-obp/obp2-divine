package obp2.divine;

import java.nio.ByteBuffer;
import java.util.function.Function;

/*
* This class implements a linear scan set with the elements in the table itself,
* it works only for elements of identical size
* - It is fixed size for now, but we can implement regrowing
* */
public class CompactLinearScanSet {
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

    ByteBuffer add(byte[] item) {
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

