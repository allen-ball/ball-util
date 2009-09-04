/*
 * $Id: ShowPropertiesTask.java,v 1.8 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.text.ArrayListTableModel;
import iprotium.text.Table;
import iprotium.util.Property;
import java.util.Collection;
import org.apache.tools.ant.BuildException;

/**
 * Ant {@link org.apache.tools.ant.Task} to find and display static {@link
 * Property} members.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.8 $
 */
public class ShowPropertiesTask extends AbstractClassFileTask {

    /**
     * Sole constructor.
     */
    public ShowPropertiesTask() { super(); }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            for (Class type : getMatchingClassFileMap().values()) {
                Collection<Property> collection =
                    Property.getStaticPropertyFields(type);

                if (! collection.isEmpty()) {
                    Table table = new PropertyTable(collection);

                    log("");
                    log(type.getName());

                    for (String line : table) {
                        log(line);
                    }
                }
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

    private class PropertyTable extends Table {
        public PropertyTable(Collection<Property> collection) {
            super(new PropertyTableModel(collection));
        }
    }

    private class PropertyTableModel extends ArrayListTableModel<Property> {
        private static final long serialVersionUID = -5904606396606185528L;

        public PropertyTableModel(Collection<Property> collection) {
            super(collection, 3);
        }

        @Override
        protected Object getValueAt(Property row, int x) {
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
/*
 * $Log: not supported by cvs2svn $
 */
