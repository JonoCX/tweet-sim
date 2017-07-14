# Tweet Sim

This is the Twitter bot network simulation program. The original plan for this program was for it to spawn and manage an actual network of users live on Twitter, however, the direction was changed during the creation of this program. This program is now able to create synthetic users, tweets, and connections (between those users), and then inject the synthetic data into actual Twitter data.

### Requirements
First and foremost, this requires that you have the bot network API installed locally in your Maven (`~./m2/`) directory. To do this, follow the instructions on the bot network API repository (no direct link as the location may have moved since writing this).

The PUC-Rio Twitter API also needs to be installed in your local Maven directory. However, there are further dependencies required. From the OpusDengue bitbucket repo, clone the following: commons-geojson and commons-persistence. Install these by doing the following (within each of the clone directories):

`$ mvn -f clean`

`$ mvn install -DskipTests`

Both Callum and I made changes to; [twitter-api](https://github.com/JonoCX/twitter-api), [tweet-classifier](https://github.com/JonoCX/tweets-classifier), and [data-crawler](https://github.com/JonoCX/tweet-sim-crawler). If these have not been moved to the OpusDengue bitbucket then you'll need to clone them from my ([JonoCX](https://github.com/JonoCX/)) github account. Once cloned, follow the same install steps as above.

### Usage
The program presents several options for running, depending on what you're trying to achieve. Open the program, and put your `config.txt` file into the `data/` directory. The format of the file should be the following:

`users:x, minTweets:y, maxTweets:z, minFollowers:a, maxFollowers:b`

The configuration file does allow multiple configurations to be ran at the same time, and the content that is generated for each is easily identifiable through an id.

There are several running options, however, the main on is `controller`. They are all defined in the runner class and it just requires that you pass one of the arguments to the program at run-time.

### What does it do?
Depending on the configuration file provided, the program will generate a set of users, tweets, and connections. These entities are stored in the `bot_network` schema via the bot network API. The number of entities generated is a randomly selected number between the minimum and maximum values (both tweets and followers).

Once these entities have been generated and stored, they are then injected into the actual Twitter data stored in the `dengue` database and `twitter` schema. The injection process is essentially a task that takes the previously generated entities, converts them to the entities within the PUC-Rio Twitter API, and then stores them in actual collected data.

This whole process is automated by supplying the `controller` option to the program at run-time. However, you can run them individually by supplying the respective option at run-time.

### What can you do with the data?
Well, the synthetic data is injected as actual Twitter data so it is subject to the same ranking processes.

### Running TwitterRank
We've designed this to work specifically with the TwitterRank process. The ranking approach works on the `tweet_metadata` table in the `twitter` schema. At the point of injection, this table is updated so the synthetic data is present there.

Clone the twitter-rank repository, open it, and run the main method within the Main class.

Remember; ensure that you have configured hibernate properly, i.e. change the username/password in the `hibernate-config.xml` file to match a user with access to your local Postgres database. This applies for all of the projects where hibernate is used!

### Misc
