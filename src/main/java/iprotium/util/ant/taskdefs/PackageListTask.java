/*
 * $Id: PackageListTask.java,v 1.2 2010-08-23 03:43:55 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.util.Set;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to determine the {@link Package}s
 * represented by the argument {@link Class} files.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
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

    protected String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    protected String getSeparator() { return separator; }
    public void setSeparator(String separator) { this.separator = separator; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        Set<String> set = new TreeSet<String>();

        for (Class<?> type : getMatchingClassFileMap().values()) {
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
        String string = "";

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
/*
 * $Log: not supported by cvs2svn $
 */
