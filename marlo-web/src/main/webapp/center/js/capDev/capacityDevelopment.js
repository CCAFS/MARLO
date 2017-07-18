  var dialog, capDevDialog;
  var $dialogContent, $dialogCapDevCategory;
  var timeoutID;
  var $contactInput;
  var $elementSelected;


  $(document).ready(init);

  function init(){
    datePickerConfig({
      "startDate": "#capdev\\.startDate",
      "endDate": "#capdev\\.endDate"
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
  $dialogContent.find("span.select, span.firstname").on("click", function() {
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
  })


    // capdev category dialog

    $dialogCapDevCategory = $("#dialog-capdevCategory");
    var dialogCapDevOptions = {
      autoOpen: false,
      height: 400,
      width: 480,
      modal: true,
      dialogClass: 'dialog-searchUsers',
      open: function(event,ui) {

      }

    };

    capDevDialog = $dialogCapDevCategory.dialog(dialogCapDevOptions);

    //event capdev Category dialog
    $(".addCapDevButton").on("click", function(e) {
      console.log("hola")
      e.preventDefault();
      openCapDevDialog($(this));
    });

    $('.close-dialog').on('click', function() {
    capDevDialog.dialog("close");

  });


  //display individual or group intervention form
  (function () {
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
          console.log("hola");
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
          console.log(data);
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
      console.log("holiss");

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
            console.log("loading...");
            $(".loader").show();
          },
          success: function(data) {
            console.log(data.length)
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
            console.log("ha ocurrido un error");

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


  // add discipline
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

  });


  //add outCome
  $( ".outComes" ).change(function() {
    var $list = $(".outComesList");
    var $item = $("#outcome-template").clone(true).removeAttr("id");
    $list.append($item);
    $item.show('slow', function() {
     width: "50%"
   });

    updateOutComesList($list);

  });


  //remove outCome
  $(".removeOutCome").on("click", function () {
    var $list = $(this).parents('.outComesList');
    var $item = $(this).parents('.outcome');
    $item.hide(1000, function() {
      $item.remove();
      updateOutComesList($list);
      //updateFundingSource();
    });

  });


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


  //event add discipline
  $(".disciplinesSelect").on("change", function() {
      var option = $(this).find("option:selected");
      if(option.val() != "-1") {
        addDiscipline(option);
       
      }

       option.remove();
      $(this).trigger("change.select2");
    });

  //event remove discipline
  $(".removeDiscipline").on("click", removeDiscipline);



  // Event add target group
    $(".targetGroupsSelect").on("change", function() {
      var option = $(this).find("option:selected");
      if(option.val() != "-1") {
        addTargetGroup(option);
       
      }

       option.remove();
      $(this).trigger("change.select2");
    });

    //event remove target group
    $(".removetargetGroup").on("click", removeTargetGroup);



    



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
    date($(element.startDate), $(element.endDate));
  }

  function date(start,end) {
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


  openCapDevDialog = function(target) {
    $elementSelected = $(target);
    capDevDialog.dialog("open");
    
        }

  openSearchDialog = function(target) {
    $elementSelected = $(target);
    dialog.dialog("open");
          
        }


  function searchUsersEvent(e) {
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
    $.ajax({
      'url': baseURL + '/searchContact.do',
      'data': {
        q: query
      },
      'dataType': "json",
      beforeSend: function() {

        $dialogContent.find(".search-loader").show();
        $dialogContent.find(".panel-body ul").empty();
      },
      success: function(data) {

        var usersFound = (data.users).length;
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
        }

      },
      error: function() {
        $dialogContent.find(".panel-body .userMessage").show();
      },

      complete: function() {
        $dialogContent.find(".search-loader").fadeOut("slow");
      }
    });

  }


  addUser = function(composedName, firstName, lastName, email) {
    $elementSelected.parents('.contactField ').find("input.contact").val(composedName).addClass('animated flash');
    $elementSelected.parents('.contactField ').find("input.ctFirsName").val(firstName);
    $elementSelected.parents('.contactField ').find("input.ctLastName").val(lastName);
    $elementSelected.parents('.contactField ').find("input.ctEmail").val(email);
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
    console.log("holaaaa");
    console.log(optionValue);
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



  }

  function removeRegion() {
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
    $('.capdevRegionsSelect').select2();

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


  // DISCIPLINE FUNCTIONS
  function addDiscipline(option){

    var canAdd = true;
    console.log(option);
    if(option.val() == "-1") {
      canAdd = false;
    }

    var $list = $("#disciplinesList").find(".list");
    var $item = $("#disciplineTemplate").clone(true).removeAttr("id");
    var v = $(option).text().length > 30 ? $(option).text().substr(0, 20) + ' ... ' : $(option).text();

    

  // Check if is already selected
    $list.find('.discipline').each(function(i,e) {
      if($(e).find('input.disciplineId').val() == option.val()) {
        canAdd = false;
        return;
      }
    });
    if(!canAdd) {
      return;
    }

  // Set discipline parameters
    $item.find(".name").attr("title", $(option).text());
    $item.find(".name").html(v);
    $item.find(".disciplineId").val(option.val());
    $item.find(".id").val(-1);
    $list.append($item);
    $item.show('slow');
    updateDisciplineList($list);
    checkDisciplineList($list);

  // Reset select
    $(option).val("-1");
    $(option).trigger('change.select2');
   }


   function removeDiscipline() {
    var $list = $(this).parents('.list');
    var $item = $(this).parents('.discipline');
    var value = $item.find(".disciplineId").val();
    var name = $item.find(".name").attr("title");

    var $select = $(".disciplinesSelect");
    $item.hide(300, function() {
      $item.remove();
      checkDisciplineList($list);
      updateDisciplineList($list);
    });
  // Add country option again
    $select.addOption(value, name);
    $select.trigger("change.select2");
  }

  function updateDisciplineList($list) {

    $($list).find('.discipline').each(function(i,e) {
      // Set country indexes
      $(e).setNameIndexes(1, i);
    });
  }

  function checkDisciplineList(block) {
    var items = $(block).find('.discipline').length;
    if(items == 0) {
      $(block).parent().find('p.emptyText').fadeIn();
    } else {
      $(block).parent().find('p.emptyText').fadeOut();
    }
  }


  // TARGET GROUPS FUNCTIONS
   function addTargetGroup(option){

    var canAdd = true;
    console.log(option);
    if(option.val() == "-1") {
      canAdd = false;
    }

    var $list = $("#targetGroupsList").find(".list");
    var $item = $("#targetGroupTemplate").clone(true).removeAttr("id");
    var v = $(option).text().length > 20 ? $(option).text().substr(0, 20) + ' ... ' : $(option).text();

    

  // Check if is already selected
    $list.find('.targetGroup').each(function(i,e) {
      if($(e).find('input.tgId').val() == option.val()) {
        canAdd = false;
        return;
      }
    });
    if(!canAdd) {
      return;
    }

  // Set target group parameters
    $item.find(".name").attr("title", $(option).text());
    $item.find(".name").html(v);
    $item.find(".tgId").val(option.val());
    $item.find(".id").val(-1);
    $list.append($item);
    $item.show('slow');
    updateTargetGroupList($list);
    checkTargetGroupList($list);

  // Reset select
    $(option).val("-1");
    $(option).trigger('change.select2');
   }


   function removeTargetGroup() {
    var $list = $(this).parents('.list');
    var $item = $(this).parents('.targetGroup');
    var value = $item.find(".tgId").val();
    var name = $item.find(".name").attr("title");

    var $select = $(".targetGroupsSelect");
    $item.hide(300, function() {
      $item.remove();
      checkTargetGroupList($list);
      updateTargetGroupList($list);
    });
  // Add country option again
    $select.addOption(value, name);
    $select.trigger("change.select2");
  }

  function updateTargetGroupList($list) {

    $($list).find('.targetGroup').each(function(i,e) {
      // Set country indexes
      $(e).setNameIndexes(1, i);
    });
  }

  function checkTargetGroupList(block) {
    var items = $(block).find('.targetGroup').length;
    if(items == 0) {
      $(block).parent().find('p.emptyText').fadeIn();
    } else {
      $(block).parent().find('p.emptyText').fadeOut();
    }
  }
