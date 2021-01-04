package ball.lang;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2021 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides mapping of Java primitive {@link Class}es to their "wrapper"
 * {@link Class}es.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class PrimitiveTypeMap extends HashMap<Class<?>,Class<?>> {
    private static final long serialVersionUID = 542657344546950531L;

    /**
     * Unmodifiable instance of a {@link PrimitiveTypeMap}.
     */
    public static final Map<Class<?>,Class<?>> INSTANCE =
        Collections.unmodifiableMap(new PrimitiveTypeMap());

    /**
     * Sole constructor.
     */
    public PrimitiveTypeMap() {
        super();

        put(Boolean.TYPE, Boolean.class);
        put(Byte.TYPE, Byte.class);
        put(Character.TYPE, Character.class);
        put(Double.TYPE, Double.class);
        put(Float.TYPE, Float.class);
        put(Integer.TYPE, Integer.class);
        put(Long.TYPE, Long.class);
        put(Short.TYPE, Short.class);
        put(Void.TYPE, Void.class);
    }

    /**
     * Static method to get the "boxed" {@link Class} for a Java primitive
     * {@link Class}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The "boxed" {@link Class} if the argument {@link Class} is a
     *          primitive type; the argument otherwise.
     */
    public static Class<?> asBoxedType(Class<?> type) {
        return (type != null && type.isPrimitive()) ? INSTANCE.get(type) : type;
    }
}
