define(function (require) {
  'use strict';

  var responseCreateHandler = require('component/ui/response_create_handler');
  var createResponseParser = require('component/data/create_response_parser');

  return initialize;

  function initialize(initData) {
    responseCreateHandler.attachTo('form[name="user-response"]');
    createResponseParser.attachTo(document);
  }
});
