package uk.ac.ncl.tweetsim;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jonathan Carlton
 */
public abstract class AbstractWorker extends Thread
{
    @Autowired private SessionFactory sessionFactory;
    protected Logger logger = Logger.getLogger(getClass());

    @Override
    public void run() {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            this.execute();
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

    protected abstract void execute() throws WorkerException;

    protected void saveChange() {
        Session session = sessionFactory.getCurrentSession();
        session.getTransaction().commit();
        sessionFactory.getCurrentSession().beginTransaction();
    }
}
