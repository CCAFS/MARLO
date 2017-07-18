$("#capdevSearchInput").on("keyup",function(){
console.log("esto es lo que escribio "+ $(this).val());

var query = $(this).val();
    if(query.length > 1) {
      if(timeoutID) {
        clearTimeout(timeoutID);
      }
      // Start a timer that will search when finished
      timeoutID = setTimeout(function() {
        getCapdevListFiltered(query);
      }, 400);
    }
})

function getCapdevListFiltered(query){
	$.ajax({
      'url': baseURL + '/filtercapdevlist.do',
      'data': {
        q: query
      },
      'dataType': "json",
      beforeSend: function() {

        /*$dialogContent.find(".search-loader").show();
        $dialogContent.find(".panel-body ul").empty();*/
      },
      success: function(data) {
        console.log(data[0]);
        var table = $(".capdevTable");
        $(".capdevTable tbody>tr.capdevTbody").remove();

        

         for (var i = 0; i  < = data.length; i++) {
          var row = $('<tr></tr>').appendTo(table);
          $('<td></td>').text(data[0].['id']).appendTo(row); 
         }
        /*var usersFound = (data.users).length;
        if(usersFound > 0) {

          $dialogContent.find(".panel-body .userMessage").hide();
          $.each(data.users, function(i,user) {

            if(user.firstName){
              var $item = $dialogContent.find("li#userTemplate").clone(true).removeAttr("id");

              $item.find('.idUser').html(user.idUser);
              $item.find('.firstname').html(user.firstName);
              $item.find('.lastname').html(user.lastName);
              $item.find('.email').html("< " +user.email+ " >");
              if(i == usersFound - 1) {
                $item.addClass('last');
              }
              $dialogContent.find(".panel-body ul").append($item);
            }

          });
        } else {

          $dialogContent.find(".panel-body .userMessage").show();
        }*/

      },
      error: function() {
        //$dialogContent.find(".panel-body .userMessage").show();
      },

      complete: function() {
        //$dialogContent.find(".search-loader").fadeOut("slow");
      }
    });
}