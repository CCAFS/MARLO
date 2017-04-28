var initMode = true;
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

  var SLO;
  var IDO;
  var subIDO;
  var crps;
  var flagships;
  var outcomes;
  var clusters;
  var keyOutputs;
  cy = cytoscape({
      container: document.getElementById(graphicContent),

      boxSelectionEnabled: false,
      autounselectify: true,
      minZoom: 0.1,
      maxZoom: 5.5,
      zoomingEnabled: true,
      userZoomingEnabled: true,
      userPanningEnabled: panningEnable,

      style: cytoscape.stylesheet().selector('node').css({
          'shape': 'roundrectangle',
          'height': 30,
          'width': 110,
          'background-fit': 'cover',
          'border-width': 0.7,
          'border-opacity': 0.7,
          'label': 'data(label)',
          'background-color': '#2388ae',
          'color': 'white',
          'background-opacity': 0.2,
          'text-opacity': 0.2,
          'text-outline-width': 1,
          'text-outline-color': '#888',
          'z-index': '5',
          'padding': 2
      }).selector('.eating').css({
          'background-opacity': 1,
          'text-opacity': 1,
      }).selector('edge').css({
          'width': 0.2,
          'source-arrow-shape': 'triangle',
          'line-color': '#999999',
          'source-arrow-color': '#999999',
          'arrow-resize': 15,
          'curve-style': 'bezier',
          'z-index': '1'
      }).selector('.center-center').css({
          'text-valign': 'center',
          'text-halign': 'center'
      }).selector('.bottom-center').css({
          'text-valign': 'bottom',
          'text-halign': 'center'
      }),

      elements: json,

      layout: {
        name: 'preset'
      }
  });

// cy init

  // Nodes init
  var nodesInit = cy.$('node');
  var colorFlagship;
  nodesInit.addClass('center-center');
  nodesInit.forEach(function(ele) {
    ele.css('background-color', ele.data('color'));

    if(ele.data('type') === 'F') {
      colorFlagship = ele.data('color');
      ele.css({
          'shape': 'rectangle',
          'border-color': '#884809',
          'color': '#884809',
          'text-outline-width': 0
      });
      if(ele.children().length > 0) {
        ele.css({

        });
        ele.addClass('bottom-center');
      }
    }

    if(ele.data('type') === 'O') {
      colorFlagship = ele.data('color');
      ele.css({
          'shape': 'rectangle',
          'background-color': '#F5F5F5',
          'color': '#884809',
          'text-outline-width': 0
      });
    }

    if(ele.data('type') === 'CoA') {
      ele.css({
          'shape': 'rectangle',
          'background-color': '#F5F5F5',
          'border-color': colorFlagship,
          'color': '#F5F5F5',
          'text-outline-width': 0
      });
      if(ele.children().length > 0) {
        ele.css({

        });
        ele.addClass('bottom-center');
      } else {
        ele.css({
          'color': colorFlagship
        });
      }
    }
  });
  cy.$('node').addClass('eating');

  if(inPopUp === true) {
    /*
     * cy.panzoom({ // options here... }); $(".cy-panzoom").css('position', 'absolute'); $(".cy-panzoom").css("right",
     * '10%'); $(".cy-panzoom").css('top', '17%'); $(".cy-panzoom").css('z-index', '99');
     */
  }

