[#ftl]

[#assign customCSS = ["${baseUrlMedia}/css/global/customDataTable.css"] /]
[#assign customCSS = ["${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]
[#assign customJS = ["${baseUrlMedia}/js/capDev/capacityDevelopment.js"] /]
[#assign customJS = ["${baseUrlMedia}/js/capDev/capdevDescription.js","${baseUrlMedia}/js/global/fieldsValidation.js"] /]

[#assign currentStage = "capdevDescription" /]

[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"},
  {"label":"capdevDescription", "nameSpace":"/capdev", "action":""}
]/]


[#include "/WEB-INF/center/global/pages/header.ftl" /]
[#include "/WEB-INF/center/global/pages/main-menu.ftl" /]





<script src="${baseUrl}/bower_components/jquery/dist/jquery.min.js"></script>
<script src="${baseUrlMedia}/js/capDev/capacityDevelopment.js"></script>



<div class="container"> 

	<div class="row">
		<div class="helpMessage infoText col-md-12 capdevinfo">
			<img class="col-md-2" src="${baseUrlMedia}/images/global/icon-help.png" />
    		<p class="col-md-10"> [@s.text name="capdev.help.description"][/@s.text] </p>
		</div>
	</div> 

	<div class="col-md-3 capDevMenu">
		[#include "/WEB-INF/center/views/capDev/menu-capdev.ftl" /]
	</div>
	
	<div class="col-md-9 ">
		<div class="col-md-12">
			<div class="pull-right">
				<a class="" href="[@s.url action='${centerSession}/capdev' /] "><span class="glyphicon glyphicon-circle-arrow-left"></span>[@s.text name="capdev.gotoBack" /]</a> 
			</div>
		</div>
	
		<div class="col-md-12">
				 Capacity Development Description	
		</div>
		
		<div class="col-md-12 form-group "> 
			[@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
			<!-- Radio Buttons-->
			<div style="display: none;">
				<div class="radio">
				  <label><input  id="individual" type="radio" hidden="true" name="capdev.category" class="radioButton" value="${capdev.category}"  /></label>
				</div>
				<div class="radio">
				  <label><input id="gruops" type="radio" hidden="true" name="capdev.category" class="radioButton"  value="${capdev.category}" /> </label>
				</div>
			</div>
						
					
					

			<div  class="fullForm borderBox" >

				<!-- Disciplines-->
				<div class="form-group row ">
					<div class="col-md-12 newCapdevField approachesListTitle" listname="capdev.disciplines">
						[@s.text name="capdev.form.listOfApproaches"][/@s.text] 
					</div>
				</div>
				<div class="form-group row approachesListContainer" >
					<div class="col-md-12 newCapdevField">
						[@customForm.select name="capdevDisciplines" listName="disciplines" keyFieldName="id" displayFieldName="name"  i18nkey="capdev.form.selectApproach" className="disciplinesSelect" multiple=false placeholder="capdev.select" help="capdev.help.discipline" /]
					</div>
					<div id="disciplinesList" class="col-md-12 newCapdevField approachesList" listname="capdev.disciplines">
						<ul class="list">
							[#if capdev.capdevDisciplines?has_content]
							[#list capdev.capdevDisciplines as discipline]
							[#if discipline.active]
							<li id="" class="discipline clearfix col-md-3">
								<div class="removeDiscipline-action removeDiscipline removeIcon" title="Remove discipline"></div>
								<input class="id" type="hidden" name="" value="${(discipline.id)!-1}" />
								<input class="disciplineId" type="hidden" name="capdevDisciplines[-1]" value="${(discipline.disciplines.id)!}" />
								<span class="name"> ${discipline.disciplines.name}</span>
								<div class="clearfix"></div>
							</li>
							[#else]

							[/#if]
							[/#list]
							[#else]
							<p class="emptyText"> [@s.text name="capdev.notDisciplines" /]</p>
							
								 
							[/#if]
						</ul>
					</div>
				</div>

				<!-- Targeted public-->
				<div class="form-group row grupsParticipantsForm">
					<div class="col-md-12 newCapdevField approachesListTitle" listname="capdev.targetgroup">
						[@s.text name="capdev.targetgroup"][/@s.text] 
					</div>
				</div>
				<div class="form-group row borderContainer grupsParticipantsForm" >
					<div class="col-md-12 newCapdevField ">
						[@customForm.select name="capdevTargetGroup" listName="targetGroups" keyFieldName="id" displayFieldName="name"  i18nkey="capdev.targetgroupselect" className="targetGroupsSelect" multiple=false placeholder="capdev.select" help="capdev.help.targetgroup" /]
					</div>

					<div id="targetGroupsList" class="col-md-12 newCapdevField" >
						<ul class="list">
							[#if capdev.capdevTargetgroups?has_content]
							[#list capdev.capdevTargetgroups as targetGroup]
							[#if targetGroup.active]
								<li id="" class="targetGroup clearfix col-md-3">
									<div class="removeTargetGroup-action removeTargetGroup removeIcon" title="Remove targetGroup"></div>
									<input class="id" type="hidden" name="" value="${(targetGroup.id)!-1}" />
									<input class="tgId" type="hidden" name="capdevTargetGroup[-1]" value="${(targetGroup.targetGroups.id)!-1}" />
									<span class="name">${targetGroup.targetGroups.name}</span>
									<div class="clearfix"></div>
								</li>
							[#else]
							[/#if]
							[/#list]
							[#else]
								<p class="emptyText"> [@s.text name="capdev.notTargetGroups" /]</p> 
							[/#if]
						</ul>
					</div>
				</div>

				
				<!-- research Area-->
				<div class="row">
					<div class="col-md-12 newCapdevField">
						<div class="col-md-6 ">
							[@customForm.select name="capdev.researchArea.id" listName="researchAreas" keyFieldName="id" displayFieldName="name"  className="capdevResearchArea" i18nkey="capdev.form.researchArea" placeholder="capdev.select" help="capdev.help.researchArea" /]
						</div>

						<!-- research program-->
						<div class="col-md-6 researchProgram ">
							[@customForm.select name="capdev.researchProgram.id" listName="researchPrograms" keyFieldName="id" displayFieldName="name"  i18nkey="capdev.form.researchProgram" placeholder="capdev.select" className="capdevResearchProgram" help="capdev.help.researchProgram" /]
						</div>
					</div>
				</div>
				
				<!-- CRP -->
				<div class="row">
					<div class="col-md-12 newCapdevField">
						<div class="col-md-6 ">
							[@customForm.select name="capdev.crp.id" listName="crps" keyFieldName="id" displayFieldName="name" i18nkey="capdev.form.crp" placeholder="capdev.select" help="capdev.help.crp" /]
						</div>
						
					</div>
				</div>

				<!-- project-->
				<div class="form-group row newCapdevField ">
					<div class="col-md-12 project" listname="capdev.project">
						[@customForm.select name="capdev.project.id" listName="projects" keyFieldName="id" displayFieldName="name" i18nkey="capdev.form.project" placeholder="capdev.select" className="capdevProject" help="capdev.help.project" /]
					</div>
				</div>
				
				

				<!-- Partners-->
				<div class="form-group row grupsParticipantsForm">
					<div class="col-md-12 newCapdevField approachesListTitle" listname="capdev.partners">
						[@s.text name="capdev.partnerts"][/@s.text] 
					</div>
				</div>
				<div class="form-group row borderContainer grupsParticipantsForm" >
					<div class="col-md-12 newCapdevField ">
						[@customForm.select name="capdevPartners" listName="partners" keyFieldName="id" displayFieldName="name"  i18nkey="capdev.partnertSelect" className="capdevPartnerSelect" multiple=false placeholder="capdev.select" help="capdev.help.partner" /]
					</div>
					
					<div id="capdevPartnersList" class=" partnersList" >
						<ul class="list">
							[#if capdev.capdevPartnerses?has_content]
							[#list capdev.capdevPartnerses as partner]
							[#if partner.active]
								<li id="" class="capdevPartner clearfix col-md-12">
									<div class="removepartner-action removepartner removeIcon" title="Remove partner"></div>
									<input class="id" type="hidden" name="capdev.capdevPartnerses[${partner_index}].id" value="${(partner.id)!-1}" />
									<input class="partnerId" type="hidden" name="capdev.capdevPartnerses[${partner_index}].id" value="${(partner.id)!}" />
									${(partner.institutions.name)!}
									<div class="clearfix"></div>
								</li>
							[#else]
							[/#if]
							[/#list] 
							[#else]
								<p class="emptyText"> [@s.text name="capdev.notPartners" /]</p> 
							[/#if]
						</ul>
					</div>
				</div>

				<!-- OutPuts-->
				<div class="form-group row">
					<div class="col-md-12 newCapdevField objectivesTitle" listname="capdev.outputs">
						[@s.text name="capdev.form.objectives"][/@s.text]
					</div>
				</div>
				<div class="form-group row outComesContainer" >
					<div class="col-md-12 newCapdevField">
						[@customForm.select name="capdevOutputs" listName="outputs" keyFieldName="id" displayFieldName="title" i18nkey="capdev.form.selectOutcome" className="capdevOutputSelect" multiple=false placeholder="capdev.select" help="capdev.help.output" /]
					</div>

					<div id="capdevOutputsList" class="outputsList" >
						<ul class="list">
							[#if capdev.capdevOutputses?has_content]
							[#list capdev.capdevOutputses as output]
							[#if output.active]
								<li id="" class="capdevOutput clearfix col-md-12">
									<div class="removeOutput-action removeOutput removeIcon" title="Remove output"></div>
									<input class="id" type="hidden" name="capdev.capdevOutputses[${output_index}].id" value="${(output.id)!-1}" />
									<input class="outputId" type="hidden" name="capdev.capdevOutputses[${output_index}].id" value="${(output.id)!}" />
									${(output.researchOutputs.title)!}
									<div class="clearfix"></div>
								</li>
							[#else]
							[/#if]
							[/#list] 
							[#else]
								<p class="emptyText"> [@s.text name="capdev.notOutput" /]</p> 
							[/#if]
						</ul>
					</div>
				</div>


				<div style="display: none;">
						[@customForm.input name="capdevID" i18nkey="capdev.id" value="${capdev.id}"  type="text"  /]
				</div>


				<!-- buttons -->
				<div class="col-md-12">
					<div class="buttons">	
			        	<div class="buttons-content">        
				          	[@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /] [/@s.submit]
				        	<div class="clearfix"></div>
			        	</div>
					</div>
				</div>

			</div>

		</div>
		
		[/@s.form]

	</div>
	

</div>




<!-- disciplines template-->
<ul style="display:none">
  <li id="disciplineTemplate" class="discipline clearfix col-md-4">
      <div class="removeDiscipline removeIcon" title="Remove discipline"></div>
      <input class="id" type="hidden" name="" value="" />
      <input class="disciplineId" type="hidden" name="capdevDisciplines[-1]" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>

<!-- target group template-->
<ul style="display:none">
  <li id="targetGroupTemplate" class="targetGroup clearfix col-md-4">
      <div class="removeTargetGroup removeIcon" title="Remove targetGroup"></div>
      <input class="id" type="hidden" name="" value="" />
      <input class="tgId" type="hidden" name="capdevTargetGroup[-1]" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>


<!-- partners template -->
<ul style="display:none">
  <li id="capdevPartnerTemplate" class="capdevPartner clearfix col-md-12">
      <div class="removepartner removeIcon" title="Remove partner"></div>
      <input class="id" type="hidden" name="" value="" />
      <input class="partnerId" type="hidden" name="capdevPartners[-1]" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>

<!-- output template -->
<ul style="display:none">
  <li id="capdevOutputTemplate" class="capdevOutput clearfix col-md-12">
      <div class="removeOutput removeIcon" title="Remove output"></div>
      <input class="id" type="hidden" name="" value="" />
      <input class="outputId" type="hidden" name="capdevOutputs[-1]" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>






[#include "/WEB-INF/center/global/pages/footer.ftl"]



[#macro objectiveMacro element index=0 isTemplate=false]
	
	<div id="objective-${isTemplate?string('template',(element.id)!)}" class="objective  borderBox row"  style="display:${isTemplate?string('none','block')}">
		<div class="removeObjective removeIcon" title="Remove objective"></div>
		<div class="col-md-12">
			 [@customForm.input name="objectiveBody" i18nkey="Objective # ${index + 1}" type="text" /]
		</div>

	</div>
	
[/#macro]





[#macro outComeMacro element isTemplate=false]
	<div id="outcome-${isTemplate?string('template',(element)!)}" class="outcome  borderBox col-md-4 " style="display:${isTemplate?string('none','block')}" >
		<div class="removeOutCome removeIcon" title="Remove outcome"></div>
		<div class="col-md-4">
			 [@s.text name="element" /]
		</div>
	</div>
[/#macro]






