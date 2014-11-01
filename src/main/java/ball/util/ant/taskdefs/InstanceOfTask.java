/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.activation.ReaderWriterDataSource;
import ball.swing.table.MapTableModel;
import ball.util.BeanMap;
import ball.util.Factory;
import java.beans.ExceptionListener;
import java.beans.XMLEncoder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.BuildException;

import static ball.util.StringUtil.NIL;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to get an instance of a specified {@link Class}.
 *
 * {@bean-info}
 *
 * @see Factory
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("instance-of")
public class InstanceOfTask extends AbstractClasspathTask {
    private String type = String.class.getName();
    private List<Argument> list = new ArrayList<>();
    protected Object instance = null;

    /**
     * Sole constructor.
     */
    public InstanceOfTask() { super(); }

    @NotNull
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public void addConfiguredArgument(Argument argument) {
        list.add(argument);
    }

    public List<Argument> getArgumentList() { return list; }

    public void setArgument(String string) { list.add(new Argument(string)); }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            Class<?> type = Class.forName(getType(), false, getClassLoader());

            log(type.getName());

            ClassList parameters = new ClassList();
            List<Object> arguments = new ArrayList<>();

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
                log(NIL);
                log(new MapTableModelImpl(map));
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
                        log(NIL);
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
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
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

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        @Override
        public String toString() { return getValue(); }
    }

    private class ClassList extends ArrayList<Class<?>> {
        private static final long serialVersionUID = -4504828433924386345L;

        public ClassList() { super(); }

        @Override
        public Class<?>[] toArray() { return toArray(new Class<?>[] { }); }
    }

    private class MapTableModelImpl extends MapTableModel<String,Object> {
        private static final long serialVersionUID = -5822168627317896186L;

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
