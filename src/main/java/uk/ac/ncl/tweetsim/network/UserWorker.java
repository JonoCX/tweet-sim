package uk.ac.ncl.tweetsim.network;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.IDs;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import uk.ac.ncl.botnetwork.domain.GeneratedTweet;
import uk.ac.ncl.botnetwork.domain.User;
import uk.ac.ncl.botnetwork.repositories.BNTweetRepository;
import uk.ac.ncl.botnetwork.repositories.ConnectionRepository;
import uk.ac.ncl.botnetwork.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 *
 * @author Jonathan Carlton
 */
@Component
@Transactional
public class UserWorker implements Runnable
{
    protected Logger logger = Logger.getLogger(getClass());

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private BNTweetRepository tweetRepository;

    private Twitter twitter;
    private User user;
    private String outStarter;
    private List<Long> connections;

    @Override
    public void run() {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            this.doWork();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            Session session = sessionFactory.getCurrentSession();
            Transaction transaction = session.getTransaction();

            if (transaction != null && transaction.isActive()) {
                transaction.commit();
            }

            if (session.isOpen()) {
                session.close();
            }
        }
    }

    private void doWork() throws TwitterException {
        Long threadId = Thread.currentThread().getId();
        this.outStarter = "[" + threadId + "] ";
        logger.info(outStarter + "Starting up...");

        logger.info(outStarter + "Checking out a user...");
        //this.user = userRepository.checkOutUser();
        logger.info(outStarter + "User checked out: " + user.getScreenName());
        this.outStarter = "[" + user.getScreenName() + "] ";

        //this.twitter = this.getConnection();
        logger.info(outStarter + "Connected to Twitter.");

        logger.info(outStarter + "Checking if network needs updating...");
        this.updateNetwork();

        // post tweet (also includes the third bullet point from below)
        logger.info(outStarter + "Beginning posting tweets...");
        logger.warn(outStarter + "This runs indefinitely and requires you to forcibly stop it.");
        while (true) {
            this.tweet();
        }
    }

    /*
        Needs to be able to update it's own network before doing anything
        - remember that the connections may have changed since the last run.

        Needs to be able to post a (randomly selected) tweet. Further, the tweets
        should be posted within a given frequency selected by the user.

        It would be good if, with some probability, the user could retweet/like
        a status that has been posted by one of the other users in its network
        (they should be users that the user follows).

        todo add in the frequency of which the user posts a tweet.

        For the above, there could be a default value that all of the users
        post at if a number is defined.
        Thoughts: How to define the number? How many times, within a 10 min
        period, do the users post.
     */

    private void updateNetwork() throws TwitterException {
        List<Long> dbConnections = connectionRepository.findAllFollowingIDs(user);
        this.connections = dbConnections;

        // this shouldn't reach the rate limit.
        List<Long> following = new ArrayList<>();
        Long cursor = -1L;
        IDs ids;
        do {
            ids = twitter.getFollowersIDs(user.getScreenName(), cursor);
            for (Long i : ids.getIDs()) {
                following.add(i);
            }
        } while ((cursor = ids.getNextCursor()) != 0);

        /*
            Logic.
                If the id is not present in the list of IDS, create friendship.
                if the id is present in the list, skip.
                If the id is present in the list, but not in the new list - destroy.
         */

        // checks for new connections
        for (Long stored : dbConnections) {
            if (!(following.contains(stored))) {
                twitter.createFriendship(stored);
            }
        }

        // removes old connections that have been removed from the db.
        for (Long online : following) {
            if (!(dbConnections.contains(online))) {
                twitter.destroyFriendship(online);
            }
        }

        logger.info(outStarter + "Network updated as per the database.");
    }

    private void tweet() throws TwitterException {
        // post tweets but also with some fixed probability retweet/like another user
        // that this user is following rather than tweeting something.

        // what should the probability be? 10% (1/10) then 1/2 (50%) of either retweet or like.

        Integer tweetProb = ThreadLocalRandom.current().nextInt(10);
        Boolean retweetOrLike = tweetProb == 0; // true if == 0, false if not.


        if (retweetOrLike) { // retweet or like.
            // select a target.
            Long targetUser = connections.get(ThreadLocalRandom.current().nextInt(connections.size()));

            // for simplicity, just retweet their most recent tweet.
            Status status = twitter.getUserTimeline(targetUser).get(0);
            Long sId = status.getId();

            Integer which = ThreadLocalRandom.current().nextInt(2); // 0 = retweet, 1 = like
            if (which == 0) { // retweet
                twitter.retweetStatus(sId);
            } else { // like
                twitter.createFavorite(sId);
            }
        }
        else { // post new tweet.
            GeneratedTweet tweet = tweetRepository.getRandomTweet();
            logger.info(outStarter + "Tweet selected: " + tweet);
            twitter.updateStatus(tweet.getText());
        }
    }
}
