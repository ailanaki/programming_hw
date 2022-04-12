package info.kgeorgiy.ja.yakupova.arrayset;
import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements SortedSet<E> {

    // :NOTE: final
    final ArrayList<E> sortedArray;
    final Comparator<E> comparator;

    // :NOTE: Collections.emptyList()
    public ArraySet() {
        this(Collections.emptyList());
    }

    public ArraySet(Collection<E> collection) {
        this(collection, null);
    }

    public ArraySet(Collection<E> collection, Comparator<E> comparator) {
        TreeSet<E> tree = new TreeSet<>(comparator);
        tree.addAll(collection);
        sortedArray = new ArrayList<>(tree);
        this.comparator = comparator;
    }

    @Override
    // :NOTE: modifiable iterator
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(sortedArray).iterator();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        int i =Collections.binarySearch(sortedArray,(E) o, comparator);
        return i >= 0;
    }

    @Override
    public int size() {
        return sortedArray.size();
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        // :NOTE: NPE
        int from = findElem(fromElement);
        int to = findElem(toElement);
        if ((comparator != null && comparator.compare(fromElement,toElement) > 0) || from > to) {
            throw new IllegalArgumentException();
        }
        return new ArraySet<E>(sortedArray.subList(from, to), comparator);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return new ArraySet<E>(sortedArray.subList(0, findElem(toElement)), comparator);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return new ArraySet<E>(sortedArray.subList(findElem(fromElement), size()), comparator);
    }

    @Override
    public E first() {
        if (sortedArray.isEmpty()) {
            throw new NoSuchElementException();
        }
        return sortedArray.get(0);
    }

    @Override
    public E last() {
        if (sortedArray.isEmpty()) {
            throw new NoSuchElementException();
        }
        return sortedArray.get(size() - 1);
    }

    private int findElem(E o) {
        int i = Collections.binarySearch(sortedArray, o, comparator);
        if (i < 0) {
            i += 1;
        }
        return Math.abs(i);
    }

};
