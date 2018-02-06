$(document).ready(initCapDev);

function initCapDev(){
	$('.researchAreasSelect').append('<option value= -1 selected>All </option>');
	$('.researchProgramSelect').append('<option value= -1 selected>All </option>');


	
	/* plugin developed by valluminarias*/
    $(".yearArea").yearselect({
    	start:2000,
    	end:2030,
    	step:1,
    	order: 'asc',
    	selected: 2017, 
    	formatDisplay: function(yr) { return yr },
    	displayAsValue: true

    })

    $('.yearArea').append('<option value=0 selected>All years</option>');

    $(".yearProgram").yearselect({
    	start:2000,
    	end:2030,
    	step:1,
    	order: 'asc',
    	selected: 2017, 
    	formatDisplay: function(yr) { return yr },
    	displayAsValue: true

    })
    $('.yearProgram').append('<option value=0 selected>All years</option>');

    

    
    setUrlCapDevByArea();
    setUrlCapDevByProgram();


}

/*$('.researchAreasSelect').on("change",function(){
		var researchArea = $(this).val();
		var yearArea = $(".yearArea").val();
		setUrlCapDevByArea();

});

$('.yearArea').on("change",function(){
	var researchArea = $('.researchAreasSelect').val();
	var yearArea = $(this).val();
	setUrlCapDevByArea();
});	

$('.researchProgramSelect').on("change",function(){
	var researchProgram = $(this).val();
	var yearProgram = $(".yearProgram").val();
	setUrlCapDevByProgram();
});

$('.yearProgram').on("change",function(){
	var researchProgram = $('.researchProgramSelect').val();
	var yearProgram = $(this).val();
	setUrlCapDevByProgram();
});*/


$("#generarReportCapdevByArea").mouseover(function() {
    setUrlCapDevByArea();
    
  });

  $("#generarReportCapdevByProgram").mouseover(function() {
    setUrlCapDevByProgram();
    
  });




function setUrlCapDevByArea(){
	console.log("setUrl")
	var researchArea = $('.researchAreasSelect').val();
	var year = $(".yearArea").val();
	
	if(year === ""){
		year = 0;
	}
	

	setUrlCapDev(researchArea,-1,year,1);
	

}

function setUrlCapDevByProgram(){
	console.log("setUrl")
	var researchProgram = $('.researchProgramSelect').val();
	var year = $(".yearProgram").val();
	
	if(year === ""){
		year = 0;
	}

	
	setUrlCapDev(-1,researchProgram,year,2);
	

	
}

function setUrlCapDev(researchArea, researchProgram, year, isFrom){
	var url = baseURL + "/centerSummaries/capdevSummaries.do?researchAreaID="+researchArea+"&researchProgramID="+researchProgram+"&year="+year+"&isFrom="+isFrom ;

	if(researchArea != -1){
		$("div a#generarReportCapdevByArea").attr('href', url).fadeIn()
	}

	if(researchProgram != -1){
		$("div a#generarReportCapdevByProgram").attr('href', url).fadeIn()
	}

	else{
		$("div a.generarReportCapdev").attr('href', url).fadeIn()
	}
	
}





 /*Se configura la anio actual por defecto*/
function setDefaultYear(){
	console.log(new Date().getFullYear())

	var currentYear = new Date().getFullYear();

	$(".capdevYearSelect select option").each(function() {
      if($(this).val() == currentYear){
        $(this).attr( "selected" , "selected");
      }
      });
}