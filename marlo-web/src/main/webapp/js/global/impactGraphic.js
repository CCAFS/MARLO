$(function() { // on dom ready

  var url = baseURL + "/impactPathway/impactPathwayGraph.do";
  var graphicContent = "mini-graphic";
  var panningEnable = false;
  var crpProgram = $("input[name='crpProgramID']").val();
  var sec = $("input[name='actionName']").val();

  if(sec != "" && sec != null && crpProgram != "" && crpProgram != null) {
    var section = sec.split('/');
    data = {
        crpProgramID: crpProgram,
        sectionName: section[1]
    };

    ajaxService(url, data, graphicContent, panningEnable, false, 'breadthfirst', false);
  }
  // function to create a new graph

});

function createGraphic(json,graphicContent,panningEnable,inPopUp,nameLayout,tooltip) {
  var crps;
  var flagships;
  var outcomes;
  var clusters;
  var cy = cytoscape({
      container: document.getElementById(graphicContent),

      boxSelectionEnabled: false,
      autounselectify: true,
      minZoom: 0.1,
      maxZoom: 5.5,
      zoomingEnabled: true,
      userZoomingEnabled: true,
      userPanningEnabled: panningEnable,

      style: cytoscape.stylesheet().selector('node').css({
          'height': 100,
          'width': 100,
          'background-fit': 'cover',
          'border-width': 1,
          'border-opacity': 0.5,
          'label': 'data(label)',
          'background-color': '#2388ae',
          'color': 'white',
          'text-outline-width': 2,
          'text-outline-color': '#888'
      }).selector('.eating').css({
          'border-width': 2,
          'background-color': '#163799'
      }).selector('.eater').css({
          'border-width': 9,
          'color': 'white'
      }).selector('edge').css({
          'width': 6,
          'source-arrow-shape': 'triangle',
          'target-arrow-shape': 'circle',
          'line-color': '#eee',
          'source-arrow-color': '#eee',
          'target-arrow-color': '#eee',
          'curve-style': 'bezier'
      }).selector('.center-center').css({
          'text-valign': 'center',
          'text-halign': 'center'
      }),

      elements: json,

      layout: {
          name: nameLayout,
          directed: true,
          padding: false,
          clockwise: false,
          equidistant: false,
          minNodeSpacing: 5,
          concentric: function(node) { // returns numeric value for each node, placing higher nodes in levels towards
            // the centre
            var weight = 0;
            if(node.data('type') == 'C') {
              weight = 10;
            }
            if(node.data('type') == 'F') {
              weight = 5;
            }
            if(node.data('type') == 'O') {
              weight = 1;
            }
            if(node.data('type') == 'CoA') {
              weight = 1;
            }
            return weight;
          }
      }
  });

// cy init

  // Nodes init
  var nodesInit = cy.$('node');
  nodesInit.addClass('center-center');
  nodesInit.forEach(function(ele) {
    ele.css('background-color', ele.data('color'));
    if(ele.data('type') === 'C') {
      ele.css("text-transform", "uppercase");
    }
  });

  if(inPopUp === true) {
    cy.panzoom({
    // options here...
    });
    $(".cy-panzoom").css('position', 'absolute');
    $(".cy-panzoom").css("right", '10%');
    $(".cy-panzoom").css('top', '15%');
    $(".cy-panzoom").css('z-index', '99');
  }

// tap a node
  cy.on('tap', function(event) {

    cy.$('node').css('background-opacity', '0.4');
    cy.$('node').css('text-opacity', '0.4');
    cy.$('edge').css('line-opacity', '0.4');
    cy.$('edge').css('line-color', '#eee');
    cy.$('edge').css('source-arrow-color', '#eee');
    cy.$('edge').css('target-arrow-color', '#eee');
    cy.$('edge').css('z-index', '1');
    $(".panel-body ul").empty();
    crps = [];
    flagships = [];
    outcomes = [];
    clusters = [];

    if(event.cyTarget == cy) {

      cy.$('node').removeClass('eating');
      cy.$('node').css('background-opacity', '1');
      cy.$('node').css('text-opacity', '1');

    } else if(event.cyTarget.isEdge()) {

      cy.$('node').removeClass('eating');
      cy.$('node').css('background-opacity', '1');
      cy.$('node').css('text-opacity', '1');
      cy.$('edge').css('line-color', '#999999');
      cy.$('edge').css('source-arrow-color', '#999999');
      cy.$('edge').css('target-arrow-color', '#999999');

    } else if(event.cyTarget.isNode()) {

      cy.$('node').removeClass('eating');
      var $this = event.cyTarget;

      var successors = $this.successors();
      var predecessors = $this.predecessors();

      predecessors.forEach(function(ele) {
        nodeSelected(ele);
      });
      nodeSelected($this);
      successors.forEach(function(ele) {
        nodeSelected(ele);
      });

      if(inPopUp === true) {
        // add info in Relations panel
        crps.forEach(function(ele) {
          $(".panel-body ul").append("<label>CRP:</label><li>" + ele + "</li>")
        });
        flagships.forEach(function(ele) {
          $(".panel-body ul").append("<label>" + ele[1] + ":</label><li>" + ele[0] + "</li>")
        });
        outcomes.forEach(function(ele) {
          $(".panel-body ul").append("<label>" + ele[1] + ":</label><li>" + ele[0] + "</li>")
        });
        clusters.forEach(function(ele) {
          $(".panel-body ul").append("<label>" + ele[1] + ":</label><li>" + ele[0] + "</li>")
        });

      }

    }
  });
  function nodeSelected(ele) {
    // change Styles
    ele.addClass('eating');
    ele.css('background-opacity', '1');
    ele.css('text-opacity', '1');
    ele.css('z-index', '3');
    ele.css('line-color', '#999999');
    ele.css('source-arrow-color', '#999999');
    ele.css('target-arrow-color', '#999999');

    // information arrays
    if(ele.data('description') != 'undefined' && ele.data('description') != null) {
      var data = [];
      if(ele.data('type') === 'C') {
        crps.push(ele.data('description'));
      } else if(ele.data('type') === 'F') {
        data.push(ele.data('description'));
        data.push(ele.data('label'));
        flagships.push(data);
      } else if(ele.data('type') === 'O') {
        data.push(ele.data('description'));
        data.push(ele.data('label'));
        outcomes.push(data);
      } else if(ele.data('type') === 'CoA') {
        data.push(ele.data('description'));
        data.push(ele.data('label'));
        clusters.push(data);
      }
    }
  }

  // Download

  $("#buttonDownload").on("click", function() {
    var image = new Image();
    image = cy.jpg();
    var name = "impactPathway_Graphic";
    $('#buttonDownload a').attr({
        href: image,
        download: name
    })
  });

  // tooltip
  if(tooltip) {
    cy.$("node").qtip({
        content: function() {
          var desc = "";
          if(this.data('type') === 'C') {
            return this.data('description');
          } else {
            desc = this.data("label");
            return desc + " - " + this.data('description');
          }

        },
        show: {
          event: 'mouseover'
        },
        hide: {
          event: 'mouseout'
        },
        position: {
            my: 'top center',
            at: 'bottom center'
        },
        style: {
            classes: 'qtip-bootstrap',
            tip: {
                width: 16,
                height: 8
            }
        }
    });
  }

}

