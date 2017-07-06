package uk.ac.ncl.tweetsim.input;

import org.apache.log4j.Logger;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import uk.ac.ncl.botnetwork.domain.Config;
import uk.ac.ncl.botnetwork.domain.User;
import uk.ac.ncl.botnetwork.repositories.ConfigRepository;
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
 * @author Callum McClean
 */
@Component
@Transactional
public class UserInputWorker extends AbstractWorker
{
    protected Logger logger = Logger.getLogger(UserInputWorker.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfigRepository configRepository;

    private Config config;

    @Override
    protected void execute() throws WorkerException {

        List<Config> configs = configRepository.findNotComplete();

        for(Config c : configs) {
            logger.info("Loading current configuration...");
            config = c;
            logger.info(config.getNumUsers() + " Users to be created.");

            logger.info("Generating users....");
            List<User> users = generateUsers();

            logger.info("Saving to database...");
            userRepository.save(users);
            logger.info("Saved to database.");
        }

    }

    /**
     * Generates the number of User objects
     * required for this configuration. Each
     * object is linked to the configuration object.
     *
     * @return List<User>
     */
    public List<User> generateUsers() {

        List<User> users = new ArrayList<>();
        User user;

        for(int i = 0; i < config.getNumUsers(); i++) {
            LocalDateTime date = new LocalDateTime();
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS");
            Long id = Long.parseLong(date.toString(formatter)) +i;

            user = new User(
                    id,
                    "BOT-"+config.getConfigId()+"-"+id,
                    config

            );

            users.add(user);
        }

        return users;
    }
}
