package uk.ac.ncl.tweetsim.input;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import uk.ac.ncl.botnetwork.domain.User;
import uk.ac.ncl.botnetwork.repositories.UserRepository;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;
import uk.ac.ncl.tweetsim.util.Util;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * A worker to input users, stored in a text file,
 * into the underlying database.
 *
 * @see uk.ac.ncl.tweetsim.util.Util for input file
 * configuration information - this will break if
 * the text file isn't a comma separated list of
 * user objects (as strings).
 *
 * @author Jonathan Carlton
 */
@Component
@Transactional
public class UserInputWorker extends AbstractWorker implements InputWorker
{
    protected Logger logger = Logger.getLogger(UserInputWorker.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void execute() throws WorkerException {
        logger.info("Loading users...");
        List<User> users = this.readFile();
        logger.info(users.size() + " users loaded");

        logger.info("Saving to database...");
        userRepository.save(users);
        logger.info("Saved to database.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> readFile() {
        List<User> users = new ArrayList<>();

        List<String> stringList = Util.readFile("users.txt");
        User user;
        for (String s : stringList) {
            String[] split = s.split(",");

            // trim leading and trailing spaces
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].trim();
            }

            user = new User(
                    Long.parseLong(split[0]),
                    split[1]
            );

            users.add(user);
        }

        return users;
    }
}
