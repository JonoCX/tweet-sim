package uk.ac.ncl.tweetsim;

/**
 * @author Jonathan Carlton
 */
public class WorkerException extends Exception
{
    private static final long serialVersionUID = 42L;

    public WorkerException(String message) { super(message); }

    public WorkerException(Throwable cause) { super(cause); }

    public WorkerException(String message, Throwable cause) { super(message, cause); }
}
