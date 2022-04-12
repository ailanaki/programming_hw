package queue;

public abstract class AbstractQueue implements Queue {

    protected int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Object[] toArray() {
        Queue copy = makeCopy();
        Object[] obj = new Object[size];
        for (int i = 0; i < size; i++) {
            obj[i] = copy.dequeue();
        }
        return obj;
    }
    public void clear(){
        size = 0;
    }

    public void enqueue(Object element){
        assert element != null;
        do_enqueue(element);
    }

    public Object dequeue(){
        assert size > 0;
        return do_dequeue();
    }

    public Object element() {
        assert size > 0;
        return do_element();
    }
    public Queue makeCopy(){
        return do_makeCopy();
    }

    protected abstract void do_enqueue(Object element);

    protected abstract Object do_dequeue();

    protected abstract Object do_element();

    protected abstract Queue do_makeCopy();


}

