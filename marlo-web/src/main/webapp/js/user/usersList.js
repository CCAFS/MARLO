$(document).ready(function() {
  
  $('table.userList').dataTable({
    "bPaginate" : true, // This option enable the table pagination
    "bLengthChange" : false, // This option disables the select table size option
    "bFilter" : true, // This option enable the search
    "bSort" : true, // this option enable the sort of contents by columns
    "bAutoWidth" : true, // This option enables the auto adjust columns width
    "iDisplayLength" : 15, // Number of rows to show on the table
    "fnDrawCallback": function(){
      // This function locates the add activity button at left to the filter box
      var table = $(this).parent().find("table");
      if( $(table).attr("id") == "currentActivities"){
        $("#currentActivities_filter").prepend($("#addActivity"));
      }
    }
  });
  
  $("#submitForm").on("submit", function(evt){
    if(confirm($("#beforeSubmitMessage").val())){
      return true;
    }else{
      evt.preventDefault();
      return false;
    }
  });
});
