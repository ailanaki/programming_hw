package queue;

public class LinkedQueue extends AbstractQueue {
    private Node end;
    private Node head;

    @Override
    protected void do_enqueue(Object element) {
        if (size == 0) {
            head = new Node(element, end);
            end = head;
        } else {
            Node node = new Node(element, null);
            end.next = node;
            end = node;
        }
        size++;
    }

    @Override
    protected Object do_dequeue() {
        Object obj = head.value;
        size--;
        head = head.next;
        return obj;
    }

    @Override
    protected Object do_element() {
        return head.value;
    }

    @Override
    protected LinkedQueue do_makeCopy() {
        final LinkedQueue copy = new LinkedQueue();
        if (size > 0) {
            copy.head = new Node(head.value, copy.end);
            copy.size = 1;
            copy.end = copy.head;
            Node curr = head.next;
            while (curr != null) {
                copy.end.next = new Node(curr.value, end);
                copy.end = copy.end.next;
                curr = curr.next;
                copy.size++;
            }
        }
        return copy;
    }


    private class Node {
        private Object value;
        private Node next;

        public Node(Object value, Node next) {
            assert value != null;
            this.value = value;
            this.next = next;
        }
    }
}
