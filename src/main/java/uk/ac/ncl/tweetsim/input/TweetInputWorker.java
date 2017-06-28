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
 */
@Component
@Transactional
public class TweetInputWorker extends AbstractWorker implements InputWorker
{
    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfigRepository configRepository;

    protected Logger logger = Logger.getLogger(TweetInputWorker.class);

    private static final Integer TWEET_LIMIT = 140;

    @Override
    protected void execute() throws WorkerException {
        logger.info("Loading current configuration...");
        Config config = configRepository.findAll(new Sort(Sort.Direction.DESC, "configId")).iterator().next();
        logger.info("min tweets: " + config.getMinTweets() + "and max tweets: " + config.getMaxTweets());


        generateTweets(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tweet> readFile() {
        List<Tweet> tweets = new ArrayList<>();

        List<String> stringList = Util.readFile("tweets.txt");
        Tweet t;
        Integer line = 1;
        for (String s : stringList) {
            String[] split = s.split(",");

            // trim leading and trailing spaces.
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].trim();
            }

            Long classification = Long.parseLong(split[1]);
            if (classification > 3 || classification < 1) {
                logger.error("The classification id needs to be between 1 and 3. See line " + line + " in the text" +
                        " file.");
            }

            t = new Tweet(split[0], classification);

            if (t.getText().length() > TWEET_LIMIT) {
                logger.warn(t.getText() + " is over the Twitter character limit (140). See line " + line + " in" +
                        "the text file.");
            } else {
                tweets.add(t);
            }
            line++;
        }

        return tweets;
    }

    public void generateTweets(Config c) {

        List<User> users = userRepository.getByConfig(c);

        for(User u: users) {

            int numTweets = ThreadLocalRandom.current().nextInt(c.getMinTweets(), c.getMaxTweets());
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
