package uk.ac.ncl.tweetsim.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jonathan Carlton
 */
@Configuration
@ComponentScan(basePackages = {"uk.ac.ncl.tweetsim", "br.les.opus.twitter"})
public class AppConfig 
{

}
