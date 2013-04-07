/*
 * $Id$
 *
 * Copyright 2011 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.io.Directory;
import iprotium.io.IOUtil;
import iprotium.util.ClassOrder;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;

import static iprotium.util.ClassUtil.isAbstract;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to generate a service provider
 * configuration file.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class ProviderFileForTask extends AbstractClassFileTask {
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private File destdir = null;
    private LinkedHashSet<String> set = new LinkedHashSet<String>();

    /**
     * Sole constructor.
     */
    public ProviderFileForTask() { super(); }

    protected File getDestdir() { return destdir; }
    public void setDestdir(File destdir) { this.destdir = destdir; }

    protected Set<String> getServiceSet() { return set; }
    public void setService(String name) { set.add(name); }
    public void addConfiguredService(Service service) {
        set.add(service.getName());
    }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (getServiceSet().isEmpty()) {
            throw new BuildException("`service' attribute must be specified");
        }

        if (getBasedir() == null) {
            setBasedir(getProject().resolveFile("."));
        }

        if (getDestdir() == null) {
            setDestdir(getBasedir());
        }

        Directory parent =
            Directory.getChildDirectory(getDestdir(), "META-INF", "services");

        try {
            for (String name : getServiceSet()) {
                Class<?> service = null;
                TreeSet<Class<?>> set = new TreeSet<Class<?>>(ClassOrder.NAME);

                for (Class<?> type : getClassSet()) {
                    if (service == null) {
                        service =
                            Class.forName(name, false, type.getClassLoader());
                    }

                    if (service.isAssignableFrom(type)) {
                        if (! isAbstract(type)) {
                            set.add(type);
                        }
                    }
                }

                generate(parent, service, set);
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

    private void generate(Directory parent,
                          Class<?> service,
                          Collection<Class<?>> providers) throws IOException {
        if (service != null && (! providers.isEmpty())) {
            PrintWriter out = null;

            try {
                IOUtil.mkdirs(parent);

                out =
                    new PrintWriter(parent.getChildFile(service.getName()),
                                    CHARSET.name());
                out.println("# " + service.getName());

                for (Class<?> provider : providers) {
                    out.println(provider.getName());
                }
            } finally {
                try {
                    IOUtil.close(out);
                } finally {
                    out = null;
                }
            }
        }
    }

    /**
     * {@link ProviderFileForTask} service specification.
     */
    public static class Service {
        private String name = null;

        /**
         * Sole constructor.
         */
        public Service() { }

        protected String getName() { return name; }
        public void setName(String name) { this.name = name; }

        /**
         * See {@link #setName(String)}.
         */
        public void addText(String name) {
            if (getName() != null) {
                name = getName() + name;
            }

            setName(name.trim());
        }

        @Override
        public String toString() { return getName(); }
    }
}
