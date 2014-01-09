define(function (require) {
  'use strict';

  var defineComponent = require('flight/lib/component');
  return defineComponent(createResponseParser);

  function createResponseParser() {
    this.submitResponse = function (e, data) {
      var that = this;
      $.post(data.endpoint, data.postData).done(function () {
        that.trigger('responseCreationSuccess', {"success": true});
      }).fail(function () {
        that.trigger('responseCreationFailure', {});
      });
    }
    this.after('initialize', function () {
      this.on(document, 'uiUserResponseSubmitted', this.submitResponse);
    });
  }
});
