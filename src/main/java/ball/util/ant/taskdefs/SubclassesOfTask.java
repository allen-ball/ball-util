/*
 * $Id$
 *
 * Copyright 2009 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.ClassOrder;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;

import static ball.util.StringUtil.NIL;
import static java.lang.reflect.Modifier.isAbstract;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to determine the {@link Class}es that are subclasses of the specified
 * type.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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

    @NotNull
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean getIncludeAbstract() { return includeAbstract; }
    public void setIncludeAbstract(boolean includeAbstract) {
        this.includeAbstract = includeAbstract;
    }

    public String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    public String getSeparator() { return separator; }
    public void setSeparator(String separator) { this.separator = separator; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            Class<?> supertype =
                Class.forName(getType(), false, getClassLoader());
            TreeSet<Class<?>> set = new TreeSet<>(ClassOrder.NAME);

            for (Class<?> type : getClassSet()) {
                if ((! isAbstract(type.getModifiers())) || getIncludeAbstract()) {
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
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
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
