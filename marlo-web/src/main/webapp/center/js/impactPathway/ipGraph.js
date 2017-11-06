var elements = {};
elements['nodes'] = [];
elements['edges'] = [];
var x = {};
var y = 100;
var count = {};
var present = {};
var ycount = {};
var yc = 0;
var yco = {};
var graphStarted = false;
var fullImpact = false;
var cutLine = 20;
var cutLinePx = 1024;
var cys;

function initGraph(programID){
  $("#ipGraph-button").on("click", function(e){
    if (!graphStarted) {
      if (programID && !fullImpact) {
        callCytos(baseURL+"/json/prePlanningIpGraph.do?programID=" + programID, "ipGraph-content");
      } else {
        callCytos(baseURL+"/json/prePlanningIpGraph.do", "ipGraph-content");
      }
      graphStarted = true;
    }
    $("#content-ip").slideToggle("slow");
  });
  
  $("#ipGraph-btnFullimpact").on("click", function(e){
	  $( "#ipGraph-content" ).empty();
	  reinitCytos();
	  fullImpact = true;
	  $("#ipGraph-btnSingleimpact").show();
	  $("#ipGraph-btnFullimpact").hide();
	  if (programID && !fullImpact) {
        callCytos(baseURL+"/json/prePlanningIpGraph.do?programID=" + programID, "ipGraph-content");
      } else {
        callCytos(baseURL+"/json/prePlanningIpGraph.do", "ipGraph-content");
      }
	  return false;
  });
  
  $("#ipGraph-btnSingleimpact").on("click", function(e){
	  $( "#ipGraph-content" ).empty();
	  reinitCytos();
	  fullImpact = false;
	  $("#ipGraph-btnFullimpact").show();
	  $("#ipGraph-btnSingleimpact").hide();
	  if (programID && !fullImpact) {
        callCytos(baseURL+"/json/json/prePlanningIpGraph.do?programID=" + programID, "ipGraph-content");
      } else {
        callCytos(baseURL+"/json/json/prePlanningIpGraph.do", "ipGraph-content");
      }
	  return false;
  });
  
  $("#ipGraph-btnPrint").on("click", function(e){
	  printCyto();
	  return false;
  });
  
  $("#ipGraph-btnMax").on("click", function(e){
	  $( "#ipGraph-content" ).empty();
	  $( "#dialog-message" ).dialog({
		  width: $(window).width()*0.95,
		  height: $(window).height()*0.95,
	      modal: true,
	      draggable: false,
	      resizable: false,
	      buttons: {
    	  "Print": {  
    		  text: '', 
              'class': 'btnPrint', 
              click: printCyto
              },
	        Ok: function() {
	          $( this ).dialog( "close" );
	        }
	      },
	      close: function( event, ui ) {
	    	  $('#ipGraph-btnMax').attr('disabled','disabled');
	    	  $('#ipGraph-btnPrint').attr('disabled','disabled');
	    	  $( "#dialog-message" ).empty();
	    	  $( "<div id=\"loading-dialog-message\" style=\"display:none;position:absolute; width:100%; height:100%;top: 45%;\"><img style=\"display: block; margin: 0 auto;\" src=\"../global/images/loading.gif\" alt=\"Loader\" /></div>" ).appendTo("#dialog-message");
	    	  reinitCytos();
	    	  if (programID && !fullImpact) {
    	        callCytos(baseURL+"/json/json/prePlanningIpGraph.do?programID=" + programID, "ipGraph-content");
    	      } else {
    	        callCytos(baseURL+"/json/json/prePlanningIpGraph.do", "ipGraph-content");
    	      }
	      }
	    });
	  reinitCytos();
	  // $("#loading-"+contentDiv).show();
	  if (programID && !fullImpact) {
        callCytos(baseURL+"/json/json/prePlanningIpGraph.do?programID=" + programID, "dialog-message");
      } else {
        callCytos(baseURL+"/json/json/prePlanningIpGraph.do", "dialog-message");
      }	  
	  return false;
  });
}

