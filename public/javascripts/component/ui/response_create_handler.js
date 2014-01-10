define(function (require) {
  'use strict';

  var defineComponent = require('flight/lib/component');

  return defineComponent(responseCreateHandler);

  function responseCreateHandler () {
    // see: https://github.com/rogeliog/learn-flight-navigation-menu-demo/blob/master/app/js/component/ui/menu_title.js
    this.responseUpdateHandler = function (e, data) {
      // visually indicate handles are valid or invalid
      this.createPieChart(data.responses);
      var message = "<span class='glyphicon glyphicon-saved'></span> You have <strong>answered</strong> successfully.";
      var $alert = $(".alert");
      if ($alert.length === 0) {
        $("<div class='alert alert-success'>"+ message +"</div>").insertBefore($("#poll-description"));
      } else {
        $alert.hide("fast").html(message).show("fast");
      }
    }

    this.createPieChart = function (pieData) {
      var color = d3.scale.category20();
      $("#chart").html("");
      var width = 100;
      var height = 100;
      var radius = Math.min(width, height) / 2;

      var svg = d3.select("#chart").append("svg")
        .attr("width", width)
        .attr("height", height)
      .append("g")
        .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

      var arc = d3.svg.arc()
        .innerRadius(radius - radius/3)
        .outerRadius(radius - radius/6);

      function arcTween(a) {
        var i = d3.interpolate(this._current, a);
        this._current = i(0);
        return function(t) {
          return arc(i(t));
        };
      }

      var pie = d3.layout.pie().value(function (d) {
        return d.count;
      });

      var path = svg.datum(pieData).selectAll("path")
          .data(pie)
        .enter().append("path")
          .attr("fill", function(d, i) {
            return color(i); })
          .attr("d", arc)
          .each(function (d) {
            this._current = d; });

      pie.value(function(d) {
        return d.count; });
      path = path.data(pie);
      path.transition().duration(750).attrTween("d", arcTween);

      $("span.response_count").remove();
      pieData.forEach(function (ea, idx) {
        var $label = $("input#choice_id_"+ea.choiceId).parent("label");
        var $appendElement = $("<span class='response_count'><em>"+ ea.count +"</em></span>")
          .css("background-color", color(idx)).css("border-width", "2px").css("padding", "2px 12px").css("border-radius", "3px");
        $label.append($appendElement);
      });
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
