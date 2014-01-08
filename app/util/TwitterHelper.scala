package util

import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Twitter, TwitterFactory}
import models.User


object TwitterHelper {
  def getTwitter4JInstance(user: User) = {
    for (accessToken <- user.accessToken; secret <- user.secret)
    yield {
      val cb = new ConfigurationBuilder()
      cb.setDebugEnabled(true)
        .setOAuthConsumerKey("*********************")
        .setOAuthConsumerSecret("******************************************")
        .setOAuthAccessToken(accessToken)
        .setOAuthAccessTokenSecret(secret)
      val tf = new TwitterFactory(cb.build())
      val twitter = tf.getInstance()
    }
  }
}
