/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import iprotium.util.ComparableUtil;

/**
 * {@link Order} implementation for ordering {@link Package} objects.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class PackageOrder extends Order<Package> {
    private static final long serialVersionUID = 4673155343065186825L;

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
