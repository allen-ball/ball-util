/*
 * $Id: JNDIListTask.java,v 1.2 2009-06-23 04:33:03 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.text.ArrayListTableModel;
import iprotium.text.Table;
import java.util.Collections;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant Task to list JNDI Context.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class JNDIListTask extends Task {

    /**
     * Sole constructor.
     */
    public JNDIListTask() { super(); }

    @Override
    public void execute() throws BuildException {
        try {
            Context context =
                NamingManager.getInitialContext(getProject().getProperties());

            log(context);
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
