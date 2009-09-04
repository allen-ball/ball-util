/*
 * $Id: ENVContext.java,v 1.2 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.naming;

import java.util.Hashtable;
import java.util.Map;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

/**
 * JNDI {@link Context} implementation for environment variables.
 *
 * @see System#getenv()
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class ENVContext implements Context {
    private final Hashtable env;

    /**
     * Sole constructor.
     */
    public ENVContext(Hashtable env) { this.env = env; }

    public Object lookup(Name name) throws NamingException {
        return ((! name.toString().equals(""))
                    ? System.getenv(name.toString())
                    : this);
    }

    public Object lookup(String string) throws NamingException {
        return lookup(new CompositeName(string));
    }

    public void bind(Name name, Object object) throws NamingException {
        throw new OperationNotSupportedException();
    }

    public void bind(String string, Object object) throws NamingException {
        bind(new CompositeName(string), object);
    }

    public void rebind(Name name, Object object) throws NamingException {
        throw new OperationNotSupportedException();
    }

    public void rebind(String string, Object object) throws NamingException {
        rebind(new CompositeName(string), object);
    }

    public void unbind(Name name) throws NamingException {
        throw new OperationNotSupportedException();
    }

    public void unbind(String string) throws NamingException {
        unbind(new CompositeName(string));
    }

    public void rename(Name oldName, Name newName) throws NamingException {
        throw new OperationNotSupportedException();
    }

    public void rename(String oldName, String newName) throws NamingException {
        rename(new CompositeName(oldName), new CompositeName(newName));
    }

    public NamingEnumeration<NameClassPair> list(Name name)
            throws NamingException {
        throw new OperationNotSupportedException();
    }

    public NamingEnumeration<NameClassPair> list(String string)
            throws NamingException {
        return list(new CompositeName(string));
    }

    public NamingEnumeration<Binding> listBindings(Name name)
            throws NamingException {
        return new BindingEnumeration(System.getenv());
    }

    public NamingEnumeration<Binding> listBindings(String string)
            throws NamingException {
        return listBindings(new CompositeName(string));
    }

    public Context createSubcontext(Name name) throws NamingException {
        throw new OperationNotSupportedException();
    }

    public Context createSubcontext(String string) throws NamingException {
        return createSubcontext(new CompositeName(string));
    }

    public void destroySubcontext(Name name) throws NamingException {
        throw new OperationNotSupportedException();
    }

    public void destroySubcontext(String string) throws NamingException {
        destroySubcontext(new CompositeName(string));
    }

    public Object lookupLink(Name name) throws NamingException {
        throw new OperationNotSupportedException();
    }

    public Object lookupLink(String string) throws NamingException {
        return lookupLink(new CompositeName(string));
    }

    public NameParser getNameParser(Name name) throws NamingException {
        throw new OperationNotSupportedException();
    }

    public NameParser getNameParser(String string) throws NamingException {
        return getNameParser(new CompositeName(string));
    }

    public Name composeName(Name name, Name prefix) throws NamingException {
        throw new OperationNotSupportedException();
    }

    public String composeName(String string,
                              String prefix) throws NamingException {
        throw new OperationNotSupportedException();
    }

    public Hashtable getEnvironment() throws NamingException { return env; }

    public Object addToEnvironment(String string,
                                   Object value) throws NamingException {
        throw new OperationNotSupportedException();
    }

    public Object removeFromEnvironment(String string) throws NamingException {
        throw new OperationNotSupportedException();
    }

    public String getNameInNamespace() throws NamingException { return ""; }

    public void close() throws NamingException { }

    private class BindingEnumeration extends NamingEnumerationImpl<Binding> {
        private static final long serialVersionUID = 4283190965750454311L;

        public BindingEnumeration(Map<String,String> map) {
            super(null);

            for (Map.Entry<String,?> entry : map.entrySet()) {
                add(new Binding(entry.getKey(), entry.getValue()));
            }
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
