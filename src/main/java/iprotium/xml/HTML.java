/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.xml;

import java.net.URI;
import javax.swing.table.TableModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static iprotium.util.StringUtil.isNil;

/**
 * Static methods to create {@link HTML} {@link Element}s.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class HTML {
    private HTML() { }

    /**
     * Method to create an HTML {@code <a/>} {@link Element}.
     *
     * @param   document        The containing {@link Document}.
     * @param   text            The {@link Element} text.
     * @param   href            The href {@link org.w3c.dom.Attribute}
     *                          value.
     *
     * @return  The HTML {code <a/>} {@link Element}.
     */
    public static Element a(Document document, String text, URI href) {
        Element a = document.createElement("a");

        a.setAttribute("href", href.toASCIIString());
        a.setTextContent((! isNil(text)) ? text : href.toString());

        return a;
    }

    /**
     * Method to create an HTML {@code <table/>} {@link Element}.
     *
     * @param   document        The containing {@link Document}.
     * @param   model           The {@link TableModel}.
     *
     * @return  The HTML {@code <table/>} {@link Element}.
     */
    public static Element table(Document document, TableModel model) {
        Element table = document.createElement("table");
        Element header = document.createElement("tr");

        for (int x = 0; x < model.getColumnCount(); x += 1) {
            Element td = document.createElement("td");
            Element b = document.createElement("b");

            b.setTextContent(String.valueOf(model.getColumnName(x)));
            td.appendChild(b);
            header.appendChild(td);
        }

        table.appendChild(header);

        for (int y = 0; y < model.getRowCount(); y += 1) {
            Element tr = document.createElement("tr");

            for (int x = 0; x < model.getColumnCount(); x += 1) {
                Element td = document.createElement("td");

                td.setTextContent(String.valueOf(model.getValueAt(y, x)));
                tr.appendChild(td);
            }

            table.appendChild(tr);
        }

        return table;
    }
}