function callCytos(url,contentDiv) {
	$("#loading-"+contentDiv).show();
	  $.getJSON(url, function(data) {
		
	    $.each(data, function(key, val) {
	      if (key == 'ipElements') {
	        $.each(val, function(attr, item) {	        
	          if (item.type.id != 1) {
	        	  present[item.id] = 1;
	        	  if (!count[item.type.id+'-'+item.program.id]){count[item.type.id+'-'+item.program.id] = 1;}
	            elements['nodes'].push(
	                    {data: {
	                        id: item.id.toString(),
	                        name: getName(item),
	                        weight: getWeight(getName(item)),
	                        descr: item.description,
	                        faveColor: getColorNode(item.type.id, item.translatedOf.length),
	                        nodeType: item.type.id,
	                        translate: (item.type.id, item.translatedOf.length>0)?1:0,
	                        faveShape: 'roundrectangle'},
	                      position: getPosition(item.type.id, (item.type.id, item.translatedOf.length>0)?1:0)
	                    }
	            );
	            count[item.type.id+'-'+item.program.id] ++;
	          } else {
	        	  
	          }
	        });
	      } else if (key == 'relations') {
	        $.each(val, function(attr, item) {
	        	if (present[item.parentID]) {
	        		elements['edges'].push({data: {id: item.id.toString(), weight: 2, source: item.parentID, target: item.childID}});
	        	}
	        });
	      }
	    });
	    // console.log(JSON.stringify(ycount, null, 4));
	    cy = cytoscape({
	      container: $('#'+contentDiv)[0],
	      style: cytoscape.stylesheet()
	              .selector('node')
	              .css({
	                'shape': 'data(faveShape)',
	                'width': 'data(weight)',
	                'content': 'data(name)',
	                'text-valign': 'center',
	                'text-outline-width': 2,
	                'text-outline-color': 'data(faveColor)',
	                'background-color': 'data(faveColor)',
	                'font-weight': 'bold',
	                'font-size': '18px',
// 'font-family' : 'sans-serif',
	                'color': '#fff'
	              })
	              .selector('$node > node')
	              .css({
	                'padding-top': '20px',
	                'padding-left': '10px',
	                'padding-bottom': '10px',
	                'padding-right': '10px'
	              })
	              .selector('edge')
	              .css({
	                'source-arrow-shape': 'triangle',
	                'width': 3,
	                'line-color': '#ddd',
	                'target-arrow-color': '#ddd'
	              })
	              .selector('.highlighted')
	              .css({
	                'background-color': '#61bffc',
	                'line-color': '#61bffc',
	                'target-arrow-color': '#61bffc',
	                'transition-property': 'background-color, line-color, target-arrow-color',
	                'transition-duration': '0.5s'
	              }),
	      elements: elements,
	      layout: {
	        name: 'preset',
// directed: true,
// roots: '#191,#204,#197',
	        zoom: 1,
	        minZoom: 1e-50,
	        maxZoom: 1e50,
	        zoomingEnabled: true,
	        userZoomingEnabled: true,
// padding: 200
	      },
	      /*
         * layout: { name: 'concentric', concentric: function () { y = 0; idType= this.data('nodeType'); trans =
         * this.data('translate'); if (idType == 1) { } else if (idType == 2) { y = 1000; } else if (idType == 3) { y =
         * 800; } else if (idType == 4) { y = 600; } else if (idType == 5) { y = 400; } if (trans > 0) { y -=100; }
         * return y; }, levelWidth: function( nodes ){ return 10; }, padding: 10 },
         */
	      boxSelectionEnabled: false,
	      ready: function() {
	        window.cy = this;
	        var bfs = cy.elements().bfs('#outcome2025', function() {
	        }, true);

	        var i = 0;
	        var highlightNextEle = function() {
	          bfs.path[i].addClass('highlighted');

	          if (i < bfs.path.length) {
	            i++;
	            setTimeout(highlightNextEle, 100);
	          }
	        };
	        cy.elements("node").qtip({
	          content: function() {
	            return this.data('descr');
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

	        reline(cy);
	      },
// panningEnabled: true,
	      userPanningEnabled: true
	    });
	    cy.panzoom({
	      // options go here
	    });
	    cy.on('tap', function(event){
    	  var evtTarget = event.cyTarget;
    	  var elems = cy.elements("edge");
    	  elems.css( 'line-color', '#ddd' );
    	  elems.css( 'source-arrow-color', '#ddd' );
    	  elems.css( 'z-index', '1' );
    	  var elems = cy.elements("node");
    	  elems.css( 'background-opacity', '0.2' );
    	  elems.css( 'text-opacity', '0.2' );
    	  elems.css( 'z-index', '2' );
    	  if( evtTarget === cy ){    	  
    		  var elems = cy.elements("node");
        	  elems.css( 'background-opacity', '1' );
        	  elems.css( 'text-opacity', '1' );
    	  } else {
    		  if(evtTarget.isEdge()) {
    			  evtTarget.css( 'line-color', '#444' );
    			  evtTarget.css( 'source-arrow-color', '#444' );
    			  evtTarget.css( 'z-index', '3' );
    			  var sons = evtTarget.connectedNodes();
    			  sons.css( 'background-opacity', '1' );
    			  sons.css( 'text-opacity', '1' );
    		  } else if(evtTarget.isNode()) {
    			  evtTarget.css( 'background-opacity', '1' );
    			  evtTarget.css( 'text-opacity', '1' );
    			  paintSources(evtTarget);
    			  paintTargets(evtTarget);
    		  }
    	  }
    	});
	    
	    function paintTargets(node) {
	    	targets = node.connectedEdges('edge[target="'+node.id()+'"]');
	    	targets.css( 'line-color', '#444' );
	    	targets.css( 'source-arrow-color', '#444' );
	    	targets.css( 'z-index', '3' );
	    	targets.sources().css( 'background-opacity', '1' );
	    	targets.sources().css( 'text-opacity', '1' );
	    	targets.sources().each(function(i, ele){
	    		paintTargets(ele);
	    	});
	    }
	    function paintSources(node) {
	    	sources = node.connectedEdges('edge[source="'+node.id()+'"]');
	    	sources.css( 'line-color', '#444' );
	    	sources.css( 'source-arrow-color', '#444' );
	    	sources.css( 'z-index', '3' );
	    	sources.targets().css( 'background-opacity', '1' );
	    	sources.targets().css( 'text-opacity', '1' );
	    	// if (sources){
	    	sources.targets().each(function(i, ele){
	    		paintSources(ele);
	    	});
	    	// }
	    }
	    
	    cys = $('#'+contentDiv).cytoscape('get');

	    function addElem(elem) {
	      var obj = $.parseJSON(elem);
	      var valid = validateIDcytoscape(obj.id);
	      if (valid == 'true') {
	        var eles = cy.add(cytoConvert(obj));
	        reline(cy);
	      }
	    }
// $('#btnDel').click(delElem);
	    function delElem(Id) {
	      var j = cy.$("#" + Id);
	      var sons = j.connectedEdges();
	      removeByEdges(sons, j.id());
	      cy.remove(j);
	      reline(cy);
	    }

	    function validateIDcytoscape(id) {
	      var elems = cy.elements("node[id='" + id + "']");
	      if (elems.size() > 0) {
	        return 'false';
	      }
	      return 'true';
	    }

	    function reline(cys) {
	    	// var type = 2;
	      // var trans = 0;
	      var lastpos = 0;
	      var outFirst = 0;
	      var lastEles = 0;
	      var y = 0;
	      for (var i=2;i<6;i++) {
	    	  for (var j=0;j<2;j++) {
	    		  if (cys.elements("node[nodeType=" + i + "][translate="+j+"]").length > 0) {
			        var eles = cys.elements("node[nodeType=" + i + "][translate="+j+"]");
			        outFirst = getDistance(eles[0].position('x'), eles[(eles[cutLine-1])?(cutLine-1):(eles.length - 1)].position('x'));
			        if (lastEles != 0) {
			          var diff = (outFirst - lastpos) / 2;
		// console.log(outFirst + ' - ' + lastpos + ' / 2 = ' + diff+ ' t:'+type);
			          position = lastEles[0].position('x');
			          cys.elements("node[nodeType=" + i + "][translate="+j+"]").each(function(k, ele) {
			        	  // if (position >= cutLinePx) {
		        		  if ((k)%cutLine==0) {
				            	// y+=100;
				            	position = lastEles[0].position('x');
				            }
			            ele.position('x', position - diff);
			            ele.position('y', ele.position('y') + y);
			            position += 250;
			            // x[i] = ele.position('x');
			          });
			        }
			        lastpos = outFirst;
			        lastEles = eles;
			        // type++;
    		  	}
	      	}
	      }
	    }
	    
	   

	    function removeByEdges(edges, source) {
	      if (edges) {
	        edges.each(function(i, ele) {
	          if (ele.source().id() == source) {
	            removeByEdges(ele.target().connectedEdges(), ele.target().id());
	            if (ele.target().connectedEdges('edge[source != "' + source + '"]').length == 0) {
	              cy.remove(ele.target());
	            }
	          }
	        });
	      }
	    }

	    function getDistance(point1, point2) {
	      var dist = Math.sqrt(Math.pow((point1 - point2), 2));
// console.log(dist + ' d');
	      return dist;
	    }
    	document.querySelector('[data-id="layer4-node"]').style.position = 'static';
	  }).always(function() {
		  $("#loading-"+contentDiv).fadeOut('slow');
		  $('#ipGraph-btnMax').removeAttr('disabled');
		  $('#ipGraph-btnPrint').removeAttr('disabled');	
	  });
	}

	function getColorNode(idType, trans) {
	  var color = '#666666';
	  if (idType == 1) {
	    color = '';
	  } else if (idType == 2) {
	    color = '#666666';
	  } else if (idType == 3) {
	    color = '#dd7e6b';
	    if (trans > 0) {
	      color = '#4387fd';
	    }
	  } else if (idType == 4) {
	    color = '#f6b26b';
	    if (trans > 0) {
	      color = '#0FA258';
	    }
	  } else if (idType == 5) {
	    color = '#93c47d';
	  }
	  return color;
	}
	
	function getConcentric (idType, trans) {
		y = 0;
		if (idType == 1) {
		
		  } else if (idType == 2) {
		    y = 1000;
		  } else if (idType == 3) {
		    y = 800;
		  } else if (idType == 4) {
		    y = 600;
		  } else if (idType == 5) {
		    y = 400;
		  }
		if (trans > 0) {
			y +=100;
		}
	}

	function getPosition(idType, trans) {
		if (!ycount[idType+'.'+trans]) {
			 ycount[idType+'.'+trans] = 0;
		}
		
		if (!yco[idType+'.'+trans]) {
			 yco[idType+'.'+trans] = yc;
		}
		 
		// if (x[idType+'.'+trans] >= cutLinePx) {
		if (ycount[idType+'.'+trans]%cutLine==0) {
			yco[idType+'.'+trans] +=100;
			x[idType+'.'+trans] = 100;
			yc+=100;
		 }
		 if (!x[idType+'.'+trans]){x[idType+'.'+trans] = 100;}
		 if (idType == 1) {
		
		} else if (idType == 2) {
			y = 300 + yco[idType+'.'+trans];
		} else if (idType == 3) {
			y = 400+ yco[idType+'.'+trans];
		} else if (idType == 4) {
			y = 600+ yco[idType+'.'+trans];
		} else if (idType == 5) {
			y = 800+ yco[idType+'.'+trans];
		}
		if (trans > 0) {		
			y += 100;
		}	
		x[idType+'.'+trans] += 250;
		ycount[idType+'.'+trans] ++;
		return {x: x[idType+'.'+trans], y: y};
	}
	
	function getName(item) {
	  return item.composedId;
		/*
     * if (item.type.id == 2 && item.program.id > 4) { return item.program.acronym + ' - ' + '2025 Vision'+'
     * #'+count[item.type.id+'-'+item.program.id]; } else { return item.program.acronym + ' - ' + item.type.name+'
     * #'+count[item.type.id+'-'+item.program.id]; }
     */
	}

	function getWeight(text) {
	  var weight = text.length+3;
	  weight *= 10	;
	  return weight;
	}

	function cytoConvert(item) {
	  var cyto = [];
	  cyto.push(
	          {
	            group: 'nodes',
	            data: {
	              id: item.id.toString(),
	              name: item.program.acronym + ' - ' + item.type.name,
	              weight: getWeight(item.program.acronym + ' - ' + item.type.name),
	              descr: item.description,
	              faveColor: getColorNode(item.type.id, item.translatedOf.length),
	              nodeType: item.type.id,
	              faveShape: 'rectangle'
	            },
	            position: getPosition(item.type.id, item.translatedOf.length)
	          }
	  );
	  if (item.parentID) {
	    cyto.push(
	            {
	              group: "edges",
	              data: {id: item.id.toString() + '-' + item.parentID.toString(), source: item.parentID.toString(), target: item.id.toString()}
	            }
	    );
	  }
	  return cyto;
	}

	function printCyto() {
		$('#download').attr('href',cys.png({full:true}));
    	$('#download').attr('download','graph.png');
    	$('#download')[0].click();
	}
	
	 function reinitCytos() {
	    	elements['nodes'] = [];
	    	elements['edges'] = [];
	    	x = {};	 
	    	count = {};
	    	ycount = {};
	    	yco = {};
	    	yc = 0;
	    }