/*
 * $Id$
 *
 * Copyright 2008 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.lang.reflect.Member;
import java.util.Set;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;

import static ball.util.ClassOrder.NAME;
import static java.lang.reflect.Modifier.isNative;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to determine the classes that have native members.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("jni-classes")
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

    public String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    public String getSeparator() { return separator; }
    public void setSeparator(String separator) { this.separator = separator; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        Set<Class<?>> set = new TreeSet<>(NAME);

        for (Class<?> type : getClassSet()) {
            boolean hasNative = false;

            try {
                if (! hasNative) {
                    hasNative |= hasNative(type.getDeclaredConstructors());
                }
            } catch (NoClassDefFoundError error) {
            }

            try {
                if (! hasNative) {
                    hasNative |= hasNative(type.getDeclaredFields());
                }
            } catch (NoClassDefFoundError error) {
            }

            try {
                if (! hasNative) {
                    hasNative |= hasNative(type.getDeclaredMethods());
                }
            } catch (NoClassDefFoundError error) {
            }

            if (hasNative) {
                set.add(type);
            }
        }

        if (getProperty() != null) {
            if (! set.isEmpty()) {
                getProject().setProperty(getProperty(), toString(set));
            }
        } else {
            for (Class<?> type : set) {
                log(type.getName());
            }
        }
    }

    private boolean hasNative(Member... members) {
        boolean hasNative = false;

        for (Member member : members) {
            hasNative |= isNative(member.getModifiers());

            if (hasNative) {
                break;
            }
        }

        return hasNative;
    }

    private String toString(Iterable<Class<?>> iterable) {
        String string = EMPTY;

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
