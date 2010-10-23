/*
 * $Id: NoFillStringFormat.java,v 1.4 2010-10-23 22:10:52 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import iprotium.util.StringUtil;

/**
 * "No fill" {@link StringFormat} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.4 $
 */
public class NoFillStringFormat extends FillStringFormat {
    private static final long serialVersionUID = -7219855768510535981L;

    /**
     * No-argument constructor.
     */
    public NoFillStringFormat() { super(0, StringUtil.SPACE); }

    @Override
    protected String fill(CharSequence sequence, int length, char character) {
        return (new StringBuilder((sequence != null)
                                      ? sequence
                                      : StringUtil.NIL)
                .toString());
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
