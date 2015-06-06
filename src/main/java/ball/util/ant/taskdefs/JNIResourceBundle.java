/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

/**
 * {@link java.util.ResourceBundle} implementation for JNI
 * {@link.uri http://ant.apache.org/ Ant} tasks.
 *
 * {@bean-info}
 *
 * @see AbstractJNIExecuteOnTask
 * @see JNICCTask
 * @see JNIClassesTask
 * @see JNILDTask
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class JNIResourceBundle extends PropertyResourceBundle {

    /**
     * Sole constructor.
     */
    public JNIResourceBundle() throws IOException { super(getInputStream()); }

    public String getString(String os, String arch, String name) {
        String value = null;

        for (String key : searchList(os, arch, name)) {
            try {
                value = getString(key);
                break;
            } catch (MissingResourceException exception) {
                continue;
            }
        }

        return value;
    }

    private String canonicalize(String string) {
        return string.replaceAll("[\\p{Space}]", "-").toLowerCase();
    }

    private List<String> searchList(String os, String arch, String name) {
        List<String> list = new ArrayList<String>();

        if (os != null) {
            os = canonicalize(os);

            if (arch != null) {
                list.add(os + "/" + canonicalize(arch) + "/" + name);
            }

            list.add(os + "/" + name);
        }

        list.add(name);

        return list;
    }

    private static InputStream getInputStream() {
        String name =
            JNIResourceBundle.class.getName().replace(".", "/")
            + ".properties";
        ClassLoader loader = JNIResourceBundle.class.getClassLoader();

        return new BufferedInputStream(loader.getResourceAsStream(name));
    }
}
