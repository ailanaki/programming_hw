package queue;

import java.util.Arrays;

public class ArrayQueue extends AbstractQueue {
    private Object[] elements = new Object[5];
    private int end;
    private int head;

    protected void do_enqueue(Object element) {
        if (size == 0) {
            head = 0;
            end = 0;
        }
        ensureCapacity(end + 1);
        size++;
        elements[end++] = element;
    }

    private void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            elements = Arrays.copyOf(elements, 2 * capacity);
        }
    }

    @Override
    protected Object do_dequeue() {
        size--;
        return elements[head++];
    }

    @Override
    protected Object do_element() {
        return elements[head];
    }

    @Override
    protected ArrayQueue do_makeCopy() {
        final ArrayQueue copy = new ArrayQueue();
        copy.elements = Arrays.copyOf(elements, end);
        copy.head = head;
        copy.end = end;
        copy.size = size();
        return copy;
    }

    public String toStr() {
        StringBuilder str = new StringBuilder();
        str.append("[");
        if (end - head > 0) {
            for (int i = head; i < end - 1; i++) {
                str.append(elements[i]).append(", ");
            }
            str.append(elements[end - 1]);
        }
        str.append("]");
        return str.toString();
    }
}