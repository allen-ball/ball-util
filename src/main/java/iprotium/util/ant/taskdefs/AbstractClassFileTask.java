/*
 * $Id: AbstractClassFileTask.java,v 1.2 2008-10-28 09:19:31 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.util.ClasspathUtils;

/**
 * Abstract base class for Ant Task implementations that select *.CLASS
 * files.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public abstract class AbstractClassFileTask extends AbstractMatchingTask {
    private static final String DOT_CLASS = ".class";

    private ClasspathUtils.Delegate delegate = null;

    /**
     * Sole constructor.
     */
    protected AbstractClassFileTask() { super(); }

    public Path createClasspath() { return delegate.createClasspath(); }
    public void setClasspathRef(Reference reference) {
        delegate.setClasspathref(reference);
    }

    @Override
    public void init() throws BuildException {
        delegate = ClasspathUtils.getDelegate(this);

        add(new ClassFileSelector());

        super.init();
    }

    protected Map<File,Class> getMatchingClassFileMap() throws BuildException {
        Map<File,Class> map = new LinkedHashMap<File,Class>();

        for (File file : getMatchingFileSet()) {
            URI uri = getBasedir().toURI().relativize(file.toURI());
            String string = uri.toString();

            if (string.toLowerCase().endsWith(DOT_CLASS)) {
                string =
                    string.substring(0, string.length() - DOT_CLASS.length());
            }

            string = string.replaceAll("[/]", ".");

            try {
                map.put(file, getClass(string));
            } catch (ClassNotFoundException exception) {
                throw new BuildException(exception);
            }
        }

        return map;
    }

    protected Class getClass(String name) throws ClassNotFoundException {
        return Class.forName(name, false, delegate.getClassLoader());
    }

    protected static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    protected static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    protected static boolean isNative(Member member) {
        return Modifier.isNative(member.getModifiers());
    }

    private static class ClassFileSelector implements FileSelector {
        public ClassFileSelector() { }

        public boolean isSelected(File basedir, String name, File file) {
            return name.toLowerCase().endsWith(DOT_CLASS);
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
