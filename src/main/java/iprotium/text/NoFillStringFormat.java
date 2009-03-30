/*
 * $Id: NoFillStringFormat.java,v 1.2 2009-03-30 06:25:12 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

/**
 * "No fill" StringFormat implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class NoFillStringFormat extends FillStringFormat {
    private static final long serialVersionUID = 4776469457570314700L;

    /**
     * No-argument constructor.
     */
    public NoFillStringFormat() { super(0, SPACE); }

    @Override
    protected String fill(int width, char filler, String string) {
        return string;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
