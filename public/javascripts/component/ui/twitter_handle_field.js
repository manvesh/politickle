define(function (require) {
  'use strict';

  var defineComponent = require('flight/lib/component');

  return defineComponent(twitterHandleField);

  function twitterHandleField () {
    // see: https://github.com/rogeliog/learn-flight-navigation-menu-demo/blob/master/app/js/component/ui/menu_title.js
    this.dataHandlesValidated = function (e, data) {
      // visually indicate handles are valid or invalid
      console.log("Validated handles: ");
      console.log(data);
    }

    this.validateTwitterHandles = function (e) {
      var handlesStr = this.$node.val();
      this.trigger('uiNeedsHandlesValidated', handlesStr.split(' '));
    };

    this.after('initialize', function () {
      this.on('change', this.validateTwitterHandles);
      this.on(document, 'dataHandlesValidated', this.dataHandlesValidated);
    });
  }
})
