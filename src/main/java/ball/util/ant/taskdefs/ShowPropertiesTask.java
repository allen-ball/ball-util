/*
 * $Id$
 *
 * Copyright 2008 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.swing.table.ArrayListTableModel;
import ball.util.Property;
import java.util.Collection;
import org.apache.tools.ant.BuildException;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to find and display static {@link Property} members.
 *
 * {@bean.info}
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
                    log(EMPTY);
                    log(type.getName());
                    log(new PropertyTableModel(collection));
                }
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }

    private class PropertyTableModel extends ArrayListTableModel<Property<?>> {
        private static final long serialVersionUID = -8530857150274440131L;

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
