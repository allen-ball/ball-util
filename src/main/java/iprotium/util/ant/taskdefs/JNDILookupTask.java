/*
 * $Id: JNDILookupTask.java,v 1.2 2009-08-15 04:02:06 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.Context;
import javax.naming.spi.NamingManager;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to perform JNDI look-up.
 *
 * @see NamingManager#getURLContext(String,Hashtable)
 * @see NamingManager#getInitialContext(Hashtable)
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class JNDILookupTask extends JNDIListTask {
    private static final Pattern PATTERN =
        Pattern.compile("(?is)(([^:]+):)?([^:]*)");
    private static final int SCHEME_GROUP = 2;
    private static final int PATH_GROUP = 3;

    private String name = null;
    private String property = null;

    /**
     * Sole constructor.
     */
    public JNDILookupTask() { super(); }

    protected String getName() { return name; }
    public void setName(String name) { this.name = name; }

    protected String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    @Override
    public void execute() throws BuildException {
        if (getName() == null) {
            throw new BuildException("`name' attribute must be specified");
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());

            Matcher matcher = PATTERN.matcher(getName());

            if (matcher.matches()) {
                Context context = getContext(matcher.group(SCHEME_GROUP));
                Object value = null;

                if (context != null) {
                    value = context.lookup(matcher.group(PATH_GROUP));
                }

                String property = getProperty();

                if (property != null && value != null) {
                    getProject().setProperty(property, String.valueOf(value));
                } else {
                    if (value instanceof Context) {
                        log((Context) value);
                    } else {
                        log(getName() + ": " + String.valueOf(value));
                    }
                }
            } else {
                throw new BuildException(matcher.toString());
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BuildException(exception);
        } finally {
            Thread.currentThread().setContextClassLoader(loader);
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */