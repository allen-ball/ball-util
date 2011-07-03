/*
 * $Id$
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.io.Directory;
import iprotium.io.IOUtil;
import iprotium.util.ClassOrder;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
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

    private String service = null;
    private File destdir = null;

    /**
     * Sole constructor.
     */
    public ProviderFileForTask() { super(); }

    protected String getService() { return service; }
    public void setService(String service) { this.service = service; }

    protected File getDestdir() { return destdir; }
    public void setDestdir(File destdir) { this.destdir = destdir; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (getService() == null) {
            throw new BuildException("`service' attribute must be specified");
        }

        if (getBasedir() == null) {
            setBasedir(getProject().resolveFile("."));
        }

        if (getDestdir() == null) {
            setDestdir(getBasedir());
        }

        PrintWriter out = null;

        try {
            Class<?> service = getClass(getService());
            TreeSet<Class<?>> set = new TreeSet<Class<?>>(ClassOrder.NAME);

            for (Class<?> type : getMatchingClassFileMap().values()) {
                if (service.isAssignableFrom(type)) {
                    if (! isAbstract(type)) {
                        set.add(type);
                    }
                }
            }

            Directory parent =
                Directory.getChildDirectory(getDestdir(),
                                            "META-INF", "services");

            IOUtil.mkdirs(parent);

            out =
                new PrintWriter(parent.getChildFile(service.getName()),
                                CHARSET.name());

            for (Class<?> provider : set) {
                out.println(provider.getName());
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BuildException(exception);
        } finally {
            try {
                IOUtil.close(out);
            } finally {
                out = null;
            }
        }
    }
}
