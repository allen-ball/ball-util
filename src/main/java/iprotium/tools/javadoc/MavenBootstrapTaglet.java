/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * {@link AbstractTaglet} whose {@link #register(Map)} method loads all
 * {@link Taglet}s specified through a {@link ServiceLoader}.  Designed to
 * be specified as {@code <tagletClass/>} in a Maven {@code pom.xml}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class MavenBootstrapTaglet extends AbstractTaglet {
    public static void register(Map<String,Taglet> map) {
        Iterator<? extends Taglet> iterator =
            ServiceLoader.load(Taglet.class,
                               MavenBootstrapTaglet.class.getClassLoader())
            .iterator();

        while (iterator.hasNext()) {
            Taglet value = iterator.next();

            if (value instanceof AbstractTaglet) {
                String key = ((AbstractTaglet) value).getName();

                if (map.containsKey(key)) {
                    map.remove(key);
                }

                map.put(key, value);
            }
        }
    }

    /**
     * Sole constructor.
     */
    public MavenBootstrapTaglet() {
        super(false, false, false, false, false, false, false);
    }
}
