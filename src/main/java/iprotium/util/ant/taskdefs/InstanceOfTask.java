/*
 * $Id$
 *
 * Copyright 2008 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.activation.ReaderWriterDataSource;
import iprotium.text.MapTableModel;
import iprotium.text.TextTable;
import iprotium.util.BeanMap;
import iprotium.util.Factory;
import java.beans.ExceptionListener;
import java.beans.XMLEncoder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to get an instance of a specified
 * {@link Class}.
 *
 * @see Factory
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class InstanceOfTask extends AbstractClasspathTask {
    private String type = String.class.getName();
    private List<Argument> list = new ArrayList<Argument>();
    protected Object instance = null;

    /**
     * Sole constructor.
     */
    public InstanceOfTask() { super(); }

    protected String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public void addConfiguredArgument(Argument argument) {
        list.add(argument);
    }

    protected List<Argument> getArgumentList() { return list; }

    public void setArgument(String string) { list.add(new Argument(string)); }

    @Override
    public void execute() throws BuildException {
        try {
            Class<?> type = Class.forName(getType(), false, getClassLoader());

            log(type.getName());

            ClassList parameters = new ClassList();
            List<Object> arguments = new ArrayList<Object>();

            for (Argument argument : list) {
                Factory<?> factory =
                    new Factory<Object>(Class.forName(argument.getType(),
                                                      false,
                                                      type.getClassLoader()));

                parameters.add(factory.getType());
                arguments.add(factory.getInstance(argument.getValue()));
            }

            log(String.valueOf(parameters));
            log(String.valueOf(arguments));

            Factory<?> factory = new Factory<Object>(type);
            Member member = factory.getFactoryMethod(parameters.toArray());

            log(String.valueOf(member));

            instance = factory.apply(member, arguments.toArray());

            log(String.valueOf(instance));

            BeanMap map = BeanMap.asBeanMap(instance);

            if (! map.isEmpty()) {
                log("");
                log(new TextTable(new MapTableModelImpl(map)));
            }

            if (getClass().isAssignableFrom(InstanceOfTask.class)) {
                ReaderWriterDataSourceImpl ds =
                    new ReaderWriterDataSourceImpl();
                XMLEncoder encoder = null;

                try {
                    encoder = new XMLEncoder(ds.getOutputStream());
                    encoder.setExceptionListener(ds);
                    encoder.writeObject(instance);
                    encoder.flush();

                    if (ds.length() > 0) {
                        log("");
                        log(ds);
                    }
                } finally {
                    if (encoder != null) {
                        encoder.close();
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

    protected void log(Iterable<String> iterable) {
        for (String line : iterable) {
            log(line);
        }
    }

    /**
     * {@link InstanceOfTask} argument.
     */
    public static class Argument {
        private String type = String.class.getName();
        private String value = null;

        /**
         * @param       value   The initial value.
         *
         * @see #setValue(String)
         */
        public Argument(String value) { setValue(value); }

        /**
         * No-argument constructor.
         */
        public Argument() { this(null); }

        protected String getType() { return type; }
        public void setType(String type) { this.type = type; }

        protected String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        @Override
        public String toString() { return getValue(); }
    }

    private class ClassList extends ArrayList<Class<?>> {
        private static final long serialVersionUID = 2550066994634751402L;

        public ClassList() { super(); }

        @Override
        public Class<?>[] toArray() { return toArray(new Class<?>[] { }); }
    }

    private class MapTableModelImpl extends MapTableModel<String,Object> {
        private static final long serialVersionUID = -8318399100323530367L;

        public MapTableModelImpl(Map<String,Object> map) {
            super(map, "Property Name", "Value");
        }

        @Override
        protected Object getValueAt(Map.Entry<String,Object> row, int x) {
            return String.valueOf(super.getValueAt(row, x));
        }
    }

    private class ReaderWriterDataSourceImpl extends ReaderWriterDataSource
                                             implements ExceptionListener {
        public ReaderWriterDataSourceImpl() { super(null, null); }

        @Override
        public void exceptionThrown(Exception exception) { clear(); }
    }
}
