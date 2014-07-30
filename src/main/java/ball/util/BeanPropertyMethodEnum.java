/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.beans.Introspector.decapitalize;

/**
 * Bean property method {@link Enum} type.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public enum BeanPropertyMethodEnum {
    GET, IS, SET;

    private static final SortedMap<String,Method> MAP =
        Collections.unmodifiableSortedMap(new MethodTemplateMap());

    @Regex
    private static final String PROPERTY_REGEX = "([\\p{Upper}][\\p{Alnum}]+)";

    private Pattern pattern = null;

    /**
     * Method to get the prototype return type {@link Class} for this method
     * type.
     *
     * @return  The return type {@link Class}.
     */
    public Class<?> getReturnType() { return MAP.get(name()).getReturnType(); }

    /**
     * Method to get the prototype parameter types ({@link Class}es) for
     * this method type.
     *
     * @return  The parameter types array (of {@link Class}es).
     */
    public Class<?>[] getParameterTypes() {
        return MAP.get(name()).getParameterTypes();
    }

    /**
     * Method to get the property name from the argument method name.
     *
     * @param   method          The candidate getter/setter method name.
     *
     * @return  The property name if method name matches the pattern;
     *          {@code null} if the argument is {@code null} or doesn't
     *          match.
     */
    public String getPropertyName(String method) {
        String name = null;

        if (method != null) {
            Matcher matcher = pattern().matcher(method);

            if (matcher.matches()) {
                name = decapitalize(matcher.group(1));
            }
        }

        return name;
    }

    private Pattern pattern() {
        if (pattern == null) {
            pattern =
                Pattern.compile(Pattern.quote(name().toLowerCase())
                                + PROPERTY_REGEX);
        }

        return pattern;
    }

    private static class MethodTemplateMap extends TreeMap<String,Method> {
        private static final long serialVersionUID = -615108090828566640L;

        public MethodTemplateMap() {
            super(String.CASE_INSENSITIVE_ORDER);

            for (Method method : Templates.class.getDeclaredMethods()) {
                put(method.getName(), method);
            }
        }

        public interface Templates<T> {
            public T GET();
            public void SET(T value);
            public boolean IS();
        }
    }
}