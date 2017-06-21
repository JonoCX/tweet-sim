package uk.ac.ncl.tweetsim.input;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ncl.botnetwork.domain.Tweet;
import uk.ac.ncl.botnetwork.repositories.TweetRepository;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan Carlton
 */
@Component
@Transactional
public class TweetInputWorker extends AbstractWorker
{
    @Autowired
    private TweetRepository tweetRepository;

    protected Logger logger = Logger.getLogger(TweetInputWorker.class);

    @Override
    protected void execute() throws WorkerException {
        logger.info("Loading tweets...");
        List<Tweet> tweets = getFileTweets();
        logger.info(tweets.size() + " tweets loaded.");
        logger.info("Saving to database...");
        tweetRepository.save(tweets);
        logger.info("Saved to database.");
    }

    private List<Tweet> getFileTweets() {
        BufferedReader br = null;
        List<Tweet> tweets = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader("data/tweets.txt"));
            String cLine;
            Tweet t;
            while ((cLine = br.readLine()) != null) {
                t = new Tweet();
                t.setText(cLine);
                tweets.add(t);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ie) {
                logger.error(ie.getMessage(), ie);
                ie.printStackTrace();
            }
        }
        return tweets;
    }
}
