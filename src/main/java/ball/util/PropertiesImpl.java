/*
 * $Id$
 *
 * Copyright 2010 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * {@link Properties} implementation that overrides
 * {@link #load(InputStream)} and {@link #store(OutputStream,String)}
 * methods to specify the {@link Charset} (as {@code UTF-8}).
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class PropertiesImpl extends Properties {
    private static final long serialVersionUID = -5034894719756448226L;

    /**
     * UTF-8
     */
    protected static final Charset CHARSET = UTF_8;

    /**
     * See {@link Properties#Properties()}.
     */
    public PropertiesImpl() { this(null); }

    /**
     * See {@link Properties#Properties(Properties)}.
     *
     * @param   defaults        A {@link Properties} that contains default
     *                          values for any keys not found in this
     *                          {@link Properties}.
     */
    public PropertiesImpl(Properties defaults) { super(defaults); }

    /**
     * See {@link #PropertiesImpl(Properties)}.
     *
     * @param   defaults        The default {@link Properties}.
     * @param   file            The {@link File} to load
     *                          (may be {@code null}).
     *
     * @throws  IOException     If {@code file} is not null and cannot be
     *                          read.
     */
    public PropertiesImpl(Properties defaults, File file) throws IOException {
        this(defaults);

        if (file != null) {
            try (InputStream in = new FileInputStream(file)) {
                load(in);
            }
        }
    }

    /**
     * See {@link #PropertiesImpl(Properties)}.
     *
     * @param   defaults        The default {@link Properties}.
     * @param   resource        The name of the {@code resource} to load
     *                          (may be {@code null}).
     *
     * @throws  IOException     If {@code resource} is not null and cannot be
     *                          read.
     */
    public PropertiesImpl(Properties defaults,
                          String resource) throws IOException {
        this(defaults);

        if (resource != null) {
            try (InputStream in = getClass().getResourceAsStream(resource)) {
                load(in);
            }
        }
    }

    /**
     * Method to configure an {@link Object} properties with values in
     * {@code this} {@link PropertiesImpl}.  (An {@link Object} "setter"
     * does not have to return {@code void} to be invoked.)
     *
     * @param   object          The {@link Object} to configure.
     *
     * @return  The argument {@link Object}.
     */
    public Object configure(Object object) { return configure(this, object); }

    @Override
    public void load(InputStream in) throws IOException { load(this, in); }

    @Override
    public void store(OutputStream out, String comment) throws IOException {
        store(this, out, comment);
    }

    protected static void load(Properties properties,
                               InputStream in) throws IOException {
        properties.load(new InputStreamReader(in, CHARSET));
    }

    protected static void store(Properties properties,
                                OutputStream out,
                                String comment) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out, CHARSET);

        properties.store(writer, comment);
        writer.flush();
    }

    /**
     * See {@link #configure(Object)}.
     *
     * @param   properties      The {@link Properties} with the
     *                          configuration parameters..
     * @param   object          The {@link Object} to configure.
     *
     * @return  The argument {@link Object}.
     */
    public static Object configure(Properties properties, Object object) {
        for (String key : properties.stringPropertyNames()) {
            Object value = properties.get(key);
            Method method =
                getSetMethod(object,
                             key, (value != null) ? value.getClass() : null);

            if (method != null) {
                value =
                    Converter.convertTo(value, method.getParameterTypes()[0]);

                try {
                    method.invoke(object, value);
                } catch (Exception exception) {
                }
            }
        }

        return object;
    }

    private static Method getSetMethod(Object object,
                                       String property, Class<?> parameter) {
        Method method = null;
        String name =
            "set"
            + property.substring(0, 1).toUpperCase()
            + property.substring(1);

        if (method == null) {
            if (parameter != null) {
                try {
                    method = object.getClass().getMethod(name, parameter);
                } catch (Exception exception) {
                }
            }
        }

        if (method == null) {
            for (Method m : object.getClass().getMethods()) {
                if (m.getName().equals(name)
                    && (! m.isVarArgs())
                    && m.getParameterTypes().length == 1) {
                    method = m;
                    break;
                }
            }
        }

        return method;
    }
}
