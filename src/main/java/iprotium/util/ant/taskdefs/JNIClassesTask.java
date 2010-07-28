/*
 * $Id: JNIClassesTask.java,v 1.8 2010-07-28 05:46:06 ball Exp $
 *
 * Copyright 2008 - 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.lang.reflect.Member;
import java.util.Set;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;

import static iprotium.util.ClassOrder.NAME;
import static iprotium.util.ClassUtil.isNative;

/**
 * Ant {@link org.apache.tools.ant.Task} to determine the classes that have
 * native members.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.8 $
 */
public class JNIClassesTask extends AbstractClassFileTask {
    private static final String COMMA = ",";

    private String property = null;
    private String separator = null;

    /**
     * Sole constructor.
     */
    public JNIClassesTask() {
        super();

        setSeparator(COMMA);
    }

    protected String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    protected String getSeparator() { return separator; }
    public void setSeparator(String separator) { this.separator = separator; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        Set<Class<?>> set = new TreeSet<Class<?>>(NAME);

        for (Class<?> type : getMatchingClassFileMap().values()) {
            if (hasNative(type.getDeclaredConstructors())
                || hasNative(type.getDeclaredFields())
                || hasNative(type.getDeclaredMethods())) {
                set.add(type);
            }
        }

        if (! set.isEmpty()) {
            if (getProperty() != null) {
                getProject().setProperty(getProperty(), toString(set));
            } else {
                for (Class<?> type : set) {
                    log(type.getName());
                }
            }
        }
    }

    private boolean hasNative(Member... members) {
        boolean hasNative = false;

        for (Member member : members) {
            hasNative |= isNative(member);

            if (hasNative) {
                break;
            }
        }

        return hasNative;
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
