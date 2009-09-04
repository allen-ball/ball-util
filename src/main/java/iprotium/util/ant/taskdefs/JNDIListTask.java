/*
 * $Id: JNDIListTask.java,v 1.4 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.text.ArrayListTableModel;
import iprotium.text.Table;
import java.util.Collections;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import org.apache.tools.ant.BuildException;

/**
 * Ant {@link org.apache.tools.ant.Task} to list JNDI Initial
 * {@link Context}.
 *
 * @see NamingManager#getInitialContext(Hashtable)
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.4 $
 */
public class JNDIListTask extends AbstractClasspathTask {

    /**
     * Sole constructor.
     */
    public JNDIListTask() { super(); }

    @Override
    public void execute() throws BuildException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());

            log(getContext(null));
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

    protected void log(Context context) throws NamingException {
        log("");

        for (String line : new ContextTable(context)) {
            log(line);
        }
    }

    private class ContextTable extends Table {
        public ContextTable(Context context) throws NamingException {
            super(new ContextTableModel(context));
        }
    }

    private class ContextTableModel extends ArrayListTableModel<Binding> {
        private static final long serialVersionUID = 1340868492541881755L;

        public ContextTableModel(Context context) throws NamingException {
            super(Collections.list(context.listBindings("")),
                  context.getNameInNamespace(), null);
        }

        @Override
        protected Object getValueAt(Binding row, int x) {
            Object value = null;

            switch (x) {
            case 0:
                value = row.getName();
                break;

            case 1:
                value = row.getObject();
                break;
            }

            return value;
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
