/*
 * $Id: SubclassesOfTask.java,v 1.3 2010-08-23 03:43:55 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.util.Set;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;

import static iprotium.util.ClassOrder.NAME;
import static iprotium.util.ClassUtil.isAbstract;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to determine the {@link Class}es
 * that are subclasses of the specified type.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
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
            Class<?> supertype = getClass(getType());
            Set<Class<?>> set = new TreeSet<Class<?>>(NAME);

            for (Class<?> type : getMatchingClassFileMap().values()) {
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
                        log(subtype.getName());
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
        String string = "";

        for (Class<?> type : iterable) {
            if (string.length() > 0) {
                if (getSeparator() != null) {
                    string += getSeparator();
                }
            }

            string += type.getName();
        }

        return string;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
