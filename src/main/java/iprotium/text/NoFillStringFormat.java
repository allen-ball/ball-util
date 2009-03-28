/*
 * $Id: NoFillStringFormat.java,v 1.1 2009-03-28 17:17:36 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

/**
 * "No fill" StringFormat implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class NoFillStringFormat extends FillStringFormat {
    private static final long serialVersionUID = -7671688907949397739L;

    /**
     * No-argument constructor.
     */
    public NoFillStringFormat() { super(0, SPACE); }

    @Override
    protected String fill(String string, int width, char filler) {
        return string;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
