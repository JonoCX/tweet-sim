package uk.ac.ncl.tweetsim.network;

import br.les.opus.twitter.domain.Tweet;
import br.les.opus.twitter.domain.TweetClassification;
import br.les.opus.twitter.domain.TweetsMetadata;
import br.les.opus.twitter.domain.TwitterUser;
import br.les.opus.twitter.repositories.TweetClassificationRepository;
import br.les.opus.twitter.repositories.TweetRepository;
import br.les.opus.twitter.repositories.TweetsMetadataRepository;
import br.les.opus.twitter.repositories.TwitterUserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;

import javax.transaction.Transactional;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
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

    @Autowired
    private TweetsMetadataRepository metaDao;

    @Autowired
    private TweetClassificationRepository classRepo;

    @Override
    protected void execute() throws WorkerException {
        try {
            StringBuilder sb = new StringBuilder();
            FileWriter writer = new FileWriter("data/tr-results.csv");
            sb.append("User,Tweet Count,Followers,Following,Twitter Rank,Participation,Interest");
            sb.append("\n");

            List<TweetsMetadata> metadata = metaDao.findAll();

            metadata.sort((o1, o2) -> o2.getTwitterRank().compareTo(o1.getTwitterRank()));

            for (TweetsMetadata md : metadata) {
                sb.append(md.getUser().getScreenName() + ",");
                sb.append(md.getTweetsCount() + ",");
                sb.append(md.getUser().getFollowers().size()+ ",");
                sb.append(md.getUser().getFollowing().size()+ ",");
                sb.append(md.getTwitterRank()+ ",");
                sb.append(md.getParticipation()+ ",");
                sb.append(md.getInterest());
                sb.append("\n");
            }
            /*
            List<TwitterUser> userList = userRepository.findByScreenNameLike("BOT-%");
            TweetClassification classification = classRepo.findOne((long) 3);
            for (TwitterUser user : userList) {
                TweetsMetadata metadata = metaDao.findOne(user, classification);
                sb.append(user.getScreenName() + ",");
                sb.append(metadata.getTweetsCount() + ",");
                sb.append(user.getFollowers().size()+ ",");
                sb.append(user.getFollowing().size()+ ",");
                sb.append(metadata.getTwitterRank()+ ",");
                sb.append(metadata.getParticipation()+ ",");
                sb.append(metadata.getInterest());
                sb.append("\n");
            }
*/
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String logMsg = "[" + this.getClass().getSimpleName() + "] ";
//
//        logger.info(logMsg + "Loading users...");
//        List<TwitterUser> userList = userRepository.findByScreenNameLike("BOT-%");
//        logger.info(logMsg + "List Size: " + userList.size());
//        for (TwitterUser user : userList) {
//
//            logger.info(logMsg + "User: " + user);
//            List<TwitterUser> followers = user.getFollowers();
//            List<TwitterUser> following = user.getFollowing();
//            logger.info(logMsg + "Followers #: " + followers.size());
//            logger.info(logMsg + "Following #: " + following.size());
//
//            logger.info(logMsg + "Followers: ");
//            for (TwitterUser followUser : followers) {
//                logger.info("\t" + followUser);
//            }
//
//            logger.info(logMsg + "Following: ");
//            for (TwitterUser followingUser : following) {
//                logger.info("\t" + followingUser.toString());
//            }
//
//        }

//        logger.info("\n\n\n");
//        logger.info(logMsg + "Loading tweets...");
//        List<Tweet> tweetList = tweetRepository
    }

    private void cleanDatabase(List<TwitterUser> users) {
        userRepository.delete(users);
    }
}
