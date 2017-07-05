package uk.ac.ncl.tweetsim.network;

import br.les.opus.twitter.domain.Tweet;
import br.les.opus.twitter.domain.TweetClassification;
import br.les.opus.twitter.domain.TweetsMetadata;
import br.les.opus.twitter.domain.TwitterUser;
import br.les.opus.twitter.repositories.TweetClassificationRepository;
import br.les.opus.twitter.repositories.TweetRepository;
import br.les.opus.twitter.repositories.TweetsMetadataRepository;
import br.les.opus.twitter.repositories.TwitterUserRepository;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import uk.ac.ncl.botnetwork.domain.Config;
import uk.ac.ncl.botnetwork.domain.User;
import uk.ac.ncl.botnetwork.domain.GeneratedTweet;
import uk.ac.ncl.botnetwork.repositories.BNTweetRepository;
import uk.ac.ncl.botnetwork.repositories.ConfigRepository;
import uk.ac.ncl.botnetwork.repositories.ConnectionRepository;
import uk.ac.ncl.botnetwork.repositories.UserRepository;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;

import javax.transaction.Transactional;
import java.util.*;

/**
 * A worker to essentially convert the data that is
 * stored separately (in the bot_network schema) to
 * the type of data needed for injection.
 *
 * This worker while it primarily just converts the
 * stored data it also saves the data into the
 * twitter schema created by PUC.
 *
 * This also performs the injection into the data
 * base.
 *
 * @author Jonathan Carlton
 */
@Component
@Transactional
public class InjectionWorker extends AbstractWorker
{
    @Autowired
    private TwitterUserRepository userRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private TweetClassificationRepository classRepository;

    @Autowired
    private TweetsMetadataRepository metaDao;

    @Autowired
    private UserRepository btUserRepo;

    @Autowired
    private BNTweetRepository btTweetRepo;

    @Autowired
    private ConnectionRepository btConnRepo;

    @Autowired
    private ConfigRepository configRepository;

    private String logMsg;
    private Config config;

    /**
     *
     * @throws WorkerException throws if the worker runs into issues, but
     * isn't thrown in this context.
     */
    @Override
    protected void execute() throws WorkerException {
        // todo the order of execution needs to be considered.
        logMsg = "[" + this.getClass().getSimpleName() + "] ";

        logger.info(logMsg + "starting up.");

        // collect configuration information.
        logger.info(logMsg + "Collecting configuration information...");
        this.config = this.getNewestConfig();
        logger.info(logMsg + "Collected.");

        // fetch the information from the bot_network schema.
        logger.info(logMsg + "Collecting data from bot_network schema...");
        List<User> btStoredUsers = this.getUserList();
        List<GeneratedTweet> btStoredTweets = this.getTweetList();
        logger.info(logMsg + "Collected. # " + btStoredTweets.size());

        // create classification map.
        Map<Long, TweetClassification> classMap = this.createMap();

        // convert users.
//        logger.info(logMsg + "Converting and injecting users...");
//        List<TwitterUser> convertedUsers = this.userConvertAndInject(btStoredUsers);
//        logger.info(logMsg + "Converted and injected " + convertedUsers.size() + " users.");

        // convert tweets.
        logger.info(logMsg + "Converting and injecting tweets...");
        List<Tweet> convertedTweets = this.tweetConvertAndInject(btStoredTweets, classMap);
        logger.info(logMsg + "Converted and injected " + convertedTweets.size() + " tweets.");

//        // convert connections
//        logger.info(logMsg + "Creating connections between users...");
//        List<TwitterUser> connectedUsers = this.connectionConvertAndInject(convertedUsers);
//        logger.info(logMsg + "Created.");
    }

    private List<TwitterUser> userConvertAndInject(List<User> list) {
        List<TwitterUser> result = new ArrayList<>();

        TwitterUser tu;
        for (User u : list) {
            tu = new TwitterUser();
            tu.setOutdatedFollowers(false); // prevent the check.

            tu.setId(u.getTwitterId());
            tu.setScreenName(u.getScreenName());

            result.add(tu);
        }

        this.userRepository.save(result);

        return result;
    }

    private List<Tweet> tweetConvertAndInject(
            List<GeneratedTweet> list,
            Map<Long, TweetClassification> classificationMap) {
        List<Tweet> result = new ArrayList<>();

        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS");
        Tweet tweet;
        Integer i = 1;
        for (GeneratedTweet t : list) {
            tweet = new Tweet();
            tweet.setText(t.getText());
            tweet.setClassification(classificationMap.get(t.getClassificationId()));

            // set random ID
            LocalDateTime ldt = new LocalDateTime();
            Long id = Long.parseLong(ldt.toString(dtf)) + i;
            tweet.setId(id);

            tweet.setUser(this.singleUserConvert(t.getUser()));

            result.add(tweet);

            i++;
        }

        this.tweetRepository.save(result);

        // create/update metadata ready for TR process.
        for (Tweet t : result) {
            this.updateMetaData(t);
        }

        return result;
    }

    private List<TwitterUser> connectionConvertAndInject(List<TwitterUser> users) {
        List<TwitterUser> result = new ArrayList<>();

        List<User> storedFollowers;
        for (TwitterUser user : users) {
            User stored = btUserRepo.findOne(user.getId());
            storedFollowers = btConnRepo.findAllFollowers(stored);

            for (User inner : storedFollowers) {
                TwitterUser find = this.singleUserConvert(inner);
                user.getFollowers().add(find);
                find.getFollowing().add(user);
                this.userRepository.save(find);
            }

            result.add(user);
        }

        this.userRepository.save(result);

        return result;
    }

    private List<User> getUserList() {
        return (List<User>) btUserRepo.findAll();
    }

    private List<GeneratedTweet> getTweetList() {
        return (List<GeneratedTweet>) btTweetRepo.findAll();
    }

    private Map<Long, TweetClassification> createMap() {
        Map<Long, TweetClassification> map = new HashMap<>();
        List<TweetClassification> list = classRepository.findAll();
        for (TweetClassification tc : list) {
            map.put(tc.getId(), tc);
        }
        return map;
    }

    private TwitterUser singleUserConvert(User user) {
        return userRepository.findOne(user.getTwitterId());
    }

    private Config getNewestConfig() {
        return this.configRepository
                .findAll(new Sort(Sort.Direction.DESC, "configId"))
                .iterator()
                .next();
    }

    private void updateMetaData(Tweet tweet) {
        TweetsMetadata metadata = metaDao.findOne(tweet.getUser(), tweet.getClassification());
        if (metadata != null) {
            metadata.incrementCount();
        } else {
            metadata = new TweetsMetadata(tweet);
        }
        metaDao.save(metadata);
    }
}