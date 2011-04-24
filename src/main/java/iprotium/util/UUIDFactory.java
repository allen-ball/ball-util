/*
 * $Id: UUIDFactory.java,v 1.1 2011-04-24 18:34:46 ball Exp $
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.nio.ByteBuffer;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * {@link UUID} {@link Factory} implementation.  Provides interfaces to
 * generate new {@link UUID}s with the {@code uuid_generate(3)} collection
 * of functions.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class UUIDFactory extends Factory<UUID> {
    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle(UUIDFactory.class.getName());

    static { System.loadLibrary(BUNDLE.getString("jnilib").trim()); }

    private static final UUIDFactory DEFAULT = new UUIDFactory();

    /**
     * {@link UUIDFactory} factory method.
     */
    public static UUIDFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    public UUIDFactory() { super(UUID.class); }

    /**
     * Method to generate a new {@link UUID} with the
     * {@code uuid_generate(3)} function.
     *
     * @return  A new unique {@link UUID}.
     */
    public UUID generate() {
        byte[] bytes = new byte[16];

        uuid_generate(bytes);

        return toUUID(bytes);
    }

    private UUID toUUID(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long msb = buffer.getLong();
        long lsb = buffer.getLong();

        return new UUID(msb, lsb);
    }

    private static native void uuid_generate(byte[] bytes);

    /**
     * Method to generate a new {@link UUID} with the
     * {@code uuid_generate_random(3)} function.
     *
     * @return  A new unique {@link UUID}.
     */
    public UUID generateRandom() {
        byte[] bytes = new byte[16];

        uuid_generate_random(bytes);

        return toUUID(bytes);
    }

    private static native void uuid_generate_random(byte[] bytes);

    /**
     * Method to generate a new {@link UUID} with the
     * {@code uuid_generate_time(3)} function.
     *
     * @return  A new unique {@link UUID}.
     */
    public UUID generateTime() {
        byte[] bytes = new byte[16];

        uuid_generate_time(bytes);

        return toUUID(bytes);
    }

    private static native void uuid_generate_time(byte[] bytes);
}
/*
 * $Log: not supported by cvs2svn $
 */
