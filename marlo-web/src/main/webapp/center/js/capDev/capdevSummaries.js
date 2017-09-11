$(document).ready(init);

function init(){
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

    $('.year').append('<option value=0 selected>Select a year </option>');

    

    //setDefaultYear();

}

$('.researchAreasSelect').on("change",function(){
		var researchArea = $(this).val();
		var year = $(".year").val();
		setUrl();
	});

	$('.year').on("change",function(){
		var researchArea = $('.researchAreasSelect').val();
		var year = $(this).val();
		setUrl();
	});	

$("#generarReportCapdev").on("click",function(){
	console.log("click")
	var researchArea = $('.researchAreasSelect').val();
	var year = $(".year").val();
	
})

function setUrl(){
	console.log("setUrl")
	var ra = $('.researchAreasSelect').val();
	var year = $(".year").val();
	if(year === ""){
		year = 0;
	}

	var url = baseURL + "/centerSummaries/capdevSummaries.do?raID="+ra+"&year="+year;
	
	$("div a.generateReportcapdev").attr('href', url).fadeIn()
	
}


function datePickerConfig(element) {
    date($(element.year));
  }

  function date(year) {
    var dateFormat = "yy";
    var from = $(year).datepicker({
      dateFormat: dateFormat,
      
       showButtonPanel: true,
      
      changeYear: true,
      onClose: function(year,inst) {
        var selectedDate = new Date(inst.selectedYear, 1);
        $(this).datepicker('setDate', selectedDate);
        
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
    $(".date-picker-year").focus(function () {
                $(".ui-datepicker-month").hide();
                $(".ui-datepicker-calendar").hide();

            });
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