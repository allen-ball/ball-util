/*
 * $Id$
 *
 * Copyright 2009 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.util.Set;
import java.util.TreeSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tools.ant.BuildException;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to determine the {@link Package}s represented by the argument
 * {@link Class} files.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("package-list")
@NoArgsConstructor @ToString
public class PackageListTask extends AbstractClassFileTask {
    private static final String COMMA = ",";

    @Getter @Setter
    private String property = null;
    @NotNull @Getter @Setter
    private String separator = null;

    { setSeparator(COMMA); }

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
        String string = EMPTY;

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
