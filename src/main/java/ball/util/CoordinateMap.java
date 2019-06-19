/*
 * $Id$
 *
 * Copyright 2016 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * {@link Coordinate} {@link java.util.Map} implementation.
 *
 * @param       <V>             The value type.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class CoordinateMap<V> extends MapView<Coordinate,V>
                              implements SortedMap<Coordinate,V>, TableModel {
    private static final long serialVersionUID = -5722715786710308482L;

    /** @serial */ private final Class<? extends V> type;
    /** @serial */ private Coordinate min = null;
    /** @serial */ private Coordinate max = null;
    /** @serial */ private final EventListenerList list = new EventListenerList();

    /**
     * Constructor to create an empty {@link CoordinateMap}.
     *
     * @param   type            The {@link CoordinateMap} value
     *                          {@link Class}.
     */
    public CoordinateMap(Class<? extends V> type) {
        super(new TreeMap<>());

        this.type = type;
    }

    /**
     * Constructor to specify minimum and maximum {@code Y} and {@code X}.
     *
     * @param   type            The {@link CoordinateMap} value
     *                          {@link Class}.
     * @param   y0              {@code MIN(y)}
     * @param   x0              {@code MIN(x)}
     * @param   yN              {@code MAX(y) + 1}
     * @param   xN              {@code MAX(x) + 1}
     */
    public CoordinateMap(Class<? extends V> type,
                         Number y0, Number x0, Number yN, Number xN) {
        this(type);

        resize(y0, x0, yN, xN);
    }

    /**
     * Constructor to specify maximum {@code Y} and {@code X} (origin
     * {@code (0, 0)}).
     *
     * @param   type            The {@link CoordinateMap} value
     *                          {@link Class}.
     * @param   yN              {@code MAX(y) + 1}
     * @param   xN              {@code MAX(x) + 1}
     */
    public CoordinateMap(Class<? extends V> type, Number yN, Number xN) {
        this(type, 0, 0, yN, xN);
    }

    private CoordinateMap(Class<? extends V> type, Map<Coordinate,V> map,
                          Number y0, Number x0, Number yN, Number xN) {
        super(map);

        this.type = type;

        resize(y0, x0, yN, xN);
    }

    @Override
    protected SortedMap<Coordinate,V> map() {
        return (SortedMap<Coordinate,V>) super.map();
    }

    /**
     * Method to get the value type of {@link.this} {@link CoordinateMap}.
     *
     * @return  The value type {@link Class}.
     */
    protected Class<? extends V> getType() { return type; }

    /**
     * Method to specify new limits for the {@link CoordinateMap}.
     *
     * @param   y0              {@code MIN(y)}
     * @param   x0              {@code MIN(x)}
     * @param   yN              {@code MAX(y) + 1}
     * @param   xN              {@code MAX(x) + 1}
     *
     * @return  {@link.this} {@link CoordinateMap}.
     */
    public CoordinateMap<V> resize(Number y0, Number x0,
                                   Number yN, Number xN) {
        resize(y0.intValue(), x0.intValue(), yN.intValue(), xN.intValue());

        return this;
    }

    /**
     * Method to specify new limits for the {@link CoordinateMap} with
     * {@code [y0, x0] = [0, 0]}.
     *
     * @param   yN              {@code MAX(y) + 1}
     * @param   xN              {@code MAX(x) + 1}
     *
     * @return  {@link.this} {@link CoordinateMap}.
     */
    public CoordinateMap<V> resize(Number yN, Number xN) {
        return resize(0, 0, yN, xN);
    }

    private void resize(int y0, int x0, int yN, int xN) {
        min = new Coordinate(Math.min(y0, yN), Math.min(x0, xN));
        max = new Coordinate(Math.max(y0, yN), Math.max(x0, xN));

        keySet().retainAll(Coordinate.generate(min, max));

        fireTableStructureChanged();
    }

    public Coordinate getMin() { return min; }
    public Coordinate getMax() { return max; }

    public int getMinY() { return getMin().getY(); }
    public int getMinX() { return getMin().getX(); }

    public int getMaxY() { return getMax().getY(); }
    public int getMaxX() { return getMax().getX(); }

    /**
     * Method to determine if the {@link Coordinate} is included in
     * {@link.this} {@link CoordinateMap}'s space.  Because the map is
     * sparse, this method may return {@code true} when
     * {@link #containsKey(Object)} returns {@code false}.
     *
     * @param   coordinate      The {@link Coordinate}.
     *
     * @return  {@code true} if within the space; {@code false} otherwise.
     */
    public boolean includes(Coordinate coordinate) {
        return coordinate.within(getMin(), getMax());
    }

    /**
     * Method to get a {@link List} of columns
     * (see {@link #column(Number)}).
     *
     * @return  The {@link List} of columns.
     */
    public List<CoordinateMap<V>> columns() {
        ArrayList<CoordinateMap<V>> list = new ArrayList<>(getColumnCount());

        if (getColumnCount() > 0) {
            for (int x = getMinX(), xN = getMaxX(); x < xN; x += 1) {
                list.add(column(x));
            }
        }

        return list;
    }

    /**
     * Method to get a {@link List} of rows (see {@link #row(Number)}).
     *
     * @return  The {@link List} of rows.
     */
    public List<CoordinateMap<V>> rows() {
        ArrayList<CoordinateMap<V>> list = new ArrayList<>(getRowCount());

        if (getRowCount() > 0) {
            for (int y = getMinY(), yN = getMaxY(); y < yN; y += 1) {
                list.add(row(y));
            }
        }

        return list;
    }

    /**
     * Method to get the {@link List} representing the specified column
     * backed by the {@link CoordinateMap}.
     *
     * @param   x               The X-coordinate.
     *
     * @return  The {@link CoordinateMap} representing the column.
     */
    public CoordinateMap<V> column(Number x) {
        return subMap(getMinY(), x, getMaxY(), x.intValue() + 1);
    }

    /**
     * Method to get the {@link List} representing the specified row backed
     * by the {@link CoordinateMap}.
     *
     * @param   y               The Y-coordinate.
     *
     * @return  The {@link CoordinateMap} representing the row.
     */
    public CoordinateMap<V> row(Number y) {
        return subMap(y, getMinX(), y.intValue() + 1, getMaxX());
    }

    /**
     * Method to get a sub-{@link Map} of {@link.this} {@link Map} also
     * backed by {@link.this} {@link Map}.
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
        return new Sub<>(this, y0, x0, yN, xN);
    }

    /**
     * Method to get {@link.this} {@link CoordinateMap} values as a
     * {@link List}.  Updates made through {@link List#set(int,Object)} will
     * be made to {@link.this} {@link CoordinateMap}.
     *
     * @return  The {@link List} of {@link CoordinateMap} values.
     */
    public List<V> asList() { return new BackedList(); }

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
     *
     * @return  The value at the coordinate (may be {@code null}).
     */
    public V get(Number y, Number x) { return get(new Coordinate(y, x)); }

    /**
     * See {@link #put(Object,Object)}.
     *
     * @param   y               The Y-coordinate.
     * @param   x               The X-coordinate.
     * @param   value           The value at the coordinate.
     *
     * @return  The previous value at the coordinate.
     */
    public V put(Number y, Number x, V value) {
        return put(new Coordinate(y, x), value);
    }

    @Override
    public V put(Coordinate key, V value) {
        if (min != null) {
            min =
                new Coordinate(Math.min(key.getY(), getMinY()),
                               Math.min(key.getX(), getMinX()));
        } else {
            min = key;
        }

        if (max != null) {
            max =
                new Coordinate(Math.max(key.getY() + 1, getMaxY()),
                               Math.max(key.getX() + 1, getMaxX()));
        } else {
            max = new Coordinate(key.getY() + 1, key.getX() + 1);
        }

        V old = super.put(key, value);

        fireTableCellUpdated(key.getY() - getMinY(), key.getX() - getMinX());

        return old;
    }

    @Override
    public V remove(Object key) {
        V old = super.remove(key);

        if (key instanceof Coordinate) {
            Coordinate coordinate = (Coordinate) key;

            fireTableCellUpdated(coordinate.getY() - getMinY(),
                                 coordinate.getX() - getMinX());
        }

        return old;
    }

    @Override
    public Comparator<? super Coordinate> comparator() {
        return map().comparator();
    }

    @Override
    public CoordinateMap<V> subMap(Coordinate from, Coordinate to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CoordinateMap<V> headMap(Coordinate key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CoordinateMap<V> tailMap(Coordinate key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Coordinate firstKey() { return map().firstKey(); }

    @Override
    public Coordinate lastKey() { return map().lastKey(); }

    @Override
    public void clear() {
        super.clear();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return (getMax() != null) ? (getMaxY() - getMinY()) : 0;
    }

    @Override
    public int getColumnCount() {
        return (getMax() != null) ? (getMaxX() - getMinX()) : 0;
    }

    @Override
    public String getColumnName(int x) { return null; }

    @Override
    public Class<? extends V> getColumnClass(int x) { return getType(); }

    @Override
    public boolean isCellEditable(int y, int x) { return false; }

    @Override
    public V getValueAt(int y, int x) {
        return get(y - getMinY(), x - getMinX());
    }

    @Override
    public void setValueAt(Object value, int y, int x) {
        put(y - getMinY(), x - getMinX(), getColumnClass(x).cast(value));
    }

    @Override
    public void addTableModelListener(TableModelListener listener) {
        list.add(TableModelListener.class, listener);
    }

    @Override
    public void removeTableModelListener(TableModelListener listener) {
        list.remove(TableModelListener.class, listener);
    }

    protected TableModelListener[] getTableModelListeners() {
        return list.getListeners(TableModelListener.class);
    }

    protected void fireTableDataChanged() {
        fireTableChanged(new TableModelEvent(this));
    }

    protected void fireTableStructureChanged() {
        fireTableChanged(new TableModelEvent(this,
                                             TableModelEvent.HEADER_ROW));
    }

    protected void fireTableRowsInserted(int start, int end) {
        fireTableChanged(new TableModelEvent(this, start, end,
                                             TableModelEvent.ALL_COLUMNS,
                                             TableModelEvent.INSERT));
    }

    protected void fireTableRowsUpdated(int start, int end) {
        fireTableChanged(new TableModelEvent(this, start, end,
                                             TableModelEvent.ALL_COLUMNS,
                                             TableModelEvent.UPDATE));
    }

    protected void fireTableRowsDeleted(int start, int end) {
        fireTableChanged(new TableModelEvent(this, start, end,
                                             TableModelEvent.ALL_COLUMNS,
                                             TableModelEvent.DELETE));
    }

    protected void fireTableCellUpdated(int row, int column) {
        fireTableChanged(new TableModelEvent(this, row, row, column));
    }

    protected void fireTableChanged(TableModelEvent event) {
        TableModelListener[] listeners = getTableModelListeners();

        for (int i = listeners.length - 1; i >= 0; i -= 1) {
            listeners[i].tableChanged(event);
        }
    }

    private static class Sub<V> extends CoordinateMap<V> {
        private static final long serialVersionUID = -7614329296625073237L;

        public Sub(CoordinateMap<V> map,
                   Number y0, Number x0, Number yN, Number xN) {
            super(map.getType(), map, y0, x0, yN, xN);
        }

        @Override
        protected CoordinateMap<V> map() {
            return (CoordinateMap<V>) super.map();
        }

        @Override
        public V get(Object key) { return get((Coordinate) key); }

        private V get(Coordinate key) {
            return key.within(getMin(), getMax()) ? super.get(key) : null;
        }

        @Override
        public V put(Coordinate key, V value) {
            if (! key.within(getMin(), getMax())) {
                throw new IllegalArgumentException(key + " is outside "
                                                   + getMin() + " and "
                                                   + getMax());
            }

            return super.put(key, value);
        }

        @Override
        public V remove(Object key) { return remove((Coordinate) key); }

        private V remove(Coordinate key) {
            return key.within(getMin(), getMax()) ? super.remove(key) : null;
        }

        @Override
        public Set<Entry<Coordinate,V>> entrySet() {
            entrySet.clear();

            for (Entry<Coordinate,V> entry : map().entrySet()) {
                if (entry.getKey().within(getMin(), getMax())) {
                    entrySet.add(entry);
                }
            }

            return entrySet;
        }
    }

    private class BackedList extends AbstractList<V> {
        private ArrayList<Coordinate> list = new ArrayList<>();

        public BackedList() {
            super();

            list.addAll(Coordinate.generate(CoordinateMap.this.getMin(),
                                            CoordinateMap.this.getMax()));
        }

        @Override
        public int size() { return list.size(); }

        @Override
        public V get(int index) {
            return CoordinateMap.this.get(list.get(index));
        }

        @Override
        public V set(int index, V value) {
            return CoordinateMap.this.put(list.get(index), value);
        }

        @Override
        public void clear() { throw new UnsupportedOperationException(); }
    }
}
