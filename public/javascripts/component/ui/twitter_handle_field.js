define(function (require) {
  'use strict';

  var defineComponent = require('flight/lib/component');

  return defineComponent(twitterHandleField);

  function twitterHandleField () {
    // see: https://github.com/rogeliog/learn-flight-navigation-menu-demo/blob/master/app/js/component/ui/menu_title.js
    this.dataHandlesValidated = function (e, data) {
      // visually indicate handles are valid or invalid
      var hiddenInputs = data.users.map(function (userId, idx) {
        return '<input class="twitter-user-id" type="hidden" name="pollTargets[' + idx + '].twitterUserId" value="' + userId + '" />'
      });

      $('form#new-poll').find('input.twitter-user-id').remove();
      $('form#new-poll').append(hiddenInputs.join(""));

    }

    this.validateTwitterHandles = function (e) {
      var handlesStr = this.$node.val();
      // If the string has content and it's not only spaces
      if (handlesStr.length > 0 && handlesStr.trim().length > 0) {
        this.trigger('uiNeedsHandlesValidated', {handles: handlesStr.trim().split(/[ ,]+/)});
      }
    };

    this.after('initialize', function () {
      this.on('change', this.validateTwitterHandles);
      this.on(document, 'dataHandlesValidated', this.dataHandlesValidated);
    });
  }
})
