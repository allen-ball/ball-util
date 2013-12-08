/*
 * $Id$
 *
 * Copyright 2009 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import static iprotium.text.FillEnum.LEFT;
import static iprotium.text.FillEnum.RIGHT;

/**
 * {@link TextTable} column model implementation.
 *
 * @see TableModel
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
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
     * Constructor to create a column and specify its {@link FillEnum}-type.
     *
     * @param   name            The name of the column.
     * @param   fill            The {@link FillEnum}-type to apply to column
     *                          values.
     */
    public ColumnModel(String name, FillEnum fill) {
        this(name, fill, null, null);
    }

    /**
     * Constructor to create a right-filled {@link Date} column.
     *
     * @param   name            The name of the column.
     * @param   date            The {@link DateFormat} to apply to column
     *                          {@link Date} values.
     */
    public ColumnModel(String name, DateFormat date) {
        this(name, RIGHT, date, null);
    }

    /**
     * Constructor to create a left-filled {@link Number} column.
     *
     * @param   name            The name of the column.
     * @param   number          The {@link NumberFormat} to apply to column
     *                          {@link Number} values.
     */
    public ColumnModel(String name, NumberFormat number) {
        this(name, LEFT, null, number);
    }

    /**
     * Constructor to fully specify column formatting characteristics.
     *
     * @param   name            The name of the column.
     * @param   fill            The {@link FillEnum} type to apply to column
     *                          values.
     * @param   date            The {@link DateFormat} to apply to column
     *                          {@link Date} values.
     * @param   number          The {@link NumberFormat} to apply to column
     *                          {@link Number} values.
     */
    public ColumnModel(String name, FillEnum fill,
                       DateFormat date, NumberFormat number) {
        this.name = name;
        this.fill = fill;
        this.date = date;
        this.number = number;
    }

    /**
     * @return  The column name.
     */
    public String getName() { return name; }

    /**
     * @return  The {@link FillEnum} type to apply to column values.
     */
    public FillEnum getFillEnum() { return fill; }

    /**
     * @return  The {@link DateFormat} to apply to column {@link Date}
     *          values.
     */
    public DateFormat getDateFormat() { return date; }

    /**
     * @return  The {@link NumberFormat} to apply to column {@link Number}
     *          values.
     */
    public NumberFormat getNumberFormat() { return number; }

    /**
     * Method to fill a {@link String} with spaces.
     *
     * @param   string          The {@link String} to fill.
     * @param   width           The fill-to width.
     *
     * @return  The filled {@link String}.
     *
     * @see #getFillEnum()
     */
    public String fill(String string, int width) {
        return getFillEnum().fill(string, width);
    }

    /**
     * Method to format a column value to a {@link String}.
     *
     * @param   object          The column value ({@link Object}).
     *
     * @return  The formatted {@link String}.
     */
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
            if (object != null) {
                string = object.toString();
            }
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
