/*
 * $Id$
 *
 * Copyright 2008 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.activation.DataSource;

/**
 * Provides common I/O utilities implemented as static methods.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class IOUtil {
    private IOUtil() { }

    /**
     * Method to copy a {@link DataSource} to another {@link DataSource}.
     *
     * @param   from            The {@link DataSource} to copy from.
     * @param   to              The {@link DataSource} to copy to.
     * @param   filters         {@link InputStream} and
     *                          {@link OutputStream} implementation
     *                          {@link Class}es used to "wrap" {@code in}
     *                          and {@code out}, respectively.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(DataSource from, DataSource to,
                            Class<?>... filters) throws IOException {
        try (InputStream in = from.getInputStream()) {
            copy(in, to, filters);
        }
    }

    /**
     * Method to copy an {@link InputStream} to a {@link DataSource}.
     *
     * @param   in              The {@link InputStream}.
     * @param   to              The {@link DataSource} to copy to.
     * @param   filters         {@link InputStream} and
     *                          {@link OutputStream} implementation
     *                          {@link Class}es used to "wrap" {@code in}
     *                          and {@code out}, respectively.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(InputStream in, DataSource to,
                            Class<?>... filters) throws IOException {
        try (OutputStream out = to.getOutputStream()) {
            copy(in, out, filters);
        }
    }

    /**
     * Method to copy a {@link File} to another {@link File}.
     *
     * @param   from            The {@link File} to copy from.
     * @param   to              The {@link File} to copy to.
     * @param   filters         {@link InputStream} and
     *                          {@link OutputStream} implementation
     *                          {@link Class}es used to "wrap" {@code in}
     *                          and {@code out}, respectively.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(File from, File to,
                            Class<?>... filters) throws IOException {
        try (InputStream in = new FileInputStream(from)) {
            copy(in, to, filters);
        }
    }

    /**
     * Method to copy an {@link DataSource} to a {@link File}.
     *
     * @param   from            The {@link DataSource} to copy from.
     * @param   to              The {@link File} to copy to.
     * @param   filters         {@link DataSource} and
     *                          {@link OutputStream} implementation
     *                          {@link Class}es used to "wrap" {@code in}
     *                          and {@code out}, respectively.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(DataSource from, File to,
                            Class<?>... filters) throws IOException {
        try (InputStream in = from.getInputStream()) {
            copy(in, to, filters);
        }
    }

    /**
     * Method to copy an {@link InputStream} to a {@link File}.
     *
     * @param   in              The {@link InputStream}.
     * @param   to              The {@link File} to copy to.
     * @param   filters         {@link InputStream} and
     *                          {@link OutputStream} implementation
     *                          {@link Class}es used to "wrap" {@code in}
     *                          and {@code out}, respectively.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(InputStream in, File to,
                            Class<?>... filters) throws IOException {
        try (OutputStream out = new FileOutputStream(to)) {
            copy(in, out, filters);
        }
    }

    /**
     * Method to copy the contents of an {@link InputStream} to an
     * {@link OutputStream}.
     *
     * @param   in              The {@link InputStream}.
     * @param   out             The {@link OutputStream}.
     * @param   filters         {@link InputStream} and
     *                          {@link OutputStream} implementation
     *                          {@link Class}es used to "wrap" {@code in}
     *                          and {@code out}, respectively.
     *
     * @throws  IOException     If an I/O error occurs.
     *
     * @see #wrap(InputStream,Class...)
     * @see #wrap(OutputStream,Class...)
     */
    public static void copy(InputStream in,
                            OutputStream out,
                            Class<?>... filters) throws IOException {
        for (Class<?> filter : filters) {
            if (InputStream.class.isAssignableFrom(filter)) {
                in = wrap(in, filter.asSubclass(InputStream.class));
            } else /*if (OutputStream.class.isAssignableFrom(filter)) */ {
                out = wrap(out, filter.asSubclass(OutputStream.class));
            }
        }

        copy(Channels.newChannel(in), Channels.newChannel(out));
        out.flush();
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
        copy(in, ByteBuffer.allocate(32 * 1024), out);
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

        if (out instanceof Flushable) {
            ((Flushable) out).flush();
        }
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

        writer.flush();
    }

    /**
     * Method to "wrap" an {@link InputStream} into {@link InputStream}
     * instances.
     *
     * @param   in              The {@link InputStream}.
     * @param   types           The {@link InputStream} implementation
     *                          {@link Class}es.
     *
     * @return  The "wrapped" {@link InputStream}.
     *
     * @throws  IOException     If any of the wrapping streams throw an
     *                          {@link IOException}.
     */
    public static InputStream wrap(InputStream in,
                                   Class<?>... types) throws IOException {
        try {
            for (Class<?> type : types) {
                in =
                    type.asSubclass(InputStream.class)
                    .getConstructor(InputStream.class)
                    .newInstance(in);
            }
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException(exception);
        } catch (InstantiationException exception) {
            throw new IllegalArgumentException(exception);
        } catch (IllegalAccessException exception) {
            throw new IllegalArgumentException(exception);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();

            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new IllegalArgumentException(exception);
            }
        }

        return in;
    }

    /**
     * Method to "wrap" an {@link OutputStream} into {@link OutputStream}
     * instances.
     *
     * @param   out             The {@link OutputStream}.
     * @param   types           The {@link OutputStream} implementation
     *                          {@link Class}es.
     *
     * @return  The "wrapped" {@link OutputStream}.
     *
     * @throws  IOException     If any of the wrapping streams throw an
     *                          {@link IOException}.
     */
    public static OutputStream wrap(OutputStream out,
                                    Class<?>... types) throws IOException {
        try {
            for (Class<?> type : types) {
                out =
                    type.asSubclass(OutputStream.class)
                    .getConstructor(OutputStream.class)
                    .newInstance(out);
            }
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException(exception);
        } catch (InstantiationException exception) {
            throw new IllegalArgumentException(exception);
        } catch (IllegalAccessException exception) {
            throw new IllegalArgumentException(exception);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();

            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new IllegalArgumentException(exception);
            }
        }

        return out;
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
}
