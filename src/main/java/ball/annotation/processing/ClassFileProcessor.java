/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import java.io.File;
import java.util.Set;
import javax.annotation.processing.Processor;

/**
 * Interface to provide an alternate entry point for annotation processing.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface ClassFileProcessor extends Processor {

    /**
     * Bootstrap method called by this {@link org.apache.tools.ant.Task}.
     *
     * @param       set         The {@link Set} of {@link Class}es to
     *                          examine.
     * @param       destdir     The root of the hierarchy to record any
     *                          output artifacts.
     *
     * @throws      Exception   If the implementation throws an
     *                          {@link Exception}.
     */
    public void process(Set<Class<?>> set, File destdir) throws Exception;
}
