define(function (require) {
  'use strict';

  var defineComponent = require('flight/lib/component');
  return defineComponent(twitterHandleValidator);

  function twitterHandleValidator() {
    this.validateHandles = function (e, data) {
      var requestData = {};

      // Convert ["@a", "@b"] to {"handles[0]": "a", "handles[1]": "b"}
      data.handles.forEach(function (handle, idx) {
        requestData['handles[' + idx + ']'] = handle.replace(/@/, "");
      });

      $.ajax({
        url: '/twitter/users',
        dataType: 'json',
        data: requestData,
        success: function (data) {
          console.log(data);
          this.trigger('dataHandlesValidated', {
            users: data
          });
        }.bind(this)
      });
    }

    this.after('initialize', function () {
      this.on('uiNeedsHandlesValidated', this.validateHandles);
    });
  }
});
