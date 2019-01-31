/*
 * $Id$
 *
 * Copyright 2011 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs.compilers;

import ball.io.CharSequenceReader;
import ball.tools.DiagnosticMap;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterExtension;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

/**
 * {@link CompilerAdapter} implementation for the system
 * {@link JavaCompiler}.  Subclass implementors will likely only need to
 * override {@link #compile(StandardJavaFileManager,DiagnosticMap)}.
 *
 * @see ToolProvider#getSystemJavaCompiler()
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class SystemJavaCompilerAdapter implements CompilerAdapter,
                                                  CompilerAdapterExtension {
    /** {@link #CARET} = {@value #CARET} */
    protected static final String CARET = "^";
    /** {@link #SPACE} = {@value #SPACE} */
    protected static final String SPACE = " ";

    private Javac javac = null;
    protected JavaCompiler compiler = null;
    protected JavaCompiler.CompilationTask task = null;

    /**
     * Sole constructor.
     */
    public SystemJavaCompilerAdapter() { }

    protected Javac getJavac() { return javac; }

    @Override
    public void setJavac(Javac javac) {
        this.javac = javac;

        javac.add(this);
        javac.setCompiler("modern");
    }

    @Override
    public boolean execute() throws BuildException {
        boolean success = false;
        DiagnosticMap map = new DiagnosticMap();

        compiler =
            Objects.requireNonNull(ToolProvider.getSystemJavaCompiler(),
                                   "No system Java compiler");

        Locale locale = null;
        Charset charset = null;

        if (getJavac().getEncoding() != null) {
            charset = Charset.forName(getJavac().getEncoding());
        }

        try (StandardJavaFileManager fm =
                 compiler.getStandardFileManager(map, locale, charset)) {
            fm.setLocation(StandardLocation.PLATFORM_CLASS_PATH,
                           asFileList(getJavac().getBootclasspath()));
            fm.setLocation(StandardLocation.CLASS_PATH,
                           asFileList(getJavac().getClasspath()));
            fm.setLocation(StandardLocation.CLASS_OUTPUT,
                           asFileList(getJavac().getDestdir()));
            fm.setLocation(StandardLocation.SOURCE_PATH,
                           asFileList(getJavac().getSrcdir()));

            success = compile(fm, map);
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throw new BuildException(throwable);
        } finally {
            log(map);
        }

        return success;
    }

    /**
     * Method to do the compilation work.  Subclass implementations should
     * override this method by first chaining back to the superclass
     * implementation.
     * <p>
     * The {@link StandardJavaFileManager} parameter will have its
     * {@link StandardLocation#PLATFORM_CLASS_PATH} set to
     * {@link Javac#getBootclasspath()},
     * {@link StandardLocation#CLASS_PATH} set to
     * {@link Javac#getClasspath()}, {@link StandardLocation#CLASS_OUTPUT}
     * set to {@link Javac#getDestdir()}, and
     * {@link StandardLocation#SOURCE_PATH} set to
     * {@link Javac#getSrcdir()}.
     *
     * @param   fm              The {@link StandardJavaFileManager}.
     * @param   map             The {@link DiagnosticMap}.
     *
     * @return  {@code true} if the compilation was successful;
     *          {@code false} otherwise.
     *
     * @throws  Throwable       If any problems are encountered in the
     *                          compilation.
     */
    protected boolean compile(StandardJavaFileManager fm,
                              DiagnosticMap map) throws Throwable {
        List<String> options =
            Arrays.asList(getJavac().getCurrentCompilerArgs());
        Iterable<? extends JavaFileObject> objects =
            fm.getJavaFileObjects(getJavac().getFileList());

        task = compiler.getTask(null, fm, map, options, null, objects);

        return task.call();
    }

    /**
     * Method to convert the argument {@link Path} to a {@link File}
     * {@link List}.
     *
     * @param   path            The {@link Path}.
     *
     * @return  The {@link Path} as a {@link File} {@link List} or
     *          {@code null} if the argument {@link Path} is {@code null}.
     */
    protected List<? extends File> asFileList(Path path) {
        File[] files = null;

        if (path != null) {
            File base = getJavac().getProject().getBaseDir();
            String[] paths = path.list();

            files = new File[paths.length];

            for (int i = 0; i < files.length; i += 1) {
                files[i] =
                    FileUtils.getFileUtils().resolveFile(base, paths[i]);
            }
        }

        return asFileList(files);
    }

    /**
     * Method to convert the argument {@link File} array to a {@link File}
     * {@link List}.
     *
     * @param   files           The {@link File} array.
     *
     * @return  The {@link File} array as a {@link File} {@link List} or
     *          {@code null} if the argument {@link File} array is
     *          {@code null}.
     */
    protected List<? extends File> asFileList(File... files) {
        return (files != null) ? Arrays.asList(files) : null;
    }

    /**
     * See {@link Javac#log(String)}.
     *
     * @param   string          The {@link String} to log.
     */
    protected void log(String string) { getJavac().log(string); }

    private void log(DiagnosticMap map) {
        for (Map.Entry<Diagnostic<? extends JavaFileObject>,String> entry :
                 map.entrySet()) {
            Diagnostic<? extends JavaFileObject> diagnostic = entry.getKey();
            String remedy = entry.getValue();

            log(String.valueOf(diagnostic));

            if (diagnostic.getPosition() != Diagnostic.NOPOS) {
                String source = source(diagnostic);

                if (source != null) {
                    if (String.valueOf(diagnostic).indexOf(source) < 0) {
                        log(source);
                        log(pointer(diagnostic));
                    }
                }
            }

            if (remedy != null) {
                log(remedy);
            }
        }

        for (Map.Entry<Diagnostic.Kind,Integer> entry :
                 map.getKindCountMap().entrySet()) {
            String kind = entry.getKey().toString().toLowerCase();
            int count = entry.getValue();

            log(count + SPACE + kind + ((count == 1) ? "" : "s"));
        }
    }

    private String source(Diagnostic<? extends JavaFileObject> diagnostic) {
        String line = null;

        try (CharSequenceReader reader =
                 new CharSequenceReader(diagnostic.getSource()
                                        .getCharContent(true))) {
            do {
                line = reader.readLine();
            } while (line != null
                     && reader.getLineNumber() < diagnostic.getLineNumber());
        } catch (IOException exception) {
        }

        return line;
    }

    private String pointer(Diagnostic<?> diagnostic) {
        StringBuilder buffer = new StringBuilder(CARET);

        while (buffer.length() < diagnostic.getColumnNumber()) {
            buffer.insert(0, SPACE);
        }

        return buffer.toString();
    }

    @Override
    public String[] getSupportedFileExtensions() {
        return new String[] { "java" };
    }
}
