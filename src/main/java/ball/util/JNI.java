/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import org.fusesource.hawtjni.runtime.JniClass;
import org.fusesource.hawtjni.runtime.Library;

/**
 * Platform interfaces.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@JniClass
public abstract class JNI {
    static { new Library("ball-util", JNI.class).load(); }

    private JNI() { }

    /**
     * {@link.man uuid_generate(3)}
     * @param   out             {@code uuid_t}
     */
    public static native void uuid_generate(byte[] out);
    /**
     * {@link.man uuid_generate_random(3)}
     * @param   out             {@code uuid_t}
     */
    public static native void uuid_generate_random(byte[] out);
    /**
     * {@link.man uuid_generate_time(3)}
     * @param   out             {@code uuid_t}
     */
    public static native void uuid_generate_time(byte[] out);
}