// tap a node
  cy.on('tap', function(event) {

    $(".panel-body ul").empty();

    SLO = [];
    IDO = [];
    subIDO = [];
    crps = [];
    flagships = [];
    outcomes = [];
    clusters = [];
    keyOutputs = [];

    if(event.cyTarget == cy) {

      cy.$('node').addClass('eating');

    } else if(event.cyTarget.isEdge()) {

      cy.$('node').addClass('eating');

    } else if(event.cyTarget.isNode()) {

      cy.$('node').removeClass('eating');
      var $this = event.cyTarget;
      // change Styles
      if(inPopUp === true) {
        $.ajax({
            'url': baseURL + '/impactPathway/relationsimpactPathway.do',
            'data': {
                id: $this._private.data.id,
                type: $this._private.data.type
            },
            beforeSend: function() {
              $("#loader").show();
            },
            success: function(data) {

              $.each(data.relations, function(i,e) {

                if(e.type == "SLO") {
                  $(".panel-body ul").append("<label>" + e.label + ":</label><li>" + e.description + "</li>");
                }
                if(e.type == "IDO") {
                  $(".panel-body ul").append("<label>" + e.label + ":</label><li>" + e.description + "</li>");
                  console.log(e);
                }
                if(e.type == "SD") {
                  $(".panel-body ul").append("<label>" + e.label + ":</label><li>" + e.description + "</li>");
                }
                if(e.type == "F") {
                  $(".panel-body ul").append("<label>" + e.label + ":</label><li>" + e.description + "</li>");
                }
                if(e.type == "O") {
                  $(".panel-body ul").append("<label>" + e.label + ":</label><li>" + e.description + "</li>");
                }
                if(e.type == "CoA") {
                  $(".panel-body ul").append("<label>" + e.label + ":</label><li>" + e.description + "</li>");
                }
                if(e.type == "KO") {
                  $(".panel-body ul").append("<label>" + e.label + ":</label><li>" + e.description + "</li>");
                }
                selectNode(e.id);

              });

            },
            complete: function() {
              $("#loader").fadeOut(500);
            }
        });
      }
    }
  });

  function selectNode(id) {
    var node = cy.$('#' + id);
    node.addClass('eating');
// node.connectedEdges();
// console.log(node.connectedEdges());
  }

  // Download

  $("#buttonDownload").on("click", function() {
    var image = new Image();
    image = cy.jpg({
      full: true
    });
    var name = "impactPathway_Graphic";
    $('a.download').attr({
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
// Controls

$(".tool").on("click", function() {

  var level = cy.zoom();
  var action = $(this).attr("id");
  switch (action) {
    case "zoomIn":
      level += 0.2;
      cy.zoom({
        level: level
      });
      break;
    case "zoomOut":
      level -= 0.2;
      cy.zoom({
        level: level
      });
      break;
    case "panRight":
      cy.panBy({
          x: -100,
          y: 0
      });
      break;
    case "panDown":
      cy.panBy({
          x: 0,
          y: -100
      });
      break;
    case "panLeft":
      cy.panBy({
          x: 100,
          y: 0
      });
      break;
    case "panUp":
      cy.panBy({
          x: 0,
          y: 100
      });
      break;
    case "resize":
      cy.zoom({
        level: 1
      });
      cy.center();
      break;
  }
})
// EVENTS

$("#mini-graphic").on("mouseenter", function() {
  $("#overlay").css("display", "block");
});

$("#mini-graphic").on("mouseleave", function() {
  $("#overlay").css("display", "none");
});

// Open PopUp Graph
$("#overlay .btn").on("click", function() {
  $("#impactGraphic-content").dialog({
      resizable: false,
      width: '90%',
      closeText: "",
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

$("#changeGraph .btn").on("click", function() {
  if($(this).hasClass("currentGraph")) {
    var url = baseURL + "/impactPathway/impactPathwayFullGraph.do";
    var dataFull = {
      crpID: currentCrpID
    }
    ajaxService(url, dataFull, "impactGraphic", true, true, 'concentric', false);
    $(this).html("Show section graph");
    $(this).addClass("fullGraph");
    $(this).removeClass("currentGraph");
  } else {
    $(this).html("Show full graph");
    var url = baseURL + "/impactPathway/impactPathwayGraph.do";
    ajaxService(url, data, "impactGraphic", true, true, 'breadthfirst', false);
    $(this).removeClass("fullGraph");
    $(this).addClass("currentGraph");
  }

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
    if(initMode == true) {
      showHelpText();
      setViewMore();
      initMode = false;
    }
    console.log("done");
    var nodes = m.elements.nodes;
    var count = {
        SLO: 0,
        IDO: 0,
        SD: 0,
        F: 0,
        O: 0,
        CoA: 0,
        KO: 0,
    };
    var totalWidth = {
        SLO: 0,
        IDO: 0,
        SD: 0,
        F: 0,
        O: 0,
        CoA: 0,
        KO: 0,
    };
    var nodeWidth = 110;
    var nodeMargin = 20;

    // For to count and set position
    for(var i = 0; i < nodes.length; i++) {
      if(nodes[i].data.type == "SLO") {
        count.SLO++;
      } else if(nodes[i].data.type == "IDO") {
        count.IDO++;
      } else if(nodes[i].data.type == "SD") {
        count.SD++;
      } else if(nodes[i].data.type == "F") {
        count.F++;
      } else if(nodes[i].data.type == "O") {
        count.O++;
      } else if(nodes[i].data.type == "CoA") {
        count.CoA++;
      } else if(nodes[i].data.type == "KO") {
        count.KO++;
      }
    }

    totalWidth.SLO = count.SLO * (nodeWidth + nodeMargin);
    totalWidth.IDO = count.IDO * (nodeWidth + nodeMargin);
    totalWidth.SD = count.SD * (nodeWidth + nodeMargin);
    totalWidth.F = count.F * (nodeWidth + nodeMargin);
    totalWidth.O = count.O * (nodeWidth + nodeMargin);
    totalWidth.CoA = count.CoA * (nodeWidth + nodeMargin);
    totalWidth.KO = (count.KO * (nodeWidth + nodeMargin)) + totalWidth.CoA;

    var move = {
        SLO: -(totalWidth.SLO / 2),
        IDO: -(totalWidth.IDO / 2),
        SD: -(totalWidth.SD / 2),
        F: -(totalWidth.F / 2),
        O: -(totalWidth.O / 2),
        CoA: -(totalWidth.CoA / 2),
        KO: -(totalWidth.KO / 2),
    };

    for(var i = 0; i < nodes.length; i++) {
      if(nodes[i].data.type == "SLO") {
        move.SLO = (move.SLO + (nodeWidth + nodeMargin));
        nodes[i].position = {
            x: move.SLO,
            y: -200
        };
      } else if(nodes[i].data.type == "IDO") {
        move.IDO = (move.IDO + (nodeWidth + nodeMargin));
        nodes[i].position = {
            x: move.IDO,
            y: -100
        };
      } else if(nodes[i].data.type == "SD") {
        move.SD = (move.SD + (nodeWidth + nodeMargin));
        nodes[i].position = {
            x: move.SD,
            y: 0
        };
      } else if(nodes[i].data.type == "F") {
        move.F = (move.F + (nodeWidth + nodeMargin));
        nodes[i].position = {
            x: move.F,
            y: 0
        };
      } else if(nodes[i].data.type == "O") {
        move.O = (move.O + (nodeWidth + nodeMargin));
        nodes[i].position = {
            x: move.O,
            y: 200
        };
      } else if(nodes[i].data.type == "CoA") {
        if(nodes[i + 1] && nodes[i + 1].data.type == "KO") {
          move.KO;
        } else {
          move.KO = (move.KO + (nodeWidth + nodeMargin + 20));
        }

        // console.log(move.KO);
        nodes[i].position = {
            x: move.KO,
            y: 400
        };
      } else if(nodes[i].data.type == "KO") {
        move.KO = (move.KO + (nodeWidth + nodeMargin + 20));
        // console.log(move.KO);
        nodes[i].position = {
            x: move.KO,
            y: 400
        };
      }
    }

    createGraphic(m.elements, contentGraph, panningEnable, inPopUp, 'breadthfirst', tooltip);
  });
}

function showHelpText() {
  $('.helpMessage').show();
  $('.helpMessage').addClass('animated flipInX');
}