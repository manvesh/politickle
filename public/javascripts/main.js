requirejs.config({
  baseUrl: '',
  paths: {
    'flight': '/assets/bower_components/flight',
    'component': '/assets/javascripts/component',
    'page': '/assets/javascripts/page'
  }
});

require(
  [
    'flight/lib/compose',
    'flight/lib/registry',
    'flight/lib/advice',
    'flight/lib/logger',
    'flight/lib/debug'
  ],

  function(compose, registry, advice, withLogging, debug) {
      debug.enable(true);
      compose.mixin(registry, [advice.withAdvice, withLogging]);

      var pageComponentsToRequire = ['page/default'];

      var initData = JSON.parse($("#init-data").val());
      if (typeof(initData.pageName) != "undefined") {
        pageComponentsToRequire.push('page/' + initData.pageName);
      }

      require(pageComponentsToRequire, function() {
        var args = Array.prototype.slice.call(arguments, 0);
        args.forEach(function (pageInitializer) {
          pageInitializer.call(initData);
        });
      });
  }
);
