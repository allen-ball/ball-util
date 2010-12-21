/*
 * $Id: EditLine.java,v 1.2 2010-12-21 15:42:00 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.jni;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Provides wrappers to the native EditLine (libedit) functions.  See the
 * editline(3) manual page.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class EditLine {
    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle(EditLine.class.getName());

    static { System.loadLibrary(BUNDLE.getString("jnilib").trim()); }

    /**
     * {@code EditLine *} holder used by native methods.
     */
    private volatile long pointer = 0;

    /**
     * See the el_init(3) manual page.
     *
     * @param   prog            The name of the invoking command.
     */
    public EditLine(String prog) { init(prog); }
    private native void init(String prog);

    /**
     * See the el_reset(3) manual page.
     */
    public native void reset();

    /**
     * See the el_gets(3) manual page.
     */
    public native String gets();

    /**
     * See the el_getc(3) manual page.
     */
    public native int getc();

    /**
     * See the el_push(3) manual page.
     *
     * @param   string          The argument {@link String}.
     */
    public native void push(String string);

    /**
     * See the el_parse(3) manual page.
     *
     * @param   argv            The argument {@code argv}.
     */
    public native int parse(String[] argv);

    /**
     * See the el_source(3) manual page.
     *
     * @param   file            The {@link File} to source.
     */
    public int source(File file) { return source(file.getAbsolutePath()); }
    private native int source(String path);

    @Override
    protected void finalize() { end(); }
    private native void end();

    /**
     * See the tok_str(3) manual page.
     *
     * @param   string          The {@link String} to tokenize.
     */
    public static native String[] tokenize(String string);
}
/*
 * $Log: not supported by cvs2svn $
 */
