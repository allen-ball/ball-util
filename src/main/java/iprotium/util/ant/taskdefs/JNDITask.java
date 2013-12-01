/*
 * $Id$
 *
 * Copyright 2009 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.text.ArrayListTableModel;
import iprotium.text.TextTable;
import iprotium.util.Regex;
import java.util.Collections;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import org.apache.tools.ant.BuildException;

import static iprotium.util.StringUtil.NIL;

/**
 * Abstract base class for JNDI <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task}s.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class JNDITask extends AbstractClasspathTask {

    /**
     * Sole constructor.
     */
    protected JNDITask() { super(); }

    /**
     * Method to get a {@link Context} for the named scheme.
     *
     * @param   scheme          The {@link String} identifying the scheme.
     *
     * @return  The {@link Context}.
     *
     * @see NamingManager#getURLContext(String,Hashtable)
     * @see NamingManager#getInitialContext(Hashtable)
     */
    protected Context getContext(String scheme) throws NamingException {
        Context context = null;
        Hashtable<?,?> env = getProject().getProperties();

        if (scheme != null) {
            context = NamingManager.getURLContext(scheme, env);
        } else {
            context = NamingManager.getInitialContext(env);
        }

        return context;
    }

    /**
     * Method to display a {@link Context}.
     *
     * @param   context         The {@link Context} to display.
     */
    protected void log(Context context) throws NamingException {
        log(NIL);

        for (String line : new ContextTable(context)) {
            log(line);
        }
    }

    private class ContextTable extends TextTable {
        public ContextTable(Context context) throws NamingException {
            super(new ContextTableModel(context));
        }
    }

    private class ContextTableModel extends ArrayListTableModel<Binding> {
        private static final long serialVersionUID = -6380659141051842412L;

        public ContextTableModel(Context context) throws NamingException {
            super(Collections.list(context.listBindings(NIL)),
                  context.getNameInNamespace(), null);
        }

        @Override
        protected Object getValueAt(Binding row, int x) {
            Object value = null;

            switch (x) {
            case 0:
            default:
                value = row.getName();
                break;

            case 1:
                value = row.getObject();
                break;
            }

            return value;
        }
    }

    /**
     * <a href="http://ant.apache.org/">Ant</a>
     * {@link org.apache.tools.ant.Task} to list a JNDI initial
     * {@link Context}.
     *
     * @see NamingManager#getInitialContext(Hashtable)
     */
    @AntTask("jndi-list")
    public static class List extends JNDITask {

        /**
         * Sole constructor.
         */
        public List() { super(); }

        @Override
        public void execute() throws BuildException {
            ClassLoader loader =
                Thread.currentThread().getContextClassLoader();

            try {
                Thread.currentThread().setContextClassLoader(getClassLoader());

                log(getContext(null));
            } catch (BuildException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new BuildException(throwable);
            } finally {
                Thread.currentThread().setContextClassLoader(loader);
            }
        }
    }

    /**
     * <a href="http://ant.apache.org/">Ant</a>
     * {@link org.apache.tools.ant.Task} to perform a JNDI look-up.
     *
     * @see NamingManager#getURLContext(String,Hashtable)
     * @see NamingManager#getInitialContext(Hashtable)
     */
    @AntTask("jndi-lookup")
    public static class Lookup extends JNDITask {
        @Regex
        private static final String REGEX = "(?is)(([^:]+):)?([^:]*)";
        private static final Pattern PATTERN = Pattern.compile(REGEX);
        private static final int SCHEME_GROUP = 2;
        private static final int PATH_GROUP = 3;

        private String name = null;
        private String property = null;

        /**
         * Sole constructor.
         */
        public Lookup() { super(); }

        protected String getName() { return name; }
        public void setName(String name) { this.name = name; }

        protected String getProperty() { return property; }
        public void setProperty(String property) { this.property = property; }

        @Override
        public void execute() throws BuildException {
            if (getName() == null) {
                throw new BuildException("`name' attribute must be specified");
            }

            ClassLoader loader =
                Thread.currentThread().getContextClassLoader();

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
                        getProject()
                            .setProperty(property, String.valueOf(value));
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
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new BuildException(throwable);
            } finally {
                Thread.currentThread().setContextClassLoader(loader);
            }
        }
    }
}
