/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.xml.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.text.MapTableModel;
import iprotium.text.TextTable;
import iprotium.util.BeanMap;
import iprotium.util.ant.taskdefs.AbstractClasspathTask;
import java.io.File;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import org.apache.tools.ant.BuildException;

import static iprotium.util.StringUtil.NIL;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to get an instance of a specified
 * {@link Class}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@AntTask("jaxb-unmarshal")
public class JAXBUnmarshalTask extends AbstractClasspathTask {
    private String context = null;
    private File file = null;

    /**
     * Sole constructor.
     */
    public JAXBUnmarshalTask() { super(); }

    protected String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    protected File getFile() { return file; }
    public void setFile(File file) { this.file = file; }

    @Override
    public void execute() throws BuildException {
        if (getContext() == null) {
            throw new BuildException("`context' attribute must be specified");
        }

        if (getFile() == null) {
            throw new BuildException("`file' attribute must be specified");
        }

        try {
            Object object =
                JAXBContext.newInstance(getContext(), getClassLoader())
                .createUnmarshaller()
                .unmarshal(getFile());

            log(String.valueOf(object));

            BeanMap map = BeanMap.asBeanMap(object);

            if (! map.isEmpty()) {
                log(NIL);
                log(new TextTable(new MapTableModelImpl(map)));
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

    private class MapTableModelImpl extends MapTableModel<String,Object> {
        private static final long serialVersionUID = -3462718921749259867L;

        public MapTableModelImpl(Map<String,Object> map) {
            super(map, "Property Name", "Value");
        }

        @Override
        protected Object getValueAt(Map.Entry<String,Object> row, int x) {
            return String.valueOf(super.getValueAt(row, x));
        }
    }
}