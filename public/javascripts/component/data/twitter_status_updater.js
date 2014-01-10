define(function (require) {
    'use strict';

    var defineComponent = require('flight/lib/component');
    return defineComponent(twitterStatusUpdater);

    function twitterStatusUpdater() {
        this.updateStatus = function (e, data) {
            var that = this;
            $.post(data.endpoint, data.tweet).done(function (res) {
                var choicesData = res.responses;
                that.trigger('tweetCreationSuccess', {"success": true});
            }).fail(function () {
                    that.trigger('tweetCreationFailure', {});
                });
        }

        this.after('initialize', function () {
            this.on('uiNeedsTweetCreated', this.updateStatus);
        });
    }
});
