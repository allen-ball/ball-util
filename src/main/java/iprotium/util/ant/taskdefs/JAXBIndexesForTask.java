/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.io.Directory;
import iprotium.io.IOUtil;
import iprotium.util.ClassOrder;
import iprotium.util.ClassUtil;
import iprotium.util.PackageOrder;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to generate jaxb.index files from
 * {@link Class}es annotated with {@link XmlRootElement}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@AntTask("jaxb-indexes-for")
public class JAXBIndexesForTask extends AbstractClassFileTask {
    private static final String JAXB_INDEX ="jaxb.index";

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private File destdir = null;

    /**
     * Sole constructor.
     */
    public JAXBIndexesForTask() { super(); }

    protected File getDestdir() { return destdir; }
    public void setDestdir(File destdir) { this.destdir = destdir; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (getBasedir() == null) {
            setBasedir(getProject().resolveFile("."));
        }

        if (getDestdir() == null) {
            setDestdir(getBasedir());
        }

        try {
            MapImpl map = new MapImpl();

            for (Class<?> type : getClassSet()) {
                if (! ClassUtil.isAbstract(type)) {
                    if (type.getAnnotation(XmlRootElement.class) != null) {
                        map.add(type);
                    }
                }
            }

            Directory destdir = Directory.asDirectory(getDestdir());

            for (Map.Entry<Package,Set<Class<?>>> entry : map.entrySet()) {
                String[] names = entry.getKey().getName().split("[.]");
                Directory parent = destdir.getChildDirectory(names);

                generate(parent, entry.getValue());
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }

    private void generate(Directory parent,
                          Set<Class<?>> set) throws IOException {
        if (! set.isEmpty()) {
            PrintWriter out = null;

            try {
                IOUtil.mkdirs(parent);

                out =
                    new PrintWriter(parent.getChildFile(JAXB_INDEX),
                                    CHARSET.name());
                out.println("# " + JAXB_INDEX);

                for (Class<?> type : set) {
                    out.println(type.getSimpleName());
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

    private class MapImpl extends TreeMap<Package,Set<Class<?>>> {
        private static final long serialVersionUID = -7248953670128074369L;

        public MapImpl() { super(PackageOrder.INSTANCE); }

        public boolean add(Class<?> type) {
            return get(type.getPackage()).add(type);
        }

        @Override
        public Set<Class<?>> get(Object key) {
            if (! super.containsKey(key)) {
                super.put((Package) key,
                          new TreeSet<Class<?>>(ClassOrder.NAME));
            }

            return super.get(key);
        }
    }
}
