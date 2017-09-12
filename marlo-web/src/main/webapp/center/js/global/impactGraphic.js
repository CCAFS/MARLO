$(function() { // on dom ready

  var url = baseURL + "/impactPathwayGraphByProgram.do";
  var graphicContent = "mini-graphic";
  var panningEnable = false;
  var crpProgram = $("input[name='programID']").val();

  if(crpProgram != "" && crpProgram != null) {
    data = {
      programID: crpProgram
    };

    ajaxService(url, data, graphicContent, panningEnable, false, 'breadthfirst', false);
  }
  // function to create a new graph

});

function createGraphic(json,graphicContent,panningEnable,inPopUp,nameLayout,tooltip,nodeWidth) {

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
          'width': nodeWidth,
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
          'width': 1,
          'source-arrow-shape': 'triangle',
          'line-color': '#aaa',
          'source-arrow-color': '#aaa',
          'arrow-resize': 15,
          'curve-style': 'bezier',
          'z-index': '1'
      }).selector('.edgeStyle').css({
          'line-color': '#000000',
          'source-arrow-color': '#000000',
          'curve-style': 'bezier',
          'z-index': '99'
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
    var label = ele.data("label");
    var shortLabel = label;
    ele.data("label", shortLabel);
    ele.css('background-color', ele.data('color'));
    if(ele.data('type') === 'F') {
      colorFlagship = ele.data('color');
    }

    // NODES WITH CHILDRENS
    if(ele.data('type') === 'SO') {
      ele.css({
          'shape': 'rectangle',
          'color': '#000000',
          'text-outline-width': 0
      });
    }

    if(ele.data('type') === 'A') {
      ele.css({
          'shape': 'rectangle',
          'color': '#884809',
          'text-outline-width': 0
      });
      if(ele.children().length > 0) {
        ele.css({

        });
        ele.addClass('bottom-center');
      }
    }

    if(ele.data('type') === 'P') {
      ele.css({
          'shape': 'rectangle',
          'color': '#884809',
          'text-outline-width': 0
      });
      if(ele.children().length > 0) {
        ele.css({
            'shape': 'rectangle',
            'color': '#ffffff',
            'text-outline-width': 0
        });
        ele.addClass('bottom-center');
      }
    }
    if(ele.data('type') === 'T') {
      ele.css({
          'shape': 'rectangle',
          'color': '#884809',
          'text-outline-width': 0
      });
      if(ele.children().length > 0) {
        ele.css({

        });
        ele.addClass('bottom-center');
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
    so = [];
    areas = [];
    programs = [];
    pImpacts = [];
    rTopics = [];
    outcomes = [];
    outputs = [];

    if(event.cyTarget == cy) {

      cy.$('node').addClass('eating');
      cy.$('edge').removeClass('edgeStyle');

    } else if(event.cyTarget.isEdge()) {

      cy.$('node').addClass('eating');
      cy.$('edge').removeClass('edgeStyle');

    } else if(event.cyTarget.isNode()) {

      cy.$('node').removeClass('eating');
      cy.$('edge').removeClass('edgeStyle');
      var $this = event.cyTarget;
      $this.predecessors().addClass("edgeStyle");
      $this.successors().addClass("edgeStyle");

      // IF NODE HAS CHILDRENS
      if($this.isParent()) {
        var childrens = $this.children();
        childrens.forEach(function(ele) {
          nodeSelected(ele);
          ele.predecessors().forEach(function(ele1) {
            nodeSelected(ele1);
          });
          ele.successors().forEach(function(ele1) {
            nodeSelected(ele1);
          });
        });
      }
      // IF NODE HAS PARENT
      if($this.isChild()) {
        var parent = $this.parent();
        nodeSelected(parent);
      }

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

        so.forEach(function(ele) {
          $(".panel-body ul").append("<label>" + ele[1] + ":</label><li>" + ele[0] + "</li>")
        });
        areas.forEach(function(ele) {
          $(".panel-body ul").append("<label>" + ele[1] + ":</label><li>" + ele[0] + "</li>")
        });
        programs.forEach(function(ele) {
          $(".panel-body ul").append("<label>" + ele[1] + ":</label><li>" + ele[0] + "</li>")
        });
        pImpacts.forEach(function(ele) {
          $(".panel-body ul").append("<label>" + ele[1] + ":</label><li>" + ele[0] + "</li>")
        });
        rTopics.forEach(function(ele) {
          $(".panel-body ul").append("<label>" + ele[1] + ":</label><li>" + ele[0] + "</li>")
        });
        outcomes.forEach(function(ele) {
          $(".panel-body ul").append("<label>" + ele[1] + ":</label><li>" + ele[0] + "</li>")
        });
        outputs.forEach(function(ele) {
          $(".panel-body ul").append("<label>" + ele[1] + ":</label><li>" + ele[0] + "</li>")
        });
      }
    }
  });
  function nodeSelected(ele) {
    ele.addClass('eating');
    var stop;
    if(ele.isChild()) {
      var parent = ele.parent();
      nodeSelected(parent);
    }

    // Validate if the node exists in any array

    so.forEach(function(array) {
      if(ele.data('description') === array[0]) {
        // console.log("asd AREA");
        stop = 1;
      }
    });
    // In flagships array
    areas.forEach(function(array) {
      if(ele.data('description') === array[0]) {
        // console.log("asd AREA");
        stop = 1;
      }
    });

    programs.forEach(function(array) {
      if(ele.data('description') === array[0]) {
        // console.log("asd AREA");
        stop = 1;
      }
    });

    // In Outcomes array
    pImpacts.forEach(function(array) {
      if(ele.data('description') === array[0]) {
        // console.log("asd");
        stop = 1;
      }
    });

    // In Outcomes array
    rTopics.forEach(function(array) {
      if(ele.data('description') === array[0]) {
        // console.log("asd");
        stop = 1;
      }
    });

    // In Outcomes array
    outcomes.forEach(function(array) {
      if(ele.data('description') === array[0]) {
        // console.log("asd");
        stop = 1;
      }
    });

    // In Outcomes array
    outputs.forEach(function(array) {
      if(ele.data('description') === array[0]) {
        // console.log("asd");
        stop = 1;
      }
    });

    // Break nodeSelected function
    if(stop == 1) {
      return;
    }

    // arrays information
    if(ele.data('description') != 'undefined' && ele.data('description') != null) {
      var data = [];
      if(ele.data('type') === 'SO') {
        data.push(ele.data('description'));
        data.push(ele.data('title'));
        so.push(data);
      } else if(ele.data('type') === 'A') {
        data.push(ele.data('description'));
        data.push(ele.data('title'));
        areas.push(data);
      } else if(ele.data('type') === 'P') {
        data.push(ele.data('description'));
        data.push(ele.data('title'));
        programs.push(data);
      } else if(ele.data('type') === 'I') {
        data.push(ele.data('description'));
        data.push(ele.data('title'));
        pImpacts.push(data);
      } else if(ele.data('type') === 'T') {
        data.push(ele.data('description'));
        data.push(ele.data('title'));
        rTopics.push(data);
      } else if(ele.data('type') === 'OC') {
        data.push(ele.data('description'));
        data.push(ele.data('title'));
        outcomes.push(data);
      } else if(ele.data('type') === 'OP') {
        data.push(ele.data('description'));
        data.push(ele.data('title'));
        outputs.push(data);
      }
    }

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
      close: function() {
        $("#changeGraph .btn").html("Show graph by area");
        $("#changeGraph .btn").addClass("currentGraph");
      },
      open: function(event,ui) {
        var url = baseURL + "/impactPathwayGraphByProgram.do";
        var crpProgram = $("input[name='programID']").val();
        var data = {
          programID: crpProgram
        }
        ajaxService(url, data, "impactGraphic", true, true, 'breadthfirst', false);
      }
  });

});

