/*
 * $Id: CharSequenceReader.java,v 1.1 2011-06-10 05:15:02 ball Exp $
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

/**
 * {@link CharSequence} {@link Reader} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class CharSequenceReader extends LineNumberReader {

    /**
     * Sole constructor.
     *
     * @param   sequence        The {@link CharSequence}.
     */
    public CharSequenceReader(CharSequence sequence) {
        super(new ReaderImpl(sequence), 1024);
    }

    private static class ReaderImpl extends Reader {
        private CharSequence sequence = null;
        private volatile int length = 0;
        private volatile int pos = 0;
        private volatile int mark = 0;

        public ReaderImpl(CharSequence sequence) {
            super();

            if (sequence != null) {
                this.sequence = sequence;
                this.length = sequence.length();
            } else {
                throw new NullPointerException("sequence");
            }
        }

        @Override
        public int read() throws IOException {
            int character = -1;

            synchronized (lock) {
                if (pos < length) {
                    character = sequence.charAt(pos++);
                }
            }

            return character;
        }

        @Override
        public int read(char chars[], int off, int len) throws IOException {
            int count = -1;

            synchronized (lock) {
                if (pos < length) {
                    count = Math.min(length - pos, len);

                    for (int i = 0; i < count; i += 1) {
                        chars[off + i] = sequence.charAt(pos + i);
                    }

                    pos += count;
                }
            }

            return count;
        }

        @Override
        public long skip(long count) throws IOException {
            if (count < 0) {
                throw new IllegalArgumentException("skip value is negative");
            }

            synchronized (lock) {
                count = Math.min(count, length - pos);
                pos += count;
            }

            return count;
        }

        @Override
        public boolean ready() throws IOException { return true; }

        @Override
        public boolean markSupported() { return true; }

        @Override
        public void mark(int readAheadLimit) throws IOException {
            if (readAheadLimit < 0){
                throw new IllegalArgumentException("Read-ahead limit < 0");
            }

            synchronized (lock) {
                mark = pos;
            }
        }

        @Override
        public void reset() throws IOException {
            synchronized (lock) {
                pos = mark;
            }
        }

        @Override
        public void close() throws IOException { sequence = null; }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */