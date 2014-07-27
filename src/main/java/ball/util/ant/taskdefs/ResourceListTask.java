/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.text.ArrayListTableModel;
import ball.text.TextTable;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import org.apache.tools.ant.BuildException;

import static ball.util.StringUtil.NIL;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to list the resources that match a specified name.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
