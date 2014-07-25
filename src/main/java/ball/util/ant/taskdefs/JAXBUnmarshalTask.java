/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.annotation.AntTask;
import ball.text.MapTableModel;
import ball.text.TextTable;
import ball.util.BeanMap;
import java.io.File;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import org.apache.tools.ant.BuildException;

import static ball.util.StringUtil.NIL;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to get an instance of a specified {@link Class}.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
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