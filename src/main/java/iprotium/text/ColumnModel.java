/*
 * $Id: ColumnModel.java,v 1.2 2009-06-21 00:12:57 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import static iprotium.text.FillEnum.LEFT;
import static iprotium.text.FillEnum.RIGHT;

/**
 * Table column model implementation.
 *
 * @see TableModel
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class ColumnModel {
    private final String name;
    private final FillEnum fill;
    private final DateFormat date;
    private final NumberFormat number;

    /**
     * Constructor to create a column with no formatting characteristics.
     *
     * @param   name            The name of the column.
     */
    public ColumnModel(String name) { this(name, RIGHT); }

    /**
     * Constructor to create a column and specify its fill-type.
     *
     * @param   name            The name of the column.
     * @param   fill            The FillEnum type to apply to column values.
     */
    public ColumnModel(String name, FillEnum fill) {
        this(name, fill, null, null);
    }

    /**
     * Constructor to create a right-filled "Date" column.
     *
     * @param   name            The name of the column.
     * @param   date            The DateFormat to apply to column Date
     *                          values.
     */
    public ColumnModel(String name, DateFormat date) {
        this(name, RIGHT, date, null);
    }

    /**
     * Constructor to create a left-filled "Number" column.
     *
     * @param   name            The name of the column.
     * @param   number          The NumberFormat to apply to column Number
     *                          values.
     */
    public ColumnModel(String name, NumberFormat number) {
        this(name, LEFT, null, number);
    }

    /**
     * Constructor to fully specify column formatting characteristics.
     *
     * @param   name            The name of the column.
     * @param   fill            The FillEnum type to apply to column values.
     * @param   date            The DateFormat to apply to column Date
     *                          values.
     * @param   number          The NumberFormat to apply to column Number
     *                          values.
     */
    public ColumnModel(String name, FillEnum fill,
                       DateFormat date, NumberFormat number) {
        this.name = name;
        this.fill = fill;
        this.date = date;
        this.number = number;
    }

    public String getName() { return name; }

    public FillEnum getFillEnum() { return fill; }

    public DateFormat getDateFormat() { return date; }

    public NumberFormat getNumberFormat() { return number; }

    public String fill(int width, String string) {
        return getFillEnum().fill(width, string);
    }

    public String format(Object object) {
        String string = null;

        if (string == null) {
            if (object instanceof Date) {
                string = format(getDateFormat(), (Date) object);
            }
        }

        if (string == null) {
            if (object instanceof Number) {
                string = format(getNumberFormat(), (Number) object);
            }
        }

        if (string == null) {
            if (object instanceof Number) {
                string = format(getDateFormat(), (Number) object);
            }
        }

        if (string == null) {
            string = (object != null) ? object.toString() : null;
        }

        return string;
    }

    private String format(DateFormat format, Date date) {
        String string = null;

        if (format != null) {
            synchronized (format) {
                string = format.format(date);
            }
        }

        return string;
    }

    private String format(DateFormat format, Number number) {
        String string = null;

        if (format != null) {
            synchronized (format) {
                string = format.format(number);
            }
        }

        return string;
    }

    private String format(NumberFormat format, Number number) {
        String string = null;

        if (format != null) {
            synchronized (format) {
                string = format.format(number);
            }
        }

        return string;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
