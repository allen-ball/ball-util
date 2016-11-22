/*
 * $Id$
 *
 * Copyright 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.AbstractMap;
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
public class MapView<K,V> extends AbstractMap<K,V> {

    private final Map<K,V> map;

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

    @Override
    public Set<Entry<K,V>> entrySet() { return map().entrySet(); }
}
