/*
 * $Id$
 *
 * Copyright 2015 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.beans.PropertyDescriptorsTableModel;
import ball.swing.table.SimpleTableModel;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ClassUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.ClasspathUtils;

import static ball.lang.Punctuation.GT;
import static ball.lang.Punctuation.LT;
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
                                          ClasspathDelegateAntTask {
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
            log(EMPTY);
            log(new TableModelImpl(bean.getPropertyDescriptors()));
            log(bean.getAdditionalBeanInfo());
        }

        private void log(BeanInfo[] beans) {
            if (beans != null) {
                for (BeanInfo bean : beans) {
                    log(EMPTY);
                    log(bean);
                }
            }
        }

        private class BeanHeaderTableModel extends SimpleTableModel {
            private static final long serialVersionUID = 2338583773511453873L;

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
                    + (supertype.isAssignableFrom(subtype) ? "" : "not ")
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

                log(String.valueOf(type));

                for (Constructor<?> constructor :
                         type.getDeclaredConstructors()) {
                    log(constructor.toGenericString());
                }

                for (Field field : type.getDeclaredFields()) {
                    log(field.toGenericString());
                }

                for (Method method : type.getDeclaredMethods()) {
                    log(method.toGenericString());
                }

                for (Class<?> cls : type.getDeclaredClasses()) {
                    log(cls.toString());
                }
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
                    log(toString(superclass));
                }
            } catch (BuildException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new BuildException(throwable);
            }
        }

        private String toString(Class<?> type) {
            StringBuilder buffer = new StringBuilder(type.getName());

            if (type.getTypeParameters().length > 0) {
                buffer.append(LT.lexeme());

                for (TypeVariable<?> parameter : type.getTypeParameters()) {
                    buffer.append(parameter);
                }

                buffer.append(GT.lexeme());
            }

            return buffer.toString();
        }
    }
}
