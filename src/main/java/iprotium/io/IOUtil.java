/*
 * $Id: IOUtil.java,v 1.11 2010-10-29 05:00:53 ball Exp $
 *
 * Copyright 2008 - 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Provides common I/O utilities implemented as static methods.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.11 $
 */
public abstract class IOUtil {
    private IOUtil() { }

    /**
     * Method to copy a {@link File} to another {@link File}.
     *
     * @param   from            The {@link File} to copy from.
     * @param   to              The {@link File} to copy to.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(File from, File to) throws IOException {
        InputStream in = null;

        try {
            in = new FileInputStream(from);

            copy(in, to);
        } finally {
            try {
                close(in);
            } finally {
                in = null;
            }
        }
    }

    /**
     * Method to copy an {@link InputStream} to a {@link File}.
     *
     * @param   in              The {@link InputStream}.
     * @param   to              The {@link File} to copy to.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(InputStream in, File to) throws IOException {
        OutputStream out = null;

        try {
            out = new FileOutputStream(to);

            copy(in, out);
        } finally {
            try {
                close(out);
            } finally {
                out = null;
            }
        }
    }

    /**
     * Method to copy the contents of an {@link InputStream} to an
     * {@link OutputStream}.
     *
     * @param   in              The {@link InputStream}.
     * @param   out             The {@link OutputStream}.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(InputStream in,
                            OutputStream out) throws IOException {
        copy(Channels.newChannel(in), Channels.newChannel(out));
        flush(out);
    }

    /**
     * Method to copy the contents of a {@link ReadableByteChannel} to a
     * {@link WritableByteChannel}.
     *
     * @param   in              The {@link ReadableByteChannel}.
     * @param   out             The {@link WritableByteChannel}.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(ReadableByteChannel in,
                            WritableByteChannel out) throws IOException {
        if (in instanceof FileChannel && out instanceof FileChannel) {
            copy((FileChannel) in, (FileChannel) out);
        } else {
            copy(in, ByteBuffer.allocate(32 * 1024), out);
        }
    }

    /**
     * Method to copy the contents of a {@link ReadableByteChannel} to a
     * {@link WritableByteChannel}.
     *
     * @param   in              The {@link ReadableByteChannel}.
     * @param   buffer          The {@link ByteBuffer} to use for the copy.
     * @param   out             The {@link WritableByteChannel}.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(ReadableByteChannel in,
                            ByteBuffer buffer,
                            WritableByteChannel out) throws IOException {
        for (;;) {
            buffer.clear();

            if (in.read(buffer) > 0) {
                buffer.flip();

                int remaining = buffer.remaining();

                if (out.write(buffer) != remaining) {
                    throw new EOFException();
                }
            } else {
                break;
            }
        }
    }

    /**
     * Method to copy the contents of a {@link FileChannel} to another
     * {@link FileChannel}.
     *
     * @param   in              The input {@link FileChannel}.
     * @param   out             The output {@link FileChannel}.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(FileChannel in,
                            FileChannel out) throws IOException {
        long count = in.size();

        if (out.transferFrom(in, 0, count) != count) {
            throw new EOFException();
        }
    }

    /**
     * Method to copy the contents of a {@link Readable} to an
     * {@link Appendable}.
     *
     * @param   in              The input {@link Readable}.
     * @param   out             The output {@link Appendable}.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(Readable in, Appendable out) throws IOException {
        copy(in, ByteBuffer.allocate(32 * 1024).asCharBuffer(), out);
    }

    /**
     * Method to copy the contents of a {@link Readable} to an
     * {@link Appendable}.
     *
     * @param   in              The input {@link Readable}.
     * @param   buffer          The {@link CharBuffer} to use for the copy.
     * @param   out             The output {@link Appendable}.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(Readable in,
                            CharBuffer buffer,
                            Appendable out) throws IOException {
        for (;;) {
            buffer.clear();

            if (in.read(buffer) > 0) {
                buffer.flip();

                out.append(buffer);
            } else {
                break;
            }
        }

        flush(out);
    }

    /**
     * Method to copy the contents of a {@link BufferedReader} to a
     * {@link PrintWriter}.
     *
     * @param   reader          The input {@link BufferedReader}.
     * @param   writer          The output {@link PrintWriter}.
     *
     * @throws  IOException     If an I/O error occurs.
     *
     * @see BufferedReader#readLine()
     * @see PrintWriter#println(String)
     */
    public static void copy(BufferedReader reader,
                            PrintWriter writer) throws IOException {
        for (;;) {
            String line = reader.readLine();

            if (line != null) {
                writer.println(line);

                if (writer.checkError()) {
                    throw new IOException();
                }
            } else {
                break;
            }
        }

        flush(writer);
    }

