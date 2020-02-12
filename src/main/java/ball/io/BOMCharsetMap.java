package ball.io;
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
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

/**
 * Byte order mark to {@link Charset} {@link Map} implementation.
 *
 * {@include #INSTANCE}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class BOMCharsetMap extends LinkedHashMap<byte[],Charset> {
    private static final long serialVersionUID = -4968264081542792700L;

    /**
     * Unmodifiable instance of {@link BOMCharsetMap}.
     */
    public static final Map<byte[],Charset> INSTANCE =
        Collections.unmodifiableMap(new BOMCharsetMap());

    /**
     * Sole constructor.
     */
    protected BOMCharsetMap() {
        super();

        put(bytes(0x00, 0x00, 0xFE, 0xFF), Charset.forName("UTF-32BE"));
        put(bytes(0xFF, 0xFE, 0x00, 0x00), Charset.forName("UTF-32LE"));

        put(bytes(0xEF, 0xBB, 0xBF), UTF_8);

        put(bytes(0xFE, 0xFF), UTF_16BE);
        put(bytes(0xFF, 0xFE), UTF_16LE);
    }

    private static byte[] bytes(int... elements) {
        byte[] bytes = new byte[elements.length];

        for (int i = 0; i < bytes.length; i += 1) {
            bytes[i] = (byte) (elements[i] & 0xFF);
        }

        return bytes;
    }

    @Override
    public String toString() {
        String string =
            entrySet().stream()
            .map(t -> toString(t.getKey()) + "=" + toString(t.getValue()))
            .collect(joining(", ", "{", "}"));

        return string;
    }

    private String toString(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();

        buffer.append("[");

        for (int i = 0; i < bytes.length; i += 1) {
            if (i > 0) {
                buffer.append(", ");
            }

            buffer.append(String.format("0x%02X", bytes[i]));
        }

        buffer.append("]");

        return buffer.toString();
    }

    private String toString(Charset charset) {
        return (charset != null) ? charset.name() : null;
    }
}
