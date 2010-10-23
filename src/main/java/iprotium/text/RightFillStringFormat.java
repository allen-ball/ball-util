/*
 * $Id: RightFillStringFormat.java,v 1.4 2010-10-23 22:10:52 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import iprotium.util.StringUtil;

/**
 * "Right fill" {@link StringFormat} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.4 $
 */
public class RightFillStringFormat extends FillStringFormat {
    private static final long serialVersionUID = -6899289721657071954L;

    /**
     * @see FillStringFormat#FillStringFormat(int,char)
     */
    public RightFillStringFormat(int width, char filler) {
        super(width, filler);
    }

    /**
     * @see FillStringFormat#FillStringFormat(int)
     */
    public RightFillStringFormat(int width) { this(width, StringUtil.SPACE); }

    @Override
    protected String fill(CharSequence sequence, int length, char character) {
        return StringUtil.rfill(sequence, length, character);
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
