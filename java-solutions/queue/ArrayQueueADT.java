package queue;

import java.util.Arrays;

public class ArrayQueueADT {
    private Object[] elements = new Object[10];
    private int end;
    private int head;

    //pre: element != null
    //post: n = n' + 1 && i = 1...n' : a[i]' = a[i] && a[n] = element
    public static void enqueue(ArrayQueueADT queue, Object element) {
        assert element != null;
        ensureCapacity(queue, queue.end + 1);
        queue.elements[queue.end++] = element;
    }

    private static void ensureCapacity(ArrayQueueADT queue, int capacity) {
        if (capacity > queue.elements.length) {
            queue.elements = Arrays.copyOf(queue.elements, 2 * capacity);
        }
    }

    //pre: n > 0
    //post: R = a[1] && n = n' - 1 && i = 1...n: a[i] = a[i - 1]'
    public static Object dequeue(ArrayQueueADT queue) {
        assert queue.end - queue.head > 0;
        return queue.elements[queue.head++];
    }

    //pre: n > 0
    //post: R = a[1] && n  = n' && i = 1...n: a[i] = a[i]'
    public static Object element(ArrayQueueADT queue) {
        assert queue.end - queue.head > 0;
        return queue.elements[queue.head];
    }

    //post: R = n && n = n' && i = 1...n: a[i] = a[i]'
    public static int size(ArrayQueueADT queue) {
        return queue.end - queue.head;
    }
    //post: R = " [ " + a  + "]" && n = n' && i = 1...n: a[i] = a[i]'
    public static String toStr(ArrayQueueADT queue) {
        StringBuilder str = new StringBuilder();
        str.append("[");
        if (queue.end - queue.head > 0) {
            for (int i = queue.head; i < queue.end - 1 ; i++) {
                str.append(queue.elements[i]).append(", ");
            }
            str.append(queue.elements[queue.end - 1]);
        }
        str.append("]");
        return str.toString();
    }
    // post: R = n > 0 && n = n' && i = 1...n: a[i] = a[i]'
    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.end - queue.head == 0;
    }

    //post: n = 0 && a = []
    public static void clear(ArrayQueueADT queue) {
        queue.head = 0;
        queue.end = 0;
    }
}

