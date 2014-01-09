define(function (require) {
  'use strict';

  var twitterHandleField = require('component/ui/twitter_handle_field')
  var twitterHandleValidator = require('component/data/twitter_handle_validator');

  return initialize;

  function initialize() {
    twitterHandleField.attachTo('input[name="pollTargetHandles"');
    twitterHandleValidator.attachTo(document);
  }
});
