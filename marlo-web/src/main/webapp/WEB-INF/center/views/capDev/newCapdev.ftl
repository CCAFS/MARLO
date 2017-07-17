[#ftl]
[#assign customCSS = ["${baseUrlMedia}/css/global/customDataTable.css"] /]
[#assign customCSS = ["${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]
[#assign customJS = ["${baseUrlMedia}/js/capDev/capacityDevelopment.js"] /]


[#include "/WEB-INF/center/global/pages/header.ftl" /]
[#include "/WEB-INF/center/global/pages/main-menu.ftl" /]

<script src="${baseUrl}/bower_components/jquery/dist/jquery.min.js"></script>
<script src="${baseUrlMedia}/js/capDev/capacityDevelopment.js"></script>


<div class="container"> 
	<div class="col-md-3 capDevMenu">
		[#include "/WEB-INF/center/views/capDev/menu-capdev.ftl" /]
	</div>
	
	
	
	[@s.form action="add" method="POST" enctype="multipart/form-data" cssClass=""]
	
	<div class="col-md-9 ">
	
	<div class="col-md-12">
			New Capacity Development		
	</div>
	
	<div class="col-md-12 form-group newCapdevForm"> 

		<!-- Title-->
		<div class="row">
			<div class="col-md-12 "> 
				<div class="newCapdevField"> 
				[@customForm.input name="capdev.title" type="text" i18nkey="capdev.form.title"  required=true  /]
				</div>
			</div>
		</div>
		<!-- type-->
		<div class="row">
			<div class="col-md-12 newCapdevField">
			<div class="col-md-6 "> 
				[@customForm.select name="capdev.title"listName="" i18nkey="capdev.form.type"  placeholder="select option..." required=true /]
			</div>
			<div class="col-md-6 ">
				[@customForm.input name="capdev.title" i18nkey="capdev.form.contactPerson" type="text"   required=true  /]
			</div>
			</div>
		</div>
		<!-- Strart date-->
		<div class="row">
			<div class="col-md-12 newCapdevField">
			<div class="col-md-6 ">
				[@customForm.input name="capdev.title" i18nkey="capdev.form.startDate" type="text"   required=true  /]
			</div>
			<div class="col-md-6 ">
				[@customForm.input name="capdev.title" i18nkey="capdev.form.endDate" type="text"   required=true  /]
			</div>
			</div>
		</div>
		<!-- Country-->
		<div class="row">
			<div class="col-md-12 newCapdevField">
			<div class="col-md-6 ">
				[@customForm.select name="" listName="" i18nkey="capdev.form.country" placeholder="select option..."  /]
			</div>
			<div class="col-md-6 ">
				[@customForm.input name="capdev.title" i18nkey="capdev.form.region" type="text"     /]
			</div>
			</div>
		</div>
		<!-- research Area-->
		<div class="row">
			<div class="col-md-12 newCapdevField">
			<div class="col-md-6 ">
				[@customForm.select name="" listName="" i18nkey="capdev.form.researchArea" placeholder="select option..."  /]
			</div>
			<div class="col-md-6 ">
				[@customForm.select name="" listName="" i18nkey="capdev.form.researchProgram" placeholder="select option..."  /]
			</div>
			</div>
		</div>
		<!-- project-->
		<div class="row">
			<div class="col-md-12 newCapdevField">
				[@customForm.select name="" listName="" i18nkey="capdev.form.project" placeholder="select option..."  /]
			</div>
		</div>
		<!-- CRP and Dictated by-->
		<div class="row">
			<div class="col-md-12 newCapdevField">
			<div class="col-md-6 ">
				[@customForm.select name="" listName="" i18nkey="capdev.form.crp" placeholder="select option..."  /]
			</div>
			<div class="col-md-6 ">
				[@customForm.input name="capdev.title" i18nkey="capdev.form.dictated" type="text"     /]
			</div>
			</div>
		</div>
		<!-- Disciplines-->
		<div class="row ">
			<div class="col-md-12 newCapdevField approachesListTitle">
				[@s.text name="capdev.form.listOfApproaches"][/@s.text] 
			</div>
		</div>
		<div class="row approachesListContainer">
			<div class="col-md-12 newCapdevField approachesList">
				[#if capDevApproaches?has_content]
                    [#list capDevApproaches as approach]
                      [@disciplineMacro approach /]  
                    [/#list]
                 [/#if]
                 <p class="text-center inf" style="display:${(capDevApproaches?has_content)?string('none','block')}">[@s.text name="capdev.notDisciplines" /]</p>
			</div>
			<div class="col-md-12 newCapdevField">
				[@customForm.select name="" listName="approaches" i18nkey="capdev.form.selectApproach" className="target" multiple=false placeholder="select option..."  /]
			</div>
		</div>
		<!-- Objectives-->
		<div class="row">
			<div class="col-md-12 newCapdevField objectivesTitle">
				[@s.text name="capdev.form.objectives"][/@s.text]
			</div>
		</div>
		
		<div class="row objectivesContainer">
			<div class="col-md-12 newCapdevField objectivesList">
				
				[#if objectives?has_content]
                    [#list objectives as objective]
                      [@objectiveMacro objective objective_index /]  
                    [/#list]
                 [/#if]

                 <p class="text-center inf" style="display:${(objectives?has_content)?string('none','block')}">[@s.text name="capdev.notObjectives" /]</p>
			</div>
			<div class="col-md-12 newCapdevField addObjectiveButton">
				<div class="pull-right">
					<div class="button-green addObjective">
						<span class="glyphicon glyphicon-plus-sign"></span>
						[@s.text name="Add an objective" /]
					</div>
				</div>
			</div>
		</div>
		<!-- participants-->
		<div class="row">
			<div class="col-md-12 newCapdevField">
			<div class="col-md-6  participantsTitle">
				[@s.text name="capdev.form.participants"][/@s.text]
			</div>
			<div class="col-md-6 ">
				<div class="pull-right">
					<button type="button" class="" aria-label="Left Align" >
	  				Dowmload template
					</button>
				</div>
			</div>
			</div>
		</div>

		<div class="row">
			<div class="col-md-12 newCapdevField participantsBox">
				[@customForm.inputFile name="" /]
			</div>
		</div>

		
		
		</div>

	</div>
	
	[/@s.form]
	

</div>


[@objectiveMacro element={} index=0 isTemplate=true /] 
[@disciplineMacro element="" isTemplate=true/]

[#include "/WEB-INF/center/global/pages/footer.ftl"]



[#macro objectiveMacro element index=0 isTemplate=false]
	
	<div id="objective-${isTemplate?string('template',(element.id)!)}" class="objective  borderBox row"  style="display:${isTemplate?string('none','block')}">
		<div class="removeObjective removeIcon" title="Remove objective"></div>
		<div class="col-md-12">
			 [@customForm.input name="objectiveBody" i18nkey="Objective # ${index + 1}" type="text" /]
		</div>

	</div>
	
[/#macro]


[#macro disciplineMacro element isTemplate=false]
	<div id="approach-${isTemplate?string('template',(element)!)}" class="approach  borderBox col-md-4 " style="display:${isTemplate?string('none','block')}" >
		<div class="removeDiscipline removeIcon" title="Remove approach"></div>
		<div class="col-md-4">
			 [@s.text name="element" /]
		</div>
	</div>
[/#macro]