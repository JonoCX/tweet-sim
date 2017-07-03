package uk.ac.ncl.tweetsim.network;

import br.les.opus.twitter.domain.TwitterUser;
import br.les.opus.twitter.repositories.TweetRepository;
import br.les.opus.twitter.repositories.TwitterUserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Jonathan Carlton
 */
@Component
@Transactional
public class TestInjectionWorker extends AbstractWorker {
    protected Logger logger = Logger.getLogger(TestInjectionWorker.class);

    @Autowired
    private TwitterUserRepository userRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Override
    protected void execute() throws WorkerException {
        String logMsg = "[" + this.getClass().getSimpleName() + "] ";

        logger.info(logMsg + "Loading users...");
        List<TwitterUser> userList = userRepository.findByScreenNameLike("BOT-%");
        logger.info(logMsg + "List Size: " + userList.size());
        for (TwitterUser user : userList) {

            logger.info(logMsg + "User: " + user);
            List<TwitterUser> followers = user.getFollowers();
            List<TwitterUser> following = user.getFollowing();
            logger.info(logMsg + "Followers #: " + followers.size());
            logger.info(logMsg + "Following #: " + following.size());

            logger.info(logMsg + "Followers: ");
            for (TwitterUser followUser : followers) {
                logger.info("\t" + followUser);
            }

            logger.info(logMsg + "Following: ");
            for (TwitterUser followingUser : following) {
                logger.info("\t" + followingUser.toString());
            }

        }
    }
}
