/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.net.ResponseCacheImpl;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URLConnection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.ClasspathUtils;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link Task} to download resources
 * using {@link URLConnection} and {@link ResponseCacheImpl}.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("download")
@NoArgsConstructor @ToString
public class DownloadTask extends Task implements AnnotatedAntTask,
                                                  ClasspathDelegateAntTask,
                                                  ConfigurableAntTask {
    static {
        if (ResponseCache.getDefault() == null) {
            ResponseCache.setDefault(ResponseCacheImpl.DEFAULT);
        }
    }

    @Getter @Setter @Accessors(chain = true, fluent = true)
    private ClasspathUtils.Delegate delegate = null;
    @NotNull @Getter @Setter
    private URI uri = null;
    @NotNull @Getter @Setter
    private File toFile = null;

    @Override
    public void init() throws BuildException {
        super.init();
        ClasspathDelegateAntTask.super.init();
        ConfigurableAntTask.super.init();
    }

    @Override
    public void execute() throws BuildException {
        super.execute();
        AnnotatedAntTask.super.execute();

        try {
            URLConnection connection = getUri().toURL().openConnection();

            IOUtils.copy(connection.getInputStream(),
                         new FileOutputStream(getToFile()));

            log(getUri() + " --> " + getToFile());
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BuildException(exception);
        }
    }
}
