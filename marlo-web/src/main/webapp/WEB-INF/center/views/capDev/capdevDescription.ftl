[#ftl]

[#assign customCSS = ["${baseUrl}/global/css/customDataTable.css",
											"${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]

[#assign pageLibs = ["select2","flat-flags"] /]
[#assign customJS = ["${baseUrlMedia}/js/capDev/capdevDescription.js",
											"${baseUrl}/global/js/fieldsValidation.js" ,
											"${baseUrlMedia}/js/capDev/capacityDevelopment.js"] /]

[#assign currentStage = "capdevDescription" /]

[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"},
  {"label":"capdevDescription", "nameSpace":"/capdev", "action":""}
]/]


[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]





<!--<script src="${baseUrlMedia}/js/capDev/capacityDevelopment.js"></script>-->



<div class="container"> 

	<div class="row">
		<div class="helpMessage infoText col-md-12 capdevinfo">
			<img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />
    		<p class="col-md-10"> [@s.text name="capdev.help.description"][/@s.text] </p>
		</div>
	</div> 

	<div class="col-md-3 capDevMenu">
		[#include "/WEB-INF/center/views/capDev/menu-capdev.ftl" /]
	</div>
	
	<div class="col-md-9 ">

		[#-- Section Messages --]
        [#include "/WEB-INF/center/views/capDev/messages-capdev.ftl" /]
        <br />
        
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
				<label>[@s.text name="capdev.form.listOfApproaches"][/@s.text] [@customForm.req/]</label>
				<div class="simpleBox" listname="capdev.disciplines">
					<div class="form-group row approachesListContainer" >
						<div class="col-md-12 ">
							[@customForm.select name="capdevDisciplines" listName="disciplines" keyFieldName="id" displayFieldName="name"  disabled=!editable i18nkey="capdev.form.selectApproach" className="disciplinesSelect" multiple=false placeholder="capdev.select" help="capdev.help.discipline" /]
						</div>
						<div id="disciplinesList" class="col-md-12  approachesList" >
							<ul class="list">
								[#if capdev.capdevDiscipline?has_content]
								[#list capdev.capdevDiscipline as discipline]
								[#if discipline.active]
								<li id="" class="discipline clearfix col-md-3">
									[#if editable]
										<div class="removeDiscipline-action removeDiscipline removeIcon" title="Remove discipline"></div>
									[/#if]
									<input class="id" type="hidden"  value="${(discipline.id)!-1}" />
									<input class="disciplineId" type="hidden"  value="${(discipline.disciplines.id)!}" />
									
									<span class="name"> ${discipline.discipline.name}</span>
									
									<div class="clearfix"></div>
								</li>
								[#else]

								[/#if]
								[/#list]
								[#else]
								
								
									 
								[/#if]
								<p class="emptyText"> [@s.text name="capdev.notDisciplines" /]</p>
							</ul>
						</div>
					</div>
					[#if editable]
						<div class="row ">
							<div class="note participantMessage">
								<p>If you don't find the discipline you are looking for, suggest it selecting <b>Other</b> then write the suggestion</p>
							</div>
						</div>
						<div>
							<label>Other <input type="checkbox" name="otherDiscipline" class="otherDisciplinecheck"   [#if (capdev.otherDiscipline)??]
							[#if (capdev.otherDiscipline) == "1"] checked="checked" [/#if] value="${(capdev.otherDiscipline)!}"[/#if] [#if !editable] disabled="true"[/#if]> </label>
							<div class="suggestDiscipline" style="display: none;">[@customForm.textArea name="capdev.disciplineSuggested" i18nkey="Suggest discipline"  className="textarea"  /]</div>
						</div>
					[/#if]
				</div>

				<!-- Targeted public-->
				<label class="grupsParticipantsForm">[@s.text name="capdev.targetgroup"][/@s.text]</label>
				<div class="simpleBox grupsParticipantsForm" listname="capdev.targetgroup">
					<div class="form-group row borderContainer grupsParticipantsForm" >
						<div class="col-md-12 newCapdevField ">
							[@customForm.select name="capdevTargetGroup" listName="targetGroups" keyFieldName="id" displayFieldName="name"  disabled=!editable i18nkey="capdev.targetgroupselect" className="targetGroupsSelect" multiple=false placeholder="capdev.select" help="capdev.help.targetgroup" /]
						</div>

						<div id="targetGroupsList" class="col-md-12 newCapdevField" >
							<ul class="list">
								[#if capdev.capdevTargetgroup?has_content]
								[#list capdev.capdevTargetgroup as targetGroup]
								[#if targetGroup.active]
									<li id="" class="targetGroup clearfix col-md-3">
										[#if editable]
											<div class="removeTargetGroup-action removeTargetGroup removeIcon" title="Remove targetGroup"></div>
										[/#if]
										<input class="id" type="hidden" name="" value="${(targetGroup.id)!-1}" />
										<input class="tgId" type="hidden"  value="${(targetGroup.targetGroups.id)!-1}" />
										<span class="name">${targetGroup.targetGroups.name}</span>
										<div class="clearfix"></div>
									</li>
								[#else]
								[/#if]
								[/#list]
								[#else]
								[/#if]
								<p class="emptyText"> [@s.text name="capdev.notTargetGroups" /]</p> 
							</ul>
						</div>
					</div>
					[#if editable]
						<div class="row grupsParticipantsForm">
							<div class="note participantMessage">
								<p>If you don't find the target group you are looking for, suggest it selecting <b>Other</b> then write the suggestion</p>
							</div>
						</div>
						<div class="grupsParticipantsForm">
							<label>Other <input type="checkbox" name="otherTargetGroup" class="otherTargetcheck"   [#if (capdev.otherTargetGroup)??]
							[#if (capdev.otherTargetGroup) == "1"] checked="checked" [/#if] value="${(capdev.otherTargetGroup)!}"[/#if] [#if !editable] disabled="true"[/#if]> </label>
							<div class="suggestTagetGroup" style="display: none;">[@customForm.textArea name="capdev.targetGroupSuggested" i18nkey="Suggest target group"  className="textarea"  /]</div>
						</div>
					[/#if]
				</div>

				<div class="simpleBox">
					<!-- research Area-->
					<div class="row">
						<div class="col-md-12 newCapdevField " >
							<div class="col-md-6 " listname="capdev.researcharea">
								[@customForm.select name="capdev.researchArea.id" listName="researchAreas" keyFieldName="id" displayFieldName="name"  required=true className="capdevResearchArea" i18nkey="capdev.form.researchArea" placeholder="capdev.select" help="capdev.help.researchArea" editable=editable/]
							</div>

							<!-- research program-->
							<div class="col-md-6 researchProgram ">
								[@customForm.select name="capdev.researchProgram.id" listName="researchPrograms" keyFieldName="id" displayFieldName="name"  editable=editable i18nkey="capdev.form.researchProgram" placeholder="capdev.select" className="capdevResearchProgram" help="capdev.help.researchProgram" /]
							</div>
						</div>
					</div>
					
					<!-- CRP -->
					<div class="row">
						<div class="col-md-12 newCapdevField">
							<div class="col-md-6 ">
								[@customForm.select name="capdev.crp.id" listName="crps" keyFieldName="id" displayFieldName="name" i18nkey="capdev.form.crp" placeholder="capdev.select" help="capdev.help.crp" editable=editable /]
							</div>
							
						</div>
					</div>

					<!-- project-->
					<div class="form-group row newCapdevField ">
						<div class="col-md-12 project" listname="capdev.project">
							[@customForm.select name="capdev.project.id" listName="projects" keyFieldName="id" displayFieldName="name" i18nkey="capdev.form.project" placeholder="capdev.select" className="capdevProject" help="capdev.help.project" editable=editable /]
						</div>
					</div>
				</div>
				
				

				<!-- Partners-->
				<label class="grupsParticipantsForm">[@s.text name="capdev.partnerts"][/@s.text] </label>
				<div class="simpleBox grupsParticipantsForm" listname="capdev.partners">
					<div class="form-group row borderContainer grupsParticipantsForm" >
						<div class="col-md-12 newCapdevField ">
							[@customForm.select name="capdevPartners" listName="partners" keyFieldName="id" displayFieldName="name"  i18nkey="capdev.partnertSelect" className="capdevPartnerSelect" multiple=false placeholder="capdev.select" help="capdev.help.partner"  disabled=!editable /]
						</div>
						
						<div id="capdevPartnersList" class=" partnersList" >
							<ul class="list">
								[#if capdev.capdevPartners?has_content]
								[#list capdev.capdevPartners as partner]
								[#if partner.active]
									<li id="" class="capdevPartner clearfix col-md-12">
										[#if editable]
											<div class="removepartner-action removepartner removeIcon" title="Remove partner"></div>
										[/#if]
										<input class="id" type="hidden" name="capdev.capdevPartners[${partner_index}].id" value="${(partner.id)!-1}" />
										<input class="partnerId" type="hidden" name="capdev.capdevPartners[${partner_index}].id" value="${(partner.id)!}" />
										${(partner.institution.name)!}
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
					[#if editable]
						<div class="row grupsParticipantsForm">
							<div class="note participantMessage">
								<p>If you don't find the partner you are looking for, suggest it selecting <b>Other</b> then write the suggestion</p>
							</div>
						</div>
						<div class="grupsParticipantsForm">
							<label>Other <input type="checkbox" name="otherPartner" class="otherPartnercheck"   [#if (capdev.otherPartner)??]
							[#if (capdev.otherPartner) == "1"] checked="checked" [/#if] value="${(capdev.otherPartner)!}"[/#if] [#if !editable] disabled="true"[/#if]> </label>
							<div class="suggestPartner" style="display: none;">[@customForm.textArea name="capdev.partnerSuggested" i18nkey="Suggest Partner"  className="textarea"  /]</div>
						</div>
					[/#if]
				</div>

				<!-- OutPuts-->
				
				<label>[@s.text name="capdev.form.objectives"][/@s.text] </label>
				<div class="simpleBox" listname="capdev.outputs">
					<div class="form-group row outComesContainer" >
						<div class="col-md-12 newCapdevField">
							[@customForm.select name="capdevOutputs" listName="outputs" keyFieldName="id" displayFieldName="title" i18nkey="capdev.form.selectOutcome" className="capdevOutputSelect" multiple=false placeholder="capdev.select" help="capdev.help.output" disabled=!editable/]
						</div>

						<div id="capdevOutputsList" class="outputsList" >
							<ul class="list">
								[#if capdev.capdevOutputs?has_content]
								[#list capdev.capdevOutputs as output]
								[#if output.active]
									<li id="" class="capdevOutput clearfix col-md-12">
										[#if editable]
											<div class="removeOutput-action removeOutput removeIcon" title="Remove output"></div>
										[/#if]
										<input class="id" type="hidden" name="capdev.capdevOutputs[${output_index}].id" value="${(output.id)!-1}" />
										<input class="outputId" type="hidden" name="capdev.capdevOutputs[${output_index}].id" value="${(output.id)!}" />
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
				</div>


				<div style="display: none;">
						[@customForm.input name="capdevID" i18nkey="capdev.id" value="${capdev.id}"  type="text"  /]
				</div>


				<!-- buttons -->
				[#include "/WEB-INF/center/views/capDev/capdev-buttons.ftl" /]

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
      <input class="disciplineId" type="hidden" name="capdevDisciplines[0]" value="" />
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






[#include "/WEB-INF/center/pages/footer.ftl"]



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






