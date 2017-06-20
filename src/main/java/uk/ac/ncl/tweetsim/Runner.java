package uk.ac.ncl.tweetsim;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * @author Jonathan Carlton
 */
@Component
@Transactional
public class Runner 
{
    protected static Logger logger = Logger.getLogger(Runner.class);
}
