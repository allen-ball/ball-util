/*
 * $Id$
 *
 * Copyright 2008 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.ant.types.TypedAttributeType;
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
import static ball.util.StringUtil.isNil;

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
public class InstanceOfTask extends TypeTask {
    private final List<TypedAttributeType> list =
        new ArrayList<TypedAttributeType>();
    protected Object instance = null;

    /**
     * Sole constructor.
     */
    public InstanceOfTask() {
        super();

        setType(String.class.getName());
    }

    public void addConfiguredArgument(TypedAttributeType argument) {
        list.add(argument);
    }

    public List<TypedAttributeType> getArgumentList() { return list; }

    public void setArgument(String string) {
        list.add(new TypedAttributeType(string));
    }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            Class<?> type = Class.forName(getType(), false, getClassLoader());

            log(type.getName());

            ClassList parameters = new ClassList();
            List<Object> arguments = new ArrayList<Object>();

            for (TypedAttributeType argument : list) {
                Factory<?> factory =
                    new Factory<Object>(Class.forName(argument.getType(),
                                                      false,
                                                      type.getClassLoader()));

                parameters.add(factory.getType());

                String string =
                    getProject().replaceProperties(argument.getValue());

                arguments.add(factory.getInstance(string));
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

    private class ClassList extends ArrayList<Class<?>> {
        private static final long serialVersionUID = -4504828433924386345L;

        public ClassList() { super(); }

        @Override
        public Class<?>[] toArray() { return toArray(new Class<?>[] { }); }
    }

    private class MapTableModelImpl extends MapTableModel {

        public MapTableModelImpl(Map<String,Object> map) {
            super(map, "Property Name", "Value");
        }

        @Override
        protected Object getValueAt(Object row, int x) {
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
