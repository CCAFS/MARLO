[#ftl]

[#assign title = "Glossary" /]
[#assign globalLibs = ["jquery", "noty"] /]
[#assign customJS = ["${baseUrl}/global/js/glossary.js"] /]
[#assign customCSS = ["${baseUrl}/global/css/glossary.css"] /]
[#assign currentSection = "home" /]
[#assign currentCycleSection = "glossary" /]
[#assign currentStage = "glossary" /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


[#assign glossaryContent= [ 
  [ "accountability", "achievements", "activities", "adoption", "adoptionStudy", "appraisal", "assumptions", "attribution", "audit"],
  [ "baseline", "behIndependence", "benchmark", "beneficiaries", "bilateralFunding",  "budget", "CCAFSbudget"],
  [ "cgiarPlatform", "clients", "clusterEvaluation", "clusterOfActivities", "clusterSample",  "comparativeAdvantage", "comparisonGroup", "conclusions", "confidenceLevel", "contribution", "controlGroup", "costBenefit", "costEffectiveness", "counterfactual", "crossCuttingDimension", "credibility", "CRP"],
  [ "deliverables", "dependentVariable", "disaggregation", "doubleLoopLearning"],
  [ "effectiveness", "efficiency","endUsers", "evaluation","evaluationCriteria", "evaluationReference", "evidence", "exanteEvaluation", "expostEvaluation", "externalEvaluation", "formativeEvaluation", "flagshipProject" ],
  [ "globalPublicGoods"],
  [ "hypothesis" ],
  [ "impact", "impactAssessment", "impactEvaluation", "impactPathway", "impartiality", "independence", "independentVariable", "indicator", "innovation", "inputs", "ido", "internalEvaluation", "internationalPublicGoods", "interoperable"],
  [ "legitimacy", "lessonsLearned", "logicalFramework" ],
  [ "metaEvaluation", "midTermEvaluation", "milestone", "mixedMethods", "monitoring","mutualAccountability" ],
  [ "nextUsers", "nares" ],
  [ "outcome", "outcomeCaseStudy", "outputs"],
  [ "participatoryEvaluation", "partners", "peerReview", "performanceManagement", "performanceMeasureement", "ppa", "primaryData", "processEvaluation", "programEvaluation", "programManagementUnit", "projectEvaluation", "projectLeader" ],
  [ "qualityAssurance", "quasiExperimentalDesign" ],
  [ "randomAssignment", "rtc", "recommendations", "regressionAnalysis", "relevance", "reliability", "replication", "researchQuality", "results", "resultsBaseManagement", "review", "riskAnalysis"],
  [ "scaling", "secondaryData", "selectionBias", "sexDisaggregatedData", "singleLoopLearning", "socialLearning", "sphereOfControl", "sphereOfInfluence", "sphereOfInterest", "spilloverEffects", "srf", "stakeholders", "subido", "survey", "surveyInstrument", "sustainability"],
  [ "targetGroup", "targetUnit", "targetvalue", "termsOfReference", "thematicEvaluation", "theoryBasedImpact", "toc", "transactionCost", "treatmentGroup", "tripleLoopLearning", "triangulation"],
  [ "use", "valueForMoney" , "validity"],
  [ "window1", "window2", "window3", "womenEmpowerment" ]
] /]

[#assign words= [ "A", "B", "C", "D", "E", "F", "G", "H", "I", "L", "M", "N", "O", "P", "R", "S", "T", "U", "V", "W"]/]

<section class="container contentForm">
  [@s.form action="glossary" cssClass="pure-form"]
  
    <article class="fullContent" id="glossary-content">
    <h1 class="contentTitle">[@s.text name="home.glossary.title"/]</h1>
    [@s.text name="home.glossary.contact"/] [@s.text name="home.glossary.mailto"/]
    
    <div id="searchInput" class="pull-right form-group search-word has-feedback">
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
    [#if word == "CCAFSbudget"]
      <ul class="listGlossaryItems">
        <li>[@s.text name="home.glossary.CCAFSbudget.definition.w1"/]</li>
        <li>[@s.text name="home.glossary.CCAFSbudget.definition.w2"/]</li>
        <li>[@s.text name="home.glossary.CCAFSbudget.definition.w3"/]</li>
        <li>[@s.text name="home.glossary.CCAFSbudget.definition.bilateral"/]</li>
      </ul> 
    [#elseif word == "budget"]
      <ul class="listGlossaryItems">
          <li>[@s.text name="home.glossary.budget.definition.w1"/]</li>
          <li>[@s.text name="home.glossary.budget.definition.w3"/]</li>
          <li>[@s.text name="home.glossary.budget.definition.bilateral"/]</li>
      </ul>
    [/#if]
  </div>
 </div>
[/#macro]

[#include "/WEB-INF/global/pages/footer.ftl"]