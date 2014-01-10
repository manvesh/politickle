define(function (require) {
    'use strict';

    var defineComponent = require('flight/lib/component');

    return defineComponent(twitterUpdateField);

    function twitterUpdateField () {
        // see: https://github.com/rogeliog/learn-flight-navigation-menu-demo/blob/master/app/js/component/ui/menu_title.js
        this.twitterStatusUpdated = function (e, data) {
            $("#tweetSuccess").removeClass("hidden")
            $("#tweetSuccess").show()
        }

        this.twitterStatusUpdateFailure = function (e, data) {
            $("#tweetFailure").removeClass("hidden")
            $("#tweetFailure").show()
        }

        this.updateTwitterStatus = function (e) {
            e.preventDefault();
            $("#tweetFailure").addClass("hidden")
            $("#tweetSuccess").addClass("hidden")
            var statusData = {
                "endpoint": this.$node.attr("action"),
                "tweet": this.$node.serialize()
            };
            this.trigger('uiNeedsTweetCreated', statusData);
        };

        this.after('initialize', function () {
            this.on('submit', this.updateTwitterStatus);
            this.on(document, 'tweetCreationSuccess', this.twitterStatusUpdated);
            this.on(document, 'tweetCreationFailure', this.twitterStatusUpdateFailure);

        });
    }
})
