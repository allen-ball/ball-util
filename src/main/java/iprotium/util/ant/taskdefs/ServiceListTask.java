/*
 * $Id$
 *
 * Copyright 2009 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.text.ArrayListTableModel;
import iprotium.text.TextTable;
import java.util.ServiceLoader;
import org.apache.tools.ant.BuildException;

import static iprotium.util.StringUtil.NIL;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to list the resources that match a
 * specified name.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@AntTask("service-list")
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
                Class.forName(getType(), false, getClassLoader())
                .asSubclass(Object.class);
            ServiceLoader<? extends Object> loader =
                ServiceLoader.load(service, service.getClassLoader());

            log(NIL);

            for (String line :
                     new TextTable(new ServiceTableModel<Object>(loader))) {
                log(line);
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
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
