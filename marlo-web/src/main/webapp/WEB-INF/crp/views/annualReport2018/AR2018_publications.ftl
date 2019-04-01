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
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js",
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js"
  ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

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
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
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
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
            
            <div class="form-group">
              [#assign guideSheetURL = "https://drive.google.com/file/d/1apWx9qJk5NXlZQTzZzhGqRNSx934Bp5H/view" /]
                <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrl}/global/images/icon-file.png" alt="" /> #C4 Peer review papers  -  Guideline </a> </small>
            </div>
            <br />
            
            <div class="form-group row">
              <div class="col-md-4">
                [#-- Total number of peer reviewed articles --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC4.totalArticles" /]</label><br />
                  <span>${(total)!}</span>
                </div>
              </div>
              
              <div class="col-md-4">
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
              
              <div class="col-md-4">
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
              <h4 class="headTitle">[@s.text name="${customLabel}.peerReviewed.title" /]</h4>
              [@numberOfPublications name="peerReviewed" list=["", ""]/]
            </div>
            
            [#-- Full list of publications published --]
            <div class="form-group viewMoreSyntesisTable-block">
              
              [#-- Modal Large --]
                <button type="button" class="btn btn-default btn-xs pull-right" data-toggle="modal" data-target="#modal-publications">
                 <span class="glyphicon glyphicon-fullscreen"></span> See Full table 6
                </button>
                <h4 class="headTitle">[@s.text name="${customLabel}.fullList.title" /]</h4>
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
                [#-- Table --]
                [@listOfPublications name="fullList" list=(deliverables)![]  allowPopups=true /]
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

[#macro listOfPublications name list=[] allowPopups=false]

    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.article" /] </th>
          [#if !allowPopups]
            <th class="text-center"> [@s.text name="${customLabel}.${name}.author" /](s) </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.date" /] </th>
          [/#if]
          <th class="text-center"> [@s.text name="${customLabel}.${name}.journal" /] </th>
          [#if !allowPopups]
            <th class="text-center"> [@s.text name="${customLabel}.${name}.volume" /] </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.issue" /] </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.page" /] </th>
          [/#if]
          <th class="text-center"> [@s.text name="${customLabel}.${name}.openAccess" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.isi" /] </th>
          [#if !allowPopups]
            <th class="text-center col-md-1"> [@s.text name="${customLabel}.${name}.identifier" /] </th>
          [/#if]
          [#if allowPopups]
            <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.includeAR" /] </th>
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
              [#-- Title --]
              <td>
                [#local publicationTitle = (item.getMetadataValue(1))!""]
                [#if !(publicationTitle?has_content) ]
                  [#local publicationTitle = (item.deliverableInfo.title)!"" ]
                [/#if]
                
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
                
                <a href="${url}" target="_blank" class="pull-right"><span class="glyphicon glyphicon-new-window"></span></a>
                
              </td>
              [#if !allowPopups]
              [#-- Authors --]
              <td>[@utils.tableList list=(item.users)![] displayFieldName="composedName" nobr=true class="authorsList mCustomScrollbar" scroll=true /]</td>
              [#-- Date of Publication --]
              <td>[@utils.tableText value=(item.getMetadataValue(17))!"" /]</td>
              [/#if]
              [#-- Journal Article --]
              <td class="urlify">[@utils.tableText value=(item.publication.journal)!"" /]</td>
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
                <img src="${baseUrl}/global/images/openAccess-${(item.dissemination.isOpenAccess?string)!'false'}.png" alt="" />
              </td>
              [#-- Is ISI --]
              <td class="text-center">
                <img src="${baseUrl}/global/images/checked-${(item.publication.isiPublication?string)!'false'}.png" alt="" />
              </td>
              [#if !allowPopups]
                [#-- DOI or Handle --]
                <td class="text-center">
                [#local doi = (item.getMetadataValue(36))!"" /]
                
                [#if doi?has_content && doi?contains("http") && !(doi?contains(";"))]
                <a target="_blank" href="${doi}"><span class="glyphicon glyphicon-link"></span></a>
                [#else]
                  [#if !(doi?has_content) ]
                   <span class="glyphicon glyphicon-link" title="Not defined"></span>
                  [#else]
                   <span class="glyphicon glyphicon-link" title="${doi}"></span>
                  [/#if]
                [/#if]              
                
                </td>
              [/#if]
              [#if allowPopups]
                [#-- Check --]
                <td class="text-center">
                  [#local isChecked = ((!reportSynthesis.reportSynthesisFlagshipProgress.deliverablesIds?seq_contains(item.id))!true) /]
                  [@customForm.checkmark id="deliverable-${(item.id)!}" name="reportSynthesis.reportSynthesisFlagshipProgress.deliverablesValue" value="${(item.id)!''}" checked=isChecked editable=editable centered=true/]
                </td>
              [/#if]
            </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
[/#macro]