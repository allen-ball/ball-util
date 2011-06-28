/*
 * $Id: LintAdapter.java,v 1.1 2011-06-28 04:23:22 ball Exp $
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs.compilers;

import iprotium.tools.DiagnosticMap;
import iprotium.tools.Remedy;
import iprotium.util.ClassOrder;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import static javax.tools.JavaFileObject.Kind.CLASS;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;

/**
 * {@link SystemJavaCompilerAdapter} implementation that provides additional
 * {@code lint}-like analysis.
 *
 * @see Remedy
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class LintAdapter extends SystemJavaCompilerAdapter {
    private static final URI FILE = URI.create("file:///");

    /**
     * Sole constructor.
     */
    public LintAdapter() { }

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
                    fm.list(CLASS_OUTPUT, "",
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

                name = name.replaceAll(Pattern.quote("/"), ".");

                try {
                    add(Class.forName(name, false, loader));
                } catch (Throwable throwable) {
                }
            }
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
