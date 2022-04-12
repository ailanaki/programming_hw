package queue;


public interface Queue {
    //pre: element != null
    //post: n = n' + 1 && i = 1...n' : a[i]' = a[i] && a[n] = element
    void enqueue(Object element);

    //pre: n > 0
    //post: R = a[1] && n = n' - 1 && i = 1...n: a[i] = a[i - 1]'
    Object dequeue();

    //pre: n > 0
    //post: R = a[1] && n  = n' && i = 1...n: a[i] = a[i]'
    Object element();

    //post: R = n && n = n' && i = 1...n: a[i] = a[i]'
    int size();

    //post: n = 0 && a = []
    void clear();

    //post: R = b && i = 1..n: b'[i] = a[i]
    Queue makeCopy();

    //post: R = n > 0 && n = n' && i = 1...n: a[i] = a[i]'
    boolean isEmpty();

    //post: R = a' && i = 1..n: a'[i] = a[i]
    Object[] toArray();


}
