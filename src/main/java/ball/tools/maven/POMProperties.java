/*
 * $Id$
 *
 * Copyright 2018 Allen D. Ball.  All rights reserved.
 */
package ball.tools.maven;

import ball.util.PropertiesImpl;
import java.io.IOException;

/**
 * {@link java.util.Properties} subclass to load Maven coordinates from a
 * {@value #POM_PROPERTIES} resource.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class POMProperties extends PropertiesImpl {
    private static final long serialVersionUID = 2214652804604884165L;

    /**
     * {@link #POM_PROPERTIES} = {@value #POM_PROPERTIES}
     */
    public static final String POM_PROPERTIES = "pom.properties";

    /**
     * {@link #GROUP_ID} = {@value #GROUP_ID}
     */
    public static final String GROUP_ID = "groupId";

    /**
     * {@link #ARTIFACT_ID} = {@value #ARTIFACT_ID}
     */
    public static final String ARTIFACT_ID = "artifactId";

    /**
     * {@link #VERSION} = {@value #VERSION}
     */
    public static final String VERSION = "version";

    /**
     * {@include #RESOURCE}
     */
    public static final String RESOURCE =
        String.format("/META-INF/maven/%%s/%%s/%s", POM_PROPERTIES);

    private final String groupId;
    private final String artifactId;

    /**
     * Constructor to load default resource at {@link #RESOURCE}.
     *
     * @param   groupId         The coordinate group ID.
     * @param   artifactId      The coordinate artifact ID.
     *
     * @throws  IOException     If {@code resource} is not null and cannot be
     *                          read.
     */
    public POMProperties(String groupId,
                         String artifactId) throws IOException {
        super(null, String.format(RESOURCE, groupId, artifactId));

        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    /**
     * Method to get the group ID if it is specified.
     *
     * @return  The group ID.
     */
    public String getGroupId() { return getProperty(GROUP_ID, groupId); }

    /**
     * Method to get the artifact ID if it is specified.
     *
     * @return  The artifact ID.
     */
    public String getArtifactId() { return getProperty(ARTIFACT_ID, groupId); }

    /**
     * Method to get the version if it is specified.
     *
     * @return  The version.
     */
    public String getVersion() { return getProperty(VERSION); }
}
