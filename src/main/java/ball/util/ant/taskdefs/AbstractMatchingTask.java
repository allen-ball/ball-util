/*
 * $Id$
 *
 * Copyright 2008 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.text.TextTable;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.table.TableModel;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ClasspathUtils;

/**
 * Abstract base class for {@link.uri http://ant.apache.org/ Ant}
 * {@link MatchingTask} implementations.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractMatchingTask extends MatchingTask
                                           implements AnnotatedTask {
    private ClasspathUtils.Delegate delegate = null;
    private File basedir = null;
    private File file = null;

    /**
     * Sole constructor.
     */
    protected AbstractMatchingTask() { super(); }

    public File getBasedir() { return basedir; }
    public void setBasedir(File basedir) { this.basedir = basedir; }

    public File getFile() { return file; }
    public void setFile(File file) { this.file = file; }

    public void setClasspathRef(Reference reference) {
        delegate.setClasspathref(reference);
    }

    public Path createClasspath() { return delegate.createClasspath(); }

    @Override
    public void init() throws BuildException {
        super.init();

        if (delegate == null) {
            delegate = ClasspathUtils.getDelegate(this);
        }
    }

    @Override
    public void execute() throws BuildException {
        validate();

        if (getBasedir() == null && getFile() == null) {
            setBasedir(getProject().resolveFile("."));
        }
    }

    /**
     * Method to get the {@link AntClassLoader} specified by this
     * {@link org.apache.tools.ant.Task}.
     *
     * @return  The {@link AntClassLoader}.
     */
    protected AntClassLoader getClassLoader() {
        if (delegate.getClasspath() == null) {
            delegate.createClasspath();
        }

        AntClassLoader loader = (AntClassLoader) delegate.getClassLoader();

        loader.setParent(getClass().getClassLoader());

        return loader;
    }

    /**
     * Method to get the {@link Class} associated with the argument name
     * using the {@link ClassLoader} provided by {@link #getClassLoader()}.
     *
     * @param   name            The fully qualified name of the desired
     *                          class.
     *
     * @return  The {@link Class} for the specified name.
     *
     * @throws  ClassNotFoundException
     *                          If the {@link Class} is not found.
     */
    protected Class<?> getClassForName(String name) throws ClassNotFoundException {
        return Class.forName(name, false, getClassLoader());
    }

    /**
     * Method to get {@code this} {@link MatchingTask}'s {@link File}s as a
     * {@link Set}.
     *
     * @return  The {@link Set} of matching {@link File}s.
     */
    protected Set<File> getMatchingFileSet() {
        TreeSet<File> set = new TreeSet<>();
        File base = getBasedir();

        if (base != null) {
            for (String path : getDirectoryScanner(base).getIncludedFiles()) {
                set.add(new File(base, path));
            }
        }

        File file = getFile();

        if (file != null) {
            set.add(file);
        }

        return set;
    }

    /**
     * See {@link #log(Iterable)}.
     *
     * @param   model           The {@link TableModel} to log.
     */
    protected void log(TableModel model) { log(model, Project.MSG_INFO); }

    /**
     * See {@link #log(Iterable,int)}.
     *
     * @param   model           The {@link TableModel} to log.
     * @param   msgLevel        The log message level.
     */
    protected void log(TableModel model, int msgLevel) {
        log(new TextTable(model), Project.MSG_INFO);
    }

    /**
     * See {@link #log(String)}.
     *
     * @param   iterable        The {@link Iterable} of {@link String}s to
     *                          log.
     */
    protected void log(Iterable<String> iterable) {
        log(iterable, Project.MSG_INFO);
    }

    /**
     * See {@link #log(String,int)}.
     *
     * @param   iterable        The {@link Iterable} of {@link String}s to
     *                          log.
     * @param   msgLevel        The log message level.
     */
    protected void log(Iterable<String> iterable, int msgLevel) {
        for (String line : iterable) {
            log(line, msgLevel);
        }
    }

    /**
     * See {@link #log(String)}.
     *
     * @param   iterator        The {@link Iterator} of {@link String}s to
     *                          log.
     */
    protected void log(Iterator<String> iterator) {
        log(iterator, Project.MSG_INFO);
    }

    /**
     * See {@link #log(String,int)}.
     *
     * @param   iterator        The {@link Iterator} of {@link String}s to
     *                          log.
     * @param   msgLevel        The log message level.
     */
    protected void log(Iterator<String> iterator, int msgLevel) {
        while (iterator.hasNext()) {
            log(iterator.next(), msgLevel);
        }
    }

    @Override
    public String toString() { return getClass().getSimpleName(); }
}
