  var dialog, capDevDialog;
  var $dialogContent, $dialogCapDevCategory;
  var timeoutID;
  var $contactInput;
  var $elementSelected;


  $(document).ready(init);

  function init(){
    $('.numParticipants').integerInput();
    $('.numMen').integerInput();
    $('.numMen').integerInput();
    $('.participant-code').integerInput();

    datePickerConfig({
      "startDate": "#capdev\\.startDate",
      "endDate": "#capdev\\.endDate",
      "publicationDate":"#capdevSupportingDocs\\.publicationDate"
    });


    $dialogContent = $("#dialog-searchContactPerson");
    var dialogOptions = {
      autoOpen: false,
      height: 600,
      width: 480,
      modal: true,
      dialogClass: 'dialog-searchUsers',
      open: function(event,ui) {

      }
    };

    // Initializing Manage users dialog
    dialog = $dialogContent.dialog(dialogOptions);


    //event search contact person
    $("input.contact").on("click", function(e) {
      e.preventDefault();
      openSearchDialog($(this));
    });

   //close search contact person dialog
   $('.close-dialog').on('click', function() {
    dialog.dialog("close");
    $dialogContent.find(".panel-body ul").empty();

  });


  //Event to find an user according to search field
  $dialogContent.find(".searchcontact-content input").on("keyup", searchUsersEvent);


    // Event when the user select the contact person from AD
  $(".selectADUser").on("click", function() {
    console.log("span.select")
    var firstName = $(this).parent().find(".firstname").text();
    var lastName = $(this).parent().find(".lastname").text();
    var email = $(this).parent().find(".email").text();
    var composedName = firstName +", " + lastName +" "+ email;

    console.log(email)
    console.log(composedName)
    // Add user
    addUser(composedName, firstName, lastName, email);
  });

  
  //event when the user add a new contact person
  $dialogContent.find(".addContactPerson").on("click", function(){
     var firstName = $(".ct_FirstName").val();
      var lastName = $(".ct_LastName").val();
      var email = $(".ct_Email").val();
      var composedName = firstName +", " + lastName +" <"+ email+">";

      console.log(email)
      console.log(composedName)
      // Add user
      addUser(composedName, firstName, lastName, email);
  });


   //set contact person 
   (function(){
    var ctFirsName = $(".ctFirsName").val();
    var ctLastName = $(".ctLastName").val();
    var ctEmail  = $(".ctEmail").val();
    if(!ctFirsName || !ctLastName || !ctEmail){
      $(".contact").val(""); 
    }
    else{
      var composedName = ctFirsName+" "+ctLastName+" <"+ctEmail+" >";
      $(".contact").val(composedName);  
    }
    
   })();

   

  //display individual or group intervention form
  (function() {
    var value = $(".radioButton").val();
    
    if (value == 1) {
    $(".fullForm").show();
    $(".individualparticipantForm").show();
    $(".grupsParticipantsForm ").hide();
    $(".individual ").show();
    $(".group").hide();
  }
  else if (value == 2) {
    $(".fullForm").show();
    $(".grupsParticipantsForm ").show();
    $(".individualparticipantForm").hide();
    $(".group").show();
  }
  })();


    
    //set value to gender field
    (function(){
      var gender = $(".genderInput").val();
     $(".genderSelect select option").each(function() {
      if($(this).val() == gender){
        $(this).attr( "selected" , "selected");
      }
      });

     var citizenship = $(".citizenshipInput").val();
     $(".pCitizenshipcountriesList select option").each(function() {
      if($(this).val() == citizenship){
        $(this).attr( "selected" , "selected");
      }
      });

     var countryOfInstitucion = $(".countryOfInstitucionInput").val();
     $(".pcountryOfInstitucionList select option").each(function() {
      if($(this).val() == countryOfInstitucion){
        $(this).attr( "selected" , "selected");
      }
      });
    })();



(function(){
  // Is this capdev has a regional dimension
  var valueSelected = $(".regional .onoffswitch-radio").val();
  if(valueSelected) {
    $(".regionsBox").show("slow");
  } 
})();




  }

  


  //event to download participants template 

  $(".dowmloadTemplate").on("click", function(){
    $.ajax({
            'url': baseURL + '/template/dowmloadTemplate',
            
            beforeSend: function() {
              console.log("estoy preparando todo")
            },
            success: function() {
               console.log("todo salio bien")

            },
            error: function() {
               console.log("Pailas algo paso")
            },

            complete: function() {
               console.log("Listo papito!")
            }
          });
  });



  

  //
  function uploadFile(){
    var file = document.getElementById('uploadFile').files[0];
      if (file) {
          
            $.ajax({
        'url': baseURL + '/previewParticipants.do',
        'data':file,
         type: 'POST',
         enctype: 'multipart/form-data',
         'dataType': "json",
         contentType:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
         processData: false,
        beforeSend: function() {
        },
        success: function(data) {
          
        },
        error: function() {
        },
        complete: function() {
        }
      });
      }
  }

  //PREVIEW participants

    $('#btnDisplay').click(function() {
      $("#participantsTable").empty();
      $("#filewarning").empty();
      

      var file = document.getElementById('uploadFile').files[0];

      if(file){
        if(file.name.indexOf("xls") > 0 || file.name.indexOf("xlsx") > 0){
           $.ajax({
          'url': baseURL + '/previewParticipants.do',
          'data':file,
           type: 'POST',
           enctype: 'multipart/form-data',
           'dataType': "json",
           contentType:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
           processData: false,
          beforeSend: function() {
            
            $(".loader").show();
          },
          success: function(data) {
           
            if(data.length > 0){
              var mytable = $('<table></table>').attr( "class","table table-bordered");
            var rows = data[0].content.length;
            var cols = data[0].headers.length;
            var tr = [];
            var header = $('<tr></tr>').appendTo(mytable);

            for (var i = 0; i < cols; i++) {
              $('<th></th>').text(data[0].headers[i]).appendTo(header); 
            }

            for (var i = 0; i < rows; i++) {
              var row = $('<tr></tr>').appendTo(mytable);
              for (var j = 0; j < cols; j++) {
                $('<td></td>').text(data[0].content[i][data[0].headers[j]]).appendTo(row); 
              }
            }

            mytable.appendTo("#participantsTable");  
            $("#filewarning").hide();
            $('#myModal').modal('show')  
            }

            else{
              $("#filewarning").html("<p> Wrong file. </p>");
              $("#filewarning").show();
            }
               

          },
          error: function() {
            
            $("#filewarning").html("<p> An error has occurred by displaying the preview </p>");
            $("#filewarning").show();

          },
          complete: function() {
            //$("#filewarning").hide();
            $(".loader").hide();
          }
        });
        }
        else{
          $("#filewarning").html("<p> You should upload only excel files. </p>");
            $("#filewarning").show();
        }
    }
    else{
      $("#filewarning").html("<p> You haven't uploaded any file. </p>");
      $("#filewarning").show();
    }

      

      
      
       



  });





  // CAPACITY DEVELOPMENT functions

  //add Objective
  $(".addObjective").on("click", function (){
  	var $list = $(".objectivesList");
  	var $item = $("#objective-template").clone(true).removeAttr("id");
    $list.append($item);
    $item.show('slow', function() {
     width: "100%"
   });


    checkItems($list);
    updateFundingSource();

  });

  // remove Objective
  $(".removeObjective").on("click", function () {
    var $list = $(this).parents('.objectivesList');
    var $item = $(this).parents('.objective');
    $item.hide(1000, function() {
      $item.remove();
      checkItems($list);
      updateFundingSource();
    });

  });


 /* // add discipline
  $( ".disciplines" ).change(function() {
  	console.log("cambio");
    var $list = $(".approachesList");
    var $item = $("#approach-template").clone(true).removeAttr("id");
    $list.append($item);
    $item.show('slow', function() {
     width: "50%"
   });

    updateDisciplineList($list);

  });



  //remove discipline
  $(".removeDiscipline").on("click", function () {
    var $list = $(this).parents('.approachesList');
    var $item = $(this).parents('.approach');
    $item.hide(1000, function() {
      $item.remove();
      updateDisciplineList($list);
      //updateFundingSource();
    });

  });*/




 //add country
  $(".capdevCountriesSelect").change(function() {
      var option = $(this).find("option:selected");
      
      if(option.val() != "-1") {
        addCountry(option);
      }
      // Remove option from select
      option.remove();
      $(this).trigger("change.select2");
    });

  //remove country
  $(".removeCountry").on("click", removeCountry);

  //remove country action
   $(".removeCountry-action").on("click", removeCountryAction);


  
  //add region
  // REGION item
    $(".capdevRegionsSelect").on("change", function() {
      var option = $(this).find("option:selected");
      if(option.val() != "-1") {
        addRegion(option);
      }

      // Remove option from select
      option.remove();
      $(this).trigger("change.select2");

    });


  //remove region
  $(".removeRegion").on("click", removeRegion);

  //remove region action
  $(".removeRegion-action").on("click", removeRegionAction);






  function checkItems(block) {
    var items = $(block).find('.objective').length;
    if(items == 0) {
      $(block).parent().find('p.inf').fadeIn();
    } else {
      $(block).parent().find('p.inf').fadeOut();
    }
  }


  function updateFundingSource() {
    $(".objectivesList").find('.objective').each(function(i,e) {
      // Set indexes
      $(e).setNameIndexes(1, i);
    });
  }


  function updateDisciplineList(block){
    var items = $(block).find('.approach').length;
    if(items == 0) {
      $(block).parent().find('p.inf').fadeIn();
    } else {
      $(block).parent().find('p.inf').fadeOut();
    }
  }


  function updateCountryList(block){
    var items = $(block).find('.country').length;
    if(items == 0) {
      $(block).parent().find('p.inf').fadeIn();
    } else {
      $(block).parent().find('p.inf').fadeOut();
    }
  }


  function updateRegionList(block){
  	var items = $(block).find('.region').length;
    if(items == 0) {
      $(block).parent().find('p.inf').fadeIn();
    } else {
      $(block).parent().find('p.inf').fadeOut();
    }
  }


  function updateOutComesList(block){
  	var items = $(block).find('.outcome').length;
    if(items == 0) {
      $(block).parent().find('p.inf').fadeIn();
    } else {
      $(block).parent().find('p.inf').fadeOut();
    }
  }



  function datePickerConfig(element) {
    date($(element.startDate), $(element.endDate),$(element.publicationDate));
  }

  function date(start,end,publicationDate) {
    var dateFormat = "yy-mm-dd";
    var from = $(start).datepicker({
      dateFormat: dateFormat,
      minDate: '2010-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth, 1);
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(end).datepicker("option", "minDate", selectedDate);
        }
      }
    });

    var to = $(end).datepicker({
      dateFormat: dateFormat,
      minDate: '2010-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth + 1, 0);
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(start).datepicker("option", "maxDate", selectedDate);
        }
      }
    });

    var to = $(publicationDate).datepicker({
      dateFormat: dateFormat,
      minDate: '2010-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth + 1, 0);
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(start).datepicker("option", "maxDate", selectedDate);
        }
      }
    });

    function getDate(element) {
      var date;
      try {
        date = $.datepicker.parseDate(dateFormat, element.value);
      } catch(error) {
        date = null;
      }

      return date;
    }
  }


  

  openSearchDialog = function(target) {
    $elementSelected = $(target);
    dialog.dialog("open");
          
        }


  function searchUsersEvent(e) {
    console.log("buscando")
    var query = $(this).val();
    if(query.length > 1) {
      if(timeoutID) {
        clearTimeout(timeoutID);
      }
      // Start a timer that will search when finished
      timeoutID = setTimeout(function() {
        getData(query);
      }, 400);
    } else {
     
    }

  }



  function getData(query) {
    console.log("preparando el ajax call")
    $.ajax({
      'url': baseURL + '/searchContact.do',
      'data': {
        q: query
      },
      'dataType': "json",
      beforeSend: function() {
        console.log("antes de enviar el ajax")
        $dialogContent.find(".search-loader").show();
        $dialogContent.find(".panel-body ul").empty();
      },
      success: function(data) {
        console.log(data)
        var usersFound = (data.users).length;
        if(usersFound > 0) {

          $dialogContent.find(".panel-body .userMessage").hide();
          $.each(data.users, function(i,user) {
            console.log("en el for each")
            if(user.firstName){
              var $item = $("#userTemplate").clone(true).removeAttr("id");

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
        }

      },
      error: function() {
        console.log("algun error")
        $dialogContent.find(".panel-body .userMessage").show();
      },

      complete: function() {
        console.log("terminado todo")
        $dialogContent.find(".search-loader").fadeOut("slow");
      }
    });

  }


  addUser = function(composedName, firstName, lastName, email) {
    $(".contact").val(composedName).addClass('animated flash');
    $(".ctFirsName").val(firstName);
    $(".ctLastName").val(lastName);
    $(".ctEmail").val(email);
    dialog.dialog("close");
  }







  /** COUNTRIES SELECT FUNCTIONS * */
  // Add a new country element
  function addCountry(option) {
    
    var canAdd = true;
    console.log(option);
    if(option.val() == "-1") {
      canAdd = false;
    }

    var $list = $("#capdevCountriesList").find(".list");
    var $item = $("#capdevCountryTemplate").clone(true).removeAttr("id");
    var v = $(option).text().length > 12 ? $(option).text().substr(0, 12) + ' ... ' : $(option).text();

    

  // Check if is already selected
    $list.find('.capdevCountry').each(function(i,e) {
      if($(e).find('input.cId').val() == option.val()) {
        canAdd = false;
        return;
      }
    });
    if(!canAdd) {
      return;
    }

  // Set country parameters
    $item.find(".name").attr("title", $(option).text());
    var $state = $('<span> <i class="flag-sm flag-sm-' + option.val() + '"></i>  ' + v + '</span>');
    $item.find(".name").html($state);
    $item.find(".cId").val(option.val());
    //$item.find(".id").val(-1);
    $list.append($item);
    $item.show('slow');
    updateCountryList($list);
    checkCountryList($list);

    

  // Reset select
    $(option).val("-1");
    $(option).trigger('change.select2');

  }

  function removeCountry() {
    var $list = $(this).parents('.list');
    var $item = $(this).parents('.capdevCountry');
    var value = $item.find(".cId").val();
    var name = $item.find(".name").attr("title");

    var $select = $(".capdevCountriesSelect");
    $item.hide(300, function() {
      $item.remove();
      checkCountryList($list);
      updateCountryList($list);
    });
  // Add country option again
    $select.addOption(value, name);
    $select.trigger("change.select2");
  }

  function updateCountryList($list) {

    $($list).find('.capdevCountry').each(function(i,e) {
      // Set country indexes
      $(e).setNameIndexes(1, i);
    });
  }

  function checkCountryList(block) {
    var items = $(block).find('.capdevCountry').length;
    if(items == 0) {
      $(block).parent().find('p.emptyText').fadeIn();
    } else {
      $(block).parent().find('p.emptyText').fadeOut();
    }
  }



  /** REGIONS SELECT FUNCTIONS * */
  // Add a new region element
  function addRegion(option) {
    var canAdd = true;
    if(option.val() == "-1") {
      canAdd = false;
    }
    var optionValue = option.val().split("-")[0];
    var optionScope = option.val().split("-")[1];

    var $list = $("#capdevRegionsList").find(".list");
    var $item = $("#capdevRegionTemplate").clone(true).removeAttr("id");
    var v = $(option).text().length > 16 ? $(option).text().substr(0, 16) + ' ... ' : $(option).text();

  // Check if is already selected
    $list.find('.capdevRegion').each(function(i,e) {
      if($(e).find('input.rId').val() == optionValue) {
        canAdd = false;
        return;
      }
    });
    if(!canAdd) {
      return;
    }

  // Set region parameters
    $item.find(".name").attr("title", $(option).text());
    $item.find(".name").html(v);
    $item.find(".rId").val(optionValue);

    //$item.find(".id").val(-1);
    $list.append($item);
    $item.show('slow');
    updateRegionList($list);
    checkRegionList($list);

    filterCountry(optionValue)

  }

  function removeRegion() {
    console.log("removeRegion")
    var $list = $(this).parents('.list');
    var $item = $(this).parents('.capdevRegion');
    var value = $item.find(".rId").val();
    var name = $item.find(".name").attr("title");

    var $select = $(".capdevRegionsSelect");
    $item.hide(300, function() {
      $item.remove();
      checkRegionList($list);
      updateRegionList($list);
    });
    var option = $select.find("option[value='" + value + "']");
    console.log(option);
    option.prop('disabled', false);
    //$('.capdevRegionsSelect').select2();

    filterCountry(-1)

  }

  function updateRegionList($list) {

    $($list).find('.capdevRegion').each(function(i,e) {
  // Set regions indexes
      $(e).setNameIndexes(1, i);
    });
  }

  function checkRegionList(block) {
    var items = $(block).find('.capdevRegion').length;
    if(items == 0) {
      $(block).parent().find('p.emptyText').fadeIn();
    } else {
      $(block).parent().find('p.emptyText').fadeOut();
    }
  }


  function removeRegionAction(){
    console.log("removeRegionAction");
    var $item = $(this).parents('.capdevRegion');
    var value = $item.find(".id").val();
    $.ajax({
      'url': baseURL + '/deleteRegion.do',
      'data': {
        q: value
      },
      beforeSend: function() {
        console.log("antes de enviar el ajax")
      },
      success: function(data) {
      },
      error: function() {
        console.log("algun error")
      },
      complete: function() {
        console.log("terminado todo")
      }
    });

  }

  function removeCountryAction(){
    console.log("removeCountryAction");
    var $item = $(this).parents('.capdevCountry');
    var value = $item.find(".id").val();
    $.ajax({
      'url': baseURL + '/deleteCountry.do',
      'data': {
        q: value
      },
      beforeSend: function() {
        console.log("antes de enviar el ajax")
      },
      success: function(data) {
      },
      error: function() {
        console.log("algun error")
      },
      complete: function() {
        console.log("terminado todo")
      }
    });

  }


  //filter countries according region selected
function filterCountry(regionID){
  
   $.ajax({
    'url': baseURL + '/filterCountry.do',
    'data': {
      q: regionID
    },
    'dataType': "json",
    beforeSend: function() {
    },
    success: function(data) {
      var length = data.length;
      $('.capdevCountriesSelect').empty();
      $('.capdevCountriesSelect').append('<option value= -1>select option... </option>');
      for (var i = 0; i < length; i++) {
        $('.capdevCountriesSelect').append('<option value=' + data[i]['countryID'] + '>' + data[i]['countryName'] + '</option>');
      }
    },
    error: function() {
    },
    complete: function() {
      /*$(".project select option").each(function() {
      if($(this).val() == projectSelected){
        $(this).attr( "selected" , "selected");
      }
      });*/
    }
  })
  
  
}

  


$(".button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var $input = $(this).parent().find('input');
    if($(this).hasClass("radio-checked")) {
      console.log(valueSelected)
      $(this).removeClass("radio-checked")
      $input.val("");
    } else {
      console.log(valueSelected)
      $input.val(valueSelected);
      $(this).parent().find("label").removeClass("radio-checked");
      $(this).addClass("radio-checked");
    }
  });

  
  // Is this capdev has a global dimension
  $(".global .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var isChecekd = $(this).hasClass('radio-checked');
    if(!valueSelected || !isChecekd) {
      // $(".countriesBox").show("slow");
    } else {
      // $(".countriesBox").hide("slow");
    }
  });

  // Is this capdev has a regional dimension
  $(".regional .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var isChecekd = $(this).hasClass('radio-checked');
    if(!valueSelected || !isChecekd) {
      $(".regionsBox").hide("slow");
    } else {

      $(".regionsBox").show("slow");
    }
  });


  

  