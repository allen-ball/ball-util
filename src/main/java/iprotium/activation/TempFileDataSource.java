/*
 * $Id: TempFileDataSource.java,v 1.1 2010-09-13 04:27:47 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.activation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * {@link javax.activation.DataSource} backed by a temporary {@link File}
 * and based on {@link FileInputStream} and {@link FileInputStream}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class TempFileDataSource extends AbstractDataSource {
    private final String prefix;
    private final String suffix;
    private final File parent;
    private File file = null;

    /**
     * @param   type            Initial {@code "ContentType"} attribute
     *                          value.
     *
     * @see File#createTempFile(String,String,File)
     */
    public TempFileDataSource(String prefix, String suffix, File parent,
                              String type) {
        super();

        if (prefix != null) {
            this.prefix = prefix;
        } else {
            this.prefix = TempFileDataSource.class.getSimpleName();
        }

        this.suffix = suffix;
        this.parent = parent;

        if (type != null) {
            setContentType(type);
        }
    }

    /**
     * @param   type            Initial {@code "ContentType"} attribute
     *                          value.
     */
    public TempFileDataSource(String type) { this(null, null, null, type); }

    {
        try {
            reset();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * Method to reset the temporary {@link File} and discard any input on
     * any open {@link #getOutputStream()}.
     */
    public void reset() {
        try {
            getOutputStream().close();
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    @Override
    public FileInputStream getInputStream() throws IOException {
        FileInputStream in = null;

        synchronized (this) {
            in = new FileInputStream(file);
        }

        return in;
    }

    @Override
    public FileOutputStream getOutputStream() throws IOException {
        FileOutputStream out = null;

        synchronized (this) {
            if (file != null) {
                file.delete();
            }

            file = File.createTempFile(prefix, suffix, parent);

            out = new FileOutputStream(file);
        }

        return out;
    }

    @Override
    protected void finalize() throws Throwable {
        if (file != null) {
            file.delete();
        }

        super.finalize();
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
