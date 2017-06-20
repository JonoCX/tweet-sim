package uk.ac.ncl.tweetsim.input;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ncl.botnetwork.repositories.TweetRepository;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkException;

/**
 * @author Jonathan Carlton
 */
public class InputWorker extends AbstractWorker
{
    @Autowired private TweetRepository tweetRepository;
    protected Logger logger = Logger.getLogger(InputWorker.class);
    private String file;
    private Boolean overwriteFlag;

    /**
     *
     * @param file the location of the tweet txt file to
     *             be read in and stored in the database
     * @param overwriteFlag if the previously stored content
     *                      should be overwritten or not.
     */
    public InputWorker(String file, Boolean overwriteFlag) {
        this.file = file;
        this.overwriteFlag = overwriteFlag;
    }

    @Override
    protected void execute() throws WorkException {
        // todo
    }
}
