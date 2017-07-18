$(function(){ // on dom ready

  var url="http://localhost:8080/marlo-web/impactPathway/impactPathwayGraph.do";

  $.ajax({
             url: url,
             type: 'GET',
             dataType: "json",
             data:{crpProgramID:78,sectionName:'outcomes'}
          }).done(function (m) {
            createGraphic(m.elements);

      });
// photos from flickr with creative commons license
  
function createGraphic(json){
var cy = cytoscape({
  container: document.getElementById('cy'),
  
  boxSelectionEnabled: false,
  autounselectify: true, 
  minZoom: 0.2,
  maxZoom: 5.5,
  zoomingEnabled: true,
  userZoomingEnabled: true,
  
  style: cytoscape.stylesheet()
    .selector('node')
      .css({
        'height': 80,
        'width': 80,
        'background-fit': 'cover',
        'border-width': 1,
        'border-opacity': 0.5,
        'label': 'data(label)',
        'background-color':'#2388ae',
        'color':'white'
      })
    .selector('.eating')
      .css({
        'border-width': 2,
        'background-color':'#163799',
        'line-color': 'blue'
      })
    .selector('.eater')
      .css({
        'border-width': 9,
        'color':'white'
      })
    .selector('edge')
      .css({
        'width': 6,
        'source-arrow-shape': 'triangle',
        'target-arrow-shape': 'circle',
        'line-color': '#999999',
        'source-arrow-color': '#999999',
        'target-arrow-color': '#999999',
        'curve-style': 'bezier'
      })
    .selector('.center-center')
      .css({
        'text-valign': 'center',
        'text-halign': 'center'
      }),
  
  elements:json ,
  
  layout: {
    name: 'breadthfirst',
    directed: true,
    padding: 10
  }
}); 

// cy init

cy.$('node').addClass('center-center')


//tap a node
cy.on('tap',function(event){

  cy.$('node').css( 'background-opacity', '0.4' );
   cy.$('node').css( 'text-opacity', '0.4' );

  if(event.cyTarget==cy){

   cy.$('node').removeClass('eating');
   cy.$('node').css( 'background-opacity', '1' );
   cy.$('node').css( 'text-opacity', '1' ); 

}else if(event.cyTarget.isEdge()){

   cy.$('node').removeClass('eating');
   cy.$('node').css( 'background-opacity', '1' );
   cy.$('node').css( 'text-opacity', '1' ); 

}else if(event.cyTarget.isNode()){

  cy.$('node').removeClass('eating');
  var $this=event.cyTarget;
  nodeSelected($this);
  var successors=$this.successors();
  var predecessors=$this.predecessors();

  predecessors.forEach(function(ele){
    nodeSelected(ele);
  });

  successors.forEach(function(ele){
    nodeSelected(ele);
  });
}
});

function nodeSelected(ele){
  ele.addClass('eating');
  ele.css( 'background-opacity', '1' );
  ele.css( 'text-opacity', '1' );
  ele.css( 'z-index', '3' );
}
}


}); // on dom ready