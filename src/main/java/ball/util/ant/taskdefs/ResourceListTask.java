/*
 * $Id$
 *
 * Copyright 2008 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.swing.table.ArrayListTableModel;
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
 * {@bean.info}
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

    @NotNull
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            log(NIL);
            log(new TableModelImpl(getName()));
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }

    private class TableModelImpl extends ArrayListTableModel<URL> {
        private static final long serialVersionUID = 3143072162049088643L;

        public TableModelImpl(String name) throws IOException {
            this(Collections.list(getClassLoader().getResources(name)), name);
        }

        private TableModelImpl(Collection<URL> collection, String name) {
            super(new LinkedHashSet<>(collection), name);
        }

        @Override
        protected URL getValueAt(URL row, int x) { return row; }
    }
}
