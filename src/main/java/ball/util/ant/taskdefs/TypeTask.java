/*
 * $Id$
 *
 * Copyright 2015 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.swing.table.ArrayListTableModel;
import ball.swing.table.SimpleTableModel;
import ball.util.BeanInfoUtil;
import ball.util.SuperclassSet;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.util.Arrays;
import org.apache.tools.ant.BuildException;

import static ball.lang.Punctuation.GT;
import static ball.lang.Punctuation.LT;
import static ball.util.ClassOrder.INHERITANCE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Abstract {@link.uri http://ant.apache.org/ Ant}
 * {@link org.apache.tools.ant.Task} to specify a type ({@link Class}).
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class TypeTask extends AbstractClasspathTask {
    private String type = null;

    /**
     * Sole constructor.
     */
    protected TypeTask() { super(); }

    @NotNull
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to display {@link BeanInfo}
     * for a specified {@link Class}.
     *
     * {@bean.info}
     */
    @AntTask("bean-info-for")
    public static class BeanInfoFor extends TypeTask {

        /**
         * Sole constructor.
         */
        public BeanInfoFor() { super(); }

        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                log(Introspector.getBeanInfo(Class.forName(getType(),
                                                           false,
                                                           getClassLoader())));
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
            log(new BeanPropertyTableModel(bean.getPropertyDescriptors()));
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

        private class BeanPropertyTableModel
                      extends ArrayListTableModel<PropertyDescriptor> {
            private static final long serialVersionUID = -5252202218618591258L;

            public BeanPropertyTableModel(PropertyDescriptor[] rows) {
                super(Arrays.asList(rows),
                      "Name", "Mode", "Type",
                      "isHidden", "isBound", "isConstrained");
            }

            @Override
            protected Object getValueAt(PropertyDescriptor row, int x) {
                Object value = null;

                switch (x) {
                case 0:
                default:
                    value = row.getName();
                    break;

                case 1:
                    value = BeanInfoUtil.getMode(row);
                    break;

                case 2:
                    value = getCanonicalName(row.getPropertyType());
                    break;

                case 3:
                    value = row.isHidden();
                    break;

                case 4:
                    value = row.isBound();
                    break;

                case 5:
                    value = row.isConstrained();
                    break;
                }

                return value;
            }

            private String getCanonicalName(Class<?> type) {
                return (type != null) ? type.getCanonicalName() : EMPTY;
            }
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to display superclasses of a
     * specified {@link Class}.
     *
     * {@bean.info}
     */
    @AntTask("is-assignable-from")
    public static class IsAssignableFrom extends TypeTask {
        private String subtype = null;

        /**
         * Sole constructor.
         */
        public IsAssignableFrom() { super(); }

        @NotNull
        public String getSubtype() { return subtype; }
        public void setSubtype(String subtype) { this.subtype = subtype; }

        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Class<?> supertype =
                    Class.forName(getType(), false, getClassLoader());
                Class<?> subtype =
                    Class.forName(getSubtype(),
                                  false, supertype.getClassLoader());

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
     * {@bean.info}
     */
    @AntTask("members-of")
    public static class MembersOf extends TypeTask {

        /**
         * Sole constructor.
         */
        public MembersOf() { super(); }

        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Class<?> type =
                    Class.forName(getType(), false, getClassLoader());

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
     * {@bean.info}
     */
    @AntTask("resource-path-to")
    public static class ResourcePathTo extends TypeTask {

        /**
         * Sole constructor.
         */
        public ResourcePathTo() { super(); }

        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Class<?> type =
                    Class.forName(getType(), false, getClassLoader());

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
     * {@bean.info}
     */
    @AntTask("superclasses-of")
    public static class SuperclassesOf extends TypeTask {

        /**
         * Sole constructor.
         */
        public SuperclassesOf() { super(); }

        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Class<?> type =
                    Class.forName(getType(), false, getClassLoader());

                for (Class<?> superclass :
                         INHERITANCE.asList(new SuperclassSet(type))) {
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
