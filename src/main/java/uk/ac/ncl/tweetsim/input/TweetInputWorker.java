package uk.ac.ncl.tweetsim.input;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ncl.botnetwork.domain.Tweet;
import uk.ac.ncl.botnetwork.repositories.TweetRepository;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;
import uk.ac.ncl.tweetsim.util.Util;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

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

    protected Logger logger = Logger.getLogger(TweetInputWorker.class);

    private static final Integer TWEET_LIMIT = 140;

    @Override
    protected void execute() throws WorkerException {
        logger.info("Loading tweets...");
        List<Tweet> tweets = this.readFile();
        logger.info(tweets.size() + " tweets loaded.");

        logger.info("Saving to database...");
        tweetRepository.save(tweets);
        logger.info("Saved to database.");
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

            t = new Tweet(split[0]);

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
}
