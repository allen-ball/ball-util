/*
 * $Id$
 *
 * Copyright 2008 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.text.ArrayListTableModel;
import iprotium.text.TextTable;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
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
@AntTask("resource-list")
public class ResourceListTask extends AbstractClasspathTask {
    private String name = null;

    /**
     * Sole constructor.
     */
    public ResourceListTask() { super(); }

    protected String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public void execute() throws BuildException {
        if (getName() == null) {
            throw new BuildException("`name' attribute must be specified");
        }

        try {
            log(NIL);

            for (String line : new TextTable(new TableModelImpl(getName()))) {
                log(line);
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }

    private class TableModelImpl extends ArrayListTableModel<URL> {
        private static final long serialVersionUID = -5969955756512777492L;

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
