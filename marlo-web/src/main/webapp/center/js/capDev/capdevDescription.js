$(document).ready(init);


function init(){

  $('form select').select2({
    width: "100%"
    });
  
    //filter research program list
    (function(){
      var researchAreaID = $(".capdevResearchArea").val();
      
      	var researchProgramSelected = $(".capdevResearchProgram").val();
      	filterResearchProgram(researchAreaID, researchProgramSelected);
      
    })();

   //filter project list 
    (function(){
      var researchProgramID = $(".capdevResearchProgram").val();
      //filterProject(researchProgramID);
      
      	var projectSelected = $(".capdevProject").val()
      	filterProject(researchProgramID, projectSelected);
      
    })();

    //filter partners and outputs list
    (function(){
      var projectID = $(".capdevProject").val();
     
        filterPartners_outputs(projectID);
      
    })();


     (function(){
      if($('input[type="checkbox"][name="capdev.otherDiscipline"]').is(":checked")){
        $(".suggestDiscipline").show();
      }
      if($('input[type="checkbox"][name="capdev.otherTargetGroup"]').is(":checked")){
        $(".suggestTagetGroup").show();
      }
      if($('input[type="checkbox"][name="capdev.otherPartner"]').is(":checked")){
        $(".suggestPartner").show();
      }
    })();


    //check discipline list
    var $list = $("#disciplinesList").find(".list");
    checkDisciplineList($list);

    //check target groups list 
    var $list = $("#targetGroupsList").find(".list");
    checkTargetGroupList($list);

    // check partners list
    var $list = $("#capdevPartnersList").find(".list");
    checkPartnerList($list);

    //check outputs list
    var $list = $("#capdevOutputsList").find(".list");
    checkOutputList($list);
    
}

  //limit number of caracter entered to the text area
  $('.textarea').keypress(function(e) {
    var tval = $('.textarea').val(),
        tlength = tval.length,
        set = 200,
        remain = parseInt(set - tlength);
    /*$('p').text(remain);*/
    if (remain <= 0 && e.which !== 0 && e.charCode !== 0) {
        $('.textarea').val((tval).substring(0, tlength - 1))
    }
})


  //event add discipline
  $(".disciplinesSelect").change(function() {
      var option = $(this).find("option:selected");
      if(option.val() != "-1") {
        addDiscipline(option);
       
      }

       option.remove();
      $(this).trigger("change.select2");
    });

  //event remove discipline
  $(".removeDiscipline").on("click", removeDiscipline);

  //remove discipline action
  $(".removeDiscipline-action").on("click", removeDisciplineAction);


  //display suggest text area to discipline
  $('input[type="checkbox"][name="capdev.otherDiscipline"]').change(function() {
     if(this.checked) {
       $(".suggestDiscipline").show();
       $(".otherDisciplinecheck").val("1")
     }
     else{
      $(".suggestDiscipline").hide();
      $(".otherDisciplinecheck").val("0")
      
     }
   });


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
$(".removeTargetGroup").on("click", removeTargetGroup);


// remove target group action
$(".removeTargetGroup-action").on("click", removeTargetGroupAction);

//display suggest text area to target groups
  $('input[type="checkbox"][name="capdev.otherTargetGroup"]').change(function() {
     if(this.checked) {
       $(".suggestTagetGroup").show();
       $(".otherTargetcheck").val("1")
     }
     else{
      $(".suggestTagetGroup").hide();
      $(".otherTargetcheck").val("0")
      
     }
   });



  //event to filter Research Program according to research area selected
$(".capdevResearchArea").on("change",ajaxFilterResearchProgram)


//funcion invocada cuando  se activa el evento onChange 
function  ajaxFilterResearchProgram(){
  var researchAreaID = $(this).val();
  filterResearchProgram(researchAreaID);
  
}


//funcion invocada cuando se carga toda la pagina
function filterResearchProgram(researchAreaID,researchProgramSelected){
  if(researchAreaID > 0){
     $.ajax({
      'url': baseURL + '/filterReasearchProgram.do',
      'data': {
        q: researchAreaID
      },
      'dataType': "json",
      beforeSend: function() {
      },
      success: function(data) {
         $('.capdevResearchProgram').removeAttr('disabled');
        var length = data.length;
        $('.capdevResearchProgram').empty();
        $('.capdevResearchProgram').append('<option value= -1>select option... </option>');
        for (var i = 0; i < length; i++) {
          $('.capdevResearchProgram').append('<option value=' + data[i]['rpID'] + '>' + data[i]['rpName'] + '</option>');
        }
      },
      error: function() {
      },
      complete: function() {
        $(".researchProgram select option").each(function(index) {
        if($(this).val() == researchProgramSelected){
          $(this).attr( "selected" , "selected");
        }
        });
      }
    })
  }
  else{
     $('.capdevResearchProgram').attr('disabled', 'disabled');
     $('.capdevResearchProgram').empty();
     $('.capdevResearchProgram').append('<option value= -1>select option... </option>');

  }
  
}



