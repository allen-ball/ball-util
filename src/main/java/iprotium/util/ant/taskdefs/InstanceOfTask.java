/*
 * $Id: InstanceOfTask.java,v 1.5 2009-01-27 22:00:19 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.util.BeanMap;
import iprotium.util.Factory;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to get an instance of a specified Class.
 *
 * @see Factory
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.5 $
 */
public class InstanceOfTask extends AbstractClasspathTask {
    private String type = String.class.getName();
    private List<Argument> list = new ArrayList<Argument>();

    /**
     * Sole constructor.
     */
    public InstanceOfTask() { super(); }

    protected String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public void addConfiguredArgument(Argument argument) { list.add(argument); }
    protected List<Argument> getArgumentList() { return list; }
    public void setArgument(String string) { list.add(new Argument(string)); }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            Class<?> type = getClass(getType());

            log(type.getName());

            List<Class> parameters = new ArrayList<Class>();
            List<Object> arguments = new ArrayList<Object>();

            for (Argument argument : list) {
                Factory<Object> factory =
                    new Factory<Object>(getClass(argument.getType()));

                parameters.add(factory.getType());
                arguments.add(factory.getInstance(argument.getValue()));
            }

            log(parameters);
            log(arguments);

            Factory<Object> factory = new Factory<Object>(type);
            Member member = factory.getFactoryMember(toArray(parameters));

            log(member);

            Object instance = factory.apply(member, toArray(arguments));

            log(instance);

            BeanMap map = BeanMap.asBeanMap(instance);

            if (! map.isEmpty()) {
                log("");
                log("Property Name", "Value");

                for (Map.Entry<String,Object> entry : map.entrySet()) {
                    log(entry.getKey(), entry.getValue());
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

    private Class[] toArray(Collection<Class> collection) {
        return collection.toArray(new Class[] { });
    }

    private Object[] toArray(Collection<Object> collection) {
        return collection.toArray(new Object[] { });
    }

    public static class Argument {
        private String type = String.class.getName();
        private String value = null;

        public Argument(String value) { this.value = value; }

        public Argument() { this(null); }

        protected String getType() { return type; }
        public void setType(String type) { this.type = type; }

        protected String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        @Override
        public String toString() { return getValue(); }
    }
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2008/12/01 01:49:50  ball
 * Use BeanMap.asBeanMap(Object) API.
 *
 * Revision 1.2  2008/11/29 06:16:48  ball
 * Display instance bean property names and values.
 *
 */
