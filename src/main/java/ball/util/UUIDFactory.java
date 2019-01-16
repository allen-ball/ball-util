/*
 * $Id$
 *
 * Copyright 2011 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.nio.LongBuffer;
import java.util.UUID;

/**
 * {@link UUID} {@link Factory} implementation.  Provides interfaces to
 * generate new {@link UUID}s with the {@link.man uuid_generate(3)}
 * collection of functions.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class UUIDFactory extends Factory<UUID> {
    private static final long serialVersionUID = 2636388251268998745L;

    private static final UUIDFactory DEFAULT = new UUIDFactory();

    /**
     * {@link UUIDFactory} factory method.
     *
     * @return  The default {@link UUIDFactory}.
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
     * {@link UUID#randomUUID()} function.
     *
     * @return  A new unique {@link UUID}.
     */
    public UUID generate() { return UUID.randomUUID(); }

    /**
     * Method to generate a {@code null} {@link UUID} with the
     * {@link.man uuid_clear(3)} function.
     *
     * @return  A new unique {@link UUID}.
     */
    public UUID generateNull() {
        long[] out = new long[2];

        JNI.uuid_clear(out);

        return toUUID(out);
    }

    /**
     * Method to generate a new {@link UUID} with the
     * {@link JNI#uuid_generate_random(long[])} function.
     *
     * @return  A new unique {@link UUID}.
     */
    public UUID generateRandom() {
        long[] out = new long[2];

        JNI.uuid_generate_random(out);

        return toUUID(out);
    }

    /**
     * Method to generate a new {@link UUID} with the
     * {@link JNI#uuid_generate_time(long[])} function.
     *
     * @return  A new unique {@link UUID}.
     */
    public UUID generateTime() {
        long[] out = new long[2];

        JNI.uuid_generate_time(out);

        return toUUID(out);
    }

    private UUID toUUID(long[] out) {
        LongBuffer buffer = LongBuffer.wrap(out);
        long msb = buffer.get();
        long lsb = buffer.get();

        return new UUID(msb, lsb);
    }
}
