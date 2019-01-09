/*
 * $Id$
 *
 * Copyright 2008 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.swing.table.ArrayListTableModel;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tools.ant.BuildException;

import static org.apache.commons.lang3.StringUtils.EMPTY;

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
@NoArgsConstructor @ToString
public class ResourceListTask extends AbstractClasspathTask {
    @NotNull @Getter @Setter
    private String name = null;

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            log(EMPTY);
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
