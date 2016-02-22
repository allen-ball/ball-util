/*
 * $Id$
 *
 * Copyright 2013 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.swing.table.MapTableModel;
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

    @NotNull
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    @NotNull
    public File getFile() { return file; }
    public void setFile(File file) { this.file = file; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            Object object =
                JAXBContext.newInstance(getContext(), getClassLoader())
                .createUnmarshaller()
                .unmarshal(getFile());

            log(String.valueOf(object));

            BeanMap map = BeanMap.asBeanMap(object);

            if (! map.isEmpty()) {
                log(NIL);
                log(new MapTableModelImpl(map));
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }

    private class MapTableModelImpl extends MapTableModel {
        private static final long serialVersionUID = -6226621284459438053L;

        public MapTableModelImpl(Map<String,Object> map) {
            super(map, "Property Name", "Value");
        }

        @Override
        protected Object getValueAt(Object row, int x) {
            return String.valueOf(super.getValueAt(row, x));
        }
    }
}
