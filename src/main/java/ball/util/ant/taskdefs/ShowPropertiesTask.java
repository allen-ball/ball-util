/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.annotation.AntTask;
import ball.text.ArrayListTableModel;
import ball.text.TextTable;
import ball.util.Property;
import java.util.Collection;
import org.apache.tools.ant.BuildException;

import static ball.util.StringUtil.NIL;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to find and display static {@link Property} members.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("show-properties")
public class ShowPropertiesTask extends AbstractClassFileTask {

    /**
     * Sole constructor.
     */
    public ShowPropertiesTask() { super(); }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            for (Class<?> type : getClassSet()) {
                Collection<Property<?>> collection =
                    Property.getStaticPropertyFields(type);

                if (! collection.isEmpty()) {
                    TextTable table = new PropertyTable(collection);

                    log(NIL);
                    log(type.getName());

                    for (String line : table) {
                        log(line);
                    }
                }
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }

    private class PropertyTable extends TextTable {
        public PropertyTable(Collection<Property<?>> collection) {
            super(new PropertyTableModel(collection));
        }
    }

    private class PropertyTableModel extends ArrayListTableModel<Property<?>> {
        private static final long serialVersionUID = -5904606396606185528L;

        public PropertyTableModel(Collection<Property<?>> collection) {
            super(collection, 3);
        }

        @Override
        protected Object getValueAt(Property<?> row, int x) {
            Object value = null;

            switch (x) {
            case 0:
            default:
                value = row.getName();
                break;

            case 1:
                value = row.isRequired();
                break;

            case 2:
                value = row.getDefaultValueAsString();
                break;
            }

            return value;
        }
    }
}