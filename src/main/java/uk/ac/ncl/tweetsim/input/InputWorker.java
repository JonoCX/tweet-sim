package uk.ac.ncl.tweetsim.input;

import java.util.List;

/**
 * An interface for the input works to implement.
 * Provides a standard between all of the workers.
 *
 * @author Jonathan Carlton
 */
public interface InputWorker
{
    /**
     * @see uk.ac.ncl.tweetsim.util.Util for file configuration
     * information - this methods requires a particular string
     * input for it to work.
     * @return  a list of objects constructed from a txt file
     * stored in the data directory of this project.
     */
    List<?> readFile();
}
