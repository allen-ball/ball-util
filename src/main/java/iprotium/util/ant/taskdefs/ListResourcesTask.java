/*
 * $Id: ListResourcesTask.java,v 1.5 2009-08-14 22:55:25 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.text.ArrayListTableModel;
import iprotium.text.Table;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to list the resources that match a specific name.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.5 $
 */
public class ListResourcesTask extends AbstractClasspathTask {
    private String name = null;

    /**
     * Sole constructor.
     */
    public ListResourcesTask() { super(); }

    protected String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public void execute() throws BuildException {
        if (getName() == null) {
            throw new BuildException("`name' attribute must be specified");
        }

        try {
            log("");

            for (String line : new Table(new TableModelImpl(getName()))) {
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

    private class TableModelImpl extends ArrayListTableModel<URL> {
        private static final long serialVersionUID = 5257580672656495451L;

        public TableModelImpl(String name) throws IOException {
            this(Collections.list(getClassLoader().getResources(name)), name);
        }

        private TableModelImpl(Collection<URL> collection, String name) {
            super(new LinkedHashSet<URL>(collection), name);
        }

        @Override
        protected URL getValueAt(URL row, int x) { return row; }
    }
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2009/06/21 03:22:08  ball
 * Use a Table to format output.
 *
 */
