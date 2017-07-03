package uk.ac.ncl.tweetsim.input;

import org.apache.log4j.Logger;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import uk.ac.ncl.botnetwork.domain.Config;
import uk.ac.ncl.botnetwork.domain.Connection;
import uk.ac.ncl.botnetwork.domain.Tweet;
import uk.ac.ncl.botnetwork.domain.User;
import uk.ac.ncl.botnetwork.repositories.ConfigRepository;
import uk.ac.ncl.botnetwork.repositories.ConnectionRepository;
import uk.ac.ncl.botnetwork.repositories.UserRepository;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;
import uk.ac.ncl.tweetsim.util.Util;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
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
 * @author Callum McClean
 *
 */
@Component
@Transactional
public class ConnectionInputWorker extends AbstractWorker
{
    protected Logger logger = Logger.getLogger(ConnectionInputWorker.class);

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfigRepository configRepository;

    private Config config;

    @Override
    protected void execute() throws WorkerException {
        logger.info("Loading current configuration...");
        config = configRepository.findAll(new Sort(Sort.Direction.DESC, "configId")).iterator().next();
        logger.info("min followers: " + config.getMinFollowers() + " and max followers: " + config.getMaxFollowers());

        generateFollowers();
    }


    public void generateFollowers() {

        List<User> users = userRepository.getByConfig(config);

        for(User u: users) {

            List<Connection> connections = new ArrayList<>();
            Connection c;

            int numFollowers = ThreadLocalRandom.current().nextInt(config.getMinFollowers(), config.getMaxFollowers());

            List<User> configUsers = userRepository.getByConfig(config);
            configUsers.remove(u);


            for(int j = 0; j < numFollowers; j++) {
                Tweet t;
                User user;

                int random = ThreadLocalRandom.current().nextInt(0, configUsers.size());
                user = configUsers.get(random);

                c = new Connection(
                        u,
                        user
                );

                connections.add(c);
                configUsers.remove(user);
            }

            logger.info("Saving to database... " + u.getScreenName());
            connectionRepository.save(connections);
            logger.info("Saved to database.");
        }

    }
}
