/*
 * $Id: envURLContextFactory.java,v 1.2 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.naming.env;

import iprotium.naming.ENVContext;
import iprotium.util.Factory;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

/**
 * {@link ENVContext} JNDI {@link InitialContextFactory} and
 * {@link ObjectFactory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class envURLContextFactory extends Factory<ENVContext>
                                  implements InitialContextFactory,
                                             ObjectFactory {

    /**
     * Sole constructor.
     */
    public envURLContextFactory() { super(ENVContext.class); }

    public ENVContext getInitialContext(Hashtable env) throws NamingException {
        return new ENVContext(env);
    }

    public Object getObjectInstance(Object object, Name name, Context context,
                                    Hashtable env) throws Exception {
        return new ENVContext(env);
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