$("#areaGraph").on("click", function(e) {
  e.preventDefault();
  $(".panel-body ul").empty();
  var url = baseURL + "/impactPathwayGraphByArea.do";
  ajaxService(url, data, "impactGraphic", true, true, 'concentric', false);

});

$("#programGraph").on("click", function(e) {
  e.preventDefault();
  $(".panel-body ul").empty();
  var url = baseURL + "/impactPathwayGraphByProgram.do";
  ajaxService(url, data, "impactGraphic", true, true, 'breadthfirst', false);
});

$("#fullGraph").on("click", function(e) {
  e.preventDefault();
  $(".panel-body ul").empty();
  var url = baseURL + "/impactPathwayGraphByCenter.do";
  ajaxService(url, data, "impactGraphic", true, true, 'breadthfirst', false);
});

// Functions

// end nodeSelected function

function ajaxService(url,data,contentGraph,panningEnable,inPopUp,nameLayout,tooltip) {
  $.ajax({
      url: url,
      type: 'GET',
      dataType: "json",
      data: data
  }).done(
      function(m) {
        // Updated
        showHelpText();
        setViewMore();
        // /////////
        console.log("done");
        var currentX = 0;
        var nodes = m.elements.nodes;
        var count = {
            SO: 0,
            A: 0,
            P: 0,
            T: 0,
            I: 0,
            OC: 0,
            OP: 0
        };
        var totalWidth = {
            SO: 0,
            A: 0,
            P: 0,
            T: 0,
            I: 0,
            OC: 0,
            OP: 0
        };
        var nodeWidth = 150;
        var nodeMargin = 20;

        // For to count and set position
        for(var i = 0; i < nodes.length; i++) {
          if(nodes[i].data.type == "SO") {
            count.SO++;
          } else if(nodes[i].data.type == "A") {
            count.A++;
          } else if(nodes[i].data.type == "P") {
            count.P++;
          } else if(nodes[i].data.type == "T") {
            count.T++;
          } else if(nodes[i].data.type == "I") {
            count.I++;
          } else if(nodes[i].data.type == "OC") {
            count.OC++;
          } else if(nodes[i].data.type == "OP") {
            count.OP++;
          }
        }

        totalWidth.SO = count.SO * (nodeWidth + nodeMargin);
        totalWidth.A = count.A * (nodeWidth + nodeMargin);
        totalWidth.P = count.P * (nodeWidth + nodeMargin);
        totalWidth.T = count.T * (nodeWidth + nodeMargin);
        totalWidth.I = count.I * (nodeWidth + nodeMargin);
        totalWidth.OC = count.OC * (nodeWidth + nodeMargin);
        totalWidth.OP = count.OP * (nodeWidth + nodeMargin);
        // totalWidth.KO = (count.KO * (nodeWidth + nodeMargin)) + totalWidth.CoA;

        var widthTest = 0;
        if(totalWidth.T > totalWidth.OC) {
          widthTest = totalWidth.T;
        } else {
          widthTest = totalWidth.OC;
        }

        if(widthTest > totalWidth.I) {
          widthTest;
        } else {
          widthTest = totalWidth.I;
        }

        if(widthTest > totalWidth.P) {
          widthTest;
        } else {
          widthTest = totalWidth.P;
        }

        console.log(widthTest);
        currentX = -1 * (widthTest / 2);

        var move = {
            SO: -(totalWidth.SO / 2),
            A: -(widthTest / 2),
            P: -(widthTest / 2),
            T: -(widthTest / 2),
            I: -(widthTest / 2),
            OC: -(widthTest / 2),
            OP: 380
        };

        // console.log(totalWidth);
        // console.log(widthTest);
        // console.log(currentX);

        for(var i = 0; i < nodes.length; i++) {
          if(nodes[i].data.type == "SO") {
            move.SO = (move.SO + (nodeWidth + nodeMargin));
            nodes[i].position = {
                x: move.SO,
                y: 0
            };
          } else if(nodes[i].data.type == "A") {
            if(nodes[i + 1] && nodes[i + 1].data.type == "P") {

            } else {
              move.A = (move.A + (nodeWidth + nodeMargin));
            }

            nodes[i].position = {
                x: move.A,
                y: 200
            };
            // PROGRAM-----------------
          } else if(nodes[i].data.type == "P") {
            if(nodes[i + 1] && nodes[i + 1].data.type == "P") {
              currentX = (currentX + (nodeWidth + nodeMargin));
              move.P = currentX;
              move.I = move.P;
              move.OC = move.P;
              console.log(move.I);
            } else if(nodes[i + 1] && nodes[i + 1].data.type == "I") {
              move.I = currentX;
            } else {
              move.I = currentX + (nodeWidth + nodeMargin);
            }
            nodes[i].position = {
                x: move.I,
                y: 200
            };

            // PROGRAM IMPACT-----------------
          } else if(nodes[i].data.type == "I") {
            if(nodes[i + 1] && nodes[i + 1].data.type == "P") {
              currentX = move.I + (nodeWidth + nodeMargin + 20);
              move.I = currentX;
              move.OC = currentX;
            } else if(nodes[i + 1] && nodes[i + 1].data.type == "A") {
              if(move.OC > move.I) {
                currentX = move.OC + (nodeWidth + nodeMargin + 20);
              } else {
                currentX = move.I + (nodeWidth + nodeMargin + 20);
              }
              move.I = currentX;
            } else {
              if(currentX > move.I) {
                currentX = currentX + (nodeWidth + nodeMargin + 20);
                move.I = currentX;
              } else {
                move.I = move.I + (nodeWidth + nodeMargin + 20);
              }
              // move.OC=currentX;
            }
            // console.log(move.KO);
            nodes[i].position = {
                x: move.I,
                y: 200
            };
            // RESEARCH TOPIC-----------------
          } else if(nodes[i].data.type == "T") {

            if(nodes[i + 1] && nodes[i + 1].data.type == "OC") {
            } else {
              move.OC = (move.OC + (nodeWidth + nodeMargin + 20));
            }
            if(nodes[i + 1] && nodes[i + 1].data.type == "P") {
              if(move.OC > move.I) {
                currentX = move.OC;
              } else {
                currentX = move.I;
              }
              move.I = currentX;
            } else if(nodes[i + 1] && nodes[i + 1].data.type == "A") {
              if(move.OC > move.I) {
                currentX = move.OC;
              } else {
                currentX = move.I;
              }
              move.I = currentX;
            }

            // console.log(move.KO);
            nodes[i].position = {
                x: move.OC,
                y: 300
            };
            // OUTCOME-----------------
          } else if(nodes[i].data.type == "OC") {
            move.OC = (move.OC + (nodeWidth + nodeMargin + 20));
            if(move.OC > move.I) {
              currentX = move.OC;
            } else {
              currentX = move.I;
            }
            if(typeof (nodes[i + 1]) != "undefined") {
              if(nodes[i + 1].data.type == "P" || nodes[i + 1].data.type == "I") {
                if(move.OC > move.I) {
                  currentX = move.OC;
                } else {
                  currentX = move.I;
                }
              }
            }

            // console.log(move.KO);
            nodes[i].position = {
                x: move.OC,
                y: 300
            };

            // OUTPUT-----------------
          } else if(nodes[i].data.type == "OP") {
            if(typeof (nodes[i + 1]) != "undefined") {
              if(nodes[i + 1] && nodes[i + 1].data.type == "OC" || nodes[i + 1].data.type == "T") {
                currentX = move.OC + (nodeWidth + nodeMargin + 20);
                move.OP = 330;
              } else if(nodes[i + 1] && nodes[i + 1].data.type == "P" || nodes[i + 1].data.type == "I"
                  || nodes[i + 1].data.type == "A") {
                move.OP = 330;
                if(move.OC > move.I) {
                  currentX = move.OC;
                } else {
                  currentX = move.I;
                }
              }
            }

            move.OP = (move.OP + 50);
            // console.log(move.KO);
            nodes[i].position = {
                x: move.OC,
                y: move.OP
            };
          }/*
           * else if(nodes[i].data.type == "CoA") { if(nodes[i + 1] && nodes[i + 1].data.type == "KO") { move.KO; }
           * else { move.KO = (move.KO + (nodeWidth + nodeMargin + 20)); } // console.log(move.KO); nodes[i].position = {
           * x: move.KO, y: 400 }; }
           */
        }

        createGraphic(m.elements, contentGraph, panningEnable, inPopUp, 'breadthfirst', tooltip, nodeWidth);
      });
}

function showHelpText() {
  $('.helpMessage').show();
  $('.helpMessage').addClass('animated flipInX');
}