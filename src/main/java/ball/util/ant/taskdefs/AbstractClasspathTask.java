/*
 * $Id$
 *
 * Copyright 2008 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.text.TextTable;
import ball.util.PropertiesImpl;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.table.TableModel;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ClasspathUtils;

/**
 * Abstract base class for {@link.uri http://ant.apache.org/ Ant}
 * {@link Task} implementations that require a classpath.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractClasspathTask extends Task
                                            implements AnnotatedTask {
    private ClasspathUtils.Delegate delegate = null;

    /**
     * Sole constructor.
     */
    protected AbstractClasspathTask() { super(); }

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
    }

    /**
     * Method to get the {@link AntClassLoader} specified by this
     * {@link Task}.
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
     * Method to extract the prefixed {@link Properties} from the
     * {@link Project} properties into a new {@link Properties}.  The prefix
     * is removed from the property names in the returned
     * {@link Properties}.
     *
     * @param   prefix          The {@link String} prefix to select
     *                          {@link Map} entry keys.
     *
     * @return  The {@link Properties} matching the argument prefix.
     */
    protected Properties getPrefixedProjectProperties(String prefix) {
        return getPrefixedPropertiesFrom(getProject().getProperties(), prefix);
    }

    /**
     * See {@link #log(Iterable)}.
     */
    protected void log(TableModel model) { log(model, Project.MSG_INFO); }

    /**
     * See {@link #log(Iterable,int)}.
     */
    protected void log(TableModel model, int msgLevel) {
        log(new TextTable(model), Project.MSG_INFO);
    }

    /**
     * See {@link #log(String)}.
     */
    protected void log(Iterable<String> iterable) {
        log(iterable, Project.MSG_INFO);
    }

    /**
     * See {@link #log(String,int)}.
     */
    protected void log(Iterable<String> iterable, int msgLevel) {
        for (String line : iterable) {
            log(line, msgLevel);
        }
    }

    /**
     * See {@link #log(String)}.
     */
    protected void log(Iterator<String> iterator) {
        log(iterator, Project.MSG_INFO);
    }

    /**
     * See {@link #log(String,int)}.
     */
    protected void log(Iterator<String> iterator, int msgLevel) {
        while (iterator.hasNext()) {
            log(iterator.next(), msgLevel);
        }
    }

    @Override
    public String getAntTaskName() { return DELEGATE.getAntTaskName(this); }

    @Override
    public void validate() throws BuildException { DELEGATE.validate(this); }

    @Override
    public String toString() { return getClass().getSimpleName(); }

    /**
     * Static method to extract prefixed {@link Properties} from a
     * {@link Map}.  A prefix must be specified to select {@link Map} keys
     * and that prefix is removed from the key name in the
     * {@link Properties}.  The returned {@link Properties} is not backed by
     * the argument {@link Map}.
     *
     * @param   map             The {@link Map}.
     * @param   prefix          The {@link String} prefix to select
     *                          {@link Map} entry keys.
     *
     * @return  The {@link Properties} matching the argument prefix.
     */
    public static Properties getPrefixedPropertiesFrom(Map<?,?> map,
                                                       String prefix) {
        Properties properties = new PropertiesImpl();

        for (Map.Entry<?,?> entry : map.entrySet()) {
            String key = entry.getKey().toString();

            if (key.startsWith(prefix)) {
                Object value = entry.getValue();

                properties.setProperty(key.substring(prefix.length()),
                                       (value != null) ? value.toString() : null);
            }
        }

        return properties;
    }
}
