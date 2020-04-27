package ball.util.ant.taskdefs;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
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
import ball.beans.PropertyDescriptorsTableModel;
import ball.lang.reflect.JavaLangReflectMethods;
import ball.swing.table.SimpleTableModel;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ClassUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.ClasspathUtils;

import static lombok.AccessLevel.PROTECTED;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Abstract {@link.uri http://ant.apache.org/ Ant} {@link Task} to specify a
 * type ({@link Class}).
 *
 * {@ant.task}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class TypeTask extends Task
                               implements AnnotatedAntTask,
                                          ClasspathDelegateAntTask,
                                          JavaLangReflectMethods {
    @Getter @Setter @Accessors(chain = true, fluent = true)
    private ClasspathUtils.Delegate delegate = null;
    @NotNull @Getter
    private String type = null;

    public void setType(String string) {
        type = string;
        ClasspathDelegateAntTask.super.setClassname(type);
    }

    @Override
    public void setClassname(String string) { setType(string); }

    @Override
    public void init() throws BuildException {
        super.init();
        ClasspathDelegateAntTask.super.init();
    }

    @Override
    public void execute() throws BuildException {
        super.execute();
        AnnotatedAntTask.super.execute();
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to display {@link BeanInfo}
     * for a specified {@link Class}.
     *
     * {@ant.task}
     */
    @AntTask("bean-info-for")
    @NoArgsConstructor @ToString
    public static class BeanInfoFor extends TypeTask {
        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                log(Introspector.getBeanInfo(getClassForName(getType())));
            } catch (BuildException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new BuildException(throwable);
            }
        }

        private void log(BeanInfo bean) {
            log(new BeanHeaderTableModel(bean.getBeanDescriptor()));
            log();
            log(new TableModelImpl(bean.getPropertyDescriptors()));
            log(bean.getAdditionalBeanInfo());
        }

        private void log(BeanInfo[] beans) {
            if (beans != null) {
                for (BeanInfo bean : beans) {
                    log();
                    log(bean);
                }
            }
        }

        private class BeanHeaderTableModel extends SimpleTableModel {
            private static final long serialVersionUID = -6207300503623991416L;

            public BeanHeaderTableModel(BeanDescriptor descriptor) {
                super(new Object[][] { }, 2);

                row("Bean Class:",
                    descriptor.getBeanClass().getName());

                if (descriptor.getCustomizerClass() != null) {
                    row("Customizer Class:",
                        descriptor.getCustomizerClass().getName());
                }
            }
        }

        private class TableModelImpl extends PropertyDescriptorsTableModel {
            private static final long serialVersionUID = -5817972183666452609L;

            public TableModelImpl(PropertyDescriptor[] rows) { super(rows); }

            @Override
            public Object getValueAt(int y, int x) {
                Object value = super.getValueAt(y, x);

                if (value instanceof Class<?>) {
                    value = ((Class<?>) value).getCanonicalName();
                }

                return value;
            }
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to display superclasses of a
     * specified {@link Class}.
     *
     * {@ant.task}
     */
    @AntTask("is-assignable-from")
    @NoArgsConstructor @ToString
    public static class IsAssignableFrom extends TypeTask {
        @NotNull @Getter @Setter
        private String subtype = null;

        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Class<?> supertype = getClassForName(getType());
                Class<?> subtype = getClassForName(getSubtype());

                log(supertype.getName() + " is "
                    + (supertype.isAssignableFrom(subtype) ? EMPTY : "not ")
                    + "assignable from " + subtype.getName());
            } catch (BuildException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new BuildException(throwable);
            }
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to display members of a specified
     * {@link Class}.
     *
     * {@ant.task}
     */
    @AntTask("members-of")
    @NoArgsConstructor @ToString
    public static class MembersOf extends TypeTask {
        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Class<?> type = getClassForName(getType());

                log(type(type));
                Stream.of(type.getDeclaredClasses())
                    .forEach(t -> log(type(t)));
                Stream.of(type.getDeclaredFields())
                    .forEach(t -> log(declaration(t)));
                Stream.of(type.getDeclaredConstructors())
                    .forEach(t -> log(declaration(t)));
                Stream.of(type.getDeclaredMethods())
                    .forEach(t -> log(declaration(t)));
            } catch (BuildException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new BuildException(throwable);
            }
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to display resource path to a
     * specified {@link Class}.
     *
     * {@ant.task}
     */
    @AntTask("resource-path-to")
    @NoArgsConstructor @ToString
    public static class ResourcePathTo extends TypeTask {
        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Class<?> type = getClassForName(getType());

                log(String.valueOf(type));

                URL url =
                    type.getClass()
                    .getResource(type.getSimpleName() + ".class");

                log(String.valueOf(url));
            } catch (BuildException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new BuildException(throwable);
            }
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to display superclasses of a
     * specified {@link Class}.
     *
     * {@ant.task}
     */
    @AntTask("superclasses-of")
    @NoArgsConstructor @ToString
    public static class SuperclassesOf extends TypeTask {
        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                ArrayList<Class<?>> list = new ArrayList<>();
                Class<?> type = getClassForName(getType());

                list.add(type);
                list.addAll(ClassUtils.getAllSuperclasses(type));
                list.addAll(ClassUtils.getAllInterfaces(type));

                for (Class<?> superclass : list) {
                    log(type(superclass));
                }
            } catch (BuildException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new BuildException(throwable);
            }
        }
    }
}
