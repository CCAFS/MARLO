[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs", "malihu-custom-scrollbar-plugin" ] /]
[#assign customJS = [ 
  "https://www.gstatic.com/charts/loader.js",
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js?20211103a",
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js?20211103a"
  ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20210219"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"table6.${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis" /]
[#assign customLabel= "annualReport2018.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="${customLabel}.help" /]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is available only at Reporting cycle</div>
  [#else]
    [#-- Program (Flagships and PMU) --]
    [#include "/WEB-INF/crp/views/annualReport2018/submenu-AR2018.ftl" /]
    
    <div class="row">
      [#-- POWB Menu --]
      <div class="col-md-3">[#include "/WEB-INF/crp/views/annualReport2018/menu-AR2018.ftl" /]</div>
      [#-- POWB Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/annualReport2018/messages-AR2018.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          <span id="actualCrpID" style="display: none;">${(action.getCurrentCrp().id)!-1}</span>
          <span id="actualPhase" style="display: none;">${(action.isSelectedPhaseAR2021())?c}</span>
          [#assign actualPhaseAR2021 = action.isSelectedPhaseAR2021()!false]
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>





          <div class="deliverableTabs">
            
            <ul class="nav nav-tabs" role="tablist"> 
                <li role="presentation" class="active"><a index="1" href="#deliverable-mainInformation" aria-controls="info" role="tab" data-toggle="tab">Peer-reviewed Publications</a></li>
                
                [#if (!action.isSelectedPhaseAR2021())]<li role="presentation" class=""><a index="2" href="#deliverable-disseminationMetadata" aria-controls="metadata" role="tab" data-toggle="tab">Grey Literature</a></li>[/#if]
                        
            </ul>
            <div class="tab-content ">
             
              <div id="deliverable-mainInformation" role="tabpanel" class="tab-pane fade in active">
            

                  <div class="form-group">
                    [#assign guideSheetURL = "https://docs.google.com/document/d/1o3Po7eFr66VKf242I4F9zAoQ5kxvgC_i/edit?rtpof=true&sd=true" /]
                      <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> #C4 Peer-reviewed papers  -  Guideline </a> </small>
                  </div>
                  <br />
                  
                  <div class="form-group row">
                    <div class="col-md-4">
                      [#-- Total number of peer reviewed articles --]
                      <div id="" class="simpleBox numberBox">
                        <label for="">[@s.text name="${customLabel}.indicatorC4.totalArticles" /]</label><br />
                        <span class="totalNumber">${(total)!}</span>
                      </div>
                    </div>
                    <div class="col-md-8">
                    </div>
                   </div>
                    
                    <div class="form-group row">
                    <div class="col-md-6">
                      [#-- Chart 10 - Number of peer reviewed articles by Open Access status --]
                      <div id="chart10" class="chartBox simpleBox">
                        [#assign chartData = [
                            {"name":"Open Acess",   "value": "${(totalOpenAccess)!0}"},
                            {"name":"Limited",      "value": "${(totalLimited)!0}"}
                          ] /] 
                        <ul class="chartData" style="display:none">
                          <li>
                            <span>[@s.text name="${customLabel}.indicatorC5.chart6.0" /]</span>
                            <span>[@s.text name="${customLabel}.indicatorC5.chart6.1" /]</span>
                          </li>
                          [#list chartData as data]
                            <li><span>${data.name}</span><span class="number">${data.value}</span></li>
                          [/#list]
                        </ul>
                      </div>
                    </div>
                    
                    <div class="col-md-6">
                      [#-- Chart 11 - Number of peer reviewed articles by ISI status --]
                      <div id="chart11" class="chartBox simpleBox">
                        [#assign chartData = [
                            {"name":"Yes",   "value": "${(totalIsis)!0}"},
                            {"name":"No",    "value": "${(totalNoIsis)!0}"}
                          ] 
                        /] 
                        <ul class="chartData" style="display:none">
                          <li>
                            <span>[@s.text name="${customLabel}.indicatorC5.chart7.0" /]</span>
                            <span>[@s.text name="${customLabel}.indicatorC5.chart7.1" /]</span>
                          </li>
                          [#list chartData as data]
                            <li><span>${data.name}</span><span class="number">${data.value}</span></li>
                          [/#list]
                        </ul>
                      </div>
                    </div>
                  </div>
                  
                  [#-- Number of peer-reviewed publications --]
                  <div class="form-group">
                    [#-- Word Document Tag --]
                    [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                    
                    <h4 class="headTitle">[@s.text name="${customLabel}.peerReviewed.title" /]</h4>
                    [@numberOfPublications name="peerReviewed" list=["", ""]/]
                  </div>
                  
                  
      
                    <div class="form-group btn-group btn-group-sm pull-right" role="group" aria-label="...">
                      <button type="button" class="btn btn-default evidenceD-export" data-toggle="modal" data-target="#modal-evidenceC"><span class="glyphicon glyphicon-fullscreen"></span> Export Evidence D</button>
                      <button type="button" class="btn btn-default btn-xs pull-right" data-toggle="modal" data-target="#modal-publications"><span class="glyphicon glyphicon-fullscreen"></span> See Full Evidence D</button>
                    </div>
                    
                    
                    [#-- Table 6 to export --]
                    <div class="modal fade" id="modal-evidenceC" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                      <div class="modal-dialog modal-lg" role="document">
                        <div class="modal-content">
                          <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title" id="myModalLabel">Export Evidence D: Full Publications List</h4>
                          </div>
                          <div class="modal-body">
                            <small><i>[@s.text name="${customLabel}.export.evidenceD.help" /]</i></small>
                            [#-- Full table --]
                            <div class="dataTableExport">
                              [@listOfPublicationsToExport name="fullList" list=(deliverables)![] /]
                            </div>
                          </div>
                          <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                          </div>
                        </div>
                      </div>
                    </div>
                    
                    [#-- See Full table 6 --]
                    <div class="modal fade" id="modal-publications" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                      <div class="modal-dialog modal-lg" role="document">
                        <div class="modal-content">
                          <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title" id="myModalLabel">[@s.text name="${customLabel}.fullList.title" /]</h4>
                          </div>
                          <div class="modal-body">
                            [@listOfPublications name="fullList" list=(deliverables)![] allowPopups=false /]
                          </div>
                          <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                          </div>
                        </div>
                      </div>
                    </div>
                    
                    
                    
                  [#-- Full list of publications published --]
                  <div class="form-group viewMoreSyntesisTablePRP-block"> 
                    [#-- Table --]
                    <h4 class="headTitle">[@s.text name="${customLabel}.fullList.title" /]</h4>
                    [@listOfPublications name="fullList" list=(deliverables)![]  allowPopups=true /]
                  </div>

        
                  
              
   
                  
               
              </div>
              [#if (!action.isSelectedPhaseAR2021())]
              <div id="deliverable-disseminationMetadata" role="tabpanel" class="tab-pane fade ">
                  [#-- Full list of publications published --]
                  <div class="form-group viewMoreSyntesisTableGrey-block"> 
                    [#-- Table --]
                    <h4 class="headTitle">[@s.text name="${customLabel}.fullGreyList.title" /]</h4>
                    <div class="form-group row">
                      <div class="col-md-4">
                        [#-- Total number of Grey Literature --]
                        <div id="" class="simpleBox numberBox">
                          <label for="">[@s.text name="${customLabel}.indicatorC4.totalGrey" /]</label><br />
                          <span class="totalGreyNumber">${(totalGrey)!}</span>
                        </div>
                      </div>
                    </div>
                    [@listOfPublications name="fullGreyList" list=(deliverablesNotPublications)![]  allowPopups=true isGrey=true /]
                  </div>
              </div>
              [/#if]
            </div>

           </div>




     
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport2018/buttons-AR2018.ftl" /]
        [/@s.form] 
      </div> 
    </div>
  [/#if] 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro numberOfPublications name list=[] ]
  
  <div class="form-group">
    <table class="table table-bordered numberPublicationsTable">
      <thead>
        <tr>
          <th class="numbersTitle"></th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.totalAmount" /] </th>
          <th class="text-center col-md-3"> [@s.text name="${customLabel}.${name}.percent" /] </th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td class="tableTitle">[@s.text name="${customLabel}.${name}.publications" /]</td>
          <td class="text-center">${total}</td>
          <td class="text-center">100%</td>
        </tr>
        <tr>
          <td class="tableTitle">[@s.text name="${customLabel}.${name}.openAccess" /]</td>
          <td class="text-center">${(totalOpenAccess)!0}</td>
          <td class="text-center">[#if totalOpenAccess != 0]${((totalOpenAccess/total)*100)?string("0.##")}[#else]0[/#if]%</td>
        </tr>
        <tr>
          <td class="tableTitle">[@s.text name="${customLabel}.${name}.isi" /]</td>
          <td class="text-center">${(totalIsis)!0}</td>
          <td class="text-center">[#if totalOpenAccess != 0]${((totalIsis/total)*100)?string("0.##")}[#else]0[/#if]%</td>
        </tr>
      </tbody>
    </table>
  </div>

[/#macro]

[#macro listOfPublications name list=[] allowPopups=false isGrey=false]
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.id" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.deliverable" /] </th>
          [#if !isGrey]
            <th class="text-center"> [@s.text name="${customLabel}.${name}.article" /] </th>
          [/#if]
          [#if !allowPopups]
            <th class="text-center"> [@s.text name="${customLabel}.${name}.author" /](s) </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.date" /] </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.journal" /] </th>
          [/#if]
          <th class="col-md-2 text-center" > [@s.text name="${customLabel}.${name}.directLink" /] </th>
          [#if !allowPopups]
            <th class="text-center"> [@s.text name="${customLabel}.${name}.volume" /] </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.issue" /] </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.page" /] </th>
          [/#if]
          [#if isGrey]
            <th class="text-center"> [@s.text name="${customLabel}.${name}.category" /] </th>
          [/#if]
          
          <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.openAccess" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.${isGrey?string('altmetricScore','isi')}" /] </th>
          [#if allowPopups]
            [#if !isGrey]
              <th class="col-md-1 text-center">[@s.text name="${customLabel}.${name}.missingFields" /]</th>
            [/#if]
            [#if PMU]
              <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.includeAR" /] 
              <br>
              <button type="button" class="selectAllCheck" id="selectAll${isGrey?then('Grey','')}" style="color: #1da5ce; font-style: italic; font-weight: 500; background-color: #F9F9F9; border-bottom: none; outline: none">Select All</button>
              [#--  [@customForm.checkmark id="selectAll${isGrey?then('Grey','')}" name="selectAll${isGrey?then('Grey','')}" value="false" checked=false editable=editable centered=true/]  --]
              </th>
              [#if actualPhaseAR2021]
                <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.QA" /]</th>
              [/#if]
            [/#if]
          [/#if]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#local isFromProject = (item.project??)!false]
            [#if isFromProject]
              [#local url][@s.url namespace="/projects" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [#else]
              [#local url][@s.url namespace="/publications" action="${(crpSession)!}/publication"][@s.param name='deliverableID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [/#if]
            <tr>
              <td> <a href="${url}" target="_blank" >D${(item.id)!""}</a>  </td>
              [#-- Deliverable Title --]
              <td  style="max-width: 100px;">
                [#local publicationTitle = (item.deliverableInfo.title)!"" ]
                [@utils.tableText value=publicationTitle /]
                [#if isFromProject]<br /> <small>(From Project P${item.project.id})</small> [/#if]
                [#if PMU]
                <br />
                <div class="form-group">
                  [#list (item.selectedFlahsgips)![] as liason]
                    <span class="programTag" style="border-color:${(liason.crpProgram.color)!'#444'}" title="${(liason.composedName)!}">${(liason.acronym)!}</span>
                  [/#list]
                </div>
                [/#if]
              </td>
              [#-- Disseminated Title --]
              [#if !isGrey]
                <td  style="max-width: 100px;">
                  [#local publicationTitle = (action.getArticleTitle(item.id))!"" ]
                  [@utils.tableText value=publicationTitle /]
                </td>
              [/#if]
              [#if !allowPopups]
              [#-- Authors --]
              <td>
                [#if item.getMetadataValue(38)?has_content && !item.users?has_content ]
                  <div class="authorsList mCustomScrollbar">
                    ${item.getMetadataValue(38)} 
                  </div>
                [#else]
                  [@utils.tableList list=(item.users)![] displayFieldName="composedName" nobr=true class="authorsList mCustomScrollbar" scroll=true /]
                [/#if]
              </td>
              [#-- Date of Publication --]
              <td >[@utils.tableText value=(item.getMetadataValue(17))!"" /]</td>
              [#-- Journal Article --]
              <td class="urlify" >[@utils.tableText value=(item.publication.journal)!"" /]</td>
              [/#if]
              [#-- DOI or Handle --]
              <td class="text-center"  style="max-width: 100px;">
                [#if item.getMetadataValue(36)?has_content]
                  [#local directLink = "https://www.doi.org/"+item.getMetadataValue(36) /]
                  [#-- TODO add www.doi.org/ to DOI identifiers. NOTE: validations will be needed. There are not just DOIs saved there and there are some
                  DOIs that has not been cleaned (being stripped of the www.doi.org/ part) yet. --]
                [#elseif item.getMetadataValue(35)?has_content]
                  [#local directLink = item.getMetadataValue(35) /]
                [#elseif item.dissemination.articleUrl?has_content]
                  [#local directLink = item.dissemination.articleUrl /]
                [#elseif item.dissemination.disseminationUrl?has_content]
                  [#local directLink = item.dissemination.disseminationUrl /]
                [#else]
                  [#local directLink = "" /]
                [/#if]
                
                [#--if directLink?has_content && directLink?contains("http") && !(directLink?contains(";"))]
                  [#--<a target="_blank" href="${directLink}"><span class="glyphicon glyphicon-link"></span></a>
                  <a target="_blank" href="${directLink}"><span class="glyphicon glyphicon-link"></span></a>
                [#else]
                  [#if !(directLink?has_content) ]
                    <span class="glyphicon glyphicon-link" title="Not defined"></span>
                  [#else]
                    <span class="glyphicon glyphicon-link" title="${directLink}"></span>
                  [/#if]
                [/#if--]
                [#if directLink?has_content && directLink?contains("http") && !(directLink?contains(";"))]
                  [#--<a target="_blank" href="${directLink}">${directLink}</span></a>--]
                  <a href="${directLink}" target="_blank" class="">[@s.text name="${customLabel}.${name}.linkToPublication" /] <span class="glyphicon glyphicon-new-window"></span></a>
                [#else]
                  [#if !(directLink?has_content) ]
                    [@utils.tableText value="" /]
                  [#else]
                    [@utils.tableText value=directLink /]
                  [/#if]
                [/#if]
              </td>
              [#if isGrey]
                <td class="text-center " style="max-width: 100px;">
                  [#local deliverableInfo = item.getDeliverableInfo(actualPhase)!]
                  [@utils.tableText value="${item.deliverableInfo.deliverableType.deliverableCategory.name} - ${deliverableInfo.deliverableType.name}" /]
                </td>
              [/#if]
              [#if !allowPopups]
                [#-- Volume --]
                <td class="text-center urlify"  style="width: 50px !important;">[@utils.tableText value=(item.publication.volume)!"" /]</td>
                [#-- Issue --]
                <td class="text-center col-md-1" style="width: 50px !important;">[@utils.tableText value=(item.publication.issue)!"" /]</td>
                [#-- Page --]
                <td class="text-center col-md-1" style="width: 50px !important;">[@utils.tableText value=(item.publication.pages)!"" /]</td>
              [/#if]
              [#-- Is OpenAccess --]
              <td class="text-center">
                <img src="${baseUrlCdn}/global/images/openAccess-${(item.dissemination.isOpenAccess?string)!'false'}.png" alt="" />
              </td>
              [#-- Is ISI / Altmetric Score (Grey) --]
              [#if !isGrey]
                <td class="text-center">
                  <img src="${baseUrlCdn}/global/images/checked-${(item.publication.isiPublication?string)!'false'}.png" alt="" />
                </td>
              [#else]
                <td class="text-center">
                  [#--local altmetricScore = (item.getDeliverableAltmetricInfo(actualPhase).score)!'Not Defined']
                  [@utils.tableText value="${altmetricScore}" /--]
                  [#local altmetricScoreUrl = (item.getDeliverableAltmetricInfo(actualPhase).imageSmall)!""]
                  [#local altmetricScore = (item.getDeliverableAltmetricInfo(actualPhase).score)!""]
                  [#if altmetricScoreUrl?? && altmetricScoreUrl?has_content]
                    <img src="${altmetricScoreUrl}" alt="" />
                    <div style="display: none">${altmetricScore}</div>
                  [#else]
                    [@utils.tableText value="Not Defined" /]
                  [/#if]
                </td>
              [/#if]
              [#if allowPopups]
                [#if !isGrey]
                  [#-- Complete Status--]
                  <td class="text-center">
                  [#assign isPublicationComplete = action.isPublicationComplete(item.id, actualPhase.id)!false /]
                  [#if isPublicationComplete]
                    <span class="glyphicon glyphicon-ok-sign mf-icon check" title="Complete"></span> 
                  [#else]
                    <span class="glyphicon glyphicon-exclamation-sign mf-icon" title="Incomplete"></span> 
                  [/#if]   
                  </td>
                [/#if]
                [#if PMU]
                  [#-- Check --]
                  [#local isChecked = ((!reportSynthesis.reportSynthesisFlagshipProgress.deliverablesIds?seq_contains(item.id))!true) /]
                  <td class="text-center" style="max-width: 20px;">
                    [@customForm.checkmark id="deliverable${isGrey?then('Grey','')}-${(item.id)!}" name="reportSynthesis.reportSynthesisFlagshipProgress.deliverablesValue" value="${(item.id)!''}" checked=isChecked editable=editable centered=true/]
                    <div id="isCheckedAR-${item.id}" style="display: none">${isChecked?string('1','0')}</div>
                  </td>
                  [#if actualPhaseAR2021]
                    <td id="QAStatusIcon-${item.id}" class="text-center">
                      [#if isChecked]
                        <i style="font-weight: normal;opacity:0.8;"><nobr>[@s.text name="global.notDefined"/]</nobr></i>
                      [#else]
                        <i style="font-weight: normal;opacity:0.8;">[@s.text name="annualReport2018.policies.table2.notInluded"/]</i>
                      [/#if]
                    </td>
                  [/#if]
                [/#if]
              [/#if]
            </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
[/#macro]

[#macro listOfPublicationsToExport name list=[] ]
  <div class="table-responsive">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th>MARLO ID</th>
          <th>Flagship(s)</th>
          <th>Project ID</th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.article" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.author" /](s) </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.date" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.journal" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.volume" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.issue" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.page" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.openAccess" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.isi" /] </th>
          <th class="text-center col-md-1"> [@s.text name="${customLabel}.${name}.directLink" /] </th>
          <th class="col-md-1 text-center"> Included in AR </th>
          
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#local isFromProject = (item.project??)!false]
            [#if isFromProject]
              [#local url][@s.url namespace="/projects" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [#else]
              [#local url][@s.url namespace="/publications" action="${(crpSession)!}/publication"][@s.param name='deliverableID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [/#if]
            <tr>
              [#-- MARLO ID --]
              <td>
                D${item.id?c}
              </td>
              [#-- Flagship --]
              <td>
                [#compress]
                [#list (item.selectedFlahsgips)![] as liason]
                  ${(liason.acronym)!}[#sep], [/#sep]
                [/#list]
                [/#compress]
              </td>
              [#-- Project ID --]
              <td>
                [#if isFromProject]P${item.project.id}[/#if]
              </td>
              [#-- Title --]
              <td>
                [#local publicationTitle = (item.getMetadataValue(1))!""]
                [#if !(publicationTitle?has_content) ]
                  [#local publicationTitle = (item.deliverableInfo.title)!"" ]
                [/#if]
                [@utils.tableText value=publicationTitle /]
              </td>
              [#-- Authors --]
              <td>
                [#compress]
                  [#if item.getMetadataValue(38)?has_content && !item.users?has_content ]
                      ${item.getMetadataValue(38)} 
                  [#else]
                    [#list (item.users)![] as user]
                      ${(user.composedName)!}[#sep]; [/#sep]
                    [/#list]
                  [/#if]
                [/#compress]
              </td>
              [#-- Date of Publication --]
              <td>[@utils.tableText value=(item.getMetadataValue(17))!"" /]</td>
              [#-- Journal Article --]
              <td class="urlify">[@utils.tableText value=(item.publication.journal)!"" /]</td>
              [#-- Volume --]
              <td class="text-center urlify"  style="width: 50px !important;">[@utils.tableText value=(item.publication.volume)!"" /]</td>
              [#-- Issue --]
              <td class="text-center col-md-1" style="width: 50px !important;">[@utils.tableText value=(item.publication.issue)!"" /]</td>
              [#-- Page --]
              <td class="text-center col-md-1" style="width: 50px !important;">[@utils.tableText value=(item.publication.pages)!"" /]</td>
              [#-- Is OpenAccess --]
              <td class="text-center">${((item.dissemination.isOpenAccess)!false)?string('Yes', 'No')}</td>
              [#-- Is ISI --]
              <td class="text-center">${((item.publication.isiPublication)!false)?string('Yes', 'No')}</td>
              [#-- DOI or Handle --]
              <td class="text-center">
                [#if item.getMetadataValue(36)?has_content]
                  [#local directLink = "www.doi.org/"+item.getMetadataValue(36) /]
                  [#-- TODO add www.doi.org/ to DOI identifiers. NOTE: validations will be needed. There are not just DOIs saved there and there are some
                  DOIs that has not been cleaned (being stripped of the www.doi.org/ part) yet. --]
                [#elseif item.getMetadataValue(35)?has_content]
                  [#local directLink = item.getMetadataValue(35) /]
                [#elseif item.dissemination.articleUrl?has_content]
                  [#local directLink = item.dissemination.articleUrl /]
                [#elseif item.dissemination.disseminationUrl?has_content]
                  [#local directLink = item.dissemination.disseminationUrl /]
                [#else]
                  [#local directLink = "" /]
                [/#if]
                ${directLink}
              </td> 
              [#-- Check --]
              <td class="text-center">
                [#local isChecked = ((!reportSynthesis.reportSynthesisFlagshipProgress.deliverablesIds?seq_contains(item.id))!true) /]
                ${isChecked?string('Yes', 'No')}
              </td>
               
            </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]