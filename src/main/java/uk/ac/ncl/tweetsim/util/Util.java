package uk.ac.ncl.tweetsim.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * General utility class for common operations that
 * occur throughout the code base.
 *
 * @author Jonathan Carlton
 */
public class Util 
{
    private static Logger logger = Logger.getLogger(Util.class);

    private static final String DATA_DIR = "data/";

    /**
     * Returns a list of CSVs (Strings).
     *
     * The file needs to be in the data directory and
     * that the strings within the file are in the order
     * of the object variable declaration.
     *
     * For example (User):
     * twitterId, screenName, consumerKey, consumerSecret,
     * accessToken, accessTokenSecret \n
     *
     * @param fileName  the name of the file to read in,
     *                  including the file extension.
     * @return          List of CSV strings.
     */
    public static List<String> readFile(String fileName) {
        BufferedReader br = null;
        List<String> list = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(DATA_DIR + fileName));

            String cLine;
            while ((cLine = br.readLine()) != null) {
                list.add(cLine);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ie) {
                logger.error(ie.getMessage(), ie);
                ie.printStackTrace();
            }
        }
        return list;
    }
}
