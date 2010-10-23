/*
 * $Id: ServiceListTask.java,v 1.3 2010-10-23 22:13:00 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.text.ArrayListTableModel;
import iprotium.text.TextTable;
import java.util.ServiceLoader;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to list the resources that match a
 * specified name.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public class ServiceListTask extends AbstractClasspathTask {
    private String type = null;

    /**
     * Sole constructor.
     */
    public ServiceListTask() { super(); }

    protected String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public void execute() throws BuildException {
        if (getType() == null) {
            throw new BuildException("`type' attribute must be specified");
        }

        try {
            Class<? extends Object> service =
                getClass(getType()).asSubclass(Object.class);
            ServiceLoader<? extends Object> loader =
                ServiceLoader.load(service, service.getClassLoader());

            log("");

            for (String line :
                     new TextTable(new ServiceTableModel<Object>(loader))) {
                log(line);
            }
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

    private class ServiceTableModel<T> extends ArrayListTableModel<T> {
        private static final long serialVersionUID = 3484542128744178765L;

        public ServiceTableModel(ServiceLoader<? extends T> loader) {
            super(loader, loader.toString());
        }

        @Override
        protected T getValueAt(T row, int x) { return row; }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
