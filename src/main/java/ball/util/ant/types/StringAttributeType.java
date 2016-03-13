/*
 * $Id$
 *
 * Copyright 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.types;

import ball.util.ant.taskdefs.NotNull;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ball.util.StringUtil.NIL;

/**
 * Class to provide a {@link String} name-value (attribute) for
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * implementations.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class StringAttributeType extends StringValueType {
    private String name = null;

    /**
     * @param   name            The attribute name.
     */
    @ConstructorProperties({ "name" })
    public StringAttributeType(String name) {
        super();

        this.name = name;
    }

    /**
     * No-argument constructor.
     */
    public StringAttributeType() { this(null); }

    @ConstructorProperties({ "name", "value" })
    private StringAttributeType(String name, String value) {
        this(name);

        setValue(value);
    }

    private StringAttributeType(String name, Object value) {
        this(name, (value != null) ? value.toString() : null);
    }

    @NotNull
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() { return getName() + "=" + getValue(); }

    /**
     * Static method to convert an {@link Iterable} of
     * {@link StringAttributeType}s to a {@link Map}.
     *
     * @param   iterable        The {@link Iterable} of
     *                          {@link StringAttributeType}s.
     *
     * @return  The {@link Map}.
     */
    public static Map<String,String> asMap(Iterable<? extends StringAttributeType> iterable) {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();

        for (StringAttributeType attribute : iterable) {
            map.put(attribute.getName(), attribute.getValue());
        }

        return map;
    }

    /**
     * Static method to convert a {@link Map} to a {@link List} of
     * {@link StringAttributeType}s.
     *
     * @param   map             The {@link Map}.
     *
     * @return  A {@link List} of {@link StringAttributeType}s.
     */
    public static List<StringAttributeType> fromMap(Map<?,?> map) {
        return fromMap(map, NIL);
    }

    /**
     * Static method to convert a {@link Map} to a {@link List} of
     * {@link StringAttributeType}s.  A prefix may be specified to select
     * and rewrite the {@link Map} keys when creating the
     * {@link StringAttributeType}s.
     *
     * @param   map             The {@link Map}.
     * @param   prefix          The {@link String} prefix to select
     *                          {@link Map} entry keys.
     *
     * @return  A {@link List} of {@link StringAttributeType}s.
     */
    public static List<StringAttributeType> fromMap(Map<?,?> map,
                                                    String prefix) {
        ArrayList<StringAttributeType> list = new ArrayList<>(map.size());

        for (Map.Entry<?,?> entry : map.entrySet()) {
            String key = entry.getKey().toString();

            if (key.startsWith(prefix)) {
                StringAttributeType attribute =
                    new StringAttributeType(key.substring(prefix.length()),
                                            entry.getValue());

                list.add(attribute);
            }
        }

        return list;
    }
}
