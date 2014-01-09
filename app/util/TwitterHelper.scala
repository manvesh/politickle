package util

import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Twitter, TwitterFactory}
import models.User
import play.Play


object TwitterHelper {
  def getTwitter4JInstance(user: User) = {
    for (accessToken <- user.accessToken; secret <- user.secret)
    yield {
      val twitterKey = Play.application().configuration().getString("securesocial.twitter.consumerKey")
      val twitterSecret = Play.application().configuration().getString("securesocial.twitter.consumerSecret")

      val cb = new ConfigurationBuilder()
      cb.setDebugEnabled(true)
        .setOAuthConsumerKey(twitterKey)
        .setOAuthConsumerSecret(twitterSecret)
        .setOAuthAccessToken(accessToken)
        .setOAuthAccessTokenSecret(secret)
      val tf = new TwitterFactory(cb.build())
      tf.getInstance()
    }
  }
}
