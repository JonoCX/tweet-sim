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
public class UserInputWorker extends AbstractWorker
{
    protected Logger logger = Logger.getLogger(UserInputWorker.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void execute() throws WorkerException {
        logger.info("Loading users...");
        List<User> users = this.getFileUsers();
        logger.info(users.size() + " users loaded");

        logger.info("Saving to database...");
        userRepository.save(users);
        logger.info("Saved to database.");
    }

    /**
     * @see uk.ac.ncl.tweetsim.util.Util for file configuration
     * information - this method requires a particular string
     * input for it to work.
     * @return  a list of users constructed from a txt file
     * stored in the data directory of this project.
     */
    private List<User> getFileUsers() {
        List<User> users = new ArrayList<>();

        List<String> stringList = Util.readFile("users.txt");
        User user;
        Integer line = 1;
        for (String s : stringList) {
            String[] split = s.split(",");

            // trim leading and trailing spaces
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].trim();
            }

            user = new User(
                    Long.parseLong(split[0]),
                    split[1],
                    split[2],
                    split[3],
                    split[4],
                    split[5]
            );

            String[] keys = {
                    user.getConsumerKey(),
                    user.getConsumerSecret(),
                    user.getAccessToken(),
                    user.getAccessTokenSecret()
            };

            if (validateKeys(keys)) {
                users.add(user);
                logger.info("The keys for the user " + user.getScreenName() + " were able to be validated against" +
                        " the Twitter API.");
            } else {
                logger.warn(user.getScreenName() + " could not have it's keys validated, see" +
                        " line " + line + " in the text file.");
            }
            line++;
        }

        return users;
    }

    private Boolean validateKeys(String[] keys) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(keys[0])
                .setOAuthConsumerSecret(keys[1])
                .setOAuthAccessToken(keys[2])
                .setOAuthAccessTokenSecret(keys[3]);
        TwitterFactory factory = new TwitterFactory(cb.build());
        Twitter twitter = factory.getInstance();

        // Throws an exception if credentials aren't valid.
        try {
            twitter.verifyCredentials();
            return Boolean.TRUE;
        } catch (TwitterException te) {
            return Boolean.FALSE;
        }
    }
}
