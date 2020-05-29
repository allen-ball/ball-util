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
import java.nio.file.Path;
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
    public void process(Set<Class<?>> set, Path destdir) throws Exception;
}
