[#ftl]

[#assign title = "Glossary" /]
[#assign globalLibs = ["jquery", "noty"] /]
[#assign customJS = ["${baseUrlCdn}/global/js/glossary.js"] /]
[#assign customCSS = ["${baseUrlCdn}/global/css/glossary.css"] /]
[#assign currentSection = "home" /]
[#assign currentCycleSection = "glossary" /]
[#assign currentStage = "glossary" /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


[#assign glossaryContent= [ 
  [ "abstract","accountability", "achievements", "activities", "audio", "adoption", "adoptionStudy", "appraisal", "assumptions", "attribution", "audit"],
  [ "baseline", "behIndependence", "benchmark", "beneficiaries", "bilateralFunding","blog", "blogPost", "book", "budget", "bookChapter", "brief", "brochure", "CCAFSbudget"],
  [ "caseStudy", "cgiarPlatform", "clients", "clusterEvaluation", "clusterOfActivities", "clusterSample",  "comparativeAdvantage", "comparisonGroup", "conclusions", "conferenceProceedings", "confidenceLevel", "contribution", "controlGroup", "costBenefit", "costEffectiveness", "counterfactual", "crossCuttingDimension", "credibility", "CRP"],
  [ "dataPaper", "dataset", "deliverables", "dependentVariable", "disaggregation", "doubleLoopLearning"],
  [ "effectiveness", "efficiency","endUsers", "equation", "evaluation","evaluationCriteria", "evaluationReference", "evidence", "exanteEvaluation", "expostEvaluation", "extensionMaterial", "externalEvaluation", "formativeEvaluation", "flagshipProject" ],
  [ "factsheet"],
  [ "globalPublicGoods"],
  [ "hypothesis" ],
  [ "image", "impact", "impactAssessment", "impactEvaluation", "impactPathway", "impartiality", "independence", "independentVariable", "indicator", "infographic", "innovation", "inputs", "ido", "internalEvaluation", "internationalPublicGoods", "interoperable"],
  [ "journalArticle", "journalIssue", "journalItem" ],
  [ "legitimacy", "lessonsLearned", "logicalFramework" ],
  [ "magazine", "magazineArticle", "manual", "metaEvaluation", "map", "midTermEvaluation", "milestone", "mixedMethods", "model", "monitoring","mutualAccountability" ],
  [ "nares", "nextUsers", "newsItem", "newsletter" ],
  [ "opinionPiece", "outcome", "outcomeCaseStudy", "outputs"],
  [ "participatoryEvaluation", "partners", "peerReview", "performanceManagement", "performanceMeasureement", "podcast", "poster", "ppa", "presentation", "pressRelease", "primaryData", "processEvaluation", "programEvaluation", "programManagementUnit", "projectEvaluation", "projectLeader", "proposal" ],
  [ "qualityAssurance", "quasiExperimentalDesign", "questionnaire" ],
  [ "randomAssignment", "rtc", "recommendations", "regressionAnalysis", "relevance", "reliability", "replication", "report", "researchQuality", "results", "resultsBaseManagement", "review", "riskAnalysis"],
  [ "scaling", "secondaryData", "selectionBias", "sexDisaggregatedData", "singleLoopLearning", "socialLearning", "socialMediaOutput", "software", "sourceCode", "sphereOfControl", "sphereOfInfluence", "sphereOfInterest", "spilloverEffects", "srf", "stakeholders", "subido", "survey", "surveyInstrument", "sustainability"],
  [ "targetGroup", "targetUnit", "targetvalue", "template", "termsOfReference", "thematicEvaluation", "theoryBasedImpact", "thesis", "toc", "trainingMaterial", "transactionCost", "treatmentGroup", "tripleLoopLearning", "triangulation"],
  [ "use", "valueForMoney" , "validity"],
  [ "video" ],
  [ "website", "window1", "window2", "window3", "womenEmpowerment", "workingPaper" ]
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