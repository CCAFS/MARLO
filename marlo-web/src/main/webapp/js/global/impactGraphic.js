$(function() { // on dom ready

  var url = baseURL + "/impactPathway/impactPathwayGraph.do";
  var graphicContent = "mini-graphic";
  var panningEnable = false;
  var crpProgram = $("input[name='crpProgramID']").val();
  var section = $("input[name='actionName']").val().split('/');

  var data = {
      crpProgramID: crpProgram,
      sectionName: section[1]
  };

  ajaxService(url, data, graphicContent, panningEnable, false, 'breadthfirst');

  // function to create a new graph

  function createGraphic(json,graphicContent,panningEnable,inPopUp,nameLayout) {
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
            'color': 'white'
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
            minNodeSpacing: 5,
        }
    });

// cy init

    // Nodes init
    var nodesInit = cy.$('node');
    nodesInit.addClass('center-center');

    if(inPopUp === true) {
      cy.panzoom({
      // options here...
      });
      $(".cy-panzoom").css('position', 'absolute');
      $(".cy-panzoom").css("right", '5%');
      $(".cy-panzoom").css('top', '10%');
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
        console.log($this);
        successors.forEach(function(ele) {
          nodeSelected(ele);
        });

        if(inPopUp === true) {
          // add info in Relations panel
          crps.forEach(function(ele) {
            $(".panel-body ul").append("<label>CRP:</label><li>" + ele + "</li>")
          });
          flagships.forEach(function(ele) {
            $(".panel-body ul").append("<label>Flagship:</label><li>" + ele + "</li>")
          });
          outcomes.forEach(function(ele) {
            $(".panel-body ul").append("<label>Outcome:</label><li>" + ele + "</li>")
          });
          clusters.forEach(function(ele) {
            $(".panel-body ul").append("<label>Cluster of Activities:</label><li>" + ele + "</li>")
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
        if(ele.data('type') === 'C') {
          crps.push(ele.data('description'));
        } else if(ele.data('type') === 'F') {
          flagships.push(ele.data('description'));
        } else if(ele.data('type') === 'O') {
          outcomes.push(ele.data('description'));
        } else if(ele.data('type') === 'CoA') {
          clusters.push(ele.data('description'));
        }
      }
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
          ajaxService(url, data, "impactGraphic", true, true, 'breadthfirst');
        }
    });

  });

  $(".yes-button-label").on("click", function() {
    var url = baseURL + "/impactPathway/impactPathwayGraph.do";
    ajaxService(url, data, "impactGraphic", true, true, 'breadthfirst');
  });

  $(".no-button-label").on("click", function() {
    var url = baseURL + "/impactPathway/impactPathwayFullGraph.do";
    var data = {
      crpID: currentCrpID
    }
    ajaxService(url, data, "impactGraphic", true, true, 'concentric');
  });

  // Functions

  // end nodeSelected function

  function ajaxService(url,data,contentGraph,panningEnable,inPopUp,nameLayout) {
    $.ajax({
        url: url,
        type: 'GET',
        dataType: "json",
        data: data
    }).done(function(m) {
      createGraphic(m.elements, contentGraph, panningEnable, inPopUp, nameLayout);
    });
  }

});
