package uk.ac.ncl.tweetsim;

import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;

/**
 * @author Jonathan Carlton
 */
public class WorkException extends Exception
{
    private static final long serialVersionUID = 42L;

    public WorkException(String message) { super(message); }

    public WorkException(Throwable cause) { super(cause); }

    public WorkException(String message, Throwable cause) { super(message, cause); }
}
