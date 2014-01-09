define(function (require) {
  'use strict';

  var defineComponent = require('flight/lib/component');

  return defineComponent(responseCreateHandler);

  function responseCreateHandler () {
    // see: https://github.com/rogeliog/learn-flight-navigation-menu-demo/blob/master/app/js/component/ui/menu_title.js
    this.responseUpdateHandler = function (e, data) {
      // visually indicate handles are valid or invalid
      console.log("Updates UI to reflect new response.");
      console.log(data);
    }

    this.submitUserResponse = function (e) {
      var responseData = this.$node.val();
      this.trigger('uiUserResponseSubmitted', responseData);
    };

    this.after('initialize', function () {
      this.on('submit', this.submitUserResponse);
      this.on(document, 'userResponseUpdated', this.responseUpdateHandler);
    });
  }
})
