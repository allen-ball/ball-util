/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import ball.util.ComparableUtil;

/**
 * {@link Order} implementation for ordering {@link Package} objects.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class PackageOrder extends Order<Package> {
    private static final long serialVersionUID = 2513191834173809640L;

    /**
     * Public static instance of {@link PackageOrder}.
     */
    public static final PackageOrder INSTANCE = new PackageOrder();

    /**
     * Sole constructor.
     */
    public PackageOrder() { super(); }

    @Override
    public int compare(Package left, Package right) {
        return ComparableUtil.compare(getName(left), getName(right));
    }

    private String getName(Package pkg) {
        return (pkg != null) ? pkg.getName() : null;
    }
}