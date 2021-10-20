[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro studyMacro element name index=-1 template=false fromProject=true ]
  [#local customName = "${name}"/]
  [#local customId = "study-${template?string('template',index)}" /]
  [#local isOutcomeCaseStudy = ((element.projectExpectedStudyInfo.studyType.id == 1)!false) && (reportingActive || upKeepActive)/]
  [#local isNew = (action.isEvidenceNew(element.id))!false /]
  
  [#local isPolicy = ((element.projectExpectedStudyInfo.isContribution)!false) ]
  [#local stageProcessOne = ((element.projectExpectedStudyInfo.repIndStageProcess.id == 1))!false ]
  [#local isStatusExtended = (element.projectExpectedStudyInfo.status.id == 4)!false]
  [#local isOtherStatus = (element.projectExpectedStudyInfo.status.id != 4)!false]

  
  <div id="${customId}" class="caseStudy evidenceBlock isNew-${isNew?string}" style="display:${template?string('none','block')}">
    <div class="borderBox">
    
      <div class="form-group">
        [#if isOutcomeCaseStudy]
          [#assign guideSheetURL = "https://docs.google.com/document/d/1C4K1iJ0dOrInk15Xzjs_wJgj_OKO4TDx/edit?rtpof=true&sd=true" /]
          <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> Outcome Impact Case Report  -  Guideline </a> </small>
        [#else]
          [#assign guideSheetURL = "https://docs.google.com/document/d/1Nd-D3K4Zid8hUX0Vv5Yca7Y9azhcs-0U/edit?rtpof=true&sd=true" /]
          <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> MELIA  -  Guideline </a> </small>
        [/#if]
      </div>
      <br />
    
      <div class="form-group row">
        <div class="col-md-4">
          [@customForm.select name="${customName}.projectExpectedStudyInfo.studyType.id" value="${(element.projectExpectedStudyInfo.studyType.id)!-1}" className="setSelect2 studyType" i18nkey="study.type" listName="studyTypes" keyFieldName="id"  displayFieldName="name" required=true editable=editable && !isOutcomeCaseStudy /]
        </div>
        <div class="col-md-4">
          [@customForm.select name="${customName}.projectExpectedStudyInfo.status.id" className="setSelect2 statusSelect" i18nkey="study.status" listName="statuses" keyFieldName="id"  displayFieldName="name" header=false required=true editable=editable /]
        </div>
        <div class="col-md-4">
          [#assign dbExpectedYear = ((element.projectExpectedStudyInfo.year)!currentCycleYear)  ]
          
           [#--
          [@customForm.select name="${customName}.projectExpectedStudyInfo.year" className="setSelect2" i18nkey="study.year" listName="getExpectedStudiesYears(${(expectedID)!})" header=false required=true editable=editable /]
            --]  
          
         <div class="block-extendedYear" style="display:${isStatusExtended?string('block', 'none')}">
            [@customForm.select name="newExpectedYear" className="setSelect2" i18nkey="study.year" listName="project.projectInfo.getYears(${currentCycleYear})" header=false required=true editable=editable /]
          </div>
          <div class="block-year" style="display:${(!isStatusExtended && isOtherStatus)?string('block', 'none')}">
            [@customForm.select name="${customName}.projectExpectedStudyInfo.year" className="setSelect2" i18nkey="study.year" listName="getExpectedStudiesYears(${(expectedID)!})" header=false required=true editable=editable /]
          </div>
        </div>
      </div>
      


      [#-- Evidences table with types and their descriptions --]
      [#if !((element.projectExpectedStudyInfo.studyType.id == 1)!false)]
          <div class="note left" style="margin: 10px 0px; font-size: .85em; padding: 4px; border-radius: 10px;">
            <div id="popup" class="helpMessage3">
              <p><a style="cursor: pointer;" data-toggle="modal" data-target="#evidenceModal" > <span class="glyphicon glyphicon-info-sign"></span> [@s.text name="study.generalInformation.studyType" /]</a></p>
            </div>
          </div>
      [#if (reportingActive)!false]
      <div class="form-group analysisGroup">
        <label for="">[@s.text name="study.covidAnalysis" /]:[@customForm.req required=false /]
        </label>        
        <div class="form-group">
          [#assign covidAnalisis = "covidAnalisis"]
          [#assign showAnalysis = (expectedStudy.projectExpectedStudyInfo.hasCovidAnalysis?string)!"" /]
          [@customForm.radioFlat id="${covidAnalisis}-yes" name="${customName}.projectExpectedStudyInfo.hasCovidAnalysis" label="Yes" value="true" checked=(showAnalysis == "true") cssClassLabel="radio-label-yes" editable=editable /]
          [@customForm.radioFlat id="${covidAnalisis}-no" name="${customName}.projectExpectedStudyInfo.hasCovidAnalysis" label="No" value="false" checked=(showAnalysis == "false") cssClassLabel="radio-label-no" editable=editable /]
        </div>  
      </div>
      [/#if]


        <div class="form-group evidenceTypeMessage">
          <div class="modal fade" id="evidenceModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-scrollable" style=" width:80%" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <!-- <h5 class="modal-title" id="exampleModalLabel">Modal title</h5> -->
                  <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                  </button>
                </div>
                <div class="modal-body">
                  
                  <table id="evidenceTypes" class="table ">
                    <thead style="background-color: #0b7ba6; font-weight: 500; color: white;">
                      <tr>
                        <th> [@s.text name="study.dialogMessage.part1" /]</th>
                        <th > [@s.text name="study.dialogMessage.part2" /] </th>
                        <th> [@s.text name="study.dialogMessage.part3" /] / [@s.text name="study.dialogMessage.part4" /] </th>
                      </tr>
                    </thead>

                    [#if studyTypes?has_content]
                    [#list studyTypes as st]
                    <tr>
                      [#--if st_index == 0]
                      <th rowspan="${action.getDeliverablesSubTypes(mt.id).size()}" class="text-center"> ${mt.name} </th>
                      [/#if--]
                      <td  >
                        ${st.name}
                      </td>
                      <td style="max-width: 90vw !important;">
                      [#if (st.description?has_content)!false]
                        ${st.description}
                      [#else]
                        <i>([@s.text name="study.dialogMessage.notProvided" /])</i>
                      [/#if]
                      </td>
                      <td>
                        [#if (((st.keyIdentifier?has_content)!false) || ((st.forNarrative?has_content)!false) || ((st.example?has_content)!false))]
                          [#if (st.keyIdentifier?has_content)!false]
                            <i><u>How to identify?</u></i> ${st.keyIdentifier}
                          [/#if]
                          [#if (st.forNarrative?has_content)!false]
                            <br><i><u>For:</u></i> ${st.forNarrative}
                          [/#if]
                          [#if (st.example?has_content)!false]
                            <br /> (<i><small>Example: ${st.example}</small></i>)
                          [/#if]
                        [#else]
                          <i>([@s.text name="study.dialogMessage.notProvided" /])</i>
                        [/#if]
                      </td>
                    </tr>
                    [/#list]
                    [/#if]
                  </table>
                </div>
                <div class="modal-footer">
                  <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                </div>
              </div>
            </div>
          </div>

          <div class="clearfix"></div>
        </div>
      [/#if]  
      [#-- REMOVED FOR AR 2020 --]
      [#--]if isOutcomeCaseStudy]
        <hr />
        [#-- Tags]
        <div class="form-group">
          <label for="">[@s.text name="study.tags" /]:[@customForm.req required=editable /]</label>
          [#local tagValue = (element.projectExpectedStudyInfo.evidenceTag.id)!-1 ]
          [#list tags as tag]
            <br /> [@customForm.radioFlat id="tag-${tag_index}" name="${customName}.projectExpectedStudyInfo.evidenceTag.id" label="${tag.name}" value="${tag.id}" checked=(tagValue == tag.id) cssClass="radioType-tags" cssClassLabel="font-normal" editable=editable /] 
          [/#list]
        </div>
      [/#if] --]
    </div>
    <div class="borderBox">
      [#-- 0. Link to PDF version of this study: AR 2020 and onwards -> ALL OICRs are ALWAYS public--]
      [#if isOutcomeCaseStudy]
        <div class="form-group">
          <div class="optionPublicComponent form-group" style="display:block">         
            <br />
            <div class="input-group">
              <span class="input-group-btn">
                <button class="btn btn-default btn-sm copyButton" type="button"> <span class="glyphicon glyphicon-duplicate"></span> Copy URL </button>
              </span>
              [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(element.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
              [@customForm.input name="${customName}.projectExpectedStudyInfo.link" i18nkey="study.link" className="form-control input-sm urlInput" value="${summaryPDF}" editable=editable readOnly=true/]
              <!--input type="text" class="form-control input-sm urlInput" value="${summaryPDF}" readonly-->
            </div>
            <div class="message text-center" style="display:none">Copied!</div>
          </div>
        </div>
      [/#if]

      [#-- 1. Title (up to 35 words if OICR, else no limit ) --]
      <div class="form-group">
        [@customForm.input name="${customName}.projectExpectedStudyInfo.title" i18nkey="study.title" help="study.title.help" className=(isOutcomeCaseStudy?then("limitWords-35","")) helpIcon=!isOutcomeCaseStudy required=true editable=editable /]
      </div>
      
      [#-- Who is commissioning this study --]
      [#if !isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.input name="${customName}.projectExpectedStudyInfo.commissioningStudy" i18nkey="study.commissioningStudy" help="study.commissioningStudy.help" className="" helpIcon=false required=true editable=editable /]
      </div>
      [/#if]
      
      [#-- 2. Short outcome/impact statement (up to 80 words) --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.outcomeImpactStatement" i18nkey="study.outcomeStatement" help="study.outcomeStatement.help" className="limitWords-80" helpIcon=false required=true editable=editable /]
      </div>
      [/#if]
      
      [#-- 3. Outcome story for communications use. REPLACED "comunicationsMaterial" --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.comunicationsMaterial" i18nkey="study.outcomestory" help="study.outcomestory.help" className="limitWords-400" helpIcon=false required=false editable=editable /]
      
        <br />
        
        
        <label for="">[@s.text name="study.outcomestoryLinks" /]:
          [@customForm.req required=false /]
          <span id="warningEmptyLinksTag" class="errorTag glyphicon glyphicon-info-sign" style="position: relative; left: 271px;" title="" aria-describedby="ui-id-5"> </span>
          [@customForm.helpLabel name="study.outcomestoryLinks.help" paramText="<a href='https://hdl.handle.net/10568/99384' target='_blank'>Personal data use authorization form</a>" showIcon=false editable=editable/]
        </label>
        <div class="linksBlock ">
          <div class="linksList">
            [#list (element.links)![{}] as link ]
              [@customForm.multiInput name="${customName}.links" element=link index=link_index class="links" placeholder="global.webSiteLink.placeholder" /]
            [/#list]
          </div>
          [#if editable]
          <div class="addButtonLink button-green pull-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add Link </div>
          <div class="clearfix"></div>
          [/#if]
        </div>
        [#-- Element item Template --]
        <div style="display:none">
          [@customForm.multiInput name="${customName}.links" element={} index=-1 template=true class="links" placeholder="global.webSiteLink.placeholder" /]
        </div>
      </div>
      [/#if]
      
      [#--  Geographic scope - Countries  --]
      <div class="form-group geographicScopeBlock">
        [#local geographicScopeList = (element.geographicScopes)![] ]
        [#local isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
        [#local isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
        [#local isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
        [#local isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]
        
        <label for="">[@s.text name="study.geographicScopeTopic" /]:[@customForm.req required=editable /]</label>
        <div class="form-group simpleBox">
          <div class="form-group row">
            <div class="col-md-6">
              [#-- Geographic Scope --]
              [@customForm.elementsListComponent name="${customName}.geographicScopes" elementType="repIndGeographicScope" elementList=element.geographicScopes  label="study.geographicScope" listName="geographicScopes" keyFieldName="id" displayFieldName="name" required=true /]
            </div>
          </div>
          <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
            [#-- Regional scope --]
            [@customForm.elementsListComponent name="${customName}.studyRegions" elementType="locElement" elementList=element.studyRegions label="study.region"  listName="regions" keyFieldName="id" displayFieldName="composedName" required=false /]
          </div>
          <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
            [#-- Multinational, National and Subnational scope --]
            [@customForm.select name="${customName}.countriesIds" label="" i18nkey="study.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="${customName}.countriesIds" multiple=true required=true className="countriesSelect" disabled=!editable/]
          </div>
          <div class="form-group">
            [#-- Comment box --]
            [@customForm.textArea name="${customName}.projectExpectedStudyInfo.scopeComments" className="limitWords-30" i18nkey="study.geographicScopeComments" help="study.geographicScopeComments.help" helpIcon=false  editable=editable required=false/]
          </div>
        </div>
      </div>
      
      [#-- 3. Link to Common Results Reporting Indicator #I3 --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [#-- Does this outcome reflect a contribution of the CGIAR in influencing or modifying policies/ strategies / laws/ regulations/ budgets/ investments or  curricula?  --]
        <div class="form-group">
          
          <label for="">[@s.text name="study.reportingIndicatorThree" /]:[@customForm.req required=editable /][@customForm.helpLabel name="study.reportingIndicatorThree.help" showIcon=false editable=editable/]</label>
          [#assign studyIndicatorThree = "studyIndicatorThree"]
          [#assign showPolicyIndicator = (element.projectExpectedStudyInfo.isContribution?string)!"" /]
          [@customForm.radioFlat id="${studyIndicatorThree}-yes" name="${name}.projectExpectedStudyInfo.isContribution" label="Yes" value="true" checked=(showPolicyIndicator == "true") cssClass="radioType-${studyIndicatorThree}" cssClassLabel="radio-label-yes" editable=editable /]
          [@customForm.radioFlat id="${studyIndicatorThree}-no" name="${name}.projectExpectedStudyInfo.isContribution" label="No" value="false" checked=(showPolicyIndicator == "false") cssClass="radioType-${studyIndicatorThree}" cssClassLabel="radio-label-no" editable=editable /]
        </div>        
        [#-- Disaggregates for CGIAR Indicator   --]
        <div class="form-group simpleBox block-${studyIndicatorThree}" style="display:${(showPolicyIndicator == "true")?string('block','none')}">
          [@customForm.elementsListComponent name="${customName}.policies" elementType="projectPolicy" elementList=element.policies label="study.policies"  listName="policyList" keyFieldName="id" displayFieldName="composedNameAlternative"/]
          [#-- Note --]
          <div class="note">[@s.text name="study.policies.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/policies'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">clicking here</a>[/@][/@]</div>
          [#local policiesGuideSheetURL = "https://drive.google.com/file/d/1GYLsseeZOOXF9zXNtpUtE1xeh2gx3Vw2/view" /]
          <small class="pull-right"><a href="${policiesGuideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> #I1 Policies -  Guideline </a> </small>
          <br>
        </div>
      </div>
      [/#if]
      
      [#-- 4.  Maturity of change reported (tick-box)  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        <label for="">[@s.text name="study.maturityChange" /]:[@customForm.req required=editable && !(isPolicy && stageProcessOne) /]
          [@customForm.helpLabel name="study.maturityChange.help" showIcon=false editable=editable/][@customForm.helpLabel name="study.maturityChange.help2" showIcon=true editable=editable/]
        </label>
        <div class="form-group">
          [#list stageStudies as stage]
            <p>[@customForm.radioFlat id="maturityChange-${stage.id}" name="${customName}.projectExpectedStudyInfo.repIndStageStudy.id" label="<small><b>${stage.name}:</b> ${stage.description}</small>" value="${stage.id}" checked=(element.projectExpectedStudyInfo.repIndStageStudy.id == stage.id)!false cssClass="" cssClassLabel="font-normal" editable=editable/]</p> 
          [/#list]
        </div>
      </div>
      [/#if]
      
      [#-- 5. Links to the Strategic Results Framework  --]
      <div class="form-group">
        [#if isOutcomeCaseStudy]
          <label for="">[@s.text name="study.stratgicResultsLink" /]:[@customForm.req required=editable /]
            [@customForm.helpLabel name="study.stratgicResultsLink.help" showIcon=false editable=editable/]
          </label>
        [#else]
          <label for="">[@s.text name="study.relevantTo" /]:[@customForm.req required=editable /]
          </label> 
        [/#if]
        [#-- Sub IDOs (maxLimit=2 if OICR, else 3) --]
        <div class="form-group simpleBox">
          [#local isPrimary = ((actualPhase.name == "AR" && actualPhase.year == 2021)?then('false', 'true'))?boolean]
          [@customForm.elementsListComponent name="${customName}.subIdos" elementType="srfSubIdo" elementList=element.subIdos label="study.stratgicResultsLink.subIDOs"  listName="subIdos" maxLimit=(isOutcomeCaseStudy?then(2,3)) keyFieldName="id" displayFieldName="composedName" hasPrimary=isPrimary/]
        </div> 
        
        [#-- Sub IDOs (maxLimit=3 -Requested for AR2019) --]      
        [#-- <div class="form-group simpleBox">
          [@customForm.primaryListComponent name="${customName}.subIdos" checkName="subIdoPrimaryId" elementType="srfSubIdo" elementList=(element.subIdos)!"" label="policy.subIDOs" labelPrimary="policy.primarySubIdo" listName="subIdos" maxLimit=3 keyFieldName="id" displayFieldName="description" required=false /]
        </div>--] 
        
        [#-- SRF Targets (maxLimit=2)  --]
        <div class="form-group simpleBox stageProcessOne">
          <label for="">[@s.text name="study.targetsOption" /]:[@customForm.req required=editable /][@customForm.helpLabel name="study.targetsOption.help" showIcon=false editable=editable/]</label><br />
          [#local targetsOption = (element.projectExpectedStudyInfo.isSrfTarget)!""]
          [#list ["targetsOptionYes", "targetsOptionNo", "targetsOptionTooEarlyToSay"] as option]
            [@customForm.radioFlat id="option-${option}" name="${customName}.projectExpectedStudyInfo.isSrfTarget" i18nkey="study.${option}" value="${option}" checked=(option == targetsOption) cssClass="radioType-targetsOption" cssClassLabel="font-normal" editable=editable /] 
          [/#list]
          [#local showTargetsComponent = (element.projectExpectedStudyInfo.isSrfTarget == "targetsOptionYes")!false /]
          <div class="srfTargetsComponent" style="display:${showTargetsComponent?string('block', 'none')}">
            [@customForm.elementsListComponent name="${customName}.srfTargets" elementType="srfSloIndicator" elementList=element.srfTargets label="study.stratgicResultsLink.srfTargets" listName="targets" maxLimit=2  keyFieldName="id" displayFieldName="title" required=editable && !(isPolicy && stageProcessOne)/]          
          </div>
        </div>
        
        [#-- Comments  --]
        [#if isOutcomeCaseStudy]
        <div class="form-group simpleBox stageProcessOne">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.topLevelComments" i18nkey="study.stratgicResultsLink.comments" help="study.stratgicResultsLink.comments.help" helpIcon=false className="limitWords-100" editable=editable required=false /]
        </div>
        [/#if]
      </div>
      
      [#if isOutcomeCaseStudy && action.hasSpecificities('crp_enable_nexus_lever_sdg_fields')]
      <br>
        <div class="form-group" simpleBox>      
          [#-- Nexus  --]
          [@customForm.elementsListComponent name="${customName}.nexus" elementType="nexus" elementList=element.nexus label="study.nexus" maxLimit=3 listName="nexusList" keyFieldName="id" displayFieldName="name" required=false/]
       
          [#-- Lever Outcomes  --]
          [@customForm.elementsListComponent name="${customName}.leverOutcomes" elementType="leverOutcome" elementList=element.leverOutcomes label="study.leverOutcomes" maxLimit=3 listName="leverOutcomeList" keyFieldName="id" displayFieldName="showName" required=false/]
       
          [#-- Sdg Targets  --]
          [@customForm.elementsListComponent name="${customName}.sdgTargets" elementType="sdgTarget" elementList=element.sdgTargets label="study.sdgTargets"  listName="sdgTargetList" keyFieldName="id" displayFieldName="showName" required=false/]
        </div>
      [/#if]
      
      [#-- Milestones --]
        [#if isOutcomeCaseStudy]
        <div class="form-group">          
          <label for="">[@s.text name="study.milestones" /]:[@customForm.req required=editable /][@customForm.helpLabel name="study.milestones.help" showIcon=false editable=editable/]</label>
          [#assign studyMilestoneLink = "studyMilestoneLink"]
          [#assign showMilestoneIndicator = (expectedStudy.projectExpectedStudyInfo.hasMilestones)!false /]
          [@customForm.radioFlat id="${studyMilestoneLink}-yes" name="${customName}.projectExpectedStudyInfo.hasMilestones" label="Yes" value="true" checked=(showMilestoneIndicator == true) cssClass="radioType-${studyMilestoneLink}" cssClassLabel="radio-label-yes" editable=editable /]
          [@customForm.radioFlat id="${studyMilestoneLink}-no" name="${customName}.projectExpectedStudyInfo.hasMilestones" label="No" value="false" checked=(showMilestoneIndicator == false) cssClass="radioType-${studyMilestoneLink}" cssClassLabel="radio-label-no" editable=editable /]
      </div>
      [#assign isAR2021 = !(action.isSelectedPhaseAR2021())]
       <div class="form-group simpleBox block-${studyMilestoneLink}" style="display:${(showMilestoneIndicator == true)?string('block','none')}">
          [@customForm.elementsListComponent name="${customName}.milestones" elementType="crpMilestone" elementList=(element.milestones)![] label="study.milestones"  listName="milestones" keyFieldName="id" displayFieldName="composedNameWithFlagship" hasPrimary=true required=isAR2021/]
          <div class="note">[@s.text name="study.milestones.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/contributionsCrpList'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
         <br/>      
        </div>
        
        [#--<div class="form-group simpleBox block-${studyMilestoneLink}" style="display:${(showMilestoneIndicator == "true")?string('block','none')}">
          [@customForm.primaryListComponent name="${customName}.milestones" checkName="study.milestonePrimaryId" elementType="crpMilestone" elementList=(element.milestones)!"" label="study.milestones" labelPrimary="policy.primaryMilestone" helpIcon=false listName="milestones" keyFieldName="id" displayFieldName="composedName" required=false /]
         <div class="note">[@s.text name="study.milestones.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/contributionsCrpList'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
         <br/>
        </div>--]
        [/#if]
      

      [#-- 7. Key Contributors  --]
      <div class="form-group">
        [#if isOutcomeCaseStudy || !fromProject]
          <label for="">[@s.text name="study.${isOutcomeCaseStudy?string('keyContributors','keyContributorsOther')}" /]:</label>
        [/#if]
        [#-- CRPs --]
        <span id="actualCRP" style="display: none;">${action.getLoggedCrp().acronym}</span>
        [#if isOutcomeCaseStudy]
        <div class="form-group simpleBox oicrContributingCRP">
          [@customForm.elementsListComponent name="${customName}.crps" elementType="globalUnit" elementList=element.crps label="study.keyContributors.crps"  listName="crps" keyFieldName="id" displayFieldName="composedName" required=false /]
        </div>
        [/#if]
        [#-- Centers --]
        [#if isOutcomeCaseStudy]
        <div class="form-group simpleBox">
          [@customForm.elementsListComponent name="${customName}.centers" elementType="institution" elementList=element.centers label="study.keyContributors.centers"  listName="centers" keyFieldName="id" displayFieldName="composedName" /]
          <div class="note">[@s.text name="study.ppapartner.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/partners'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
        </div>
        [/#if]
        [#-- Flagships or Levers (Alliance) --]
        [#if (isOutcomeCaseStudy || !fromProject) && !action.hasSpecificities('crp_enable_nexus_lever_sdg_fields')]
          <div class="form-group simpleBox stageProcessOne">
            [#if !fromProject && editable]
              <p class="note">To the [@s.text name="programManagement.flagship.title"/](s) selected, the system grants permission to edit this ${(element.projectExpectedStudyInfo.studyType.name)!'study'} to their [@s.text name="CrpProgram.leaders"/] and [@s.text name="CrpProgram.managers"/]</p>
            [/#if]
            [@customForm.elementsListComponent name="${customName}.flagships" elementType="crpProgram" id="FP" elementList=element.flagships label="study.keyContributors.flagships"  listName="flagshipList" keyFieldName="id" displayFieldName="composedName" required=true /]
          </div>
        [/#if]
        [#-- Levers (Alliance) --]
        [#if isOutcomeCaseStudy && action.hasSpecificities('crp_enable_nexus_lever_sdg_fields')]
          <div class="form-group simpleBox">
            [@customForm.elementsListComponent name="${customName}.levers" elementType="allianceLever" elementList=element.levers label="study.keyContributors.flagships"  listName="leverList" keyFieldName="id" displayFieldName="showName" required=false /]
          </div>
        [/#if]
        [#-- Regions --]
        [#if (isOutcomeCaseStudy || !fromProject) && regionList?has_content]
          <div class="form-group simpleBox stageProcessOne">
            [#if !fromProject && editable]
              <p class="note">To the Region(s) selected, the system grants permission to edit this ${(element.projectExpectedStudyInfo.studyType.name)!'study'} to their [@s.text name="regionalMapping.CrpProgram.leaders"/] and [@s.text name="regionalMapping.CrpProgram.managers"/]</p>
            [/#if]
            [@customForm.elementsListComponent name="${customName}.regions" elementType="crpProgram" id="RP" elementList=element.regions label="study.keyContributors.regions"  listName="regionList" keyFieldName="id" displayFieldName="composedName" required=false /]
          </div>
        [/#if]
        [#-- External Partners --]
        [#if isOutcomeCaseStudy]
        <div class="form-group simpleBox stageProcessOne">
          [@customForm.elementsListComponent name="${customName}.institutions" elementType="institution" elementList=element.institutions label="study.keyContributors.externalPartners"  listName="institutions" keyFieldName="id" displayFieldName="composedName" required=false /]
          [#-- Request partner adition --]
          [#if editable]
          <p id="addPartnerText" class="helpMessage">
            [@s.text name="projectPartners.addPartnerMessage.first" /]
            <a class="popup" href="[@s.url namespace="/projects" action='${crpSession}/partnerSave'][@s.param name='expectedID']${(expectedID)!}[/@s.param][/@s.url]">
              [@s.text name="projectPartners.addPartnerMessage.second" /]
            </a>
          </p> 
          [/#if]
        </div>
        [/#if]
      </div>
      
      [#--  CGIAR innovation(s) or findings that have resulted in this outcome or impact.   --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.cgiarInnovation" i18nkey="study.innovationsNarrative" help="study.innovationsNarrative.help" helpIcon=false className="" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
         
        [@customForm.elementsListComponent name="${customName}.innovations" elementType="projectInnovation" elementList=element.innovations label="study.innovationsList"  listName="innovationsList" keyFieldName="id" displayFieldName="composedNameAlternative" required=false /]
      </div>
      [/#if]
      
      [#--  Elaboration of Outcome/Impact Statement  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.elaborationOutcomeImpactStatement" i18nkey="study.elaborationStatement" help="study.elaborationStatement.help" helpIcon=false className="limitWords-400" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
      </div>
      [/#if]
      
      [#-- 9. References cited  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        <div class="form-group">
          <span id="warningEmptyReferencesTag" class="errorTag glyphicon glyphicon-info-sign" style="position: relative; left: 750px;" title="" aria-describedby="ui-id-5"> </span>
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.referencesText" i18nkey="study.referencesCited" help="study.referencesCited.help2" helpIcon=false className="" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
          <div class="referenceBlock ">
            <div class="referenceList">
              <div class="row">
                <div class="col-sm-7 colTitleCenter" style="font-weight: 600; text-align: center;">Reference[@customForm.req required=editable  /]</div>
                <div class="col-sm-3 colTitleCenter" style="font-weight: 600; text-align: center;">URL[@customForm.req required=editable  /]</div>
              </div>
              [#list (element.references)![{}] as link ]
                [@customForm.references name="${customName}.references" element=link index=link_index class="references" /]
              [/#list]
            </div>
            [#if editable]
            <div class="addButtonReference button-green pull-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add Reference </div>
            <div class="clearfix"></div>
            [/#if]
          </div>
          [#-- Element item Template --]
          <div style="display:none">
            [@customForm.references name="${customName}.references" element={} index=-1 template=true class="references" /]
          </div>
        </div>
        <p class="note"> <small>[@s.text name="message.shortenURLsDisclaimer"][@s.param value="93" /][/@s.text]</small> </p>
        [#-- 
        <div class="form-group" style="position:relative" listname="">
          [@customForm.fileUploadAjax 
            fileDB=(element.projectExpectedStudyInfo.referencesFile)!{} 
            name="${customName}.projectExpectedStudyInfo.referencesFile.id" 
            label="study.referencesCitedAttach" 
            dataUrl="${baseUrl}/uploadStudies.do" 
            path="${(action.getPath())!}"
            isEditable=editable
            labelClass="label-min-width"
            required=false
          /]          
        </div>
         --]
         
      </div>
      [/#if]
      
      [#-- 10. Quantification (where data is available)  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [#--
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.quantification" i18nkey="study.quantification" help="study.quantification.help" helpIcon=false className=" " required=editable && !(isPolicy && stageProcessOne) editable=editable /]
        --]
        <label for="">[@s.text name="study.quantification" /]:[@customForm.helpLabel name="study.quantification.help" showIcon=false editable=editable/]</label><br />
        <div class="quantificationsBlock">
          <div class="quantificationsList">
          [#list (element.quantifications)![] as item]
            [@quantificationMacro name="${customName}.quantifications" element=item index=item_index /]
          [/#list]
          </div>
          [#if editable]
            <div class="addStudyQualification bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addStudyQualification"/]</div>
          [/#if]
        </div>
        [#-- Element item Template --]
        <div style="display:none">
          [@quantificationMacro name="${customName}.quantifications" element={} index=-1 template=true /]
        </div>
        <br />
      </div>
      [/#if]
      
      [#-- 11. Gender, Youth, and Capacity Development  --]
      [#if isOutcomeCaseStudy]
      <div class="form-group">
        [#assign ccGuideSheetURL = "https://drive.google.com/file/d/1oXb5UHABZIbyUUczZ8eqnDsgdzwABXPk/view?usp=sharing" /]
        <small class="pull-right"><a href="${ccGuideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" />Cross-Cutting Markers  -  Guideline </a> </small>
      </div>
      <div class="form-group">
        [@tag name="Indicator #3" /]
        <label for="">[@s.text name="study.crossCuttingRelevance" /]:
          [@customForm.helpLabel name="study.crossCuttingRelevance.help" showIcon=false editable=editable/]
        </label>
        [#-- Gender --]
        <div class="simpleBox ccRelevanceBlock">
          <label for="">[@s.text name="study.genderRelevance" /]:[@customForm.req required=editable /]</label>
          <div class="form-group">
            [#assign genderLevel = (element.projectExpectedStudyInfo.genderLevel.id)!1 ]
            [#list focusLevels  as cc]
              [@customForm.radioFlat id="genderRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.genderLevel.id" label="${cc.powbName}" value="${cc.id}" checked=(genderLevel == cc.id)!false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div>
          <div class="ccCommentBox" style="display:${((genderLevel == 2) || (genderLevel == 3))?string('block', 'none')}">
            <div class="form-group stageProcessOne">
              [@customForm.textArea name="${customName}.projectExpectedStudyInfo.describeGender" i18nkey="study.achievementsGenderRelevance" className="limitWords-100" required=editable && !(isPolicy && stageProcessOne)editable=editable /]
            </div>
          </div>
        </div>
        [#-- Youth  --]
        <div class="simpleBox ccRelevanceBlock">
          <label for="">[@s.text name="study.youthRelevance" /]:[@customForm.req required=editable /]</label>
          <div class="form-group">
            [#assign youthLevel = (element.projectExpectedStudyInfo.youthLevel.id)!1 ]
            [#list focusLevels  as cc]
              [@customForm.radioFlat id="youthRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.youthLevel.id" label="${cc.powbName}" value="${cc.id}" checked=(youthLevel == cc.id)!false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div> 
          <div class="ccCommentBox" style="display:${((youthLevel == 2) || (youthLevel == 3))?string('block', 'none')}">
            <div class="form-group stageProcessOne">
              [@customForm.textArea name="${customName}.projectExpectedStudyInfo.describeYouth" i18nkey="study.achievementsYouthRelevance"  className="limitWords-100" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
            </div>
          </div>
        </div>
        [#-- CapDev   --]
        <div class="simpleBox ccRelevanceBlock">
          <label for="">[@s.text name="study.capDevRelevance" /]:[@customForm.req required=editable /]</label>
          <div class="form-group">
            [#assign capdevLevel = (element.projectExpectedStudyInfo.capdevLevel.id)!1 ]
            [#list focusLevels  as cc]
              [@customForm.radioFlat id="capDevRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.capdevLevel.id" label="${cc.powbName}" value="${cc.id}" checked=(capdevLevel == cc.id)!false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div>
          <div class="ccCommentBox" style="display:${((capdevLevel == 2) || (capdevLevel == 3))?string('block', 'none')}">
            <div class="form-group stageProcessOne">
              [@customForm.textArea name="${customName}.projectExpectedStudyInfo.describeCapdev" i18nkey="study.achievementsCapDevRelevance"  className="limitWords-100" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
            </div>
          </div>
        </div>
        [#-- Climate Change  --]
        <div class="simpleBox ccRelevanceBlock">
          <label for="">[@s.text name="study.climateChangeRelevance" /]:[@customForm.req required=editable /]</label>
          <div class="form-group">
            [#assign climateChangeLevel = (element.projectExpectedStudyInfo.climateChangeLevel.id)!1 ]
            [#list focusLevels  as cc]
              [@customForm.radioFlat id="climateChangeRelevance-${cc_index}" name="${name}.projectExpectedStudyInfo.climateChangeLevel.id" label="${cc.powbName}" value="${cc.id}" checked=(climateChangeLevel == cc.id)!false cssClass="" cssClassLabel="font-normal" editable=editable /]
            [/#list]
          </div>
          <div class="ccCommentBox" style="display:${((climateChangeLevel == 2) || (climateChangeLevel == 3))?string('block', 'none')}">
            <div class="form-group stageProcessOne">
              [@customForm.textArea name="${customName}.projectExpectedStudyInfo.describeClimateChange" i18nkey="study.achievementsClimateChangeRelevance"  className="limitWords-100" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
            </div>
          </div>
        </div> 
        
      </div>
      [/#if]
      
      [#--  Other cross-cutting dimensions   --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        <label for="">[@s.text name="study.otherCrossCutting" /]:</label> 
        [@customForm.helpLabel name="study.otherCrossCuttingOptions" showIcon=false editable=editable/]<br />
        [#local otherCrossCuttingSelection = (element.projectExpectedStudyInfo.otherCrossCuttingSelection)!"" ]
        [#list ["Yes", "No", "NA"] as option]
          [@customForm.radioFlat id="option-${option}" name="${customName}.projectExpectedStudyInfo.otherCrossCuttingSelection" i18nkey="study.otherCrossCutting${option}" value="${option}" checked=(otherCrossCuttingSelection == option) cssClass="radioType-otherCrossCuttingOption" cssClassLabel="font-normal" editable=editable /] 
        [/#list]
        [#local showOtherCrossCuttingOptionsComponent = true /]
        <div class="otherCrossCuttingOptionsComponent form-group" style="display:${showOtherCrossCuttingOptionsComponent?string('block', 'none')}">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.otherCrossCuttingDimensions" i18nkey="study.otherCrossCutting.comments" help="study.otherCrossCutting.comments.help" helpIcon=false  className="limitWords-200" required=false editable=editable /]
        </div>
      </div>
      [/#if]
      
      [#--  Communications materials    --]
      [#if isOutcomeCaseStudy]
      [#-- 
      <div class="form-group stageProcessOne">
        <div class="form-group">
          [@customForm.textArea name="${customName}.projectExpectedStudyInfo.comunicationsMaterial" i18nkey="study.communicationMaterials" help="study.communicationMaterials.help" helpIcon=false className=" " required=false editable=editable /]
        </div>
        <div class="form-group" style="position:relative" listname="">
          [@customForm.fileUploadAjax 
            fileDB=(element.projectExpectedStudyInfo.outcomeFile)!{} 
            name="${customName}.projectExpectedStudyInfo.outcomeFile.id" 
            label="study.communicationMaterialsAttach" 
            dataUrl="${baseUrl}/uploadStudies.do" 
            path="${(action.getPath())!}"
            isEditable=editable
            labelClass="label-min-width"
          /]
        </div>
      </div>
       --]
      [/#if]

      [#--  Contact person    --]
      [#if isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.contacts" i18nkey="study.contacts" help="study.contacts.help" className="" helpIcon=false required=editable && !(isPolicy && stageProcessOne) editable=editable /]
      </div>
      [/#if]
      
      [#--  Comments for other studies--]
      [#if !isOutcomeCaseStudy]
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.topLevelComments" i18nkey="study.activityDescription"  placeholder="" className="limitWords-150" required=editable && !(isPolicy && stageProcessOne) editable=editable /]
      </div>
      
      <div class="form-group stageProcessOne">
        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.MELIAPublications" i18nkey="study.MELIAPublications"  placeholder="" help="study.MELIAPublications.help" helpIcon=false className="" required=false editable=editable /]
      </div>
      [/#if]
      
    </div>
    
    [#-- Private Option: FOR AR 2020 and onwards -> ALL OICRs are ALWAYS public --]
    [#--if isOutcomeCaseStudy]
      <h3 class="headTitle">[@s.text name="study.confidentialTitle" /]</h3>
      <div class="borderBox">
        <label for="">[@s.text name="study.public" ][@s.param]${(element.projectExpectedStudyInfo.studyType.name)!}[/@][/@] 
          [@customForm.helpLabel name="study.public.help" showIcon=false paramText="${(element.projectExpectedStudyInfo.studyType.name)!}" editable=editable/]
        </label> <br />
        [#local isPublic = (element.projectExpectedStudyInfo.isPublic)!true /]
        [#local isPublic = true /]
        [@customForm.radioFlat id="optionPublic-yes"  name="${customName}.projectExpectedStudyInfo.isPublic" i18nkey="Yes"  value="true"  checked=isPublic  cssClass="radioType-optionPublic" cssClassLabel="font-normal radio-label-yes" editable=editable /] 
        [@customForm.radioFlat id="optionPublic-no"   name="${customName}.projectExpectedStudyInfo.isPublic" i18nkey="No"   value="false" checked=!isPublic cssClass="radioType-optionPublic" cssClassLabel="font-normal radio-label-no"  editable=editable /] 
        
        <div class="optionPublicComponent form-group" style="display:${isPublic?string('block', 'none')}">         
          <br />
          <div class="input-group">
            <span class="input-group-btn">
              <button class="btn btn-default btn-sm copyButton" type="button"> <span class="glyphicon glyphicon-link"></span> Copy URL </button>
            </span>
            [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(element.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
            <input type="text" class="form-control input-sm urlInput" value="${summaryPDF}" readonly>
          </div>
          <div class="message text-center" style="display:none">Copied!</div>
        </div>
      </div>
    [/#if--]
    
    [#-- Projects shared --]
    [#if fromProject]
    <h3 class="headTitle">[@s.text name="study.sharedProjects.title" /]</h3>
    <div class="borderBox">
      [@customForm.elementsListComponent name="${customName}.projects" elementType="project" elementList=element.projects label="study.sharedProjects"  listName="myProjects" keyFieldName="id" displayFieldName="composedName" required=false /]
    </div>
    [/#if]
  </div>
[/#macro]

[#macro tag name=""]
  [#-- <span class="label label-info pull-right"> <i class="fas fa-tag"></i> ${name} </span> --]
[/#macro]

[#--macro studyLink name element index=-1 template=false]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]
  <div id="studyLink-${(template?string('template', ''))}" class="studyLink form-group grayBox">
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <span class="pull-left" style="width:4%"><strong><span class="indexTag">${index + 1}</span>.</strong></span>
    <span class="pull-left" style="width:90%">[@customForm.input name="${customName}.link" placeholder="global.webSiteLink.placeholder" showTitle=false i18nkey="" className="" editable=editable /]</span>
    [#if editable]<div class="removeElement sm removeIcon removeLink" title="Remove"></div>[/#if]
    <div class="clearfix"></div>
  </div>
[/#macro--]

[#macro quantificationMacro name element index=-1 template=false]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]
  <div id="quantification-${(template?string('template', ''))}" class="quantification form-group simpleBox">
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <div class="form-group">
      [#-- Index --]
      <div class="leftHead gray sm">
        <span class="index">${index+1}</span>
      </div>
      [#-- Remove Button --]
      [#if editable]<div class="removeIcon removeElement removeQuantification" title="Remove"></div>[/#if]
      [#-- Quantification type --]
      <div class="form-group">
        <label for="">[@s.text name="study.quantificationType" /]:[@customForm.req required=editable /]</label>
        <br />[@customForm.radioFlat id="quantificationType-1" name="${customName}.typeQuantification" i18nkey="study.quantification.quantificationType-1" value="A" checked=((element.typeQuantification == "A")!false) cssClass="" cssClassLabel="font-normal" editable=editable /]
        <br />[@customForm.radioFlat id="quantificationType-2" name="${customName}.typeQuantification" i18nkey="study.quantification.quantificationType-2" value="B" checked=((element.typeQuantification == "B")!false) cssClass="" cssClassLabel="font-normal" editable=editable /]
      </div>
    </div>
    [#-- Units --]
    <div class="form-group row">
      <div class="col-md-4">
        [@customForm.input name="${customName}.number" i18nkey="study.quantification.number" className="numericInput" required=true editable=editable /]
      </div>
      <div class="col-md-4"> 
        [@customForm.input name="${customName}.targetUnit" i18nkey="study.quantification.targetUnit" className="" required=true editable=editable /]
      </div> 
    </div>
    [#-- Comments --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.comments" i18nkey="study.quantification.comments" help="study.quantification.comments.help"  placeholder="" className="" required=true editable=editable /]
    </div>
  </div>
[/#macro]

[#macro studiesList deliverables={} owned=true canValidate=false canEdit=false isReportingActive=false namespace="/clusters" defaultAction="deliverableList" currentTable=true]
  <table class="projectsList" id="studies">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="studiesTitles" >[@s.text name="projectStudies.studiesTitles" /]</th>
        <th id="studiesEDY">[@s.text name="projectStudies.studiesYear" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if deliverables?has_content]
      [#list deliverables as deliverable]
        [#-- Is New --]
        [#assign isDeliverableNew = action.isDeliverableNew(deliverable.id) /]
        [#-- Has draft version (Auto-save) --]
        [#assign hasDraft = (action.getAutoSaveFilePath(deliverable.class.simpleName, "deliverable", deliverable.id))!false /]
        [#-- Is Complete --]
        [#assign isDeliverableComplete = action.isDeliverableComplete(deliverable.id, actualPhase.id) /]
        [#-- To Report --]
        [#local toReport = reportingActive && !isDeliverableComplete ]

        <tr>
          [#-- ID --]
          <td class="deliverableId">
            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              D${deliverable.id}
            </a>
          </td>
          [#-- Deliverable Title --]
          <td class="left">
            [#-- Hidden title to sort correctly by title --]
            <span class="hidden">${deliverable.deliverableInfo.title!''}</span>
            [#-- Draft Tag --]
            [#if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if]
            [#-- Report --]
            [#if toReport]<span class="label label-primary" title="Required for this cycle"><span class="glyphicon glyphicon-flash" ></span> Report</span>[/#if]
            [#-- New Tag --]
            [#if isDeliverableNew]<span class="label label-info">New</span>[/#if]

            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              [@utilities.wordCutter string=(deliverable.deliverableInfo.title)! maxPos=160 /]
            </a>
          </td>
          [#-- Deliverable Year --]
          <td class="text-center">

            [#if deliverable.deliverableInfo.year== -1]
              None
            [#else]
              ${(deliverable.deliverableInfo.year)!'None'}
              [#if
                    ((deliverable.deliverableInfo.status == 4 || deliverable.deliverableInfo.status==3)!false )
                    && ((deliverable.deliverableInfo.newExpectedYear != -1)!false)
                    ]
                Extended to ${deliverable.deliverableInfo.newExpectedYear}
              [/#if]
            [/#if]

          </td>
        </tr>
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]

[#function findElementID list id]
  [#list (list)![] as item]
    [#if (item.repIndGeographicScope.id == id)!false][#return true][/#if]
  [/#list]
  [#return false]
[/#function]
