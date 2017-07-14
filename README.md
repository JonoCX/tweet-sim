# Tweet Sim

This is the Twitter bot network simulation program. The original plan for this program was for it to spawn and manage an actual network of users live on Twitter, however, the direction was changed during the creation of this program. This program is now able to create synthetic users, tweets, and connections (between those users), and then inject the synthetic data into actual Twitter data.

### Requirements
First and foremost, this requires that you have the bot network API installed locally in your Maven (`~./m2/`) directory. To do this, follow the instructions on the bot network API repository (no direct link as the location may have moved since writing this).

The PUC-Rio Twitter API also needs to be installed in your local Maven directory. However, there are further dependencies required. From the OpusDengue bitbucket repo, clone the following: commons-geojson and commons-persistence. Install these by doing the following (within each of the clone directories):

`$ mvn -f clean`
`$ mvn install -DskipTests`

Both Callum and I made changes to; [twitter-api](https://github.com/JonoCX/twitter-api), [tweet-classifier](https://github.com/JonoCX/tweets-classifier), and [data-crawler](https://github.com/JonoCX/tweet-sim-crawler). If these have not been moved to the OpusDengue bitbucket then you'll need to clone them from my ([JonoCX](https://github.com/JonoCX/)) github account. Once cloned, follow the same install steps as above.

### Usage

### Misc
