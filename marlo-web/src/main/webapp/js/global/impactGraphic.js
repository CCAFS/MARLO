$(function() { // on dom ready

  var url = baseURL + "/impactPathway/impactPathwayGraph.do";
  var graphicContent = "mini-graphic";
  var panningEnable = false;
  var crpProgram = $("input[name='crpProgramID']").val();
  var section = $("input[name='actionName']").val().split('/');

  $.ajax({
      url: url,
      type: 'GET',
      dataType: "json",
      data: {
          crpProgramID: crpProgram,
          sectionName: section[1]
      }
  }).done(function(m) {
    createGraphic(m.elements, graphicContent, panningEnable, false);

  });

  // function to create a new graph

  function createGraphic(json,graphicContent,panningEnable,inPopUp) {
    var infoRelations;
    var cy = cytoscape({
        container: document.getElementById(graphicContent),

        boxSelectionEnabled: false,
        autounselectify: true,
        minZoom: 0.2,
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
            'background-color': '#163799',
            'line-color': 'blue'
        }).selector('.eater').css({
            'border-width': 9,
            'color': 'white'
        }).selector('edge').css({
            'width': 6,
            'source-arrow-shape': 'triangle',
            'target-arrow-shape': 'circle',
            'line-color': '#999999',
            'source-arrow-color': '#999999',
            'target-arrow-color': '#999999',
            'curve-style': 'bezier'
        }).selector('.center-center').css({
            'text-valign': 'center',
            'text-halign': 'center'
        }),

        elements: json,

        layout: {
            name: 'breadthfirst',
            directed: true,
            padding: false
        }
    });

// cy init

    // Nodes init
    var nodesInit = cy.$('node');
    nodesInit.addClass('center-center');

// tap a node
    cy.on('tap', function(event) {

      cy.$('node').css('background-opacity', '0.4');
      cy.$('node').css('text-opacity', '0.4');
      $(".panel-body ul").empty();
      infoRelations = [];

      if(event.cyTarget == cy) {

        cy.$('node').removeClass('eating');
        cy.$('node').css('background-opacity', '1');
        cy.$('node').css('text-opacity', '1');

      } else if(event.cyTarget.isEdge()) {

        cy.$('node').removeClass('eating');
        cy.$('node').css('background-opacity', '1');
        cy.$('node').css('text-opacity', '1');

      } else if(event.cyTarget.isNode()) {

        cy.$('node').removeClass('eating');
        var $this = event.cyTarget;
        console.log($this);
        nodeSelected($this);
        var successors = $this.successors();
        var predecessors = $this.predecessors();

        predecessors.forEach(function(ele) {
          nodeSelected(ele);
        });

        successors.forEach(function(ele) {
          nodeSelected(ele);
        });

        if(inPopUp === true) {

          // console.log(infoRelations);
          infoRelations.forEach(function(ele) {
            $(".panel-body ul").append("<li>" + ele + "</li>")
          });

        }

      }
    });

    function nodeSelected(ele) {
      ele.addClass('eating');
      ele.css('background-opacity', '1');
      ele.css('text-opacity', '1');
      ele.css('z-index', '3');
      if(ele.data('description') != 'undefined' && ele.data('description') != null) {
        infoRelations.push(ele.data('description'));
      }
    }
  }

  $("#mini-graphic").mouseenter(function() {
    $("#overlay").css("display", "block");
  })

  $("#mini-graphic").mouseleave(function() {
    $("#overlay").css("display", "none");
  })

  // Open PopUp Graph
  $("#overlay span").on("click", function() {
    $("#impactGraphic-content").dialog({
        resizable: false,
        width: '85%',
        modal: true,
        height: $(window).height() * 0.70,
        show: {
            effect: "blind",
            duration: 500
        },
        hide: {
            effect: "fadeOut",
            duration: 500
        },
        open: function(event,ui) {
          $.ajax({
              url: url,
              type: 'GET',
              dataType: "json",
              data: {
                  crpProgramID: crpProgram,
                  sectionName: section[1]
              }
          }).done(function(m) {
            createGraphic(m.elements, "impactGraphic", true, true);
          });
        }
    });

  });

});
