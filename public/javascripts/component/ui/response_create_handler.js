define(function (require) {
  'use strict';

  var defineComponent = require('flight/lib/component');

  return defineComponent(responseCreateHandler);

  function responseCreateHandler () {
    // see: https://github.com/rogeliog/learn-flight-navigation-menu-demo/blob/master/app/js/component/ui/menu_title.js
    this.responseUpdateHandler = function (e, data) {
      // visually indicate handles are valid or invalid
      var message = "<span class='glyphicon glyphicon-saved'></span> You have <strong>answered</strong> successfully.";
      var $alert = $(".alert");
      if ($alert.length === 0) {
        $("<div class='alert alert-success'>"+ message +"</div>").insertBefore($("#poll-description"));
      } else {
        $alert.hide("fast").html(message).show("fast");
      }
    }

    this.submitUserResponse = function (e) {
      e.preventDefault();
      var responseData = {
        "endpoint": this.$node.attr("action"),
        "postData": this.$node.serialize()
      };
      this.trigger('uiUserResponseSubmitted', responseData);
    };

    this.after('initialize', function () {
      this.on('submit', this.submitUserResponse);
      this.on(document, 'responseCreationSuccess', this.responseUpdateHandler);
    });
  }
})
