[ftl]
[#assign title = "Project Contributions to LP6" /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectContributionsCrpList.js",
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectsContributionToLP6.css"
  ]
/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]


<h3 class="headTitle">[@s.text name="projects.LP6Contribution.contributionTitle" /]</h3>  
  <div id="projectContributionToLP6" class="borderBox">
    <div class="form-group">
      [@customForm.textArea name="projects.LP6Contribution.narrativeContribution"  i18nkey="projects.LP6Contribution.narrativeContribution"  className="limitWords-100" required=true /]
    </div>
  </div>

[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro contributionToLP6Form name className ]


      [@s.text name="projects.LP6Contribution.workingAcrossFlagships"/][@customForm.req required=true /]
      [@customForm.radioFlat id="flagshipLevels-yes" name="flagshipLevels" label="Yes" value="true" checked=false cssClassLabel="radio-label-yes"/]
      [@customForm.radioFlat id="flagshipLevels-no" name="flagshipLevels" label="No" value="false" checked=false cssClassLabel="radio-label-no"/]
      [@customForm.textArea name="projects.LP6Contribution.narrativeContribution"  i18nkey="projects.LP6Contribution.narrativeContribution"  className="limitWords-100" required=true /]


[/#macro]

