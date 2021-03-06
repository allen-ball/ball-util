package ball.util.ant.taskdefs;
/*-
 * ##########################################################################
 * Utilities
 * %%
 * Copyright (C) 2008 - 2022 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import ball.activation.ReaderWriterDataSource;
import ball.util.Factory;
import ball.util.ant.types.TypedAttributeType;
import java.beans.ExceptionListener;
import java.beans.XMLEncoder;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
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

            List<Class<?>> parameters = new ArrayList<>(list.size());
            List<Object> arguments = new ArrayList<>(list.size());

            for (TypedAttributeType argument : list) {
                Factory<?> factory = new Factory<>(getClassForName(argument.getType()));

                parameters.add(factory.getType());

                String string = getProject().replaceProperties(argument.getValue());

                arguments.add(factory.getInstance(string));
            }

            log(String.valueOf(parameters));
            log(String.valueOf(arguments));

            Factory<?> factory = new Factory<>(type);
            Member member = factory.getFactoryMethod(parameters.stream().toArray(Class<?>[]::new));

            log(String.valueOf(member));

            instance = factory.apply(member, arguments.stream().toArray(Object[]::new));

            log(String.valueOf(instance));

            if (getClass().isAssignableFrom(InstanceOfTask.class)) {
                ReaderWriterDataSourceImpl ds = new ReaderWriterDataSourceImpl();

                try (XMLEncoder encoder = new XMLEncoder(ds.getOutputStream())) {
                    encoder.setExceptionListener(ds);
                    encoder.writeObject(instance);
                    encoder.flush();
                }

                if (ds.length() > 0) {
                    log();
                    log(ds.getBufferedReader().lines());
                }
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }

    private class ReaderWriterDataSourceImpl extends ReaderWriterDataSource implements ExceptionListener {
        public ReaderWriterDataSourceImpl() { super(null, null); }

        @Override
        public void exceptionThrown(Exception exception) { clear(); }
    }
}
