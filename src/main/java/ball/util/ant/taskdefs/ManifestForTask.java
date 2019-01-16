/*
 * $Id$
 *
 * Copyright 2015 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Manifest;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tools.ant.BuildException;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to locate a {@link Manifest} for a {@link Class}.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("manifest-for")
@NoArgsConstructor @ToString
public class ManifestForTask extends TypeTask {
    private static final String _CLASS = ".class";

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            Class<?> type = getClassForName(getType());

            log(type.getCanonicalName());

            URL url = getManifestURLFor(type);

            log(url.toString());

            Manifest manifest = getManifestFor(type);

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

    /**
     * Method to get {@link Manifest} for a {@link Class}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link Manifest} if the {@code MANIFEST.MF} file is
     *          located and parsed; {@code null} otherwise.
     */
    protected static Manifest getManifestFor(Class<?> type) {
        Manifest manifest = null;
        URL url = getManifestURLFor(type);

        try (InputStream in = url.openStream()) {
            manifest = new Manifest(in);
        } catch (IOException exception) {
        }

        return manifest;
    }

    /**
     * Method to locate the {@code MAINFEST.MF} for a {@link Class}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link URL} if the {@code MANIFEST.MF} file is located;
     *          {@code null} otherwise.
     */
    protected static URL getManifestURLFor(Class<?> type) {
        URL url = null;
        String resource = type.getSimpleName() + _CLASS;
        String path = type.getResource(resource).toString();

        path =
            path.substring(0,
                           path.length()
                           - (type.getCanonicalName().length()
                              + _CLASS.length()))
            + "META-INF/MANIFEST.MF";

        try {
            url = new URL(path);
        } catch (MalformedURLException exception) {
        }

        return url;
    }
}
