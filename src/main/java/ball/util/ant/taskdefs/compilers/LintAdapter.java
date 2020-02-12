package ball.util.ant.taskdefs.compilers;
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
import ball.tools.DiagnosticMap;
import ball.tools.Remedy;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import lombok.NoArgsConstructor;

import static java.util.Comparator.comparing;
import static javax.tools.JavaFileObject.Kind.CLASS;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * {@link SystemJavaCompilerAdapter} implementation that provides additional
 * {@link.man lint(1)}-like analysis.
 *
 * @see Remedy
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor
public class LintAdapter extends SystemJavaCompilerAdapter {
    /** {@link #PERIOD} = {@value #PERIOD} */
    protected static final String PERIOD = ".";
    /** {@link #SLASH} = {@value #SLASH} */
    protected static final String SLASH = "/";

    private static final URI FILE = URI.create("file:///");

    @Override
    protected boolean compile(StandardJavaFileManager fm,
                              DiagnosticMap map) throws Throwable {
        boolean success = super.compile(fm, map);
        SortedSet<Class<?>> classes =
            Collections.unmodifiableSortedSet(new OutputClassSet(fm));

        for (Map.Entry<Diagnostic<? extends JavaFileObject>,String> entry :
                 map.entrySet()) {
            Diagnostic<? extends JavaFileObject> key = entry.getKey();
            String code = key.getCode();
            Remedy remedy = Remedy.getRemedyMap().get(code);

            if (remedy != null) {
                entry.setValue(remedy.getRx(key, fm, classes));
            } else {
                entry.setValue(code);
            }
        }

        return success;
    }

    private static class OutputClassSet extends TreeSet<Class<?>> {
        private static final long serialVersionUID = 3581687174576253455L;

        public OutputClassSet(StandardJavaFileManager fm) {
            super(comparing(Class::getName));

            URI root = fm.getLocation(CLASS_OUTPUT).iterator().next().toURI();
            ClassLoader loader = fm.getClassLoader(CLASS_PATH);
            Iterable<JavaFileObject> iterable = null;

            try {
                iterable =
                    fm.list(CLASS_OUTPUT, EMPTY,
                            Collections.singleton(CLASS), true);
            } catch (Throwable throwable) {
                iterable = Collections.<JavaFileObject>emptyList();
            }

            for (JavaFileObject object : iterable) {
                URI uri = FILE.resolve(object.toUri());
                String name = root.relativize(uri).toString();

                if (name.toLowerCase().endsWith(CLASS.extension)) {
                    name =
                        name.substring(0,
                                       name.length()
                                       - CLASS.extension.length());
                }

                name = name.replaceAll(Pattern.quote(SLASH), PERIOD);

                try {
                    add(Class.forName(name, false, loader));
                } catch (Throwable throwable) {
                }
            }
        }
    }
}
