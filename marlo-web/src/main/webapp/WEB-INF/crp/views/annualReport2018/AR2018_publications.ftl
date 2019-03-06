[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
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
  {"label":"${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
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
              [#assign peerReviewedArticles = 12 /]
                [#-- Total number of peer reviewed articles --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC4.totalArticles" /]</label><br />
                  <span>${(peerReviewedArticles)!}</span>
                </div>
              </div>
              
              <div class="col-md-4">
                [#-- Chart 10 - Number of peer reviewed articles by Open Access status --]
                <div id="chart10" class="chartBox simpleBox">
                  [#assign chartData = [
                      {"name":"Open Acess",   "value": "10"},
                      {"name":"Limited",      "value": "3"}
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
                      {"name":"Yes",   "value": "14"},
                      {"name":"No",    "value": "3"}
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
            <div class="form-group">
              [#-- Modal Large --]
                <button type="button" class="pull-right btn btn-link btn-sm" data-toggle="modal" data-target="#tableA-bigger"> 
                  <span class="glyphicon glyphicon-fullscreen"></span> See Full Table 2
                </button>
                <div id="tableA-bigger" class="modal fade bs-example-modal-lg " tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
                  <div class="modal-dialog modal-lg bigger" role="document">
                    <div class="modal-content">
                      <div class="modal-header">
                        [@s.text name="${customLabel}.fullList.title" /]
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                      </div>
                      [@listOfPublications name="fullList" list=[] allowPopups=false /]
                    </div>
                  </div>
                </div>
                [#-- Table --]
                [@listOfPublications name="fullList" list=[] allowPopups=true /]
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
          <td></td>
          <td></td>
        </tr>
        <tr>
          <td class="tableTitle">[@s.text name="${customLabel}.${name}.openAccess" /]</td>
          <td></td>
          <td></td>
        </tr>
        <tr>
          <td class="tableTitle">[@s.text name="${customLabel}.${name}.isi" /]</td>
          <td></td>
          <td></td>
        </tr>
      </tbody>
    </table>
  </div>

[/#macro]

[#macro listOfPublications name list=[] allowPopups=false]


  <div class="form-group">
    [#if allowPopups]
      <h4 class="headTitle">[@s.text name="${customLabel}.fullList.title" /]</h4>
    [/#if]
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.author" /] </th>
          <th class="text-center col-md-2"> [@s.text name="${customLabel}.${name}.date" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.article" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.journal" /] </th>
          [#if !allowPopups]
            <th class="text-center"> [@s.text name="${customLabel}.${name}.volume" /] </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.issue" /] </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.page" /] </th>
          [/#if]
            <th class="text-center col-md-2"> [@s.text name="${customLabel}.${name}.openAccess" /] </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.isi" /] </th>
          [#if !allowPopups]
           <th class="text-center"> [@s.text name="${customLabel}.${name}.identifier" /] </th>
          [/#if]
          [#if allowPopups]
           <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.includeAR" /] </th>
          [/#if]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
          <tr>
            <td>${item.author}</td>
            <td>${item.date}</td>
            <td>${item.article}</td>
            <td>${item.journal}</td>
            [#if !allowPopups]
              <td>${item.volume}</td>
              <td>${item.issue}</td>
              <td>${item.page}</td>
            [/#if]
            <td class="text-center">
              <span style="display:none">${(item.open?string)!'false'}</span>
              <img src="${baseUrl}/global/images/openAccess-${(item.open?string)!'false'}.png" alt="" />
            </td>
            <td class="text-center">
              <span style="display:none">${(item.isi?string)!'false'}</span>
              <img src="${baseUrl}/global/images/checked-${(item.isi?string)!'false'}.png" alt="" />
            </td>
            [#if !allowPopups]
              <td>${item.identifier}</td>
            [/#if]
            [#if allowPopups]
              <td class="text-center">
                [@customForm.checkmark id="" name="" checked=false editable=editable centered=true/] 
              </td>
            [/#if]
          </tr>
          [/#list]
        [#else]
          <tr>
            [#if allowPopups]
              <td class="text-center" colspan="7"><i>No entries added yet.</i></td>
            [#else]
              <td class="text-center" colspan="10"><i>No entries added yet.</i></td>
            [/#if]
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]