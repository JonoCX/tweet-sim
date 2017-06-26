package uk.ac.ncl.tweetsim.input;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ncl.botnetwork.domain.Connection;
import uk.ac.ncl.botnetwork.domain.User;
import uk.ac.ncl.botnetwork.repositories.ConnectionRepository;
import uk.ac.ncl.botnetwork.repositories.UserRepository;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;
import uk.ac.ncl.tweetsim.util.Util;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
@Component
@Transactional
public class ConnectionInputWorker extends AbstractWorker implements InputWorker
{
    protected Logger logger = Logger.getLogger(ConnectionInputWorker.class);

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void execute() throws WorkerException {
        logger.info("Loading connections...");
        List<Connection> connections = this.readFile();
        logger.info(connections.size() + " connections loaded.");

        logger.info("Saving to database...");
        connectionRepository.save(connections);
        logger.info("Saved to database.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Connection> readFile() {
        List<Connection> connections = new ArrayList<>();

        List<String> stringList = Util.readFile("connections.txt");
        Connection c;
        Integer line = 1;

        for (String s : stringList) {
            String[] split = s.split(",");

            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].trim();
            }

            User userOne = userRepository.findOne(Long.parseLong(split[0]));
            User userTwo = userRepository.findOne(Long.parseLong(split[1]));

            if (userOne == null || userTwo == null) {
                logger.error("One of the two users provided do not exist within " +
                        "the database. Please add the users first before creating" +
                        " a connection. " +
                        "Line " + line + ", Origin: " + userOne + ", Destination: " + userTwo);
            } else {
                c = new Connection(
                        userOne,
                        userTwo
                );
                connections.add(c);
            }
        }

        return connections;
    }
}
