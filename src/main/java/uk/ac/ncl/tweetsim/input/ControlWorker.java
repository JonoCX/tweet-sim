package uk.ac.ncl.tweetsim.input;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;
import uk.ac.ncl.tweetsim.network.UserWorker;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Jonathan Carlton
 */
@Component
@Transactional
public class ControlWorker extends AbstractWorker
{
    protected Logger logger = Logger.getLogger(ControlWorker.class);

    /**
     * forcing the commit so you can see this.
     * @throws WorkerException
     */
    @Override
    protected void execute() throws WorkerException {
        String logMsg = "[" + this.getClass().getSimpleName() + "]";

        logger.info(logMsg + "Creating cached thread pool...");
        try {
            ExecutorService service = Executors.newCachedThreadPool();
            Collection<Future<?>> futures = new LinkedList<>();
            futures.add(service.submit(new ConfigWorker()));
            futures.add(service.submit(new UserInputWorker()));
            futures.add(service.submit(new TweetInputWorker()));
            futures.add(service.submit(new ConnectionInputWorker()));
            logger.info(logMsg + "Created.");
            logger.info(logMsg + "Running Injection thread.");
            for (Future<?> future : futures) {
                future.get();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}