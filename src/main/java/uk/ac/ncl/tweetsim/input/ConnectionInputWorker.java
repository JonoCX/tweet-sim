package uk.ac.ncl.tweetsim.input;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ncl.botnetwork.repositories.ConnectionRepository;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;

/**
 * A worker to input connections between users into
 * the underlying database. These are the follower
 * and following relationships that are formed on Twitter.
 *
 * In order to create a bi-directional relationship between
 * two users, just include two entries in the text file.
 *
 * For example:
 *      user1, user2
 *      user2, user1
 *
 * @author Jonathan Carlton
 */
public class ConnectionInputWorker extends AbstractWorker
{
    protected Logger logger = Logger.getLogger(ConnectionInputWorker.class);

    @Autowired
    private ConnectionRepository connectionRepository;

    @Override
    protected void execute() throws WorkerException {

    }
}
