package l2trunk.gameserver.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameObjectArray<E extends GameObject> implements Iterable<E> {
    private static final Logger _log = LoggerFactory.getLogger(GameObjectArray.class);

    public final String name;
    public final int initCapacity;
    private final int resizeStep;
    private final List<Integer> freeIndexes;
    private E[] elementData;
    private int size = 0, real_size = 0;

    public GameObjectArray(String _name, int initialCapacity, int _resizeStep) {
        name = _name;
        resizeStep = _resizeStep;
        initCapacity = initialCapacity;

        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity (" + name + "): " + initialCapacity);
        if (resizeStep < 1)
            throw new IllegalArgumentException("Illegal resize step (" + name + "): " + resizeStep);

        freeIndexes = new ArrayList<>(resizeStep);
        elementData = (E[]) new GameObject[initialCapacity];
    }

    public int size() {
        return size;
    }

    public int getRealSize() {
        return real_size;
    }

    public int capacity() {
        return elementData.length;
    }

    public synchronized int add(E e) {
        Integer freeIndex = null;

        if (freeIndexes.size() > 0)
            freeIndex = freeIndexes.remove(freeIndexes.size() - 1);// remove last

        if (freeIndex != null) {
            real_size++;
            elementData[freeIndex] = e;
            return freeIndex;
        }

        if (elementData.length <= size) {
            int newCapacity = elementData.length + resizeStep;
            _log.warn("Object array [" + name + "] resized: " + elementData.length + " -> " + newCapacity);
            elementData = Arrays.copyOf(elementData, newCapacity);
        }
        elementData[size++] = e;
        real_size++;
        return size - 1;
    }

    public synchronized E remove(int index, int expectedObjId) {
        if (index >= size)
            return null;

        E old = elementData[index];
        if (old == null || old.getObjectId() != expectedObjId)
            return null;

        elementData[index] = null;
        real_size--;
        if (index == size - 1)
            size--;
        else
            freeIndexes.add(index);
        return old;
    }

    public E get(int index) {
        return index >= size ? null : elementData[index];
    }

    public E findByObjectId(int objId) {
        if (objId <= 0)
            return null;
        E o;
        for (int i = 0; i < size; i++) {
            o = elementData[i];
            if (o != null && o.getObjectId() == objId)
                return o;
        }
        return null;
    }

    public E findByName(String s) {
        if (s == null)
            return null;
        E o;
        for (int i = 0; i < size; i++) {
            o = elementData[i];
            if (o != null && s.equalsIgnoreCase(o.getName()))
                return o;
        }
        return null;
    }

    public List<E> findAllByName(String s) {
        if (s == null)
            return null;

        List<E> result = new ArrayList<>();
        E o;
        for (int i = 0; i < size; i++) {
            o = elementData[i];
            if (o != null && s.equalsIgnoreCase(o.getName()))
                result.add(o);
        }
        return result;
    }

    public List<E> getAll() {
        return getAll(new ArrayList<>(size));
    }

    public List<E> getAll(List<E> list) {
        E o;
        for (int i = 0; i < size; i++) {
            o = elementData[i];
            if (o != null)
                list.add(o);
        }
        return list;
    }

    private int indexOf(E o) {
        if (o == null)
            return -1;

        for (int i = 0; i < size; i++)
            if (o.equals(elementData[i]))
                return i;

        return -1;
    }

    public boolean contains(E o) {
        return indexOf(o) > -1;
    }

    @SuppressWarnings("unchecked")
    public synchronized void clear() {
        elementData = (E[]) GameObject.EMPTY_L2OBJECT_ARRAY;
        size = 0;
        real_size = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * хитрый итератор :)
     *
     * @author Drin
     */
    class Itr implements Iterator<E> {
        private int cursor = 0;
        private E _next;

        /**
         * находим и запоминаем
         */
        @Override
        public boolean hasNext() {
            while (cursor < size)
                if ((_next = elementData[cursor++]) != null)
                    return true;
            return false;
        }

        /**
         * возвращаем то что до этого нашли в hasNext()
         */
        @Override
        public E next() {
            E result = _next;
            _next = null;
            if (result == null)
                throw new NoSuchElementException();
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}