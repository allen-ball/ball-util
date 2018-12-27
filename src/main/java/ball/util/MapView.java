/*
 * $Id$
 *
 * Copyright 2016 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link Map} implementation base class to provide a view of a wrapped
 * {@link Map}.  Subclass designers should override {@link #entrySet()}
 * first.  All implemented methods simply pass the call to the wrapped
 * {@link Map} (all other methods are implemented by {@link AbstractMap}).
 *
 * @param       <K>             The key type.
 * @param       <V>             The value type.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class MapView<K,V> extends AbstractMap<K,V> implements Serializable {
    private static final long serialVersionUID = -1914866590078121842L;

    /** @serial */ private final Map<K,V> map;
    /** @serial */ protected final EntrySet entrySet = new EntrySet();

    /**
     * Sole constructor.
     *
     * @param   map             The {@link Map}.
     */
    public MapView(Map<K,V> map) {
        super();

        this.map = map;
    }

    /**
     * @return  The "wrapped" {@link Map}.
     */
    protected Map<K,V> map() { return map; }

    @Override
    public V get(Object key) { return map().get(key); }

    @Override
    public V put(K key, V value) { return map().put(key, value); }

    @Override
    public V remove(Object key) { return map().remove(key); }

    @Override
    public void clear() { map().clear(); }

    /**
     * Method returns a single {@link Set} backed by {@link #map()}.
     * Subclass implementations should update {@link #entrySet}.
     *
     * @return  {@link #entrySet}
     */
    @Override
    public Set<Entry<K,V>> entrySet() {
        entrySet.clear();
        entrySet.addAll(map().entrySet());

        return entrySet;
    }

    /**
     * {@link #entrySet} implementation class.
     */
    protected class EntrySet extends LinkedHashSet<Entry<K,V>> {
        private static final long serialVersionUID = 5015923978722180032L;

        /**
         * Sole constructor.
         */
        public EntrySet() { super(); }

        @Override
        public boolean remove(Object object) {
            boolean changed = super.remove(object);

            if (changed) {
                MapView.this.remove(((Entry<?,?>) object).getKey());
            }

            return changed;
        }

        @Override
        public Iterator<Entry<K,V>> iterator() {
            ArrayList<Entry<K,V>> list = new ArrayList<>();
            Iterator<Entry<K,V>> iterator = super.iterator();

            while (iterator.hasNext()) {
                list.add(iterator.next());
            }

            return new EntryIterator(list.iterator());
        }
    }

    private class EntryIterator implements Iterator<Entry<K,V>> {
        private final Iterator<Entry<K,V>> iterator;
        private Entry<K,V> current = null;

        public EntryIterator(Iterator<Entry<K,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() { return iterator.hasNext(); }

        @Override
        public Entry<K,V> next() {
            current = iterator.next();

            return current;
        }

        @Override
        public void remove() {
            iterator.remove();
            MapView.this.remove(current.getKey());
        }

        @Override
        public String toString() { return iterator.toString(); }
    }
}
