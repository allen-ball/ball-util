/*
 * $Id: MapTable.java,v 1.1 2009-03-30 06:35:16 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.util.Map;

/**
 * Map Table implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class MapTable<K,V> extends Table {

    /**
     * Sole constructor.
     *
     * @param   map             The underlying Map.
     * @param   key             The Object describing the key ColumnModel.
     * @param   value           The Object describing the value
     *                          ColumnModel.
     * @param   tabs            The preferred tab stops.
     *
     * @see MapTableModel
     */
    public MapTable(Map<K,V> map, Object key, Object value, int... tabs) {
        super(new MapTableModel<K,V>(map, key, value), tabs);
    }

    @Override
    public MapTableModel getModel() {
        return (MapTableModel) super.getModel();
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
