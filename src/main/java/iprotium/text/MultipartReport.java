/*
 * $Id: MultipartReport.java,v 1.1 2009-03-30 06:28:12 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.util.Collection;
import java.util.LinkedHashSet;
import javax.activation.DataSource;

/**
 * Multipart Report implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class MultipartReport extends Report {
    private final LinkedHashSet<DataSource> set =
        new LinkedHashSet<DataSource>();

    /**
     * @see Report#Report()
     */
    public MultipartReport() { super(); }

    /**
     * Method to attach a DataSource to this report.
     *
     * @param   attachment      The DataSource to attach.
     */
    public void attach(DataSource attachment) { set.add(attachment); }

    /**
     * Method to attach a Collection of DataSource objects to this report.
     *
     * @param   collection      The Collection of DataSource objects to
     *                          attach.
     */
    public void attachAll(Collection<DataSource> collection) {
        set.addAll(collection);
    }

    /**
     * Method to get the Set of attachment (DataSource) objects.
     *
     * @return  The LinkedHashSet of attachments.
     */
    public LinkedHashSet<DataSource> getAttachmentSet() { return set; }
}
/*
 * $Log: not supported by cvs2svn $
 */
