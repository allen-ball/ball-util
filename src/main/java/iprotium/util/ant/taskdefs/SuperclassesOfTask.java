/*
 * $Id: SuperclassesOfTask.java,v 1.1 2008-10-27 00:10:03 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.util.TreeSet;
import org.apache.tools.ant.BuildException;

import static iprotium.util.ClassOrder.INHERITANCE;
import static iprotium.util.ClassOrder.NAME;

/**
 * Ant Task to display superclasses of a specified Class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class SuperclassesOfTask extends AbstractClasspathTask {
    private String name = null;

    /**
     * Sole constructor.
     */
    public SuperclassesOfTask() { super(); }

    protected String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public void execute() throws BuildException {
        try {
            if (getName() == null) {
                throw new BuildException("`name' attribute must be specified");
            }

            Class<?> type =
                Class.forName(getName(), false, delegate.getClassLoader());

            for (Object object : INHERITANCE.asList(new SuperclassSet(type))) {
                log(String.valueOf(object));
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

    private class SuperclassSet extends TreeSet<Class<?>> {
        private static final long serialVersionUID = -4406313542581593672L;

        public SuperclassSet(Class<?> type) {
            super(NAME);

            addSuperclassesOf(type);
        }

        private void addSuperclassesOf(Class<?> type) {
            if (type != null) {
                if (! contains(type)) {
                    add(type);

                    addSuperclassesOf(type.getSuperclass());
                    addSuperclassesOf(type.getInterfaces());
                }
            }
        }

        private void addSuperclassesOf(Class<?>[] types) {
            for (Class<?> type : types) {
                addSuperclassesOf(type);
            }
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
