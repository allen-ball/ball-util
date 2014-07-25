/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import ball.util.ComparableUtil;
import java.io.File;

/**
 * Abstract {@link Order} base class for ordering {@link File} objects.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
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
        private static final long serialVersionUID = -4915735222147552488L;

        public LastModified() { super(); }

        @Override
        public int compare(File left, File right) {
            return sign(left.lastModified() - right.lastModified());
        }
    }

    private static class Length extends FileOrder {
        private static final long serialVersionUID = 1547911156572989492L;

        public Length() { super(); }

        @Override
        public int compare(File left, File right) {
            return sign(left.length() - right.length());
        }
    }

    private static class Name extends FileOrder {
        private static final long serialVersionUID = -472162600260469422L;

        public Name() { super(); }

        @Override
        public int compare(File left, File right) {
            return ComparableUtil.compare(getName(left), getName(right));
        }

        private String getName(File file) {
            return (file != null) ? file.getName() : null;
        }
    }
}