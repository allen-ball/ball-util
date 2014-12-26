/*
 * $Id$
 *
 * Copyright 2011 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs.compilers;

import ball.tools.DiagnosticMap;
import ball.tools.Remedy;
import ball.util.ClassOrder;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import static ball.util.StringUtil.NIL;
import static javax.tools.JavaFileObject.Kind.CLASS;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;

/**
 * {@link SystemJavaCompilerAdapter} implementation that provides additional
 * {@link.man lint(1)}-like analysis.
 *
 * @see Remedy
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class LintAdapter extends SystemJavaCompilerAdapter {
    /** {@link #PERIOD} = {@value #PERIOD} */
    protected static final String PERIOD = ".";
    /** {@link #SLASH} = {@value #SLASH} */
    protected static final String SLASH = "/";

    private static final URI FILE = URI.create("file:///");

    /**
     * Sole constructor.
     */
    public LintAdapter() { super(); }

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
            super(ClassOrder.NAME);

            URI root = fm.getLocation(CLASS_OUTPUT).iterator().next().toURI();
            ClassLoader loader = fm.getClassLoader(CLASS_PATH);
            Iterable<JavaFileObject> iterable = null;

            try {
                iterable =
                    fm.list(CLASS_OUTPUT, NIL,
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
