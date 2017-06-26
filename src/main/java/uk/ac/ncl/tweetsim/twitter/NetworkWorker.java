package uk.ac.ncl.tweetsim.twitter;

import org.springframework.stereotype.Component;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;

import javax.transaction.Transactional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jonathan Carlton
 */
@Component
@Transactional
public class NetworkWorker extends AbstractWorker
{

    @Override
    protected void execute() throws WorkerException {
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < 2; i++) {
            service.execute(new UserWorker(123));
        }
    }

    /*
        This needs to enable management control over the network...

        Spawn a given number of users (must be below the number of users
        defined in the database).

     */
}
