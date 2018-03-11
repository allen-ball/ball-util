/*
 * $Id$
 *
 * Copyright 2018 Allen D. Ball.  All rights reserved.
 */
package ball.tools.maven;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

/**
 * Abstract base class for {@link EmbeddedMaven} to load Plexus Core.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class EmbeddedPlexusCore {
    private static final String PLEXUS_CORE = "plexus.core";

    private final ClassLoader loader =
        Thread.currentThread().getContextClassLoader();
    private final ClassWorld world = new ClassWorld(PLEXUS_CORE, loader);
    private final ClassRealm realm;

    /**
     * Sole constructor.
     */
    protected EmbeddedPlexusCore() {
        ClassRealm realm = world.getClassRealm(PLEXUS_CORE);

        if (realm == null) {
            realm = world.getRealms().iterator().next();
        }

        this.realm = realm;
    }

    /**
     * Method to get the {@link ClassWorld} for this embedded instance.
     *
     * @return  The {@link ClassWorld}.
     */
    public ClassWorld getClassWorld() { return world; }

    /**
     * Method to get the {@link ClassRealm} for this embedded instance.
     *
     * @return  The {@link ClassRealm}.
     */
    public ClassRealm getClassRealm() { return realm; }

    @Override
    public String toString() { return super.toString(); }
}
