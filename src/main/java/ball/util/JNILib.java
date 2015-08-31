/*
 * $Id$
 *
 * Copyright 2015 Allen D. Ball.  All rights reserved.
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
