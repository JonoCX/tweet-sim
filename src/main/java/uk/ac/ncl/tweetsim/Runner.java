package uk.ac.ncl.tweetsim;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import uk.ac.ncl.tweetsim.input.*;
import uk.ac.ncl.tweetsim.network.NetworkWorker;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * The main class.
 *
 * Various workers can be spawned from here.
 *
 * @author Jonathan Carlton
 * @author Callum McClean
 */
@Component
@Transactional
public class Runner 
{
    protected static Logger logger = Logger.getLogger(Runner.class);

    private static final String TWEET_INPUT = "tweet-input";
    private static final String USER_INPUT = "user-input";
    private static final String CONNECTION_INPUT = "connection-input";
    private static final String NETWORK = "network";
    private static final String CONFIG = "config";
    private static final String CONTROLLER = "controller";

    @Autowired
    private TweetInputWorker tweetInput;

    @Autowired
    private UserInputWorker userInput;

    @Autowired
    private ConnectionInputWorker connectionInput;

    @Autowired
    private NetworkWorker network;

    @Autowired
    private ConfigWorker config;



    public static void main(String[] args) {
        // todo change this to be more dynamic in options.
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("tweet-sim-app-config.xml");
            Runner runner = (Runner) context.getBean("runner");

            String choice = CONTROLLER;

            if(choice == CONTROLLER) {
                runner.seq(CONFIG);
                runner.seq(USER_INPUT);
                runner.seq(TWEET_INPUT);
                runner.seq(CONNECTION_INPUT);
            } else {
                runner.run(choice);
            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Transactional
    public void run(String option) throws WorkerException {
            getWorkers().get(option).start();
    }

    @Transactional
    public void seq(String option) throws WorkerException {
        getWorkers().get(option).run();
    }

    private Map<String, AbstractWorker> getWorkers() {
        Map<String, AbstractWorker> workers = new HashMap<>();
        workers.put(TWEET_INPUT, tweetInput);
        workers.put(USER_INPUT, userInput);
        workers.put(CONNECTION_INPUT, connectionInput);
        workers.put(NETWORK, network);
        workers.put(CONFIG, config);
        return workers;
    }
}
