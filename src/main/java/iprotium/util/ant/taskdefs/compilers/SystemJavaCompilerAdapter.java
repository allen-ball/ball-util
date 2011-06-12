/*
 * $Id: SystemJavaCompilerAdapter.java,v 1.2 2011-06-12 21:21:58 ball Exp $
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs.compilers;

import iprotium.io.CharSequenceReader;
import iprotium.io.IOUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;
import java.util.TreeMap;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
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
 * {@link JavaCompiler}.
 *
 * @see ToolProvider#getSystemJavaCompiler()
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class SystemJavaCompilerAdapter
             implements CompilerAdapter, CompilerAdapterExtension,
                        DiagnosticListener<JavaFileObject> {
    private static final String CARAT = "^";
    private static final String SPACE = " ";

    private Javac task = null;
    private JavaCompiler compiler = null;
    private StandardJavaFileManager fm = null;
    private DiagnosticKindCountMap map = null;

    /**
     * Sole constructor.
     */
    public SystemJavaCompilerAdapter() { }

    @Override
    public void setJavac(Javac task) throws BuildException {
        this.task = task;

        if (compiler == null) {
            compiler = ToolProvider.getSystemJavaCompiler();

            if (compiler == null) {
                throw new BuildException("No system Java compiler");
            }
        }

        task.add(this);
        task.setCompiler("modern");
    }

    @Override
    public boolean execute() throws BuildException {
        boolean success = false;

        try {
            Locale locale = null;
            Charset charset = null;

            if (task.getEncoding() != null) {
                charset = Charset.forName(task.getEncoding());
            }

            map = new DiagnosticKindCountMap();

            fm = compiler.getStandardFileManager(this, locale, charset);
            fm.setLocation(StandardLocation.PLATFORM_CLASS_PATH,
                           asFileIterable(task, task.getBootclasspath()));
            fm.setLocation(StandardLocation.CLASS_PATH,
                           asFileIterable(task, task.getClasspath()));
            fm.setLocation(StandardLocation.CLASS_OUTPUT,
                           Arrays.asList(task.getDestdir()));

            Iterable<String> options =
                Arrays.asList(task.getCurrentCompilerArgs());
            Iterable<? extends JavaFileObject> objects =
                fm.getJavaFileObjects(task.getFileList());

            success =
                compiler
                .getTask(null, fm, this, options, null, objects)
                .call();

            for (Diagnostic.Kind key : map.keySet()) {
                int count = map.get(key);

                task.log(count
                         + SPACE
                         + key.toString().toLowerCase()
                         + ((count == 1) ? "" : "s"));
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throw new BuildException(throwable);
        } finally {
            try {
                IOUtil.close(fm);
            } finally {
                fm = null;
            }

            compiler = null;
        }

        return success;
    }

    private Iterable<? extends File> asFileIterable(Javac task, Path path) {
        File[] files = null;

        if (path != null) {
            File base = task.getProject().getBaseDir();
            String[] paths = path.list();

            files = new File[paths.length];

            for (int i = 0; i < files.length; i += 1) {
                files[i] =
                    FileUtils.getFileUtils().resolveFile(base, paths[i]);
            }
        }

        return (files != null) ? Arrays.asList(files) : null;
    }

    @Override
    public String[] getSupportedFileExtensions() {
        return new String[] { "java" };
    }

    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        map.count(diagnostic.getKind());
        task.log(String.valueOf(diagnostic));

        if (diagnostic.getPosition() != Diagnostic.NOPOS) {
            String source = source(diagnostic);

            if (source != null) {
                task.log(source);
                task.log(pointer(diagnostic));
            }
        }
    }

    private String source(Diagnostic<? extends JavaFileObject> diagnostic) {
        String line = null;
        CharSequenceReader reader = null;

        try {
            CharSequence sequence =
                diagnostic.getSource().getCharContent(false);

            reader = new CharSequenceReader(sequence);

            do {
                line = reader.readLine();
            } while (line != null
                     && reader.getLineNumber() < diagnostic.getLineNumber());
        } catch (IOException exception) {
        } finally {
            try {
                IOUtil.close(reader);
            } finally {
                reader = null;
            }
        }

        return line;
    }

    private String pointer(Diagnostic<?> diagnostic) {
        StringBuilder buffer = new StringBuilder(CARAT);

        while (buffer.length() < diagnostic.getColumnNumber()) {
            buffer.insert(0, SPACE);
        }

        return buffer.toString();
    }

    private static class DiagnosticKindCountMap
                         extends TreeMap<Diagnostic.Kind,Integer> {
        private static final long serialVersionUID = -2282739052913017696L;

        public DiagnosticKindCountMap() { super(); }

        public void count(Diagnostic.Kind key) { count(key, 1); }

        public void count(Diagnostic.Kind key, int count) {
            put(key, count + (containsKey(key) ? get(key) : 0));
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
