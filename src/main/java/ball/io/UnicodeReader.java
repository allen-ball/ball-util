/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

/**
 * {@link java.io.BufferedReader} implementation which analyzes the
 * underlying {@link InputStream} for byte order marks and selects the
 * appropriate {@link Charset}.
 *
 * @see BOMCharsetMap
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class UnicodeReader extends LineNumberReader {
    private static final Charset DEFAULT = Charset.forName("UTF-8");

    /**
     * @param   file            The {@link File} to open.
     */
    public UnicodeReader(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    /**
     * @param   in              The underlying {@link InputStream}.
     */
    public UnicodeReader(InputStream in) {
        this(in instanceof CharsetDetectInputStream
                 ? ((CharsetDetectInputStream) in)
                 : new CharsetDetectInputStream(in, DEFAULT));
    }

    private UnicodeReader(CharsetDetectInputStream in) {
        super(new InputStreamReader(in, in.getCharset()));
    }

    @Override
    public String toString() { return super.toString(); }

    private static class CharsetDetectInputStream extends PushbackInputStream {
        private final Charset charset;

        public CharsetDetectInputStream(InputStream in, Charset charset) {
            super(in, 8);

            try {
                for (Map.Entry<byte[],Charset> entry :
                         BOMCharsetMap.INSTANCE.entrySet()) {
                    byte[] bytes = new byte[entry.getKey().length];
                    int length = read(bytes);

                    if (length < 0) {
                        break;
                    }

                    if (bytes.length == length
                        && Arrays.equals(bytes, entry.getKey())) {
                        charset = entry.getValue();
                        break;
                    } else {
                        if (length > 0) {
                            unread(bytes, 0, length);
                        }
                    }
                }

                if (charset != null) {
                    this.charset = charset;
                } else {
                    throw new NullPointerException("charset");
                }
            } catch (Exception exception) {
                throw new ExceptionInInitializerError(exception);
            }
        }

        public Charset getCharset() { return charset; }

        @Override
        public String toString() { return super.toString(); }
    }
}