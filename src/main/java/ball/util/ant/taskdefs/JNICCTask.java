/*
 * $Id$
 *
 * Copyright 2008 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.ant.types.StringAttributeType;
import java.io.File;
import java.util.LinkedHashSet;
import org.apache.tools.ant.BuildException;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to compile JNI shared objects.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("jni-cc")
public class JNICCTask extends AbstractJNIExecuteOnTask {
    private File include = null;
    private LinkedHashSet<StringAttributeType> defineSet =
        new LinkedHashSet<StringAttributeType>();

    /**
     * Sole constructor.
     */
    public JNICCTask() {
        super();

        setParallel(false);
    }

    public File getInclude() { return include; }
    public void setInclude(File include) { this.include = include; }

    public void addConfiguredDefine(StringAttributeType definition) {
        defineSet.add(definition);
    }

    @Override
    protected String command() {
        String string = getBundleString("cc");

        for (StringAttributeType definition : defineSet) {
            if (definition.isActive(getProject())) {
                string += SPACE;
                string += getBundleString("cc-D");
                string += definition.getName();

                String value = definition.getValue();

                if (value != null) {
                    string += EQUALS;
                    string += value;
                }
            }
        }

        if (getInclude() != null) {
            string += SPACE;
            string += getBundleString("cc-I");
            string += getInclude().getAbsolutePath();
        }

        string += SPACE;
        string += getBundleString("cc-args");

        return string;
    }
}
