/*
 * $Id: IOUtil.java,v 1.2 2009-01-27 22:00:19 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Common I/O utilities implemented as static methods.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class IOUtil {
    private IOUtil() { }

    /**
     * Method to copy a File to another File.
     *
     * @param   from            The File to copy from.
     * @param   to              The File to copy to.
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
     * Method to copy af InputStream to File.
     *
     * @param   in              The InputStream.
     * @param   to              The File to copy to.
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
     * Method to copy the contents of an InputStream to an OutputStream.
     *
     * @param   in              The InputStream.
     * @param   out             The OutputStream.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public static void copy(InputStream in,
                            OutputStream out) throws IOException {
        copy(Channels.newChannel(in), Channels.newChannel(out));
    }

    /**
     * Method to copy the contents of a ReadableByteChannel to a
     * WritableByteChannel.
     *
     * @param   in              The ReadableByteChannel.
     * @param   out             The WritableByteChannel.
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

    private static void copy(ReadableByteChannel in,
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
     * Method to copy the contents of a FileChannel to another FileChannel.
     *
     * @param   in              The input FileChannel.
     * @param   out             The output FileChannel.
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
     * Static method to delete a File.  Throws an IOException if the File
     * cannot be deleted.
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
     * Static method to delete a file and all its containing directories
     * back to a specified directory if those directories are empty.  Throws
     * an IOException if the File cannot be deleted.
     *
     * @param   directory       The containing directory (not normally
     *                          deleted).
     * @param   file            The file to be deleted.
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
     * Static method to create directory (and containing parent directories)
     * specified by the argument.
     *
     * @param   directory       The directory to be created.
     *
     * @throws  IOException     If the directory cannot be created for any
     *                          reason.
     */
    public static void mkdirs(File directory) throws IOException {
        if (directory != null) {
            if (! directory.exists()) {
                mkdirs(directory.getParentFile());
                directory.mkdir();

                if (! directory.exists()) {
                    throw new IOException("Cannot create directory "
                                          + directory);
                }
            } else {
                if (! directory.isDirectory()) {
                    throw new IOException(directory + " is not a directory");
                }
            }
        }
    }

    /**
     * Static method to "touch" a File.  Creates the file and any containing
     * directories if they do not exist.
     *
     * @param   file            The file to be "touched."
     *
     * @throws  IOException     If the file cannot be created.
     * @throws  NullPointerException
     *                          If file is null.
     */
    public static void touch(File file) throws IOException {
        touch(file, System.currentTimeMillis());
    }

    /**
     * Static method to "touch" a File.  Creates the file and any containing
     * directories if they do not exist.
     *
     * @param   file            The file to be "touched."
     * @param   dtcm            The DTCM for the file.
     *
     * @throws  IOException     If the file cannot be created.
     * @throws  NullPointerException
     *                          If file is null.
     */
    public static void touch(File file, long dtcm) throws IOException {
        mkdirs(file.getParentFile());
        file.createNewFile();
        file.setLastModified(dtcm);
    }

    /**
     * Method to quietly close a Closeable.
     *
     * @param   closeable       The Closeable to be closed.
     */
    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException exception) {
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/10/27 05:47:15  ball
 * Initial writing.
 *
 */
