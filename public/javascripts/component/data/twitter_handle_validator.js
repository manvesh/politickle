define(function (require) {
  'use strict';

  var defineComponent = require('flight/lib/component');
  return defineComponent(twitterHandleValidator);

  function twitterHandleValidator() {
    this.validateHandles = function (e, data) {
      // TODO: make request to https://api.twitter.com/1.1/users/lookup.json
      // See: https://dev.twitter.com/docs/api/1.1/get/users/lookup

      // On success:
      this.trigger('dataHandlesValidated', {
        users: {'@MicheleBachmann': 14244109, '@HilaryClinton': 18217624}
      });
    }

    this.after('initialize', function () {
      this.on('uiNeedsHandlesValidated', this.validateHandles);
    });
  }
});
