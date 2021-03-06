package uk.ac.ncl.tweetsim.input;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import uk.ac.ncl.botnetwork.domain.Config;
import uk.ac.ncl.botnetwork.repositories.ConfigRepository;
import uk.ac.ncl.botnetwork.repositories.ConnectionRepository;
import uk.ac.ncl.tweetsim.AbstractWorker;
import uk.ac.ncl.tweetsim.WorkerException;
import uk.ac.ncl.tweetsim.util.Util;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Splits a config file in to key value pairs
 * and stores them in to the database.
 *
 * @author Callum McClean
 */

@Component
@Transactional
public class ConfigWorker extends AbstractWorker
{
    protected Logger logger = Logger.getLogger(ConfigWorker.class);

    @Autowired
    private ConfigRepository configRepository;

    /**
     *  Method overrides AbstractWorker and runs when this worker is
     *  called.
     *
     * @throws WorkerException
     */
    @Override
    protected void execute() throws WorkerException {
        logger.info("Loading configs...");
        List<Config> configs = this.createConfig();
        logger.info(configs.size() + " configurations loaded.");

        logger.info("Saving to database...");
        configRepository.save(configs);
        System.out.println(configRepository.findAll(new Sort(Sort.Direction.DESC, "configId")).iterator().next().getConfigId());
        logger.info("Saved to database.");
    }

    /**
     *
     * Using the Util readFile method, config.txt is split
     * in to key pairs and stored in a map. These values
     * are used to create a Config object which is saved
     * into the database for use by other classes.
     *
     * @return List<Config>
     */
    public List<Config> createConfig() {
        List<Config> configs = new ArrayList<>();

        List<String> stringList = Util.readFile("config.txt");
        Config c;
        Integer line = 1;

        for (String s : stringList) {
            String[] split = s.split(",");

            if(split.length !=  5){
                logger.error("Too many or too few values provided. Please check config.txt");

                System.exit(0);
            }

            Map<String, String> map = new HashMap<String, String>();

            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].trim();
                String[] pair = split[i].split(":");
                map.put(pair[0], pair[1]);
            }

            for(Map.Entry<String, String> entry : map.entrySet()) {
                try {
                    Integer.parseInt(entry.getValue());
                } catch( Exception e ) {
                    logger.error("The value supplied for " + entry.getKey() + " must be an Integer");
                    System.exit(0);
                }
            }

            if(Integer.parseInt(map.get("maxFollowers")) > Integer.parseInt(map.get("users")) - 1) {
                logger.error("Maximum number of followers can not exceed number of users - 1");
                System.exit(0);
            }


            int numUsers = Integer.parseInt(map.get("users"));
            int maxTweets = Integer.parseInt(map.get("maxTweets"));
            int minTweets = Integer.parseInt(map.get("minTweets"));
            int maxFollowers = Integer.parseInt(map.get("maxFollowers"));
            int minFollowers = Integer.parseInt(map.get("minFollowers"));

            c = new Config(
                    numUsers,
                    maxTweets,
                    minTweets,
                    maxFollowers,
                    minFollowers
            );

            configs.add(c);
        }

        return configs;
    }
}
