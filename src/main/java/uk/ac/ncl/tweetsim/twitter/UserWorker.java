package uk.ac.ncl.tweetsim.twitter;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.Twitter;
import twitter4j.conf.ConfigurationBuilder;
import uk.ac.ncl.botnetwork.domain.User;
import uk.ac.ncl.botnetwork.repositories.UserRepository;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;

import javax.transaction.Transactional;

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

    private void doWork() {

    }

    /*
        Needs to be able to update it's own network before doing anything
        - remember that the connections may have changed since the last run.

        Needs to be able to post a (randomly selected) tweet. Further, the tweets
        should be posted within a given frequency selected by the user.

        It would be good if, with some probability, the user could retweet/like
        a status that has been posted by one of the other users in its network
        (they should be users that the user follows).
     */

    private Twitter getConnection() {
        return null
    }

    private void updateNetwork() {

    }
}
