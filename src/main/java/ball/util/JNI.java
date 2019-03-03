/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import org.fusesource.hawtjni.runtime.JniClass;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.Library;

import static org.fusesource.hawtjni.runtime.FieldFlag.CONSTANT;
import static org.fusesource.hawtjni.runtime.MethodFlag.CONSTANT_INITIALIZER;

/**
 * Platform native interfaces.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@JniClass
public abstract class JNI {
    static { new Library("ball-util", JNI.class).load(); init(); }

    private JNI() { }

    @JniMethod(flags = { CONSTANT_INITIALIZER })
    private static final native void init();

    /** See discussion in Darwin {@link.man stat(2)}. */
    @JniField(flags = { CONSTANT })
    public static int _DARWIN_FEATURE_64_BIT_INODE;

    /**
     * {@link.man uuid_clear(3)}
     * @param   out             {@code uuid_t}
     */
    public static native void uuid_clear(long[] out);
    /**
     * {@link.man uuid_generate(3)}
     * @param   out             {@code uuid_t}
     */
    public static native void uuid_generate(long[] out);
    /**
     * {@link.man uuid_generate_random(3)}
     * @param   out             {@code uuid_t}
     */
    public static native void uuid_generate_random(long[] out);
    /**
     * {@link.man uuid_generate_time(3)}
     * @param   out             {@code uuid_t}
     */
    public static native void uuid_generate_time(long[] out);
}
