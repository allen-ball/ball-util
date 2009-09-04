/*
 * $Id: MultipartReport.java,v 1.2 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.util.Collection;
import java.util.LinkedHashSet;
import javax.activation.DataSource;

/**
 * Multipart {@link Report} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class MultipartReport extends Report {
    private final LinkedHashSet<DataSource> set =
        new LinkedHashSet<DataSource>();

    /**
     * @see Report#Report()
     */
    public MultipartReport() { super(); }

    /**
     * Method to attach a {@link DataSource} to this report.
     *
     * @param   attachment      The {@link DataSource} to attach.
     */
    public void attach(DataSource attachment) { set.add(attachment); }

    /**
     * Method to attach a {@link Collection} of {@link DataSource} objects
     * to this report.
     *
     * @param   collection      The {@link Collection} of {@link DataSource}
     *                          objects to attach.
     */
    public void attachAll(Collection<DataSource> collection) {
        set.addAll(collection);
    }

    /**
     * Method to get the {@link java.util.Set} of attachment
     * ({@link DataSource}) objects.
     *
     * @return  The {@link LinkedHashSet} of attachments.
     */
    public LinkedHashSet<DataSource> getAttachmentSet() { return set; }
}
/*
 * $Log: not supported by cvs2svn $
 */
