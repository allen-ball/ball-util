/*
 * $Id$
 *
 * Copyright 2014 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.beans;

import ball.annotation.Regex;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.beans.Introspector.decapitalize;

/**
 * Bean property method {@link Enum} type.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public enum PropertyMethodEnum {
    GET, IS, SET;

    private static final Map<PropertyMethodEnum,Method> MAP =
        Collections.unmodifiableMap(new MethodPrototypeMap());

    @Regex
    private static final String PROPERTY_REGEX = "([\\p{Upper}][\\p{Alnum}]*)";

    private Pattern pattern = null;

    /**
     * Method to get the prototype return type {@link Class} for this method
     * type.
     *
     * @return  The return type {@link Class}.
     */
    public Class<?> getReturnType() { return MAP.get(this).getReturnType(); }

    /**
     * Method to get the prototype parameter types ({@link Class}es) for
     * this method type.
     *
     * @return  The parameter types array (of {@link Class}es).
     */
    public Class<?>[] getParameterTypes() {
        return MAP.get(this).getParameterTypes();
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

    /**
     * Static method to get a property name from a {@link Method} name.
     * (This method does not check return type or parameter types.)
     *
     * @param   method          The {@link Method}.
     *
     * @return  The property name if method name matches the pattern;
     *          {@code null} if the argument is {@code null} or the name
     *          doesn't match.
     */
    public static String getPropertyName(Method method) {
        String name = null;

        if (method != null) {
            for (PropertyMethodEnum methodEnum : values()) {
                name = methodEnum.getPropertyName(method.getName());

                if (name != null) {
                    break;
                }
            }
        }

        return name;
    }

    private static class MethodPrototypeMap
                         extends EnumMap<PropertyMethodEnum,Method> {
        private static final long serialVersionUID = 6408568606272721794L;

        public MethodPrototypeMap() {
            super(PropertyMethodEnum.class);

            for (Method method : Prototypes.class.getDeclaredMethods()) {
                put(PropertyMethodEnum.valueOf(method.getName()), method);
            }
        }

        public interface Prototypes<T> {
            public T GET();
            public boolean IS();
            public void SET(T value);
        }
    }
}
