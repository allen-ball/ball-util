/*
 * $Id: Report.java,v 1.1 2009-03-30 06:28:12 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import iprotium.activation.ReaderWriterDataSource;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import static java.lang.Character.isWhitespace;

/**
 * Text Report base class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class Report extends ReaderWriterDataSource implements Flushable {
    private final PrintWriter writer;

    /**
     * Sole constructor.
     */
    public Report() {
        super(null, "text/plain");

        try {
            writer = getPrintWriter();
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * @see PrintWriter#println()
     */
    public void println() { writer.println(); }

    /**
     * @see PrintWriter#println(boolean)
     */
    public void println(boolean arg) { writer.println(arg); }

    /**
     * @see PrintWriter#println(char)
     */
    public void println(char arg) { writer.println(arg); }

    /**
     * @see PrintWriter#println(int)
     */
    public void println(int arg) { writer.println(arg); }

    /**
     * @see PrintWriter#println(long)
     */
    public void println(long arg) { writer.println(arg); }

    /**
     * @see PrintWriter#println(float)
     */
    public void println(float arg) { writer.println(arg); }

    /**
     * @see PrintWriter#println(double)
     */
    public void println(double arg) { writer.println(arg); }

    /**
     * @see PrintWriter#println(char[])
     */
    public void println(char arg[]) { writer.println(arg); }

    /**
     * @see PrintWriter#println(String)
     */
    public void println(String arg) { writer.println(arg); }

    /**
     * @see PrintWriter#println(Object)
     */
    public void println(Object arg) { writer.println(arg); }

    /**
     * @see PrintWriter#format(String,Object...)
     */
    public PrintWriter format(String format, Object... args) {
        return writer.format(format, args);
    }

    /**
     * @see PrintWriter#format(Locale,String,Object...)
     */
    public PrintWriter format(Locale locale, String format, Object... args) {
        return writer.format(locale, format, args);
    }

    /**
     * @see PrintWriter#flush()
     */
    public void flush() { writer.flush(); }

    /**
     * Protected method for subclass implementations to print a
     * StringBuilder.  The argument StringBuilder is "right trimmed" before
     * printing.
     *
     * @param   buffer          The StringBuilder to print.
     *
     * @see #rtrim(StringBuilder)
     */
    protected void println(StringBuilder buffer) {
        writer.println(rtrim(buffer));
    }

    /**
     * Method to right trim a StringBuilder.
     *
     * @param   buffer          The StringBuilder to trim.
     *
     * @return  The argument StringBuilder.
     */
    protected StringBuilder rtrim(StringBuilder buffer) {
        if (buffer != null) {
            while (buffer.length() > 0) {
                if (isWhitespace(buffer.charAt(buffer.length() - 1))) {
                    buffer.setLength(buffer.length() - 1);
                } else {
                    break;
                }
            }
        }

        return buffer;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
