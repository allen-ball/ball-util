/*
 * $Id$
 *
 * Copyright 2009 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.ClassOrder;
import java.util.TreeSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tools.ant.BuildException;

import static java.lang.reflect.Modifier.isAbstract;
import static org.apache.commons.lang3.StringUtils.EMPTY;

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

    @NotNull @Getter @Setter
    private String type = null;
    @Getter @Setter
    private boolean includeAbstract = false;
    @Getter @Setter
    private String property = null;
    @Getter @Setter
    private String separator = null;

    { setSeparator(COMMA); }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            Class<?> supertype = getClassForName(getType());
            TreeSet<Class<?>> set = new TreeSet<>(ClassOrder.NAME);

            for (Class<?> type : getClassSet()) {
                if ((! isAbstract(type.getModifiers())) || isIncludeAbstract()) {
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
        String string = EMPTY;

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
