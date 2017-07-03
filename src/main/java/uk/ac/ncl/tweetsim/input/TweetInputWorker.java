package uk.ac.ncl.tweetsim.input;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import uk.ac.ncl.botnetwork.domain.Config;
import uk.ac.ncl.botnetwork.domain.Tweet;
import uk.ac.ncl.botnetwork.domain.User;
import uk.ac.ncl.botnetwork.repositories.ConfigRepository;
import uk.ac.ncl.botnetwork.repositories.TweetRepository;
import uk.ac.ncl.botnetwork.repositories.UserRepository;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;
import uk.ac.ncl.tweetsim.util.Util;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A worker to input tweets, stored in a text file,
 * into the underlying database.
 *
 * @see uk.ac.ncl.tweetsim.util.Util for input file
 * configuration information - this will break if
 * the text file isn't a comma separated list of tweet
 * objects (as strings).
 *
 * @author Jonathan Carlton
 * @author Callum McClean
 */
@Component
@Transactional
public class TweetInputWorker extends AbstractWorker
{
    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfigRepository configRepository;

    private Config config;

    protected Logger logger = Logger.getLogger(TweetInputWorker.class);

    private static final Integer TWEET_LIMIT = 140;

    @Override
    protected void execute() throws WorkerException {
        logger.info("Loading current configuration...");
        config = configRepository.findAll(new Sort(Sort.Direction.DESC, "configId")).iterator().next();
        logger.info("min tweets: " + config.getMinTweets() + " and max tweets: " + config.getMaxTweets());


        generateTweets();
    }


    public void generateTweets() {

        List<User> users = userRepository.getByConfig(config);

        for(User u: users) {

            int numTweets = ThreadLocalRandom.current().nextInt(config.getMinTweets(), config.getMaxTweets());
            List<Tweet> tweets = new ArrayList<>();
            Long relevant = new Long(3);

            for(int j = 0; j < numTweets; j++) {
                Tweet t;

                t = new Tweet(
                        "BOT TWEET",
                        relevant,
                        u
                );

                tweets.add(t);
            }
            logger.info("Saving to database... " + u.getScreenName());
            tweetRepository.save(tweets);
            logger.info("Saved to database.");

        }

    }
}
