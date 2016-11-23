/*
 * $Id$
 *
 * Copyright 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * {@link Coordinate} {@link java.util.Map} implementation.
 *
 * @param       <V>             The value type.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class CoordinateMap<V> extends MapView<Coordinate,V> {
    private static final long serialVersionUID = -5266359325878137898L;

    protected Coordinate min = null;
    protected Coordinate max = null;

    /**
     * No-argument constructor.
     */
    public CoordinateMap() { super(new TreeMap<Coordinate,V>()); }

    /**
     * Constructor to specify minimum and maximum {@code Y} and {@code X}.
     *
     * @param   y0              {@code MIN(y)}
     * @param   x0              {@code MIN(x)}
     * @param   yN              {@code MAX(y) + 1}
     * @param   xN              {@code MAX(x) + 1}
     */
    public CoordinateMap(Number y0, Number x0, Number yN, Number xN) {
        this(y0.intValue(), x0.intValue(), yN.intValue(), xN.intValue());
    }

    private CoordinateMap(int y0, int x0, int yN, int xN) {
        this();

        this.min = new Coordinate(Math.min(y0, yN), Math.min(x0, xN));
        this.max = new Coordinate(Math.max(y0, yN), Math.max(x0, xN));
    }

    /**
     * Method to get a sub-{@link Map} of {@code this} {@link Map} also
     * backed by {@code this} {@link Map}.
     *
     * @param   y0              {@code MIN(y)}
     * @param   x0              {@code MIN(x)}
     * @param   yN              {@code MAX(y) + 1}
     * @param   xN              {@code MAX(x) + 1}
     *
     * @return  The sub-{@link Map} ({@link CoordinateMap}).
     */
    public CoordinateMap<V> subMap(Number y0, Number x0,
                                   Number yN, Number xN) {
        return new Backed(y0, x0, yN, xN);
    }

    /**
     * Constructor to specify maximum {@code Y} and {@code X} (origin
     * {@code (0, 0)}).
     *
     * @param   yN              {@code MAX(y) + 1}
     * @param   xN              {@code MAX(x) + 1}
     */
    public CoordinateMap(Number yN, Number xN) { this(0, yN, 0, xN); }

    /**
     * Method to get the {@link List} representing the specified column
     * backed by the {@link CoordinateMap}.
     *
     * @param   x               The X-coordinate.
     *
     * @return  The {@link List} of values.
     */
    public List<V> getColumn(Number x) { return new Column(x.intValue()); }

    /**
     * Method to get the {@link List} representing the specified row backed
     * by the {@link CoordinateMap}.
     *
     * @param   y               The Y-coordinate.
     *
     * @return  The {@link List} of values.
     */
    public List<V> getRow(Number y) { return new Row(y.intValue()); }

    /**
     * See {@link #containsKey(Object)}.
     *
     * @param   y               The Y-coordinate.
     * @param   x               The X-coordinate.
     *
     * @return  {@code true} if the {@link CoordinateMap} contains a key
     *          with the specified {@link Coordinate}; {@code false}
     *          otherwise.
     */
    public boolean containsKey(Number y, Number x) {
        return containsKey(new Coordinate(y, x));
    }

    /**
     * See {@link #get(Object)}.
     *
     * @param   y               The Y-coordinate.
     * @param   x               The X-coordinate.
     */
    public V get(Number y, Number x) { return get(new Coordinate(y, x)); }

    /**
     * See {@link #put(Coordinate,V)}.
     *
     * @param   y               The Y-coordinate.
     * @param   x               The X-coordinate.
     */
    public V put(Number y, Number x, V value) {
        return put(new Coordinate(y, x), value);
    }

    @Override
    public V put(Coordinate key, V value) {
        if (min != null) {
            min =
                new Coordinate(Math.min(key.getY(), min.getY()),
                               Math.min(key.getX(), min.getX()));
        } else {
            min = key;
        }

        if (max != null) {
            max =
                new Coordinate(Math.max(key.getY() + 1, max.getY()),
                               Math.max(key.getX() + 1, max.getX()));
        } else {
            max = new Coordinate(key.getY() + 1, key.getX() + 1);
        }

        return super.put(key, value);
    }

    private class Backed extends CoordinateMap<V> {
        private static final long serialVersionUID = -5583234581295736510L;

        private final TreeSet<Coordinate> keySet = new TreeSet<Coordinate>();

        public Backed(Number y0, Number x0, Number yN, Number xN) {
            super(y0, x0, yN, xN);

            for (Coordinate key : map().keySet()) {
                if (key.within(min, max)) {
                    keySet.add(key);
                }
            }
        }

        @Override
        public V get(Object key) {
            return keySet.contains(key) ? super.get(key) : null;
        }

        @Override
        public V put(Coordinate key, V value) {
            V old = get(key);

            keySet.add(key);
            super.put(key, value);

            return old;
        }

        @Override
        public V remove(Object key) {
            return keySet.remove(key) ? super.remove(key) : null;
        }

        @Override
        public void clear() {
            map().keySet().removeAll(keySet);
            keySet.clear();
        }

        @Override
        public Set<Coordinate> keySet() { return keySet; }

        @Override
        public Set<Entry<Coordinate,V>> entrySet() {
            keySet.retainAll(map().keySet());
            entrySet.clear();

            for (Entry<Coordinate,V> entry : map().entrySet()) {
                if (keySet.contains(entry.getKey())) {
                    entrySet.add(entry);
                }
            }

            return this.entrySet;
        }
    }

    private abstract class Line extends AbstractList<V> {
        protected Line() {
            super();
        }

        @Override
        public boolean add(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() { throw new UnsupportedOperationException(); }

        @Override
        public boolean addAll(int index, Collection<? extends V> collection) {
            throw new UnsupportedOperationException();
        }
    }

    private class Column extends Line {
        private final int y0;
        private final int yN;
        private final int x;

        public Column(int x) {
            super();

            this.y0 = min.getY();
            this.yN = max.getY();
            this.x = x;
        }

        @Override
        public V get(int y) { return CoordinateMap.this.get(y - y0, x); }

        @Override
        public V set(int y, V value) {
            return CoordinateMap.this.put(y - y0, x, value);
        }

        @Override
        public int size() { return yN - y0; }
    }

    private class Row extends Line {
        private final int y;
        private final int x0;
        private final int xN;

        public Row(int y) {
            super();

            this.y = y;
            this.x0 = min.getX();
            this.xN = max.getX();
        }

        @Override
        public V get(int x) { return CoordinateMap.this.get(y, x - x0); }

        @Override
        public V set(int x, V value) {
            return CoordinateMap.this.put(y, x - x0, value);
        }

        @Override
        public int size() { return xN - x0; }
    }
}