    /**
     * Static method to delete a {@link File}.  Throws an
     * {@link IOException} if the {@link File} cannot be deleted.
     *
     * @param   file            The file to be deleted.
     *
     * @throws  IOException     If the file cannot be deleted.
     * @throws  NullPointerException
     *                          If file is null.
     */
    public static void delete(File file) throws IOException {
        if (file.exists()) {
            file.delete();

            if (file.exists()) {
                throw new IOException("Cannot delete " + file);
            }
        }
    }

    /**
     * Static method to delete a file and all its containing ancestor
     * directories back to a specified directory if those directories are
     * empty.  Throws an {@link IOException} if the {@link File} cannot be
     * deleted.
     *
     * @param   directory       The containing directory ({@link File}); not
     *                          normally deleted.
     * @param   file            The {@link File} to be deleted.
     *
     * @throws  IOException     If the file cannot be deleted.
     * @throws  NullPointerException
     *                          If file is null.
     */
    public static void delete(File directory, File file) throws IOException {
        delete(file);

        if (directory != null) {
            File parent = file.getParentFile();

            while ((! parent.equals(directory))
                   && (parent.delete() || (! parent.exists()))) {
                parent = parent.getParentFile();
            }
        }
    }

    /**
     * Static method to create directories (and containing ancestor
     * directories) specified by the arguments.
     *
     * @param   files           The directories ({@link File}s) to be
     *                          created.
     *
     * @throws  IOException     If a directory cannot be created for any
     *                          reason.
     */
    public static void mkdirs(File... files) throws IOException {
        for (File file : files) {
            if (file != null) {
                if (! file.exists()) {
                    mkdirs(file.getParentFile());
                    file.mkdir();

                    if (! file.exists()) {
                        throw new IOException("Cannot create directory "
                                              + file);
                    }
                } else {
                    if (! file.isDirectory()) {
                        throw new IOException(file + " is not a directory");
                    }
                }
            }
        }
    }

    /**
     * Static method to "touch" {@link File}s.  Creates the files and any
     * containing ancestor directories if they do not exist.
     *
     * @param   dtcm            The DTCM for the files.
     * @param   files           The {@link File}s to be "touched."
     *
     * @throws  IOException     If a file cannot be created.
     * @throws  NullPointerException
     *                          If any argument is null.
     */
    public static void touch(long dtcm, File... files) throws IOException {
        for (File file : files) {
            mkdirs(file.getParentFile());
            file.createNewFile();
            file.setLastModified(dtcm);
        }
    }

    /**
     * Static method to "touch" {@link File}s.  Creates the files and any
     * containing ancestor directories if they do not exist.
     *
     * @param   files           The {@link File}s to be "touched."
     *
     * @throws  IOException     If a file cannot be created.
     * @throws  NullPointerException
     *                          If any argument is null.
     */
    public static void touch(File... files) throws IOException {
        touch(System.currentTimeMillis(), files);
    }

    /**
     * Method to quietly close an {@link Object} if it is an instance of
     * {@link Closeable}.
     *
     * @param   object          The {@link Object} to close if it is an
     *                          instance of {@link Closeable}.
     */
    public static void close(Object object) {
        try {
            if (object instanceof Closeable) {
                ((Closeable) object).close();
            }
        } catch (IOException exception) {
        }
    }

    /**
     * Method to quietly flush an {@link Object} if it is an instance of
     * {@link Flushable}.
     *
     * @param   object          The {@link Object} to flush if it is an
     *                          instance of {@link Flushable}.
     */
    public static void flush(Object object) {
        try {
            if (object instanceof Flushable) {
                ((Flushable) object).flush();
            }
        } catch (IOException exception) {
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.10  2010/10/18 05:12:51  ball
 * Added copy(Readable,Appendable) method.
 * Changed close(Closeable) method to close(Object) and added
 * flush(Object) method.
 * In copy() methods, flush output after completing the copy.
 *
 * Revision 1.9  2010/08/21 04:04:01  ball
 * In mkdirs(File...), use File.exists() to verify each directory has
 * been created.
 *
 */
