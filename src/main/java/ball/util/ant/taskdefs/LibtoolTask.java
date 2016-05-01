/*
 * $Id$
 *
 * Copyright 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.ant.types.OptionalTextType;
import ball.util.ant.types.StringAttributeType;
import ball.util.ant.types.StringValueType;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecuteOn;
import org.apache.tools.ant.types.Commandline;

/**
 * {@link.uri http://ant.apache.org/ Ant}
 * {@link org.apache.tools.ant.Task} to invoke {@man libtool(1)}.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class LibtoolTask extends ExecuteOn implements AnnotatedTask {
    private static final String OS = System.getProperty("os.name");

    private static final FileDirBoth FILE = new FileDirBoth();

    static { FILE.setValue(FileDirBoth.FILE); }

    /**
     * {@link #EQUALS} = {@value #EQUALS}
     */
    protected static final String EQUALS = "=";

    /**
     * {@link #SPACE} = {@value #SPACE}
     */
    protected static final String SPACE = " ";

    /**
     * {@link #SRCFILE} = {@value #SRCFILE}
     */
    protected static final String SRCFILE = "SRCFILE";

    /**
     * {@link #TARGETFILE} = {@value #TARGETFILE}
     */
    protected static final String TARGETFILE = "TARGETFILE";

    private String tag = null;
    private String mode = null;
    private String command = "gcc";
    private boolean shared = true;
    private boolean _static = false;
    private ArrayList<StringValueType> optionList =
        new ArrayList<StringValueType>();

    /**
     * Sole constructor.
     */
    protected LibtoolTask() {
        super();

        setMode(getClass().getSimpleName().toLowerCase());

        setFailonerror(true);
        setType(FILE);
        setVerbose(true);
    }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    @NotNull
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    @NotNull
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    /**
     * @{code -shared}
     */
    public boolean getShared() { return shared; }
    public void setShared(boolean shared) { this.shared = shared; }

    /**
     * @{code -static}
     */
    public boolean getStatic() { return _static; }
    public void setStatic(boolean _static) { this._static = _static; }

    public void addConfiguredOption(StringValueType option) {
        optionList.add(option);
    }

    protected String command() {
        StringBuilder buffer = new StringBuilder();

        buffer.append("libtool");

        if (getTag() != null) {
            buffer.append(SPACE).append("--tag=").append(getTag());
        }

        buffer
            .append(SPACE).append("--mode=").append(getMode())
            .append(SPACE).append(getCommand());

        for (StringValueType option : optionList) {
            buffer.append(SPACE).append(option.getValue());
        }

        if (getShared()) {
            buffer.append(SPACE).append("-shared");
        }

        if (getStatic()) {
            buffer.append(SPACE).append("-static");
        }

        return buffer.toString();
    }

    @Override
    public void execute() throws BuildException {
        validate();

        String command = getProject().replaceProperties(command());

        log(command);

        for (String argument : Commandline.translateCommandline(command)) {
            if (argument.equals(SRCFILE)) {
                createSrcfile();
            } else if (argument.equals(TARGETFILE)) {
                createTargetfile();
            } else {
                if (cmdl.getExecutable() == null) {
                    setExecutable(argument);
                } else {
                    createArg().setValue(argument);
                }
            }
        }

        super.execute();
    }

    @Override
    public String getAntTaskName() { return DELEGATE.getAntTaskName(this); }

    @Override
    public void validate() throws BuildException { DELEGATE.validate(this); }

    @Override
    public String toString() { return getClass().getSimpleName(); }

    /**
     * {@link.uri http://ant.apache.org/ Ant} {@link Task} to invoke
     * {@code "libtool --mode=compile"}.
     *
     * {@bean-info}
     */
    @AntTask("libtool-compile")
    public static class Compile extends LibtoolTask {
        private ArrayList<StringValueType> includeList =
            new ArrayList<StringValueType>();
        private LinkedHashSet<StringAttributeType> defineSet =
            new LinkedHashSet<StringAttributeType>();

        /**
         * Sole constructor.
         */
        public Compile() {
            super();

            setParallel(false);
        }

        public void addConfiguredInclude(StringValueType include) {
            includeList.add(include);
        }

        public void addConfiguredDefine(StringAttributeType definition) {
            defineSet.add(definition);
        }

        @Override
        protected String command() {
            StringBuilder buffer = new StringBuilder(super.command());

            for (StringValueType include : includeList) {
                buffer.append(SPACE).append("-I").append(include.getValue());
            }

            for (StringAttributeType define : defineSet) {
                if (define.isActive(getProject())) {
                    String name = define.getName();
                    String value = define.getValue();

                    buffer.append(SPACE).append("-D").append(name);

                    if (value != null) {
                        buffer.append(EQUALS).append(value);
                    }
                }
            }

            buffer
                .append(SPACE).append("-c").append(SPACE).append(SRCFILE)
                .append(SPACE).append("-o").append(SPACE).append(TARGETFILE);

            return buffer.toString();
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant} {@link Task} to invoke
     * {@code "libtool --mode=link"}.
     *
     * {@bean-info}
     */
    @AntTask("libtool-link")
    public static class Link extends LibtoolTask {
        private boolean module = true;
        private ArrayList<OptionalTextType> linkSet =
            new ArrayList<OptionalTextType>();
        private ArrayList<OptionalTextType> objectSet =
            new ArrayList<OptionalTextType>();

        /**
         * Sole constructor.
         */
        public Link() {
            super();

            setCommand("g++");

            if (OS.equalsIgnoreCase("Mac OS X")) {
                setCommand("g++ -dynamiclib");
            }

            setParallel(true);
        }

        /**
         * @{code -module}
         */
        public boolean getModule() { return module; }
        public void setModule(boolean module) { this.module = module; }

        public void addConfiguredLink(OptionalTextType library) {
            linkSet.add(library);
        }

        public void addConfiguredObject(OptionalTextType object) {
            objectSet.add(object);
        }

        @Override
        protected String command() {
            StringBuilder buffer = new StringBuilder(super.command());

            if (getModule()) {
                buffer.append(SPACE).append("-module");
            }

            for (OptionalTextType library : linkSet) {
                if (library.isActive(getProject())) {
                    buffer.append(SPACE).append("-l").append(library);
                }
            }

            for (OptionalTextType object : objectSet) {
                if (object.isActive(getProject())) {
                    if (new File(object.toString()).exists()) {
                        buffer.append(SPACE).append(object);
                    }
                }
            }

            buffer
                .append(SPACE).append(SRCFILE)
                .append(SPACE).append("-o").append(SPACE).append(TARGETFILE);

            return buffer.toString();
        }
    }
}
