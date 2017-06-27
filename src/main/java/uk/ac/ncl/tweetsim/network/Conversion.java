package uk.ac.ncl.tweetsim.network;

import br.les.opus.twitter.domain.Tweet;
import br.les.opus.twitter.domain.TweetClassification;
import br.les.opus.twitter.domain.TwitterUser;
import br.les.opus.twitter.repositories.TweetClassificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ncl.botnetwork.domain.Connection;
import uk.ac.ncl.botnetwork.domain.User;

import javax.transaction.Transactional;
import java.util.*;

/**
 * @author Jonathan Carlton
 */
public class Conversion 
{
    public static List<TwitterUser> userConvert(List<User> list) {
        List<TwitterUser> result = new ArrayList<>();

        TwitterUser tu;
        for (User u : list) {
            tu = new TwitterUser();
            tu.setOutdatedFollowers(false); // prevent the check.

            tu.setId(u.getTwitterId());
            tu.setScreenName(u.getScreenName());

            result.add(tu);
        }

        return result;
    }

    public static List<Tweet> tweetConvert(
            List<uk.ac.ncl.botnetwork.domain.Tweet> list,
            Map<Long, TweetClassification> classificationMap)
    {
        List<Tweet> result = new ArrayList<>();

        Tweet tweet;
        for (uk.ac.ncl.botnetwork.domain.Tweet t : list) {
            tweet = new Tweet();
            tweet.setText(t.getText());
            tweet.setClassification(classificationMap.get(t.getClassificationId()));

            // set random ID
            tweet.setId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);

            result.add(tweet);
        }

        return result;
    }

    public static List<TwitterUser> connectionConvert(
            List<TwitterUser> users, List<Connection> connections)
    {
        List<TwitterUser> result = new ArrayList<>();

        Map<TwitterUser, List<Long>> followingMap = new HashMap<>();
        for (TwitterUser u : users) {
            followingMap.put(u, new ArrayList<>());
        }

        for (TwitterUser u : users) {
            List<Long> temp = new ArrayList<>();
            for (Connection conn : connections) {
                if (conn.getOrigin().getTwitterId().equals(u.getId())) {
                    temp.add(conn.getDestination().getTwitterId());
                }
            }
            followingMap.put(u, temp);
        }

        for (Map.Entry<TwitterUser, List<Long>> m : followingMap.entrySet()) {
            List<Long> list = m.getValue();
            TwitterUser origin = m.getKey();

            for (TwitterUser user : users) {
                if (list.contains(user.getId())) {
                    for (Long l : list) {
                        if (l.equals(user.getId())) {
                            origin.getFollowing().add(user);
                        }
                    }
                }
            }

            result.add(origin);
        }

        return result;
    }
}
