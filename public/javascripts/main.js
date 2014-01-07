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

      require(['page/default'], function(initializeDefault) {
        initializeDefault();
      });
  }
);