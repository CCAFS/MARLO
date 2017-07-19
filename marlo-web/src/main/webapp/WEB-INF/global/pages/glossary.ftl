[#ftl]

[#assign title = "Glossary" /]
[#assign globalLibs = ["jquery", "noty"] /]
[#assign customJS = ["${baseUrlMedia}/js/global/glossary.js"] /]
[#assign customCSS = ["${baseUrlMedia}/css/global/glossary.css"] /]
[#assign currentSection = "home" /]
[#assign currentCycleSection = "glossary" /]
[#assign currentStage = "glossary" /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]


[#assign glossaryContent= [ 
  [ "accountability", "activities", "adoption", "appraisal", "attribution", "audit"],
  [ "baseline", "behavioralIndependence", "behIndependence", "beneficiaries", "budget"],
  [ "clients","clusterOfActivities",  "comparativeAdvantage", "costEffectiveness", "counterfactual"],
  [ "deliverables"],
  [  "effectiveness", "efficiency","endUsers", "evaluation","evaluationCriteria", "evaluationReference", "evidence", "formativeEvaluation" ],
  [ "globalPublicGoods"],
  [ "impact", "impactAssesment", "impactEvaluation", "impactPathway", "impartiality", "independence", "indicator", "inputs", "ido", "internationalPublicGoods", "interoperable"],
  [ "legitimacy"],
  [ "managementLiason","mog",  "monitoring","mutualAccountability", "nextUsers"],
  [ "orgIndependence", "outcome", "outcomestatement", "outcomestory", "outputs"],
  [ "partners", "peerReview", "performanceManagement", "performanceMeasureement", "project", "projectLeader", "projectOutcome", "projectOutcomeStory", "projectPartner"],
  [ "relevance", "researchOutcomes", "results", "resultsBaseManagement", "review"],
  [ "scaling", "stakeholders", "summativeEvaluation", "sustainability", "systemLevelOutcomes"],
  [ "targetGroup", "targetnarrative", "targetvalue", "toc", "transactionCost", "triangulation"],
  [ "update" , "use"]
] /]

[#assign words= [ "A", "B", "C", "D", "E", "F", "G", "I", "L", "M", "N", "O", "P", "R", "S", "T", "U"]/]

<section class="container contentForm">
  [@s.form action="glossary" cssClass="pure-form"]
  
    <article class="fullContent" id="glossary-content">
    <h1 class="contentTitle">[@s.text name="home.glossary.title"/]</h1>
    [@s.text name="home.glossary.contact"/] [@s.text name="home.glossary.mailto"/]
    
    <div id="searchInput" class="pull-right form-group has-feedback"">
      <input type="text" class="form-control" name="search" id="search" placeholder="search" autocomplete="off">
      <span class="glyphicon glyphicon-search form-control-feedback"></span>
    </div>
    <hr />
    [#list words as word] 
      <span id="" class="tag">${word}</span>
    [/#list]
    
      <div id="content">  
        [#-- List the terms with his definition --]
        [#list glossaryContent as letter]          
          [#list letter as word] 
            [@wordDefinition word=word /]
          [/#list]
        [/#list]
        <div id="message" class="col-md-12 text-center" style="display:none;">
        <hr />
          <span >Sorry, this word was not found in the glossary</span>
        </div>  
        
        <div class="lastUpdate">
          <p>[@s.text name="home.glossary.lastUpdate"/]</p>
        </div>
      </div>
  [/@s.form]
</section>

[#macro wordDefinition word]
<div id="${word}" class="word">
<hr />
  <div class="wordTitle">[@s.text name="home.glossary.${word}"/]</div>
  <div class="wordDefinition">[@s.text name="home.glossary.${word}.definition"/]
    [#if word == "budget"]
      <ul class="listGlossaryItems">
        <li>[@s.text name="home.glossary.budget.definition.w1"/]</li>
        <li>[@s.text name="home.glossary.budget.definition.w2"/]</li>
        <li>[@s.text name="home.glossary.budget.definition.w3"/]</li>
        <li>[@s.text name="home.glossary.budget.definition.bilateral"/]</li>
      </ul>
    [#elseif word == "partners"]
      <ul>
        <li>[@s.text name="home.glossary.partners.definition2"/]</li>
        <li>[@s.text name="home.glossary.partners.definition3"/]</li>
      </ul>
      [@s.text name="home.glossary.partners.definition4"/]  
    [/#if]
  </div>
 </div>
[/#macro]

[#include "/WEB-INF/global/pages/footer.ftl"]