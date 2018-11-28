[#ftl]
[#assign currentSectionString = "capdev-${actionName?replace('/','-')}-${capdevID}-phase-${(actualPhase.id)!}" /]
[#assign customCSS = [
  "${baseUrlMedia}/css/capDev/capacityDevelopment.css",
  "${baseUrl}/global/css/customDataTable.css"
  ] 
/]
[#assign pageLibs = ["select2","flat-flags"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/capDev/capacityDevelopment.js",
  "${baseUrlMedia}/js/capDev/capdevDescription.js",
  "${baseUrl}/global/js/fieldsValidation.js",
  "${baseUrl}/global/js/autoSave.js"
  ]
/]

[#assign currentSection = "capdev" /]
[#assign currentStage = "capdevDescription" /]

[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"},
  {"label":"capdevDescription", "nameSpace":"/capdev", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container"> 

  <div class="helpMessage infoText capdevinfo">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />
    <p class="col-md-10"> [@s.text name="capdev.help.description"][/@s.text] </p>
  </div>

  <div class="col-md-3 capDevMenu">
    [#include "/WEB-INF/center/views/capDev/menu-capdev.ftl" /]
  </div>
  
  <div class="col-md-9 ">

    [#-- Section Messages --]
    [#include "/WEB-INF/center/views/capDev/messages-capdev.ftl" /]
    <br />
        
    <div class="">
      <div class="pull-right">
        [#if projectID > 0]
          <a class="" href="[@s.url namespace='/monitoring' action='${centerSession}/projectCapdev'] [@s.param name='projectID']${projectID?c}[/@s.param][@s.param name='edit' value="true" /] [/@s.url] "><span class="glyphicon glyphicon-circle-arrow-left"></span>[@s.text name="capdev.gotoBackProjects" /]</a>
        [#else]
          <a class="" href="[@s.url action='${centerSession}/capdev' /] "><span class="glyphicon glyphicon-circle-arrow-left"></span>[@s.text name="capdev.gotoBack" /]</a>
        [/#if]
      </div>
    </div>
  

    <h4 class="headTitle"> [@s.text name="capdev.additionalInformation" /] </h4>
    <div class="form-group "> 
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
    
      <div  class="fullForm borderBox" >

        [#-- Disciplines --]
        <label>[@s.text name="capdev.form.listOfApproaches"][/@s.text]:[@customForm.req required=editable /]</label>
        <div class="simpleBox" listname="capdev.disciplines">
          <div class="form-group approachesListContainer listSelectBlock" >
            [#if editable]
            <div class=" ">
              [@customForm.select name="" listName="disciplines" keyFieldName="id" displayFieldName="name"  disabled=!editable i18nkey="capdev.form.selectApproach" className="disciplinesSelect" multiple=false placeholder="capdev.select" help="capdev.help.discipline" /]
            </div>
            [/#if]
            <div id="disciplinesList" class="approachesList" >
              <ul class="list">
                [#if (capdev.capdevDisciplineList?has_content)!false]
                  [#list capdev.capdevDisciplineList as discipline]
                    <li id="" class="discipline">
                      [#if editable]
                        <a class="removeDiscipline-action removeDiscipline removeIcon" title="Remove discipline" href="[@s.url action='${centerSession}/deleteDiscipline'][@s.param name="capdevID" value=capdevID /][@s.param name="projectID" value=projectID /][@s.param name="edit" value=true /][@s.param name="capdevDiscipline" value=discipline.id /][/@s.url]"></a>
                      [/#if]
                      <input class="id" type="hidden"  value="${(discipline.id)!}" name="capdev.capdevDisciplineList[${discipline_index}].id" />
                      <input class="disciplineId" type="hidden"  value="${(discipline.discipline.id)!}" name="capdev.capdevDisciplineList[${discipline_index}].discipline.id" />
                      <span class="name"> ${(discipline.discipline.name)!}</span>
                    </li>
                  [/#list]
                [#else]
                
                   
                [/#if]
                <p class="emptyText"> [@s.text name="capdev.notDisciplines" /]</p>
              </ul>
            </div>
          </div>
          [#if editable]
            <div class="">
              <div class="note participantMessage">
                <small>If you cannot find the discipline you are looking for, suggest another one by sending a e-mail to <a href="mailto:MARLOSupport@cgiar.org?Subject=MARLO - CapDev suggest Discipline" target="_top">MARLOSupport@cgiar.org</a></small>
              </div>
            </div>
          [/#if]
        </div>

        [#-- Targeted public--]
        [#if ((capdev.category == 2)!false)]
        <label class="grupsParticipantsForm">[@s.text name="capdev.targetgroup"][/@s.text]:[@customForm.req required=editable /]</label>
        <div class="simpleBox grupsParticipantsForm" listname="capdev.targetgroup">
          <div class="form-group borderContainer grupsParticipantsForm listSelectBlock" >
            [#if editable]
            <div class=" newCapdevField ">
              [@customForm.select name="" listName="targetGroups" keyFieldName="id" displayFieldName="name"  disabled=!editable i18nkey="capdev.targetgroupselect" className="targetGroupsSelect" multiple=false placeholder="capdev.select" help="capdev.help.targetgroup" /]
            </div>
            [/#if]
            <div id="targetGroupsList" class=" newCapdevField" >
              <ul class="list">
                [#if (capdev.capdevTargetGroupList?has_content)!false]
                  [#list capdev.capdevTargetGroupList as targetGroup]
                    <li id="" class="targetGroup">
                      [#if editable]
                        <a class=" removeTargetGroup removeIcon" title="Remove targetGroup" href="[@s.url action='${centerSession}/deleteTargetGroup'][@s.param name="capdevID" value=capdevID /][@s.param name="projectID" value=projectID /][@s.param name="edit" value=true /][@s.param name="capdevTargetGroup" value=targetGroup.id /][/@s.url]"></a>
                      [/#if]
                      <input class="id" type="hidden"  value="${(targetGroup.id)!}" name="capdev.capdevTargetGroupList[${targetGroup_index}].id" />
                      <input class="tgId" type="hidden"  value="${(targetGroup.targetGroups.id)!-1}" name="capdev.capdevTargetGroupList[${targetGroup_index}].targetGroups.id" />
                      <span class="name">${(targetGroup.targetGroups.name)!'null'}</span>
                    </li>
                  [/#list]
                [/#if]
                <p class="emptyText"> [@s.text name="capdev.notTargetGroups" /]</p> 
              </ul>
            </div>
          </div>
          [#if editable]
            <div class="grupsParticipantsForm">
              <div class="note participantMessage">
                <small>If you cannot find the target group you are looking for, suggest another one by clicking on the box <b>"Other"</b></small>
              </div>
            </div>
            <div class="grupsParticipantsForm">
              <label>Other <input type="checkbox" name="capdev.otherTargetGroup" class="otherTargetcheck"   [#if (capdev.otherTargetGroup)??]
              [#if (capdev.otherTargetGroup) == "1"] checked="checked" [/#if] value="${(capdev.otherTargetGroup)!}"[/#if] [#if !editable] disabled="true"[/#if]> </label>
              <div class="suggestTagetGroup" style="display: none;">[@customForm.textArea name="capdev.targetGroupSuggested" i18nkey="Suggest target group"  className="textarea"  /]</div>
            </div>
          [/#if]
        </div>
        [/#if]
        [#-- Partners --]
        [#if ((capdev.category == 2)!false)]
        <label class="grupsParticipantsForm">[@s.text name="capdev.partnerts"][/@s.text]:[@customForm.req required=editable /]</label>
        <div class="simpleBox grupsParticipantsForm" listname="capdev.partners">
          <div class="form-group borderContainer grupsParticipantsForm listSelectBlock" >
            [#if editable]
            <div class="newCapdevField ">
              [@customForm.select name="" listName="partners" keyFieldName="id" displayFieldName="name"  i18nkey="capdev.partnertSelect" className="capdevPartnerSelect" multiple=false placeholder="capdev.select" help="capdev.help.partner"  disabled=!editable /]
            </div>
            [/#if]
            
            <div id="capdevPartnersList" class="partnersList" >
              <ul class="list">
                [#if (capdev.capdevPartnersList?has_content)!false]
                [#list capdev.capdevPartnersList as partner]
                  <li id="" class="capdevPartner clearfix ">
                    [#if editable]
                      <a class="removepartner removeIcon" title="Remove partner" href="[@s.url action='${centerSession}/deletePartner'][@s.param name="capdevID" value=capdevID /][@s.param name="projectID" value=projectID /][@s.param name="edit" value=true /][@s.param name="capdevPartner" value=partner.id /][/@s.url]"></a>
                    [/#if]
                    <input class="id" type="hidden" name="capdev.capdevPartnersList[${partner_index}].id" value="${(partner.id)!}" />
                    <input class="partnerId" type="hidden" name="capdev.capdevPartnersList[${partner_index}].institution.id" value="${(partner.institution.id)!}" />
                    ${(partner.institution.name)!}
                    <div class="clearfix"></div>
                  </li>
                [/#list] 
                [#else]
                [/#if]
                  <p class="emptyText"> [@s.text name="capdev.notPartners" /]</p> 
              </ul>
            </div>
          </div>
          [#-- Request partner adition --]
          [#if editable]
          <p id="addPartnerText" class="helpMessage">
            <small>If you cannot find the institution you are looking for, please 
            <a class="popup" href="[@s.url action='${crpSession}/partnerSave' namespace="/projects"][@s.param name='capdevID']${(capdevID)!}[/@s.param][@s.param name='context']${(actionName)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              click here to [@s.text name="projectPartners.addPartnerMessage.second" /]
            </a>
            </small>
          </p> 
          [/#if]
        </div>
        [/#if]
        [#-- Outputs --]
        <label>[@s.text name="capdev.form.objectives"][/@s.text] </label>
        <div class="simpleBox" listname="capdev.outputs">
          <div class="form-group outComesContainer listSelectBlock" >
            [#if editable]
            <div class=" newCapdevField">
              [@customForm.select name="" listName="outputs" keyFieldName="id" displayFieldName="title" i18nkey="capdev.form.selectOutcome" className="capdevOutputSelect" multiple=false placeholder="capdev.select" help="capdev.help.output" disabled=!editable/]
            </div>
            [/#if]
            <div id="capdevOutputsList" class="outputsList" >
              <ul class="list">
                [#if (capdev.capdevOutputsList?has_content)!false ]
                [#list capdev.capdevOutputsList as output]
                  <li id="" class="capdevOutput clearfix ">
                    [#if editable]
                      <div class="removeOutput-action removeOutput removeIcon" title="Remove output"></div>
                      <a class="removeOutput removeIcon" title="Remove output" href="[@s.url action='${centerSession}/deleteOutput'][@s.param name="capdevID" value=capdevID /][@s.param name="projectID" value=projectID /][@s.param name="edit" value=true /][@s.param name="capdevOutput" value=output.id /][/@s.url]"></a>
                    [/#if]
                    <input class="id" type="hidden" name="capdev.capdevOutputsList[${output_index}].id" value="${(output.id)!}" />
                    <input class="outputId" type="hidden" name="capdev.capdevOutputsList[${output_index}].researchOutputs.id" value="${(output.researchOutputs.id)!}" />
                    ${(output.researchOutputs.title)!}
                    <div class="clearfix"></div>
                  </li>
                [/#list] 
                [#else]
                [/#if]
                  <p class="emptyText"> [@s.text name="capdev.notOutput" /]</p> 
              </ul>
            </div>
          </div>
        </div>

      </div>
      
      [#-- buttons --]
      [#include "/WEB-INF/center/views/capDev/buttons-capdev.ftl" /]

    </div>
    
    [/@s.form]

  </div>
  

</div>

<!-- disciplines template-->
<ul style="display:none">
  <li id="disciplineTemplate" class="discipline ">
      <div class="removeDiscipline removeIcon" title="Remove discipline"></div>
      <input class="id" type="hidden" name="" value="" />
      <input class="disciplineId" type="hidden" name="capdev.capdevDisciplineList[-1].discipline.id" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>

<!-- target group template-->
<ul style="display:none">
  <li id="targetGroupTemplate" class="targetGroup">
      <div class="removeTargetGroup removeIcon" title="Remove targetGroup"></div>
      <input class="id" type="hidden" name="" value="" />
      <input class="tgId" type="hidden" name="capdev.capdevTargetGroupList[-1].targetGroups.id" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>


<!-- partners template -->
<ul style="display:none">
  <li id="capdevPartnerTemplate" class="capdevPartner clearfix ">
      <div class="removepartner removeIcon" title="Remove partner"></div>
      <input class="id" type="hidden" name="" value="" />
      <input class="partnerId" type="hidden" name="capdev.capdevPartnersList[-1].institution.id" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>

<!-- output template -->
<ul style="display:none">
  <li id="capdevOutputTemplate" class="capdevOutput clearfix ">
      <div class="removeOutput removeIcon" title="Remove output"></div>
      <input class="id" type="hidden" name="" value="" />
      <input class="outputId" type="hidden" name="capdev.capdevOutputsList[-1].researchOutputs.id" value="" />
      <span class="name"></span>
      <div class="clearfix"></div>
    </li>
</ul>

[#include "/WEB-INF/global/pages/footer.ftl" /]

[#macro objectiveMacro element index=0 isTemplate=false]
  
  <div id="objective-${isTemplate?string('template',(element.id)!)}" class="objective  borderBox row"  style="display:${isTemplate?string('none','block')}">
    <div class="removeObjective removeIcon" title="Remove objective"></div>
    <div class="">
       [@customForm.input name="objectiveBody" i18nkey="Objective # ${index + 1}" type="text" /]
    </div>

  </div>
  
[/#macro]

[#macro outComeMacro element isTemplate=false]
  <div id="outcome-${isTemplate?string('template',(element)!)}" class="outcome  borderBox " style="display:${isTemplate?string('none','block')}" >
    <div class="removeOutCome removeIcon" title="Remove outcome"></div>
    <div class="">[@s.text name="element" /]</div>
  </div>
[/#macro]