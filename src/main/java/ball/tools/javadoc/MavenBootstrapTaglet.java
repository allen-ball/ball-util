/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import com.sun.tools.doclets.Taglet;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * {@link AbstractTaglet} whose {@link #register(Map)} method loads all
 * {@link Taglet}s specified through a {@link ServiceLoader}.  Designed to
 * be specified as {@code <tagletClass/>} in a Maven {@code pom.xml}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public abstract class MavenBootstrapTaglet extends AbstractTaglet {

    /**
     * Static method to register all service providers for {@link Taglet}.
     *
     * @param   map             The {@link Map} of {@link Taglet}s.
     */
    public static void register(Map<Object,Object> map) {
        try {
            ServiceLoader.load(Taglet.class,
                               MavenBootstrapTaglet.class.getClassLoader())
                .iterator()
                .forEachRemaining(t -> map.putIfAbsent(t.getName(), t));
        } catch (Throwable throwable) {
            throwable.printStackTrace(System.err);

            if (throwable instanceof Error) {
                throw (Error) throwable;
            } else {
                throw new ExceptionInInitializerError(throwable);
            }
        }
    }

    private MavenBootstrapTaglet() {
        super(false, false, false, false, false, false, false);
    }
}
