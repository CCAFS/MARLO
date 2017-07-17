$(document).ready(init);


function init(){
    //filter research program list
    (function(){
      var researchAreaID = $(".capdevResearchArea").val();
      if(researchAreaID > 0){
      	var researchProgramSelected = $(".capdevResearchProgram").val();
      	filterResearchProgram(researchAreaID, researchProgramSelected);
      }
    })();

   //filter project list 
    (function(){
      var researchProgramID = $(".capdevResearchProgram").val();
      //filterProject(researchProgramID);
      if(researchProgramID > 0){
      	var projectSelected = $(".capdevProject").val()
      	filterProject(researchProgramID, projectSelected);
      }
    })();

    //filter partners list
    (function(){

    })();
}



  //event to filter Research Program according to research area selected
$(".capdevResearchArea").on("change",ajaxFilterResearchProgram)


//funcion invocada cuando el se activa el evento onChange 
function  ajaxFilterResearchProgram(){
  var researchAreaID = $(this).val();
  filterResearchProgram(researchAreaID);
  
}


//funcion invocada cuando se carga toda la pagina
function filterResearchProgram(researchAreaID,researchProgramSelected){
   $.ajax({
      'url': baseURL + '/filterReasearchProgram.do',
      'data': {
        q: researchAreaID
      },
      'dataType': "json",
      beforeSend: function() {
      },
      success: function(data) {
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
	      	console.log("if del complete")
	        $(this).attr( "selected" , "selected");
	      }
      	});
      }
    })
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
   $.ajax({
      'url': baseURL + '/filterProject.do',
      'data': {
        q: researchProgramID
      },
      'dataType': "json",
      beforeSend: function() {
      },
      success: function(data) {
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

//event to filter partners according to project selected
$(".capdevProject").on("change", ajaxFilterPartners_outputs);

function ajaxFilterPartners_outputs(){
	 var projectID = $(this).val();
  filterPartners_outputs(projectID);
}

function filterPartners_outputs(projectID){
	console.log("projectID "+projectID)
	$.ajax({
      'url': baseURL + '/filterPartners_outputs.do',
      'data': {
        q: projectID
      },
      'dataType': "json",
      beforeSend: function() {
      },
      success: function(data) {
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