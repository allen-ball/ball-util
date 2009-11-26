/*
 * $Id: FileOrder.java,v 1.1 2009-11-21 21:35:47 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.io.File;

/**
 * Abstract {@link Order} base class for ordering {@link File} objects.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public abstract class FileOrder extends Order<File> {

    /**
     * {@link File#lastModified()} {@link Order}.
     */
    public static final FileOrder LAST_MODIFIED = new LastModified();

    /**
     * {@link File#length()} {@link Order}.
     */
    public static final FileOrder LENGTH = new Length();

    /**
     * {@link File#getName()} {@link Order}.
     */
    public static final FileOrder NAME = new Name();

    /**
     * Sole constructor.
     */
    protected FileOrder() { super(); }

    private static int sign(long number) {
        return ((number < 0) ? -1 : ((number > 0) ? +1 : 0));
    }

    private static class LastModified extends FileOrder {
        private static final long serialVersionUID = -5216377443815426533L;

        public LastModified() { super(); }

        @Override
        public int compare(File left, File right) {
            return sign(left.lastModified() - right.lastModified());
        }
    }

    private static class Length extends FileOrder {
        private static final long serialVersionUID = -1120368648966263536L;

        public Length() { super(); }

        @Override
        public int compare(File left, File right) {
            return sign(left.length() - right.length());
        }
    }

    private static class Name extends FileOrder {
        private static final long serialVersionUID = -6737592480295485315L;

        public Name() { super(); }

        @Override
        public int compare(File left, File right) {
            return left.getName().compareTo(right.getName());
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */