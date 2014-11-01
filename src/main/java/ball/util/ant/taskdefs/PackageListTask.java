/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.util.Set;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;

import static ball.util.StringUtil.NIL;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to determine the {@link Package}s represented by the argument
 * {@link Class} files.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("package-list")
public class PackageListTask extends AbstractClassFileTask {
    private static final String COMMA = ",";

    private String property = null;
    private String separator = null;

    /**
     * Sole constructor.
     */
    public PackageListTask() {
        super();

        setSeparator(COMMA);
    }

    public String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    @NotNull
    public String getSeparator() { return separator; }
    public void setSeparator(String separator) { this.separator = separator; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        Set<String> set = new TreeSet<>();

        for (Class<?> type : getClassSet()) {
            set.add(type.getPackage().getName());
        }

        if (! set.isEmpty()) {
            if (getProperty() != null) {
                getProject().setProperty(getProperty(), toString(set));
            } else {
                for (String name : set) {
                    log(name);
                }
            }
        }
    }

    private String toString(Iterable<String> iterable) {
        String string = NIL;

        for (String name : iterable) {
            if (string.length() > 0) {
                if (getSeparator() != null) {
                    string += getSeparator();
                }
            }

            string += name;
        }

        return string;
    }
}