// EVENTS

$("#mini-graphic").on("mouseenter", function() {
  $("#overlay").css("display", "block");
});

$("#mini-graphic").on("mouseleave", function() {
  $("#overlay").css("display", "none");
});

// Open PopUp Graph
$("#overlay a").on("click", function() {
  $("#impactGraphic-content").dialog({
      resizable: false,
      width: '90%',
      modal: true,
      height: $(window).height() * 0.80,
      show: {
          effect: "blind",
          duration: 500
      },
      hide: {
          effect: "fadeOut",
          duration: 500
      },
      open: function(event,ui) {
        var url = baseURL + "/impactPathway/impactPathwayGraph.do";
        ajaxService(url, data, "impactGraphic", true, true, 'breadthfirst', false);
      }
  });

});

$(".yes-button-label").on("click", function() {
  var url = baseURL + "/impactPathway/impactPathwayGraph.do";
  ajaxService(url, data, "impactGraphic", true, true, 'breadthfirst', false);
});

$(".no-button-label").on("click", function() {
  var url = baseURL + "/impactPathway/impactPathwayFullGraph.do";
  var data = {
    crpID: currentCrpID
  }
  ajaxService(url, data, "impactGraphic", true, true, 'concentric', false);
});

// Functions

// end nodeSelected function

function ajaxService(url,data,contentGraph,panningEnable,inPopUp,nameLayout,tooltip) {
  $.ajax({
      url: url,
      type: 'GET',
      dataType: "json",
      data: data
  }).done(function(m) {
    createGraphic(m.elements, contentGraph, panningEnable, inPopUp, nameLayout, tooltip);
  });
}