package uk.ac.ncl.tweetsim.network;

import br.les.opus.twitter.domain.Tweet;
import br.les.opus.twitter.domain.TweetClassification;
import br.les.opus.twitter.domain.TwitterUser;
import br.les.opus.twitter.repositories.TweetClassificationRepository;
import br.les.opus.twitter.repositories.TweetRepository;
import br.les.opus.twitter.repositories.TwitterUserRepository;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ncl.botnetwork.domain.Connection;
import uk.ac.ncl.botnetwork.domain.User;
import uk.ac.ncl.botnetwork.repositories.ConnectionRepository;
import uk.ac.ncl.botnetwork.repositories.UserRepository;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;

import javax.transaction.Transactional;
import java.util.*;

/**
 * @author Jonathan Carlton
 */
@Component
@Transactional
public class ConversionWorker extends AbstractWorker
{
    @Autowired
    private TwitterUserRepository userRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private TweetClassificationRepository classRepository;

    @Autowired
    private UserRepository btUserRepo;

    @Autowired
    private uk.ac.ncl.botnetwork.repositories.TweetRepository btTweetRepo;

    @Autowired
    private ConnectionRepository btConnRepo;

    @Override
    protected void execute() throws WorkerException {

    }

    public List<TwitterUser> userConvert(List<User> list) {
        List<TwitterUser> result = new ArrayList<>();

        TwitterUser tu;
        for (User u : list) {
            tu = new TwitterUser();
            tu.setOutdatedFollowers(false); // prevent the check.

            tu.setId(u.getTwitterId());
            tu.setScreenName(u.getScreenName());

            userRepository.save(tu);

            result.add(tu);
        }

        return result;
    }

    public List<Tweet> tweetConvert(
            List<uk.ac.ncl.botnetwork.domain.Tweet> list,
            Map<Long, TweetClassification> classificationMap)
    {
        List<Tweet> result = new ArrayList<>();

        Tweet tweet;
        for (uk.ac.ncl.botnetwork.domain.Tweet t : list) {
            tweet = new Tweet();
            tweet.setText(t.getText());
            tweet.setClassification(classRepository.findOne(t.getClassificationId()));

            // set random ID
            LocalDateTime ldt = new LocalDateTime();
            Long id = Long.parseLong(ldt.toString());
            tweet.setId(id);

            tweetRepository.save(tweet);

            result.add(tweet);
        }

        return result;
    }

    public List<TwitterUser> connectionConvert(
            List<TwitterUser> users, List<Connection> connections)
    {
        List<TwitterUser> result = new ArrayList<>();



        return result;
    }
}
