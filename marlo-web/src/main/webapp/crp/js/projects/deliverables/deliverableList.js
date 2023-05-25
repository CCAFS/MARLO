var table;
var previusTable;

$(document).ready(function() {

  var $deliverableList = $('.currentDeliverables table.deliverableList');
  var $previusDdeliverableList = $('.previousDeliverables table.deliverableList');

  $(".fairDiagram").on("click", openDialog);

  $(".deliverableList a").on("click", saveSearch);


  

  table = $deliverableList.DataTable({
      "bPaginate": true, // This option enable the table pagination
      "bLengthChange": true, // This option disables the select table size option
      "bFilter": true, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "iDisplayLength": 50, // Number of rows to show on the table
      language:{
        searchPlaceholder: "Search..."
      },
      "fnDrawCallback": function() {
        // This function locates the add activity button at left to the filter box
        var table = $(this).parent().find("table");
        if($(table).attr("id") == "currentActivities") {
          $("#currentActivities_filter").prepend($("#addActivity"));
        }
      },
      "order": [
        [
            3, 'asc'
        ]
      ],
      aoColumnDefs: [
          {
              bSortable: true,
              aTargets: [
                -1
              ]
          }, {
              sType: "natural",
              aTargets: [
                0
              ]
          }
      ]
  });

//hide colums when inizializate table  
var columnaNames = ['Duplicated','Responsible person']; 

for (var i = 0; i < columnaNames.length; i++) {
  var columnaName = columnaNames[i];
  var columns = table.columns().indexes();
  var columnIndex = -1; 

  columns.each(function(index) {
    var name = table.column(index).header().textContent.trim();
    if (name === columnaName) {
      columnIndex = index;
      return false;
    }
  });

  var column = table.column(columnIndex);
  if (columnIndex !== -1) {
    column.visible(false);
  }
}



  $('.buttonAddColumn').on('click', function() {

    if ($(this).hasClass('active')) {
      $(this).removeClass('active');
      var header = $deliverableList.find('thead tr');
      var columnas = header.find('th')
      var columnas2 = table.columns().indexes();
      var columnaNombre = $(this).text();
      var columnaIndex2 = -1; 
      columnas2.each(function(index) {
        var nombre = table.column(index).header().textContent.trim();
        if (nombre === columnaNombre) {
            columnaIndex2 = index;
            return false; 
        }
    }); 
      var columna = table.column(columnaIndex2);
      if (columnaIndex2 !== -1) {
      columna.visible(false);
      }
    } else {
      $(this).addClass('active');
      var columnaNombre = $(this).text();
      var columnas = table.columns().indexes();
      var columnaIndex = -1;    
      columnas.each(function(index) {
          var nombre = table.column(index).header().textContent.trim();
          if (nombre === columnaNombre) {
              columnaIndex = index;
              return false; 
          }
      });       
      if (columnaIndex !== -1) {
          var columna = table.column(columnaIndex);
          columna.visible(true);
      }
    }

});



  previusTable = $previusDdeliverableList.DataTable({
    "bPaginate": true, // This option enable the table pagination
    "bLengthChange": true, // This option disables the select table size option
    "bFilter": true, // This option enable the search
    "bSort": true, // this option enable the sort of contents by columns
    "bAutoWidth": false, // This option enables the auto adjust columns width
    "iDisplayLength": 50, // Number of rows to show on the table
    language:{
      searchPlaceholder: "Search..."
    },
    "fnDrawCallback": function() {
      // This function locates the add activity button at left to the filter box
      var table = $(this).parent().find("table");
      if($(table).attr("id") == "currentActivities") {
        $("#currentActivities_filter").prepend($("#addActivity"));
      }
    },
    "order": [
      [
          3, 'asc'
      ]
    ],
    aoColumnDefs: [
        {
            bSortable: true,
            aTargets: [
              -1
            ]
        }, {
            sType: "natural",
            aTargets: [
              0
            ]
        }
    ]
});



//Add styles to the table
  var iconSearch = $("<div></div>").addClass("iconSearch");
  var containerKeywords = $("<div></div>").addClass("containerKeywords");
  var divDataTables_filter = $('.dataTables_filter').parent();
  var keywords = JSON.parse(window.localStorage.getItem('keywords'));
  var pTag = $('<p>').text('Last searches:');
  var containerLastSearches = $('<div></div>').addClass("containerLastSearches");
  iconSearch.append('<img src="' + baseUrl + '/global/images/search_outline.png" alt="Imagen"  style="width: 24px; margin: auto;" >');
  iconSearch.prependTo(divDataTables_filter);
  var divDataTables_length =$('.dataTables_length').parent();
  divDataTables_length.css("position", "absolute");
  divDataTables_length.css("bottom", "8px");
  divDataTables_length.css("margin-left", "45%");
  divDataTables_length.css("z-index", "1");
  pTag.addClass("LastSearches");

  if(keywords){
    containerLastSearches.insertAfter(divDataTables_filter.parent().parent().children().first());
    for (var i = 0; i < keywords.length; i++) {
      var keywordsDiv = $("<div></div>").addClass("keywords");
      keywordsDiv.text(keywords[i]);
      keywordsDiv.prependTo(containerKeywords);  
      containerKeywords.prependTo(containerLastSearches);     
    }
    pTag.prependTo(containerLastSearches);
  }else{
    $('.currentDeliverables .iconSearch').parent().css("margin", "18px 0 18px 0px")
  }

$('.currentDeliverables .keywords').on("click", fillInput);


});

function openDialog() {
  $("#diagramPopup").dialog({
      title: 'FAIR Diagram',
      width: '90%',
      /* heigth: '100%', */
      modal: true,
      closeText: ""
  });
}

function fillInput() {

  if ($(this).hasClass('active')) {
    $(this).removeClass('active');
  } else {
    $(this).addClass('active');
  }

  var inputValue = $(this).text();
  var input = $('.currentDeliverables #deliverables_filter input');
  
  if(inputValue == input.val()){
    input.val('');
    table.search('').draw();
  }else{
    input.val(inputValue);
    table.search(inputValue).draw();
    
  }
  
}



function saveSearch() {
  
  var firstInput = $(this).parents().find('#deliverables_filter input:first');
  var storedKeywordsJSON = window.localStorage.getItem('keywords');
  var storedKeywords = [];

  if (storedKeywordsJSON) {
    storedKeywords = JSON.parse(storedKeywordsJSON);
  }

  // Add new keyword to array
  if(firstInput.val() != null && firstInput.val() != "" && firstInput.val() != " "){
    storedKeywords.push(firstInput.val());
  }

  // Check if array exceeds 5 element limit
  if (storedKeywords.length > 5) {
    // Delete the first element of the array
    storedKeywords.shift();
  }

  // Save the updated fix to localStorage
  var updatedKeywordsJSON = JSON.stringify(storedKeywords);
  window.localStorage.setItem('keywords', updatedKeywordsJSON);
}
