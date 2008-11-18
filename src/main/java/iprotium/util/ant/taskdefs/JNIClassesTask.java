/*
 * $Id: JNIClassesTask.java,v 1.3 2008-11-18 06:56:59 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.lang.reflect.Member;
import java.util.Set;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;

import static iprotium.util.ClassOrder.NAME;

/**
 * Ant Task to determine the classes that have native members.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public class JNIClassesTask extends AbstractClassFileTask {
    private String property = null;

    /**
     * Sole constructor.
     */
    public JNIClassesTask() { super(); }

    protected String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

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
                String name = getProperty();

                getProject().setProperty(name, toString(set));

                log(name + ": " + getProject().getProperty(name));
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

    private static String toString(Iterable<Class<?>> iterable) {
        String string = "";

        for (Class<?> type : iterable) {
            if (string.length() > 0) {
                string += ",";
            }

            string += type.getName();
        }

        return string;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
