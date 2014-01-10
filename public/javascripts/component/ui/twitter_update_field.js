define(function (require) {
    'use strict';

    var defineComponent = require('flight/lib/component');

    return defineComponent(twitterUpdateField);

    function twitterUpdateField () {
        // see: https://github.com/rogeliog/learn-flight-navigation-menu-demo/blob/master/app/js/component/ui/menu_title.js
        this.dataHandlesValidated = function (e, data) {

        }

        this.updateTwitterStatus = function (e) {
            e.preventDefault();
            var statusData = {
                "postData": this.$node.serialize()
            };
            this.trigger('updateStatusSubmitted', statusData);
        };

        this.after('initialize', function () {
            this.on('submit', this.updateTwitterStatus);
            this.on(document, 'dataHandlesValidated', this.dataHandlesValidated);
        });
    }
})
/**
 * Created by avirmani on 1/9/14.
 */
