$(document).ready(initCapDev);

function initCapDev(){
	$('.researchAreasSelect').append('<option value= -1 selected>All </option>');


	
	/* plugin developed by valluminarias*/
    $(".year").yearselect({
    	start:2000,
    	end:2030,
    	step:1,
    	order: 'asc',
    	selected: 2017, 
    	formatDisplay: function(yr) { return yr },
    	displayAsValue: true

    })

    $('.year').append('<option value=0 selected>All years</option>');

    

    //setDefaultYear();
    setUrlCapDev();

}

$('.researchAreasSelect').on("change",function(){
		var researchArea = $(this).val();
		var year = $(".year").val();
		setUrlCapDev();
	});

	$('.year').on("change",function(){
		var researchArea = $('.researchAreasSelect').val();
		var year = $(this).val();
		setUrlCapDev();
	});	

$("#generarReportCapdev").on("click",function(){
	console.log("click")
	var researchArea = $('.researchAreasSelect').val();
	var year = $(".year").val();
	
})

function setUrlCapDev(){
	console.log("setUrl")
	var ra = $('.researchAreasSelect').val();
	var year = $(".year").val();
	if(year === ""){
		year = 0;
	}

	var url = baseURL + "/centerSummaries/capdevSummaries.do?raID="+ra+"&year="+year ;
	
	$("div a.generateReportcapdev").attr('href', url).fadeIn()
	
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