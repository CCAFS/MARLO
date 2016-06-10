[#ftl]
[#assign title = "Impact Pathway - Outcomes" /]
[#assign pageLibs = [] /]
[#assign customJS = [] /]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "outcomes" /]

[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"", "action":"outcomes"},
  {"label":"outcomes", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]



<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]  
        
        <h4 class="sectionTitle">Flagship {0} - Outcomes </h4>
        <div class="outcomes-list">
          [#assign outcomes= [
            {
              'name': 'Outcome #1', 
              'milestones': [
                {}
              ],
              'subIdos': [
                {}
              ]
            }
          ]  /]
          [#list outcomes as outcome]
          <div class="outcome form-group borderBox">
            <div class="leftHead">
              <span class="index">${outcome_index+1}</span>
              <span class="elementId">Outcome #${outcome_index+1}</span>
            </div>
            <div class="removeElement" title="Remove Outcome"></div>
            <br />
            <div class="form-group">
              [@customForm.textArea name="" i18nkey="Outcome Statement" required=true className="outcome-statement" editable=true /]
            </div>
            <div class="row form-group">
              <div class="col-md-4">[@customForm.input name="" type="text" showTitle=false placeholder="Target Value" className="targetValue" required=true editable=true /]</div>
              <div class="col-md-4">[@customForm.input name="" type="text" showTitle=false placeholder="Target Year" className="targetYear" required=true editable=true /]</div>
              <div class="col-md-4">[@customForm.select name="" showTitle=false placeholder="Select a target Unit..." listName="" editable=true  /]</div>
            </div>
            
            <h5 class="sectionSubTitle">Milestones</h5>
            [#list outcome.milestones as milestone]
            <div class="milestone simpleBox">
              <div class="form-group">
                [@customForm.textArea name="" i18nkey="Milestone Statement" required=true className="milestone-statement" editable=true /]
              </div>
              <div class="row form-group">
                <div class="col-md-4">[@customForm.input name="" type="text" showTitle=false placeholder="Target Value" className="targetValue" required=true editable=true /]</div>
                <div class="col-md-4">[@customForm.input name="" type="text" showTitle=false placeholder="Target Year" className="targetYear" required=true editable=true /]</div>
                <div class="col-md-4">[@customForm.select name="" showTitle=false placeholder="Select a target Unit..." listName="" editable=true  /]</div>
              </div>
            </div>
            [/#list]
            
            <h5 class="sectionSubTitle">Sub-IDOs</h5>
            [#list outcome.subIdos as subIdo]
            <div class="sub-ido simpleBox">
              <div class="row form-group">
                <div class="col-md-4">[@customForm.select name="" i18nkey="IDO" placeholder="Select an IDO..." listName="" editable=true  /]</div>
                <div class="col-md-4">[@customForm.select name="" i18nkey="SubIDO" placeholder="Select a Sub-IDO..." listName="" editable=true  /]</div>
                <div class="col-md-4">[@customForm.input name="" type="text" i18nkey="Contribution" placeholder="% of contribution" className="contribution" required=true editable=true /]</div>
              </div>
              <h5>Assumptions:</h5>
              <div class="assumptions form-group">
                [@customForm.input name="" type="text" showTitle=false placeholder="Assumption statement #1" className="assumption" required=true editable=true /]
              </div>
            </div>
            [/#list]
          </div>
          [/#list]
        </div>
        <div class="buttons">
          [@s.submit type="button" name="save" cssClass=""][@s.text name="form.buttons.save" /][/@s.submit]
        </div>
        
        [/@s.form]
      </div>
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]
