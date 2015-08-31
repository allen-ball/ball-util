/*
 * $Id$
 *
 * Copyright 2015 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.ClassUtil;
import java.net.URL;
import java.util.jar.Manifest;
import org.apache.tools.ant.BuildException;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to locate a {@link Manifest} for a {@link Class}.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("manifest-for")
public class ManifestForTask extends TypeTask {

    /**
     * Sole constructor.
     */
    public ManifestForTask() { super(); }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            Class<?> type = Class.forName(getType(), false, getClassLoader());

            log(type.getCanonicalName());

            URL url = ClassUtil.getManifestURLFor(type);

            log(url.toString());

            Manifest manifest = ClassUtil.getManifestFor(type);

            if (manifest != null) {
                manifest.write(System.out);
            } else {
                log("Could not load " + url);
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }
}
