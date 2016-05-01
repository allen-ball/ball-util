/*
 * $Id$
 *
 * Copyright 2015, 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.jar.Manifest;

/**
 * Utility methods for working with JNI libraries.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class JNILib {
    private JNILib() { }

    /**
     * This arhitecture-specific shared library name prefix.
     */
    public static final String PREFIX;

    /**
     * This arhitecture-specific shared library name suffix.
     */
    public static final String SUFFIX;

    private static final String DOT = ".";

    static {
        String name = "LIBNAME";
        String[] tokens = System.mapLibraryName(name).split(name, 2);
        String prefix = (tokens.length > 0) ? tokens[0] : null;
        String suffix = (tokens.length > 1) ? tokens[1] : null;

        if (suffix != null) {
            while (suffix.startsWith(DOT)) {
                suffix = suffix.substring(DOT.length());
            }
        }

        PREFIX = prefix;
        SUFFIX = suffix;
    }

    /**
     * Method to load the JNI library specified by the {@code jnilib}
     * attribute in the {@link Manifest} associated with the argument
     * {@link Class}.
     *
     * @param   type            The {@link Class}.
     */
    public static void loadFor(Class<?> type) {
        Manifest manifest = ClassUtil.getManifestFor(type);

        if (manifest != null) {
            String jnilib = manifest.getMainAttributes().getValue("jnilib");

            if (jnilib != null) {
                System.loadLibrary(jnilib);
            }
        }
    }
}
