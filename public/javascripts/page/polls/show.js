define(function (require) {
  'use strict';

  var responseCreateHandler = require('component/ui/response_create_handler');
  var createResponseParser = require('component/data/create_response_parser');
  var twitterUpdateField = require('component/data/twitter_update_field');

  return initialize;

  function initialize(initData) {
    responseCreateHandler.attachTo('form[name="user-response"]');
    twitterUpdateField.attachTo('form[name="tweet-response"]');
    createResponseParser.attachTo(document);
  }
});
