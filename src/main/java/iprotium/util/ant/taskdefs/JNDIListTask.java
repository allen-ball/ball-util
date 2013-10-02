/*
 * $Id$
 *
 * Copyright 2009 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.text.ArrayListTableModel;
import iprotium.text.TextTable;
import java.util.Collections;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import org.apache.tools.ant.BuildException;

import static iprotium.util.StringUtil.NIL;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to list JNDI Initial {@link Context}.
 *
 * @see NamingManager#getInitialContext(Hashtable)
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@AntTask("jndi-list")
public class JNDIListTask extends AbstractClasspathTask {

    /**
     * Sole constructor.
     */
    public JNDIListTask() { super(); }

    @Override
    public void execute() throws BuildException {
        try {
            log(getContext(null));
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BuildException(exception);
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
        private static final long serialVersionUID = 1340868492541881755L;

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
}