//event to filter project according to research program selected
$(".capdevResearchProgram").on("change", ajaxFilterProject)

//funcion invocada cuando se selecciona un programa
function  ajaxFilterProject(){
  var researchProgramID = $(this).val();
  filterProject(researchProgramID);
}


//funcion invocada cuando se carga toda la pagina
function filterProject(researchProgramID,projectSelected){
  if(researchProgramID > 0){
     $.ajax({
      'url': baseURL + '/filterProject.do',
      'data': {
        q: researchProgramID
      },
      'dataType': "json",
      beforeSend: function() {
      },
      success: function(data) {
        $('.capdevProject').removeAttr('disabled');
        var length = data.length;
        $('.capdevProject').empty();
        $('.capdevProject').append('<option value= -1>select option... </option>');
        for (var i = 0; i < length; i++) {
          $('.capdevProject').append('<option value=' + data[i]['projectID'] + '>' + data[i]['projectName'] + '</option>');
        }
      },
      error: function() {
      },
      complete: function() {
        $(".project select option").each(function() {
        if($(this).val() == projectSelected){
          $(this).attr( "selected" , "selected");
        }
        });
      }
    })
  }
  else{
    $('.capdevProject').attr('disabled', 'disabled');
    $('.capdevProject').empty();
    $('.capdevProject').append('<option value= -1>select option... </option>');
  }
  
}

//event to filter partners according to project selected
$(".capdevProject").on("change", ajaxFilterPartners_outputs);

function ajaxFilterPartners_outputs(){
	 var projectID = $(this).val();
  filterPartners_outputs(projectID);
}

