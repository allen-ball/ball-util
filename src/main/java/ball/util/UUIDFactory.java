/*
 * $Id$
 *
 * Copyright 2011 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.nio.ByteBuffer;
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
     * {@link.man uuid_generate(3)} function.
     *
     * @return  A new unique {@link UUID}.
     */
    public UUID generate() {
        byte[] bytes = new byte[16];

        JNI.uuid_generate(bytes);

        return toUUID(bytes);
    }

    private UUID toUUID(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long msb = buffer.getLong();
        long lsb = buffer.getLong();

        return new UUID(msb, lsb);
    }

    /**
     * Method to generate a new {@link UUID} with the
     * {@link.man uuid_generate_random(3)} function.
     *
     * @return  A new unique {@link UUID}.
     */
    public UUID generateRandom() {
        byte[] bytes = new byte[16];

        JNI.uuid_generate_random(bytes);

        return toUUID(bytes);
    }

    /**
     * Method to generate a new {@link UUID} with the
     * {@link.man uuid_generate_time(3)} function.
     *
     * @return  A new unique {@link UUID}.
     */
    public UUID generateTime() {
        byte[] bytes = new byte[16];

        JNI.uuid_generate_time(bytes);

        return toUUID(bytes);
    }
}
