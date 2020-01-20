/*
 * $Id$
 *
 * Copyright 2008 - 2020 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.activation.ReaderWriterDataSource;
import ball.util.Factory;
import ball.util.ant.types.TypedAttributeType;
import java.beans.ExceptionListener;
import java.beans.XMLEncoder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tools.ant.BuildException;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to get an instance of a specified {@link Class}.
 *
 * {@ant.task}
 *
 * @see Factory
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@AntTask("instance-of")
@NoArgsConstructor @ToString
public class InstanceOfTask extends TypeTask {
    private final List<TypedAttributeType> list = new ArrayList<>();
    protected Object instance = null;

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
            Class<?> type = getClassForName(getType());

            log(type.getName());

            ClassList parameters = new ClassList();
            List<Object> arguments = new ArrayList<>();

            for (TypedAttributeType argument : list) {
                Factory<?> factory =
                    new Factory<>(getClassForName(argument.getType()));

                parameters.add(factory.getType());

                String string =
                    getProject().replaceProperties(argument.getValue());

                arguments.add(factory.getInstance(string));
            }

            log(String.valueOf(parameters));
            log(String.valueOf(arguments));

            Factory<?> factory = new Factory<>(type);
            Member member = factory.getFactoryMethod(parameters.toArray());

            log(String.valueOf(member));

            instance = factory.apply(member, arguments.toArray());

            log(String.valueOf(instance));

            if (getClass().isAssignableFrom(InstanceOfTask.class)) {
                ReaderWriterDataSourceImpl ds =
                    new ReaderWriterDataSourceImpl();

                try (XMLEncoder encoder =
                         new XMLEncoder(ds.getOutputStream())) {
                    encoder.setExceptionListener(ds);
                    encoder.writeObject(instance);
                    encoder.flush();

                    if (ds.length() > 0) {
                        log();
                        log(ds.getBufferedReader().lines());
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

    @NoArgsConstructor
    private class ClassList extends ArrayList<Class<?>> {
        private static final long serialVersionUID = -4504828433924386345L;

        @Override
        public Class<?>[] toArray() { return toArray(new Class<?>[] { }); }
    }

    private class ReaderWriterDataSourceImpl extends ReaderWriterDataSource
                                             implements ExceptionListener {
        public ReaderWriterDataSourceImpl() { super(null, null); }

        @Override
        public void exceptionThrown(Exception exception) { clear(); }
    }
}
