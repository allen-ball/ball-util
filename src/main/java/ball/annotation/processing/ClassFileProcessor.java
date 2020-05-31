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
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import static java.lang.reflect.Modifier.isAbstract;
import static java.util.Collections.singleton;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Interface to provide an alternate entry point for annotation processing
 * on compiled class files.
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

    /**
     * Static method to scan generated class files for
     * {@link ClassFileProcessor} implementation classes and invoke
     * {@link #process(Set,JavaFileManager)}.
     *
     * @param   fm              The configured {@link JavaFileManager} (for
     *                          writing output files).
     *
     * @throws  Throwable       Nothing is intercepted by this method.
     */
    public static void process(JavaFileManager fm) throws Throwable {
        Comparator<Class<?>> comparator = Comparator.comparing(Class::getName);
        TreeSet<Class<?>> set = new TreeSet<>(comparator);
        ClassLoader loader = fm.getClassLoader(CLASS_PATH);

        for (JavaFileObject file :
                 fm.list(CLASS_OUTPUT, EMPTY,
                         singleton(JavaFileObject.Kind.CLASS), true)) {
            String name = fm.inferBinaryName(CLASS_OUTPUT, file);

            set.add(Class.forName(name, true, loader));
        }

        Class<?> bootstrap =
            set.stream()
            .filter(t -> comparator.compare(ClassFileProcessor.class, t) == 0)
            .findFirst().orElse(ClassFileProcessor.class);
        Method method =
            bootstrap.getMethod("process", Set.class, JavaFileManager.class);

        for (Class<?> type : set) {
            if (! isAbstract(type.getModifiers())) {
                if (bootstrap.isAssignableFrom(type)) {
                    Object object = type.asSubclass(bootstrap).newInstance();

                    method.invoke(object, set, fm);
                }
            }
        }
    }
}
