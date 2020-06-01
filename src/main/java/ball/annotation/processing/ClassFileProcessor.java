package ball.annotation.processing;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileManager;

/**
 * Interface to provide an alternate entry point for annotation processing
 * on compiled class files.  Implementors must extend
 * {@link AnnotatedProcessor}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface ClassFileProcessor extends Processor {

    /**
     * {@link Processor} alternate entry point.
     *
     * @param   set             The {@link Set} of {@link Class}es to
     *                          analyze.
     * @param   fm              The configured {@link JavaFileManager} (for
     *                          writing output files).
     *
     * @throws  Throwable       If the implementation throws a
     *                          {@link Throwable}.
     */
    public void process(Set<Class<?>> set,
                        JavaFileManager fm) throws Throwable;
}
