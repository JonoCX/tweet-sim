package uk.ac.ncl.tweetsim.network;

import br.les.opus.twitter.domain.Tweet;
import br.les.opus.twitter.domain.TwitterUser;
import uk.ac.ncl.botnetwork.domain.Connection;
import uk.ac.ncl.botnetwork.domain.User;

import java.util.ArrayList;
import java.util.List;

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
            List<uk.ac.ncl.botnetwork.domain.Tweet> list)
    {
        List<Tweet> result = new ArrayList<>();

        return result;
    }

    public static List<TwitterUser> connectionConvert(
            List<TwitterUser> users, List<Connection> connections)
    {
        List<TwitterUser> result = new ArrayList<>();

        return result;
    }
}
