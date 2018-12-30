/*
 * $Id$
 *
 * Copyright 2014 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.io;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Byte order mark to {@link Charset} {@link Map} implementation.
 *
 * {@include #INSTANCE}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
        StringBuilder buffer = new StringBuilder();

        buffer.append("{");

        Iterator<Map.Entry<byte[],Charset>> iterator = entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<byte[],Charset> entry = iterator.next();

            buffer
                .append(toString(entry.getKey()))
                .append("=")
                .append(toString(entry.getValue()));

            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }

        buffer.append("}");

        return buffer.toString();
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