function filterPartners_outputs(projectID){
  if(projectID > 0){
    $.ajax({
      'url': baseURL + '/filterPartners_outputs.do',
      'data': {
        q: projectID
      },
      'dataType': "json",
      beforeSend: function() {
      },
      success: function(data) {
         $(".capdevOutputSelect").removeAttr('disabled');
        //set  partners List
        var partnersLength = data[0]['partners'].length;
        $('.capdevPartnerSelect').empty();
        $('.capdevPartnerSelect').append('<option value= -1>select option... </option>');
        for (var i = 0; i < partnersLength; i++) {
          $('.capdevPartnerSelect').append('<option value=' + data[0]['partners'][i]['idPartner'] + '>' + data[0]['partners'][i]['partnerName']  + '</option>');
        }

        //set  output list
        var outputsLength = data[1]['outputs'].length;
        $('.capdevOutputSelect').empty();
        $('.capdevOutputSelect').append('<option value= -1>select option... </option>');
        for (var i = 0; i < outputsLength; i++) {
          $('.capdevOutputSelect').append('<option value=' + data[1]['outputs'][i]['idOutput'] + '>' + data[1]['outputs'][i]['outputTitle']  + '</option>');
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
  else{
    $(".capdevOutputSelect").attr('disabled', 'disabled');
    $('.capdevOutputSelect').empty();
    $('.capdevOutputSelect').append('<option value= -1>select option... </option>');
  }
	
	
}


//event add partners
$(".capdevPartnerSelect").change(function() {
	var option = $(this).find("option:selected");
	if(option.val() != "-1") {
	addPartner(option);
	}
	// Remove option from select
	option.remove();
	$(this).trigger("change.select2");
});

//event remove partner
$(".removepartner").on("click", removePartner);

//remove partner action
$(".removepartner-action").on("click", removePartnerAction);

//display suggest text area to partners
  $('input[type="checkbox"][name="capdev.otherPartner"]').change(function() {
     if(this.checked) {
       $(".suggestPartner").show();
       $(".otherPartnercheck").val("1")
     }
     else{
      $(".suggestPartner").hide();
      $(".otherPartnercheck").val("0")
      
     }
   });


//event add output
$(".capdevOutputSelect").change(function() {
	var option = $(this).find("option:selected");

	if(option.val() != "-1") {
	addOutput(option);
	}
	// Remove option from select
	option.remove();
	$(this).trigger("change.select2");
});

//event remove output
$(".removeOutput").on("click", removeOutput);


//remove output action
$(".removeOutput-action").on("click", removeOutputAction);


//function add partner
function addPartner(option) {
    var canAdd = true;
    if(option.val() == "-1") {
      canAdd = false;
    }
    var $list = $("#capdevPartnersList").find(".list");
    var $item = $("#capdevPartnerTemplate").clone(true).removeAttr("id");
    var v = $(option).text().length > 12 ? $(option).text().substr(0, 12) + ' ... ' : $(option).text();

  // Check if is already selected
    $list.find('.capdevPartner').each(function(i,e) {
      if($(e).find('input.partnerId').val() == option.val()) {
        canAdd = false;
        return;
      }
    });
    if(!canAdd) {
      return;
    }

  // Set partner parameters
    $item.find(".name").attr("title", $(option).text());
    $item.find(".name").html($(option).text());
    $item.find(".partnerId").val(option.val());
    $list.append($item);
    $item.show('slow');
    updatePartnerList($list);
    checkPartnerList($list);

  // Reset select
    $(option).val("-1");
    $(option).trigger('change.select2');
}

//function remove partner 
function removePartner() {
	var $list = $(this).parents('.list');
	var $item = $(this).parents('.capdevPartner');
	var value = $item.find(".partnerId").val();
	var name = $item.find(".name").attr("title");

	var $select = $(".capdevPartnerSelect");
	$item.hide(300, function() {
		$item.remove();
		updatePartnerList($list);
		checkPartnerList($list);
	});
	// Add partner option again
	$select.addOption(value, name);
	$select.trigger("change.select2");
}


function removePartnerAction(){
    console.log("removePartnerAction");
    var $item = $(this).parents('.capdevPartner');
    var value = $item.find(".id").val();
    $.ajax({
      'url': baseURL + '/deletePartner.do',
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


function updatePartnerList($list) {
	$($list).find('.capdevPartner').each(function(i,e) {
	  // Set country indexes
	  $(e).setNameIndexes(1, i);
	});
}

function checkPartnerList(block) {
	var items = $(block).find('.capdevPartner').length;
	if(items == 0) {
	  $(block).parent().find('p.emptyText').fadeIn();
	} else {
	  $(block).parent().find('p.emptyText').fadeOut();
	}
}


//function add output
function addOutput(option) {
    var canAdd = true;
    if(option.val() == "-1") {
      canAdd = false;
    }
    var $list = $("#capdevOutputsList").find(".list");
    var $item = $("#capdevOutputTemplate").clone(true).removeAttr("id");
    var v = $(option).text().length > 12 ? $(option).text().substr(0, 12) + ' ... ' : $(option).text();

  // Check if is already selected
    $list.find('.capdevOutput').each(function(i,e) {
      if($(e).find('input.outputId').val() == option.val()) {
        canAdd = false;
        return;
      }
    });
    if(!canAdd) {
      return;
    }

  // Set output parameters
    $item.find(".name").attr("title", $(option).text());
    //var $state = $('<span> <i class="flag-sm flag-sm-' + option.val() + '"></i>  ' + v + '</span>');
    $item.find(".name").html($(option).text());
    $item.find(".outputId").val(option.val());
    $list.append($item);
    $item.show('slow');
    updateOutputList($list);
    checkOutputList($list);

  // Reset select
    $(option).val("-1");
    $(option).trigger('change.select2');
}

//function remove oputput 
function removeOutput() {
	var $list = $(this).parents('.list');
	var $item = $(this).parents('.capdevOutput');
	var value = $item.find(".outputId").val();
	var name = $item.find(".name").attr("title");

	var $select = $(".capdevOutputSelect");
	$item.hide(300, function() {
	  $item.remove();
	updateOutputList($list);
    checkOutputList($list);
	});
	// Add output option again
	$select.addOption(value, name);
	$select.trigger("change.select2");
}


function removeOutputAction(){
    console.log("removeOutputAction");
    var $item = $(this).parents('.capdevOutput');
    var value = $item.find(".id").val();
    $.ajax({
      'url': baseURL + '/deleteOutput.do',
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


function updateOutputList($list) {
	$($list).find('.capdevOutput').each(function(i,e) {
	  // Set country indexes
	  $(e).setNameIndexes(1, i);
	});
}

function checkOutputList(block) {
	var items = $(block).find('.capdevOutput').length;
	if(items == 0) {
	  $(block).parent().find('p.emptyText').fadeIn();
	} else {
	  $(block).parent().find('p.emptyText').fadeOut();
	}
}



// DISCIPLINE FUNCTIONS
  function addDiscipline(option){

    var canAdd = true;
    console.log(option.val());
    if(option.val() == "-1") {
      canAdd = false;
    }

    var $list = $("#disciplinesList").find(".list");
    var $item = $("#disciplineTemplate").clone(true).removeAttr("id");
    var name = $(option).text().length > 30 ? $(option).text().substr(0, 20) + ' ... ' : $(option).text();

    

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
    $item.find(".name").html(name);
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


  function removeDisciplineAction(){
    console.log("removeDisciplineAction");
    var $item = $(this).parents('.discipline');
    var value = $item.find(".id").val();
    $.ajax({
      'url': baseURL + '/deleteDiscipline.do',
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


function removeTargetGroupAction(){
   console.log("removeTargetGroupAction");
    var $item = $(this).parents('.targetGroup');
    var value = $item.find(".id").val();
    $.ajax({
      'url': baseURL + '/deletetargetGroup.do',
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
