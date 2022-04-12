package queue;

import java.util.Arrays;

public class ArrayQueueModule {
    private static Object[] elements = new Object[5];
    private static int end;
    private static int head;

    //pre: element!=null
    //post: n = n' + 1 && i = 1...n' : a[i]' = a[i] && a[n] = element
    public static void enqueue(Object element) {
        assert element != null;
        ensureCapacity(end + 1);
        elements[end++] = element;
    }

    private static void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            elements = Arrays.copyOf(elements, 2 * capacity);
        }
    }
    //pre: n > 0
    //post: R = a[1] && n = n' - 1 && i = 1...n: a[i] = a[i - 1]'
    public static Object dequeue() {
        assert end - head > 0;
        return elements[head++];
    }

    //pre: n > 0
    //post: R = a[1] && n  = n' && i = 1...n: a[i] = a[i]'
    public static Object element() {
        assert end - head > 0;
        return elements[head];
    }

    //post: R = n && n = n' && i = 1...n: a[i] = a[i]'
    public static int size() {
        return end - head;
    }

    // post: R = n > 0 && n = n' && i = 1...n: a[i] = a[i]'
    public static boolean isEmpty() {
        return end - head == 0;
    }

    //post: n = 0 && a = []
    public static void clear() {
        head = 0;
        end = 0;
    }

    //post: R = " [ " + a  + "]" && n = n' && i = 1...n: a[i] = a[i]'
    public static String toStr(){
        StringBuilder str = new StringBuilder();
        str.append("[");
        if (end - head > 0) {
            for (int i = head; i < end - 1 ; i++) {
                str.append(elements[i]).append(", ");
            }
            str.append(elements[end - 1]);
        }
        str.append("]");
        return str.toString();
    }


}
