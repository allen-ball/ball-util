/*
 * $Id$
 *
 * Copyright 2009 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.util.ClassOrder;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;

import static iprotium.util.ClassUtil.isAbstract;
import static iprotium.util.StringUtil.NIL;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to determine the {@link Class}es
 * that are subclasses of the specified type.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@AntTask("subclasses-of")
public class SubclassesOfTask extends AbstractClassFileTask {
    private static final String COMMA = ",";

    private String type = null;
    private boolean includeAbstract = false;
    private String property = null;
    private String separator = null;

    /**
     * Sole constructor.
     */
    public SubclassesOfTask() {
        super();

        setSeparator(COMMA);
    }

    protected String getType() { return type; }
    public void setType(String type) { this.type = type; }

    protected boolean getIncludeAbstract() { return includeAbstract; }
    public void setIncludeAbstract(boolean includeAbstract) {
        this.includeAbstract = includeAbstract;
    }

    protected String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    protected String getSeparator() { return separator; }
    public void setSeparator(String separator) { this.separator = separator; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (getType() == null) {
            throw new BuildException("`type' attribute must be specified");
        }

        try {
            Class<?> supertype =
                Class.forName(getType(), false, getClassLoader());
            TreeSet<Class<?>> set = new TreeSet<Class<?>>(ClassOrder.NAME);

            for (Class<?> type : getClassSet()) {
                if ((! isAbstract(type)) || getIncludeAbstract()) {
                    if (supertype.isAssignableFrom(type)) {
                        set.add(type);
                    }
                }
            }

            if (! set.isEmpty()) {
                if (getProperty() != null) {
                    getProject().setProperty(getProperty(), toString(set));
                } else {
                    for (Class<?> subtype : set) {
                        log(toString(subtype));
                    }
                }
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BuildException(exception);
        }
    }

    private String toString(Iterable<Class<?>> iterable) {
        String string = NIL;

        for (Class<?> type : iterable) {
            if (string.length() > 0) {
                if (getSeparator() != null) {
                    string += getSeparator();
                }
            }

            string += toString(type);
        }

        return string;
    }

    private String toString(Class<?> type) {
        return (type != null) ? type.getCanonicalName() : null;
    }
}
