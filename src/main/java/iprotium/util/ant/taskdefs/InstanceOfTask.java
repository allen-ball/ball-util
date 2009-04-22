/*
 * $Id: InstanceOfTask.java,v 1.7 2009-04-22 04:29:19 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.text.MapTable;
import iprotium.util.BeanMap;
import iprotium.util.Factory;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to get an instance of a specified Class.
 *
 * @see Factory
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.7 $
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

    public void addConfiguredArgument(Argument argument) {
        list.add(argument);
    }

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

            log(String.valueOf(parameters));
            log(String.valueOf(arguments));

            Factory<Object> factory = new Factory<Object>(type);
            Member member =
                factory.getFactoryMember(parameters.toArray(new Class[] { }));

            log(String.valueOf(member));

            Object instance =
                factory.apply(member, arguments.toArray(new Object[] { }));

            log(String.valueOf(instance));

            BeanMap map = BeanMap.asBeanMap(instance);

            if (! map.isEmpty()) {
                MapTable table =
                    new MapTable<String,Object>(map, "Property Name", "Value");

                log("");

                for (String line : table) {
                    log(line);
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
 */
