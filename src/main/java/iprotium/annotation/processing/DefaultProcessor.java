/*
 * $Id$
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.beans.ConvertWith;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 * Default {@link javax.annotation.processing.Processor} implementation to
 * claim annotations.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@SupportedAnnotationTypes({ "java.beans.ConstructorProperties" })
public class DefaultProcessor extends AbstractProcessor {

    /**
     * Sole constructor.
     */
    public DefaultProcessor() { super(); }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        TreeSet<String> set =
            new TreeSet<String>(super.getSupportedAnnotationTypes());

        set.add(ConvertWith.class.getCanonicalName());

        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        return true;
    }
}
