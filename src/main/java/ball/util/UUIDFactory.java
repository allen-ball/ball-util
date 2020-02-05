package ball.util;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
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
import java.nio.LongBuffer;
import java.util.UUID;

/**
 * {@link UUID} {@link Factory} implementation.  Provides interfaces to
 * generate new {@link UUID}s with the {@link.man uuid_generate(3)}
 * collection of functions.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
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
