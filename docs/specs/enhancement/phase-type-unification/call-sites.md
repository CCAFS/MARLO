# Phase Type Unification — Call-Site Inventory

Generated at commit `9e2a9ed838` (2026-10-28; regenerated in Judgment Day round 1, JD-3/JD-6) as the evidence behind the
`shared-state` and `consumer` rows of the design's Premise Ledger. Prefixes: `main:` = `marlo-web/src/main/{java/org/cgiar/ccafs/marlo|webapp/WEB-INF|webapp|resources}/`,
`test:` = `marlo-web/src/test/java/org/cgiar/ccafs/marlo/`, `data:` = `marlo-data/src/main/java/org/cgiar/ccafs/marlo/`.
Regenerate with the commands below before each B4 batch; a count that differs from this file is investigated before the batch starts.

## A. Old type methods and FTL properties

Command (as run): `grep -rnwE "isPOWB|isAWPBActive|isPlanningActive|planningActive|isProgressActive|isUpKeepActive|upKeepActive|UpKeepActive|isReportingActive|reportingActive|setPlanningActive|setReportingActive" marlo-web/src marlo-data/src --include=*.java --include=*.ftl --include=*.js --include=*.xml --include=*.sql --include=*.properties`, third-party JS dropped, plus `grep -rnE "\[#if POWB\?exists" marlo-web/src --include=*.ftl`.

**Totals:** 432 lines — java 159, ftl 262, js 9, xml 1, sql 1. Occurrences by token: isPOWB 20 (+2 `POWB` property), isAWPBActive 11, isPlanningActive 37, planningActive 5, isProgressActive 33, isUpKeepActive 54, upKeepActive 8, UpKeepActive 6, isReportingActive 93, reportingActive 215. Declarations included.

### A1. Sites to migrate
```
main:action/BaseAction.java:471:  private boolean planningActive;
main:action/BaseAction.java:487:  private boolean reportingActive;
main:action/BaseAction.java:1273:        if (!this.isProgressActive()) {
main:action/BaseAction.java:1333:    if (this.isPlanningActive()) {
main:action/BaseAction.java:1446:    if (this.isPlanningActive()) {
main:action/BaseAction.java:3015:      if (this.isReportingActive()) {
main:action/BaseAction.java:3145:            if (this.isReportingActive() || this.isUpKeepActive()) {
main:action/BaseAction.java:3200:              if (this.isReportingActive() || this.isUpKeepActive()) {
main:action/BaseAction.java:3241:              if (this.isReportingActive() || this.isUpKeepActive()) {
main:action/BaseAction.java:4966:          if (this.hasSpecificities(APConstants.CRP_LP6_ACTIVE) && this.isReportingActive()) {
main:action/BaseAction.java:4998:              this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(), section);
main:action/BaseAction.java:5022:                  this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(), section);
main:action/BaseAction.java:5052:              this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(), section);
main:action/BaseAction.java:5106:            this.getCurrentCycleYear(), this.isUpKeepActive(), section);
main:action/BaseAction.java:5164:             * status.setUpkeep(this.isUpKeepActive());
main:action/BaseAction.java:5183:          if (this.isReportingActive()) {
main:action/BaseAction.java:5195:            if (this.isReportingActive() && this.hasSpecificities(this.getCrpEnableBudgetExecution())) {
main:action/BaseAction.java:5204:              this.getCurrentCycleYear(), this.isUpKeepActive(), section);
main:action/BaseAction.java:5216:            this.getCurrentCycleYear(), this.isUpKeepActive(), section);
main:action/BaseAction.java:5222:            if (this.isReportingActive()) {
main:action/BaseAction.java:5261:                this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(), section);
main:action/BaseAction.java:5287:              this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(), section);
main:action/BaseAction.java:5315:              this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(), section);
main:action/BaseAction.java:5337:              this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(), section);
main:action/BaseAction.java:5352:            this.getCurrentCycleYear(), this.isUpKeepActive(), section);
main:action/BaseAction.java:5366:            this.getCurrentCycleYear(), this.isUpKeepActive(), section);
main:action/BaseAction.java:5926:        this.getCurrentCycleYear(), this.isUpKeepActive(), ProjectSectionStatusEnum.FUNDINGSOURCE.getStatus());
main:action/BaseAction.java:5953:        this.getCurrentCycleYear(), this.isUpKeepActive(), ProjectSectionStatusEnum.INNOVATIONS.getStatus());
main:action/BaseAction.java:6152:        this.getCurrentCycleYear(), this.isUpKeepActive(), ProjectSectionStatusEnum.POLICIES.getStatus());
main:action/BaseAction.java:6222:        this.getCurrentCycleYear(), this.isUpKeepActive(), ProjectSectionStatusEnum.EXPECTEDSTUDIES.getStatus());
main:action/BaseAction.java:6347:  public boolean isAWPBActive() {
main:action/BaseAction.java:7683:      if (this.isReportingActive() && this.getCrpSession().equals("ccafs") && this.getCurrentCycleYear() <= 2016) {
main:action/BaseAction.java:7694:  public boolean isPlanningActive() {
main:action/BaseAction.java:7739:  public boolean isPOWB() {
main:action/BaseAction.java:7820:  public boolean isProgressActive() {
main:action/BaseAction.java:8064:  public boolean isReportingActive() {
main:action/BaseAction.java:8203:  public boolean isUpKeepActive() {
main:action/BaseAction.java:8438:    if (this.isReportingActive()) {
main:action/BaseAction.java:8453:    if (this.isReportingActive()) {
main:action/BaseAction.java:8479:    if (this.isReportingActive()) {
main:action/BaseAction.java:8512:    if (this.isReportingActive()) {
main:action/BaseAction.java:8692:    if (this.isReportingActive()) {
main:action/BaseAction.java:8924:  public void setPlanningActive(boolean planningActive) {
main:action/BaseAction.java:8925:    this.planningActive = planningActive;
main:action/BaseAction.java:8928:  public void setReportingActive(boolean reportingActive) {
main:action/BaseAction.java:8929:    this.reportingActive = reportingActive;
main:action/BaseAction.java:9248:      if (this.isProgressActive() && status != Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())) {
main:action/BaseAction.java:9261:        this.getCurrentCycleYear(), this.isUpKeepActive(), ProjectSectionStatusEnum.POLICIES.getStatus());
test:action/BaseActionGlobalUnitRulesTest.java:104:      public boolean isUpKeepActive() {
main:action/center/monitoring/outcome/MonitoringOutcomesListAction.java:177:    // if (this.isPlanningActive()) {
main:action/funding/FundingSourceAction.java:219:    return permission && !this.isReportingActive();
main:action/funding/FundingSourceAction.java:302:    if (!this.isReportingActive()) {
main:action/funding/FundingSourceListAction.java:366:    return permission && !this.isReportingActive();
main:action/home/DashboardAction.java:317:    if (this.isPlanningActive()) {
main:action/json/annualReport/ValidateSectionStatusReportSynthesis2018Action.java:149:    if (this.isPlanningActive()) {
main:action/json/annualReport/ValidateSectionStatusReportSynthesisAction.java:148:    if (this.isPlanningActive()) {
main:action/json/global/DeliverableStatusAction.java:76:    if (this.isPlanningActive() && !this.isUpKeepActive()) {
main:action/json/global/ProgressActiveValidationAction.java:51:    if (this.isUpKeepActive()) {
main:action/json/global/SendNotificationEmailAction.java:203:    if (this.isPlanningActive()) {
main:action/json/powb/ValidateSectionStatusPowbSynthesis2019Action.java:122:    if (this.isPlanningActive()) {
main:action/json/powb/ValidateSectionStatusPowbSynthesisAction.java:137:    if (this.isPlanningActive()) {
main:action/json/project/CanDeleteDeliverableWithSharedTrainees.java:62:        if (!this.isProgressActive() && deliverableID != 0 && phaseID != 0) {
main:action/json/project/ValidateProjectSectionAction.java:199:          if (this.isUpKeepActive() || this.isReportingActive()) {
main:action/json/project/ValidateProjectSectionAction.java:222:          if (this.isPlanningActive() || ((this.isReportingActive() || this.isUpKeepActive())
main:action/json/project/ValidateProjectSectionAction.java:298:    if (this.isPlanningActive()) {
main:action/json/project/ValidateProjectSectionAction.java:314:          if (this.hasSpecificities(APConstants.CRP_LP6_ACTIVE) && this.isReportingActive()) {
main:action/json/project/ValidateProjectSectionAction.java:406:              if (this.isPlanningActive() && this.isUpKeepActive()) {
main:action/json/project/ValidateProjectSectionAction.java:551:            if (this.isAWPBActive()) {
main:action/json/project/ValidateProjectSectionAction.java:587:         * if (this.isReportingActive() && !this.hasSpecificities(this.getCrpEnableBudgetExecution())) {
main:action/projects/DeliverableAction.java:510:    if (this.isReportingActive() || this.isUpKeepActive()) {
main:action/projects/DeliverableAction.java:1811:        if (this.isReportingActive() || this.isUpKeepActive()) {
main:action/projects/DeliverableAction.java:2087:      if (this.isPlanningActive() && !this.isUpKeepActive()) {
main:action/projects/DeliverableAction.java:2347:      if (this.isReportingActive() || this.isUpKeepActive()) {
main:action/projects/DeliverableAction.java:3414:      if (this.isReportingActive() || this.isUpKeepActive()) {
main:action/projects/DeliverableAction.java:3480:      if (this.hasSpecificities(APConstants.CRP_LP6_ACTIVE) && this.isReportingActive()
main:action/projects/DeliverableAction.java:3491:      if (this.isReportingActive() || this.isUpKeepActive()) {
main:action/projects/DeliverableAction.java:5594:    if (this.isPlanningActive() && !this.isUpKeepActive()) {
main:action/projects/DeliverableAction.java:5654:    if (this.isReportingActive() || this.isUpKeepActive()) {
main:action/projects/DeliverableListAction.java:166:    if (this.isPlanningActive() && !this.isUpKeepActive()) {
main:action/projects/DeliverableListAction.java:300:        if (this.isPlanningActive() && !this.isUpKeepActive()) {
main:action/projects/DeliverableListAction.java:356:        if (this.isPlanningActive()) {
main:action/projects/DeliverableListAction.java:814:      if (this.isReportingActive() || this.isUpKeepActive()) {
main:action/projects/ProjectBudgetByPartnersAction.java:142:    if (this.isReportingActive()) {
main:action/projects/ProjectBudgetByPartnersAction.java:184:    if (this.isReportingActive()) {
main:action/projects/ProjectBudgetByPartnersAction.java:207:    if (this.isReportingActive()) {
main:action/projects/ProjectBudgetByPartnersAction.java:216:    if (this.isReportingActive()) {
main:action/projects/ProjectBudgetByPartnersAction.java:226:    if (this.isReportingActive()) {
main:action/projects/ProjectBudgetByPartnersAction.java:702:        if ((this.isReportingActive() || this.isUpKeepActive())
main:action/projects/ProjectBudgetByPartnersAction.java:770:      if ((this.isReportingActive() || this.isUpKeepActive())
main:action/projects/ProjectBudgetByPartnersAction.java:784:      if (this.isPlanningActive()) {
main:action/projects/ProjectBudgetByPartnersAction.java:788:      if ((this.isReportingActive() || this.isUpKeepActive())
main:action/projects/ProjectListAction.java:264:      status.setUpkeep(this.isUpKeepActive());
main:action/projects/ProjectListAction.java:404:        status.setUpkeep(this.isUpKeepActive());
main:action/projects/ProjectListAction.java:637:    if (this.isPlanningActive()) {
main:action/projects/ProjectListAction.java:687:    if (this.isReportingActive()) {
main:action/projects/ProjectOutcomeAction.java:422:    if (this.isReportingActive()) {
main:action/projects/ProjectOutcomeAction.java:1468:        if (this.isReportingActive()) {
main:action/projects/ProjectOutcomeAction.java:1520:    if (this.isReportingActive()) {
main:action/projects/ProjectOutcomeAction.java:1864:            if (this.isReportingActive()) {
main:action/projects/ProjectOutcomeAction.java:1936:            if (this.isPlanningActive()) {
main:action/projects/ProjectOutcomeAction.java:2039:    if (this.isPlanningActive()) {
main:action/projects/ProjectPartnerAction.java:1561:    if (this.isReportingActive()) {
main:action/projects/ProjectPartnerAction.java:2309:      if (this.isLessonsActive() && this.isReportingActive()) {
main:action/projects/ProjectPartnerAction.java:2327:      if (this.isReportingActive() && projectDB.getProjectInfo() != null && project.getProjectInfo() != null
main:action/projects/ProjectSubmissionAction.java:304:    if (!this.isPlanningActive()) {
main:action/projects/ProjectSubmissionAction.java:311:    if (this.isPlanningActive()) {
main:action/projects/ProjectSubmissionAction.java:327:    if (this.isPlanningActive()) {
main:crp/macros/projectsListTemplate.ftl:12:  [#local showBudget = !archived && !reportingActive && !planningActive && !centerGlobalUnit /]
main:crp/macros/projectsListTemplate.ftl:174:              [#elseif !archived && !reportingActive]
main:crp/views/annualReport/annualReport_ccDimensions.ftl:32:  [#if !reportingActive]
main:crp/views/annualReport/annualReport_control.ftl:36:  [#if !reportingActive]
main:crp/views/annualReport/annualReport_crossPartnerships.ftl:33:  [#if !reportingActive]
main:crp/views/annualReport/annualReport_crpProgress.ftl:33:  [#if !reportingActive]
main:crp/views/annualReport/annualReport_efficiency.ftl:27:  [#if !reportingActive]
main:crp/views/annualReport/annualReport_externalPartnerships.ftl:32:  [#if !reportingActive]
main:crp/views/annualReport/annualReport_financial.ftl:27:  [#if !reportingActive]
main:crp/views/annualReport/annualReport_flagshipProgress.ftl:30:  [#if !reportingActive]
main:crp/views/annualReport/annualReport_fundingUse.ftl:27:  [#if !reportingActive]
main:crp/views/annualReport/annualReport_influence.ftl:36:  [#if !reportingActive]
main:crp/views/annualReport/annualReport_melia.ftl:34:  [#if !reportingActive]
main:crp/views/annualReport/annualReport_plannedVariance.ftl:27:  [#if !reportingActive]
main:crp/views/annualReport/annualReport_risks.ftl:26:  [#if !reportingActive]
main:crp/views/annualReport/annualReport_TEMPLATE.ftl:24:  [#if !reportingActive]
main:crp/views/annualReport/menu-annualReport.ftl:114:[#if false && (canUnSubmit && submission) && !crpClosed && !reportingActive]
main:crp/views/annualReport2018/AR2018_ccDimension.ftl:36:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_control.ftl:27:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_crpProgress.ftl:34:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_efficiency.ftl:27:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_financial.ftl:27:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_flagshipProgress.ftl:28:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_fundingUse.ftl:27:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_governance.ftl:27:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_influence.ftl:31:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_innovations.ftl:34:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_intellectualAssets.ftl:27:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_melia.ftl:32:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_narrative.ftl:27:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_oicr.ftl:32:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_outcomeMilestones.ftl:35:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_partnerships.ftl:35:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_policies.ftl:34:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_publications.ftl:34:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_risks.ftl:27:  [#if !reportingActive]
main:crp/views/annualReport2018/AR2018_TEMPLATE.ftl:27:  [#if !reportingActive]
main:crp/views/annualReport2018/menu-AR2018.ftl:112:[#if (canUnSubmit && submission) && !crpClosed && !reportingActive]
main:crp/views/powb/menu-powb.ftl:116:[#if (canUnSubmit && submission) && !crpClosed && !reportingActive]
main:crp/views/powb2019/menu-powb2019.ftl:126:[#if (canUnSubmit && submission) && !crpClosed && !reportingActive]
main:crp/views/projects/deliverableInfo.ftl:62:  <div class="${reportingActive?string('fieldFocus','simpleBox')}">
main:crp/views/projects/deliverableInfo.ftl:78:         	[#if reportingActive]
main:crp/views/projects/deliverableInfo.ftl:134:    [#if isDeliverableNew && editable && (reportingActive)]<i class="text-center">The status of this deliverable should be 'Com
main:crp/views/projects/deliverableInfo.ftl:198:  [#if action.hasSpecificities('crp_lp6_active') && reportingActive && ((action.getProjectLp6ContributionValue(project.id, actu
main:crp/views/projects/deliverableList.ftl:44:    <p class="col-md-10">[#if reportingActive] [@s.text name="project.deliverableList.help2" /] [#else] [@s.text name="project.d
main:crp/views/projects/deliverableList.ftl:74:      <p class="alertText">[#if reportingActive] [@s.text name="project.deliverableList.help2" /] [#else] [@s.text name="project
main:crp/views/projects/deliverableList.ftl:121:          [#if (reportingActive || actualPhase.upkeep) && action.hasSpecificities("crp_has_disemination")]
main:crp/views/projects/deliverableList.ftl:170:            [#if reportingActive]
main:crp/views/projects/deliverableList.ftl:188:              [@deliverableList.deliverablesList deliverables=(currentDeliverableList)![] canValidate=true canEdit=candit  isRe
main:crp/views/projects/deliverableList.ftl:191:                [@deliverableList.deliverablesList deliverables=(project.getCurrentDeliverables(actualPhase))![] canValidate=tr
main:crp/views/projects/deliverableList.ftl:226:              [@deliverableList.deliverablesList deliverables=(project.getPreviousDeliverables(actualPhase) + previousSharedDel
main:crp/views/projects/menu-projects.ftl:23:      [#--  { 'slug': 'budgetByFlagships',  'name': 'projects.menu.budgetByFlagships',  'action': 'budgetByFlagship',  'active': t
main:crp/views/projects/menu-projects.ftl:45:      { 'slug': 'contributionsLP6',  'name': 'projects.menu.contributionLP6',  'action': 'contributionsLP6',  'active': true, 'sho
main:crp/views/projects/menu-projects.ftl:49:      { 'slug': 'projectStudies',  'name': 'projects.menu.expectedStudies',  'action': 'studies',  'active': true, 'show': !center
main:crp/views/projects/menu-projects.ftl:50:      { 'slug': 'projectStudies',  'name': 'projects.menu.studies',           'action': 'studies',  'active': true, 'show': report
main:crp/views/projects/menu-projects.ftl:57:      { 'slug': 'innovations',  'name': 'projects.menu.innovations',  'action': 'innovationsList',  'active': action.hasSpecificit
main:crp/views/projects/menu-projects.ftl:70:      { 'slug': 'leverages',  'name': 'Leverages',  'action': 'leverages',  'active': true, 'show': reportingActive && action.hasS
main:crp/views/projects/menu-projects.ftl:73:    { 'title': 'Safeguards', 'show':(UpKeepActive || reportingActive) && !project.projectInfo.administrative && (isCountryCluster 
main:crp/views/projects/menu-projects.ftl:75:      { 'slug': 'safeguards',  'name': 'projects.menu.safeguards',  'action': 'safeguards',  'active': true  ,'show': (UpKeepActiv
main:crp/views/projects/menu-projects.ftl:190:  [#if !reportingActive && canSwitchProject && ( completedPreProject || projectEditLeader) && !crpClosed && !centerGlobalUnit]
main:crp/views/projects/menu-projects.ftl:221:    [#assign enableUnsubmitButton = !upKeepActive ]
main:crp/views/projects/projectActivities.ftl:46:    <p class="col-md-10">[#if reportingActive] [@s.text name="project.activities.help2" /] [#else] [@s.text name="project.acti
main:crp/views/projects/projectActivities.ftl:61:        [#if reportingActive] [@s.text name="project.activities.help2" /] [#else] [@s.text name="project.activities.help1" /] 
main:crp/views/projects/projectActivities.ftl:241:      <div class="row activityStatusBlock ${reportingActive?string('fieldFocus','')}">
main:crp/views/projects/projectActivities.ftl:247:      [#if reportingActive]
main:crp/views/projects/projectBudgetByCoAs.ftl:225:    [#if reportingActive]
main:crp/views/projects/projectBudgetByCoAs.ftl:239:    [#if reportingActive]
main:crp/views/projects/projectBudgetByFlagships.ftl:84:                <li class="[#if year == selectedYear]active[/#if]"><a href="#year-${year}" role="tab" data-toggle="tab"
main:crp/views/projects/projectBudgetByFlagships.ftl:243:    [#if reportingActive]
main:crp/views/projects/projectBudgetByFlagships.ftl:257:    [#if reportingActive]
main:crp/views/projects/projectBudgetByPartners.ftl:31:[#if !reportingActive]
main:crp/views/projects/projectBudgetByPartners.ftl:92:                <li class="[#if year == selectedYear]active[/#if]"><a href="#year-${year}" role="tab" data-toggle="tab">
main:crp/views/projects/projectBudgetByPartners.ftl:122:                        [#if (reportingActive)  && action.hasSpecificities(crpEnableBudgetExecution)]
main:crp/views/projects/projectBudgetByPartners.ftl:147:            [#if true || !reportingActive]
main:crp/views/projects/projectBudgetByPartners.ftl:238:          [#if (reportingActive)  && action.hasSpecificities(crpEnableBudgetExecution)]
main:crp/views/projects/projectBudgetByPartners.ftl:541:    [#if reportingActive]
main:crp/views/projects/projectBudgetByPartners.ftl:555:    [#if reportingActive]
main:crp/views/projects/projectCcafsOutcomes.ftl:106:                          [#assign isTargetValueRequired = isYearRequired(year) && (action.hasPermission("target") || (!pr
main:crp/views/projects/projectCcafsOutcomes.ftl:107:                          [#assign isTargetValueEditable = editable && (currentCycleYear lte year) && action.hasPermission
main:crp/views/projects/projectCcafsOutcomes.ftl:129:                        [#if (currentCycleYear lte year) && reportingActive]
main:crp/views/projects/projectCcafsOutcomes.ftl:137:                        [#if reportingActive && (year == currentCycleYear)]
main:crp/views/projects/projectCcafsOutcomes.ftl:152:                          <label>[@s.text name="projectCcafsOutcomes.targetNarrative" /]:[@customForm.req required=isYearR
main:crp/views/projects/projectCcafsOutcomes.ftl:153:                          [#if editable && (currentCycleYear lte year) && action.hasPermission("description") && !reportin
main:crp/views/projects/projectCcafsOutcomes.ftl:167:                      [#if reportingActive && (year == currentCycleYear)]
main:crp/views/projects/projectCcafsOutcomes.ftl:189:                          <label>[@s.text name="projectCcafsOutcomes.targetGender" /]:[@customForm.req required=isYearRequ
main:crp/views/projects/projectCcafsOutcomes.ftl:190:                          [#if editable && (currentCycleYear lte year) && action.hasPermission("gender") && !reportingActi
main:crp/views/projects/projectCcafsOutcomes.ftl:204:                      [#if reportingActive && (year == currentCycleYear)]
main:crp/views/projects/projectCcafsOutcomes.ftl:265:    [#if reportingActive]
main:crp/views/projects/projectContributionCrp.ftl:200:                  [#if !action.isPOWB() && ((cpiOutcome.file.fileName??)!false)]
main:crp/views/projects/projectContributionCrp.ftl:218:              [@customForm.input name="projectOutcome.expectedValue" i18nkey="projectOutcome.expectedValueAF" paramText=
main:crp/views/projects/projectContributionCrp.ftl:221:              [@customForm.textArea name="projectOutcome.narrativeTarget" required=true className="limitWords-150" edita
main:crp/views/projects/projectContributionCrp.ftl:310:          [#assign cpiAnswerQuestions = reportingActive /]
main:crp/views/projects/projectContributionCrp.ftl:325:          [#if reportingActive && action.hasSpecificities('crp_show_project_outcome_communications') ]
main:crp/views/projects/projectContributionCrp.ftl:356:            [#if editable && !reportingActive]
main:crp/views/projects/projectContributionCrp.ftl:366:          [#if !action.isProjectNew(project.id) && action.isReportingActive() && action.isLessonsActive()]
main:crp/views/projects/projectContributionCrp.ftl:367:          [#assign lessonsCycle = reportingActive?string('reporting','planning') /]
main:crp/views/projects/projectContributionCrp.ftl:373:              <span class="cpi-field__label"><label class="readOnly">[@customForm.text name="projectOutcome.previousLess
main:crp/views/projects/projectContributionCrp.ftl:383:              [@customForm.textArea name="projectOutcome.projectComponentLesson.lessons" i18nkey="projectOutcome.lessons
main:crp/views/projects/projectContributionCrp.ftl:559:  [#local isUpkeepCycle = (action.isUpKeepActive())!false /]
main:crp/views/projects/projectContributionCrp.ftl:560:  [#local isPlanningCycle = (action.isPlanningActive())!false /]
main:crp/views/projects/projectContributionCrp.ftl:570:  [#local achievedPhase = reportingActive || isUpkeepCycle /]
main:crp/views/projects/projectContributionCrp.ftl:578:  [#local canExpected = editable && !reportingActive && isCurrentPeriod /]
main:crp/views/projects/projectContributionCrp.ftl:579:  [#local canNarrative = editable && !reportingActive && isCurrentPeriod /]
main:crp/views/projects/projectContributionCrp.ftl:584:  [#local achievedRequired = isCurrentPeriod && isAiccraRules?then(reportingActive, achievedPhase) /]
main:crp/views/projects/projectContributionCrp.ftl:621:          <div class="cpi-field ${canExpected?string('is-edit', (reportingActive || !isCurrentPeriod)?string('is-read','
main:crp/views/projects/projectContributionCrp.ftl:627:            [@customForm.input name="${customName}.achievedValue" i18nkey="projectOutcomeMilestone.achievedValue" type="
main:crp/views/projects/projectContributionCrp.ftl:649:      [@customForm.textArea name="${customName}.narrativeAchieved" i18nkey="projectOutcomeMilestone.achievedNarrative" r
main:crp/views/projects/projectContributionCrp.ftl:671:  [#local canTitle = editable && (!reportingActive || (!element.nextUser?has_content)!true) /]
main:crp/views/projects/projectContributionCrp.ftl:672:  [#local canKnowledge = editable && (!reportingActive || (!element.knowledge?has_content)!true) /]
main:crp/views/projects/projectContributionCrp.ftl:673:  [#local canStrategies = editable && (!reportingActive || (!element.strategies?has_content)!true) /]
main:crp/views/projects/projectContributionCrp.ftl:678:      [#if editable && !reportingActive]
main:crp/views/projects/projectContributionCrp.ftl:702:    [#if reportingActive]
main:crp/views/projects/projectContributionCrp.ftl:740:        [@customForm.input name="${customName}.value" i18nkey="projectOutcomeBaseline.expectedValue" className="targetVa
main:crp/views/projects/projectContributionCrp.ftl:743:        [#if reportingActive]
main:crp/views/projects/projectContributionCrp.ftl:750:      [@customForm.textArea name="${customName}.narrative" i18nkey="projectOutcomeBaseline.expectedNarrative" value="${(
main:crp/views/projects/projectContributionCrp.ftl:752:    [#if reportingActive]
main:crp/views/projects/projectContributionCrp.ftl:778:          [@customForm.input name="${customName}.value" i18nkey="projectOutcomeBaseline.expectedValue" className="target
main:crp/views/projects/projectContributionCrp.ftl:781:          [#if reportingActive]
main:crp/views/projects/projectContributionCrp.ftl:792:        [#-- && !reportingActive  --]
main:crp/views/projects/projectContributionCrp.ftl:795:      [#if reportingActive]
main:crp/views/projects/projectContributionCrp.ftl:1116:    [#if reportingActive]
main:crp/views/projects/projectContributionsCrpList.ftl:95:        [#if reportingActive && canEdit] <p class="note">[@s.text name="projectContributionsCrpList.reportingHelp"/]
main:crp/views/projects/projectContributionsCrpList.ftl:425:        [#if reportingActive && !isThisComplete]
main:crp/views/projects/projectContributionsCrpList.ftl:485:    <div class="form-group ${reportingActive?string('fieldFocus','')}">
main:crp/views/projects/projectContributionsCrpList.ftl:497:        [@customForm.textArea name="${customName}.description" className="otherContributionDescription limitWords-1
main:crp/views/projects/projectContributionsCrpList.ftl:506:          [@customForm.input name="${customName}.target" className="otherContributionTarget ${reportingActive?strin
main:crp/views/projects/projectContributionsCrpList.ftl:514:[#if action.hasSpecificities('crp_lp6_active') && reportingActive]
main:crp/views/projects/projectContributionsCrpList.ftl:547:    [#if reportingActive]
main:crp/views/projects/projectDeliverable.ftl:53:    <p class="col-md-10">[#if reportingActive] [@s.text name="project.deliverable.help2" /] [#else] [@s.text name="project.de
main:crp/views/projects/projectDeliverable.ftl:68:      <p class="alertText">[#if reportingActive] [@s.text name="project.deliverable.help2" /] [#else] [@s.text name="project.
main:crp/views/projects/projectDeliverable.ftl:280:                [#if (reportingActive || actualPhase.upkeep) && action.hasSpecificities("crp_has_disemination") ]
main:crp/views/projects/projectDeliverable.ftl:299:              [#if (reportingActive || actualPhase.upkeep) && action.hasSpecificities("crp_has_disemination") ]
main:crp/views/projects/projectDeliverable.ftl:467:[#if reportingActive || upKeepActive]
main:crp/views/projects/projectDescription.ftl:47:        [#if (reportingActive)!false]
main:crp/views/projects/projectDescription.ftl:71:          [#if (reportingActive)!false]
main:crp/views/projects/projectDescription.ftl:195:            <div class="form-group ${reportingActive?string('fieldFocus','')}">
main:crp/views/projects/projectHighlightsList.ftl:100:              [#if reportingActive && ((hl.projectHighlightInfo.year == currentCycleYear)!false)]
main:crp/views/projects/projectInnovation.ftl:113:        [#assign isProgressActive = action.isProgressActive() /]
main:crp/views/projects/projectLocations.ftl:37:    <p class="col-md-10">[#if reportingActive] [@s.text name="projectLocations.help2" /] [#else] [@s.text name="projectLocation
main:crp/views/projects/projectLocations.ftl:42:[#if !reportingActive]
main:crp/views/projects/projectLocations.ftl:53:        <!--  [#if reportingActive] 
main:crp/views/projects/projectLocations.ftl:164:                [#if reportingActive && action.hasSpecificities("crp_location_csv_activities")]
main:crp/views/projects/projectOtherContributions.ftl:66:            [#if reportingActive]
main:crp/views/projects/projectOutcomes.ftl:85:                [#if reportingActive && (year == currentCycleYear) ]
main:crp/views/projects/projectOutcomes.ftl:133:               <label>[@customForm.text name="projectPartners.previousLessons.${reportingActive?string('reporting','planning')}
main:crp/views/projects/projectOutcomes.ftl:143:                    [@customForm.textArea name="project.projectComponentLesson.lessons" i18nkey="projectOutcomes.lessons.${repo
main:crp/views/projects/projectOutcomes.ftl:168:    [#if reportingActive]
main:crp/views/projects/projectOverviewByMogs.ftl:77:                    <label>[@customForm.text name="planning.project.overviewByMogs.expectedBulletPoints" readText=reportin
main:crp/views/projects/projectOverviewByMogs.ftl:78:                      [@customForm.textArea name="project.overviews[${overviewMogIndex}].anualContribution"  showTitle=fal
main:crp/views/projects/projectOverviewByMogs.ftl:83:                     <label>[@customForm.text name="reporting.project.overviewByMogs.summaryAnnualContribution" readText=!
main:crp/views/projects/projectOverviewByMogs.ftl:84:                      [@customForm.textArea name="project.overviews[${overviewMogIndex}].briefSummary" showTitle=false  ed
main:crp/views/projects/projectOverviewByMogs.ftl:89:                      <label>[@customForm.text name="planning.project.overviewByMogs.expectedSocialAndGenderPlan" readText
main:crp/views/projects/projectOverviewByMogs.ftl:90:                      [@customForm.textArea name="project.overviews[${overviewMogIndex}].genderContribution" showTitle=fal
main:crp/views/projects/projectOverviewByMogs.ftl:95:                      <label>[@customForm.text name="reporting.project.overviewByMogs.summarySocialInclusionDimmension" re
main:crp/views/projects/projectOverviewByMogs.ftl:96:                      [@customForm.textArea name="project.overviews[${overviewMogIndex}].summaryGender" showTitle=false ed
main:crp/views/projects/projectPartners.ftl:83:                [#if projectEditLeader][#if reportingActive][@s.text name="projectPartners.help3" /][#else][@s.text name="projec
main:crp/views/projects/projectsList.ftl:61:          [@s.param][@s.text name="projectsList.active${reportingActive?string('Reporting', 'Planning')}.help" /][/@s.param]
main:crp/views/projects/projectsList.ftl:67:    [#if action.canAddCoreProject() && (!crpClosed) && !reportingActive && phaseEditable]
main:crp/views/projects/projectsList.ftl:118:  [#if !reportingActive]
main:crp/views/projects/projectStudies.ftl:175:              [#if reportingActive && !oldFormat && (item.projectExpectedStudyInfo.year == actualPhase.year) && isOwner && !isTh
main:crp/views/projects/projectStudy.ftl:42:[#assign isOutcomeCaseStudy = ((expectedStudy.projectExpectedStudyInfo.studyType.id == 1)!false) && reportingActive/]
main:crp/views/publications/publication.ftl:68:          [#if (reportingActive || actualPhase.upkeep) && action.hasSpecificities("crp_has_disemination") ]
main:crp/views/publications/publicationsList.ftl:24:  [#if reportingActive]
main:global/macros/deliverableListTemplate.ftl:4:[#macro deliverablesList deliverables={} owned=true canValidate=false canEdit=false isReportingActive=false namespace="/" defa
main:global/macros/deliverableListTemplate.ftl:14:        [#if isReportingActive]
main:global/macros/deliverableListTemplate.ftl:51:        [#local toReport = reportingActive && (deliverable.deliverableInfo.isRequiredToBeReported())]
main:global/macros/deliverableListTemplate.ftl:121:          [#if isReportingActive]
main:global/macros/deliverableListTemplate.ftl:206:[#macro deliverablesSummaryList deliverables={} owned=true canValidate=false canEdit=false isReportingActive=false namespace
main:global/macros/deliverableListTemplate.ftl:225:        [#local toReport = reportingActive && (deliverable.deliverableInfo.isRequiredToBeReported()) ]
main:global/macros/deliverableListTemplate.ftl:272:[#macro deliverablesListExtended deliverables={} owned=true canValidate=false canEdit=false isReportingActive=false FAIRColu
main:global/macros/deliverableListTemplate.ftl:280:        [#if isReportingActive || FAIRColumn]
main:global/macros/deliverableListTemplate.ftl:301:        [#local toReport = reportingActive && (deliverable.deliverableInfo.isRequiredToBeReported()) ]
main:global/macros/deliverableListTemplate.ftl:350:          [#if isReportingActive || FAIRColumn]
main:global/macros/deliverableMacros.ftl:384:              [@customForm.text name="involveParticipants.sharedClusters.participants" /][@customForm.req required=(reportingActiv
main:global/macros/deliverableMacros.ftl:390:              [@customForm.text name="involveParticipants.sharedClusters.females" /][@customForm.req required=(reportingActive && 
main:global/macros/deliverableMacros.ftl:396:              [@customForm.text name="involveParticipants.sharedClusters.africans" /][@customForm.req required=(reportingActive &&
main:global/macros/deliverableMacros.ftl:402:              [@customForm.text name="involveParticipants.sharedClusters.youth" /][@customForm.req required=(reportingActive && va
main:global/macros/deliverableMacros.ftl:584:        <label for="">[@s.text name="project.deliverable.dissemination.confidential" /]? [@customForm.req required=(reportingActiv
main:global/macros/deliverableMacros.ftl:1542:      [#if reportingActive]
main:global/macros/deliverableMacros.ftl:1558:      [#if reportingActive]
main:global/macros/forms.ftl:1256:    <span class="pull-left" style="width:42%">[@customForm.input name="${customName}.reference" placeholder="expectedStudy.reference.placehol
main:global/macros/forms.ftl:1257:    <span class="pull-left" style="width:42%; margin-left: 10px">[@customForm.input name="${customName}.link" placeholder="global.webSiteLink
main:global/macros/homeDashboard.ftl:4:[#macro deliverablesHomeList deliverables={} owned=true canValidate=false canEdit=false isReportingActive=false namespace="/clusters" de
main:global/macros/homeDashboard.ftl:26:        [#local toReport = reportingActive && !isDeliverableComplete ]
main:global/macros/homeDashboard.ftl:84:[#macro studiesHomeList studies={} owned=true canValidate=false canEdit=false isReportingActive=false namespace="/clusters" defaultActi
main:global/macros/homeDashboard.ftl:105:        [#local toReport = reportingActive && !isThisComplete ]
main:global/macros/homeDashboard.ftl:161:[#macro innovationsHomeList innovations={} owned=true canValidate=false canEdit=false isReportingActive=false namespace="/clusters" de
main:global/macros/homeDashboard.ftl:183:        [#local toReport = reportingActive && !isThisComplete ]
main:global/macros/homeDashboard.ftl:245:[#macro policiesHomeList policies={} owned=true canValidate=false canEdit=false isReportingActive=false namespace="/clusters" defaultA
main:global/macros/homeDashboard.ftl:266:        [#local toReport = reportingActive && !isThisComplete ]
main:global/macros/innovationTemplates.ftl:136:        [#local isProgressActive = action.isProgressActive() ]
main:global/macros/innovationTemplates.ftl:192:                [@customForm.select name="innovation.projectInnovationInfo.repIndStageInnovation.id" label=""  i18nkey="projectI
main:global/macros/innovationTemplates.ftl:209:                  [@customForm.input name="innovation.projectInnovationInfo.otherInnovationNature"  type="text" i18nkey="project
main:global/macros/innovationTemplates.ftl:279:                  [@customForm.input name="innovation.projectInnovationInfo.otherInnovationType"  type="text" i18nkey="projectIn
main:global/macros/innovationTemplates.ftl:308:                    [@customForm.elementsListComponent name="innovation.geographicScopes" elementType="repIndGeographicScope" el
main:global/macros/innovationTemplates.ftl:315:                  [@customForm.select name="innovation.countriesIds" label="" i18nkey="projectInnovations.countries" listName="c
main:global/macros/innovationTemplates.ftl:331:              [@customForm.labelText name="projectInnovations.geographicScope" text="projectInnovations.geographicScope" require
main:global/macros/innovationTemplates.ftl:354:                    [#-- [@customForm.elementsListComponent name="innovation.geographicScopes" elementType="repIndGeographicScop
main:global/macros/innovationTemplates.ftl:363:                    [@customForm.select name="innovation.countriesIds" label="" i18nkey="projectInnovations.country" listName="c
main:global/macros/innovationTemplates.ftl:383:                      [@customForm.select name="innovation.countriesIds" label="" i18nkey="projectInnovations.countries" listNam
main:global/macros/innovationTemplates.ftl:406:                [@customForm.elementsListComponent name="innovation.centers" i18nkey="innovation.centers" elementType="instituti
main:global/macros/innovationTemplates.ftl:426:              [@customForm.textArea name="innovation.projectInnovationInfo.descriptionStage" i18nkey="projectInnovations.stageDe
main:global/macros/innovationTemplates.ftl:439:                [@customForm.select name="innovation.projectInnovationInfo.leadOrganization.id" label=""  i18nkey="projectInnova
main:global/macros/innovationTemplates.ftl:452:              [@customForm.elementsListComponent name="innovation.studies" elementType="projectExpectedStudy" elementList=innova
main:global/macros/innovationTemplates.ftl:457:              [@customForm.input name="innovation.projectInnovationInfo.evidenceLink"  type="text" i18nkey="projectInnovations.e
main:global/macros/innovationTemplates.ftl:468:              [@customForm.elementsListComponent name="innovation.crpOutcomes" elementType="crpOutcome" elementList=(innovation.
main:global/macros/innovationTemplates.ftl:494:              [@customForm.elementsListComponent name="innovation.subIdos" elementType="srfSubIdo" elementList=(innovation.subId
main:global/macros/studiesTemplates.ftl:79:        [@customForm.input name="${customName}.projectExpectedStudyInfo.commissioningStudy" i18nkey="study.generalInformation.commis
main:global/macros/studiesTemplates.ftl:96:        <label for="${customName}.projectExpectedStudyInfo.repIndStageStudy.id" class="label--2">[@s.text name="study.generalInforma
main:global/macros/studiesTemplates.ftl:117:        <label for="" class="${isOutcomeCaseStudy?then('label--2','')}">[@s.text name="study.generalInformation.geographicScopeTopi
main:global/macros/studiesTemplates.ftl:128:              <label for="${customName}.geographicScopes" class="col-md-4">[@s.text name="study.generalInformation.geographicScope"
main:global/macros/studiesTemplates.ftl:129:              <label for="" name="study.generalInformation.geographicImpact" class="col-md-8" style="display:${isDisplayTitleScope?
main:global/macros/studiesTemplates.ftl:135:              [@customForm.elementsListComponent name="${customName}.geographicScopes" elementType="repIndGeographicScope" elementL
main:global/macros/studiesTemplates.ftl:139:              [@customForm.select name="${customName}.countriesIds" label="" i18nkey="study.generalInformation.countries" listName=
main:global/macros/studiesTemplates.ftl:160:            <label for="">[@s.text name="study.generalInformation.reportingIndicatorThree" /]:[@customForm.req required=(editable &
main:global/macros/studiesTemplates.ftl:182:          <label for="" class="label--2">[@s.text name="study.generalInformation.${isOutcomeCaseStudy?string('keyContributors','key
main:global/macros/studiesTemplates.ftl:208:            [@customForm.elementsListComponent name="${customName}.centers" elementType="institution" elementList=element.centers l
main:global/macros/studiesTemplates.ftl:243:        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.cgiarInnovation" i18nkey="study.generalInformation.innov
main:global/macros/studiesTemplates.ftl:255:            <label for="" class="label--2">[@s.text name="study.generalInformation.stratgicResultsLink" /]:[@customForm.req require
main:global/macros/studiesTemplates.ftl:259:            <label for="">[@s.text name="study.generalInformation.relevantTo" /]:[@customForm.req required=(editable && !action.isP
main:global/macros/studiesTemplates.ftl:276:              <label for="">[@s.text name="study.generalInformation.targetsOption" /]:[@customForm.req required=(editable && !actio
main:global/macros/studiesTemplates.ftl:291:              [@customForm.elementsListComponent name="${customName}.srfTargets" elementType="srfSloIndicator" elementList=element.
main:global/macros/studiesTemplates.ftl:307:          <label for="" class="${isOutcomeCaseStudy?then('label--2','')}">[@s.text name="study.generalInformation.outcomes" /]:[@cu
main:global/macros/studiesTemplates.ftl:321:          <label for="" class="label--2">[@s.text name="study.generalInformation.outcomes" /]:[@customForm.req required=(editable &
main:global/macros/studiesTemplates.ftl:337:          [@customForm.elementsListComponent name="${customName}.projectOutcomes" elementType="projectOutcome" elementList=(element
main:global/macros/studiesTemplates.ftl:344:          [@customForm.elementsListComponent name="${customName}.crpOutcomes" elementType="crpOutcome" elementList=(element.crpOutc
main:global/macros/studiesTemplates.ftl:372:        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.elaborationOutcomeImpactStatement" i18nkey="study.genera
main:global/macros/studiesTemplates.ftl:398:            [@customForm.textAreaReferences name="${customName}.projectExpectedStudyInfo.referencesText" i18nkey="study.generalInfo
main:global/macros/studiesTemplates.ftl:402:            [@customForm.textAreaReferences name="${customName}.projectExpectedStudyInfo.referencesText" i18nkey="study.generalInfo
main:global/macros/studiesTemplates.ftl:410:                  <div class="col-sm-6 colTitleCenter" style="font-weight: 600; text-align: center;">Reference[@customForm.req requ
main:global/macros/studiesTemplates.ftl:411:                  <div class="col-sm-4 colTitleCenter" style="font-weight: 600; text-align: center;">URL[@customForm.req required=(
main:global/macros/studiesTemplates.ftl:455:        [@customForm.labelText name="expectedStudy.projectExpectedStudyInfo.quantification" text="study.generalInformation.quantifi
main:global/macros/studiesTemplates.ftl:484:        [@customForm.labelText name="expectedStudy.projectExpectedStudyInfo.crossCuttingRelevance" text="study.generalInformation.c
main:global/macros/studiesTemplates.ftl:489:            <label for="">[@s.text name="study.generalInformation.genderRelevance" /]:[@customForm.req required=(editable && report
main:global/macros/studiesTemplates.ftl:492:              [@customForm.select name="${customName}.projectExpectedStudyInfo.genderLevel.id" label="" listName="focusLevels" keyF
main:global/macros/studiesTemplates.ftl:497:            <label for="">[@s.text name="study.generalInformation.youthRelevance" /]:[@customForm.req required=(editable && reporti
main:global/macros/studiesTemplates.ftl:500:              [@customForm.select name="${customName}.projectExpectedStudyInfo.youthLevel.id" label="" listName="focusLevels" keyFi
main:global/macros/studiesTemplates.ftl:505:            <label for="">[@s.text name="study.generalInformation.capDevRelevance" /]:[@customForm.req required=(editable && report
main:global/macros/studiesTemplates.ftl:508:              [@customForm.select name="${customName}.projectExpectedStudyInfo.capdevLevel.id" label="" listName="focusLevels" keyF
main:global/macros/studiesTemplates.ftl:514:            <label for="">[@s.text name="study.generalInformation.climateChangeRelevance" /]:[@customForm.req required=(editable &&
main:global/macros/studiesTemplates.ftl:517:              [@customForm.select name="${customName}.projectExpectedStudyInfo.climateChangeLevel.id" label="" listName="focusLevel
main:global/macros/studiesTemplates.ftl:531:        <label for="" class="label--2">[@s.text name="study.generalInformation.otherCrossCutting" /]:[@customForm.req required=(edi
main:global/macros/studiesTemplates.ftl:575:        [@customForm.textArea name="${customName}.projectExpectedStudyInfo.topLevelComments" i18nkey="study.generalInformation.acti
main:global/macros/studiesTemplates.ftl:955:      <label class="label--2" for=""><b>[@s.text name="study.allianceAligment.linkToLevers.title" /]</b>[@customForm.req required=r
main:global/macros/studiesTemplates.ftl:959:        [@customForm.selectableCheckToCheckboxMacro label="study.allianceAligment.linkToLevers.options.primaryLever" name="${custom
main:global/macros/studiesTemplates.ftl:1044:        [@customForm.select name="${customName}.quantificationType.id" value="${(element.quantificationType.id)!-1}" className="se
main:global/macros/studiesTemplates.ftl:1048:        [@customForm.input name="${customName}.number" type="number" i18nkey="study.quantification.number" className="numericInput
main:global/macros/studiesTemplates.ftl:1052:        [@customForm.input name="${customName}.targetUnit" i18nkey="study.quantification.targetUnit" className="" required=(editab
main:global/macros/studiesTemplates.ftl:1057:      [@customForm.textArea name="${customName}.comments" i18nkey="study.quantification.comments" help="study.quantification.comme
main:global/pages/guide-button.ftl:24:    [#if reportingActive?exists && reportingActive]
main:global/pages/guide-button.ftl:91:    [#if POWB?exists && POWB ]
main:global/pages/guide-button.ftl:153:    [#if UpKeepActive?exists &&  UpKeepActive]
main:global/pages/guide-button.ftl:222:      [#if reportingActive?exists && reportingActive]
main:global/pages/guide-button.ftl:225:      [#if POWB?exists && POWB ]
main:global/pages/guide-button.ftl:228:      [#if UpKeepActive?exists &&  UpKeepActive]
main:global/pages/javascript-constants.ftl:30:  var reportingActive= ${((reportingActive)!false)?string};
main:global/pages/javascript-constants.ftl:31:  var upKeepActive= ${((upKeepActive)!false)?string};
main:global/pages/main-menu.ftl:4:[#assign reportingActiveMenu = (reportingActive)!false ]
main:global/pages/main-menu.ftl:48:  { 'slug': 'additionalReporting', 'name': 'menu.additionalReporting',      'namespace': '/publications',       'action': '${(crpSession)!}/
main:global/pages/main-menu.ftl:55:  { 'slug': 'synthesis', 'name': 'menu.synthesis',      'namespace': '/powb${powb2019?string("2019", "")}',   'action': '${(crpSession)!}/ad
main:global/pages/main-menu.ftl:61:  { 'slug': 'synthesis', 'name': 'menu.synthesis',      'namespace': '/${reportingDefaultNamespace}',       'action': '${(crpSession)!}/${re
main:global/pages/replicateButton.ftl:3:[#if reportingActive && false]
main:interceptor/annualReport/CanEditReportSynthesisInterceptor.java:114:    if (baseAction.isReportingActive()) {
main:interceptor/annualReport/CanEditReportSynthesisInterceptor.java:165:          if (baseAction.isReportingActive()) {
main:interceptor/powb/CanEditPowbSynthesisInterceptor.java:112:    if (baseAction.isPlanningActive() && !baseAction.getActualPhase().getUpkeep()) {
main:interceptor/powb/CanEditPowbSynthesisInterceptor.java:165:          if (baseAction.isPlanningActive()) {
main:interceptor/project/EditDeliverableInterceptor.java:307:      if (!baseAction.isUpKeepActive() && baseAction
main:interceptor/project/EditDeliverableInterceptor.java:317:        if ((baseAction.isReportingActive() || baseAction.isUpKeepActive())
main:interceptor/project/EditDeliverableInterceptor.java:326:        if ((baseAction.isReportingActive() || baseAction.isUpKeepActive())
main:interceptor/project/EditDeliverableInterceptor.java:350:      if (baseAction.isPlanningActive() && !baseAction.isUpKeepActive()) {
main:interceptor/project/EditDeliverableInterceptor.java:370:      if (baseAction.isPlanningActive() && !baseAction.isUpKeepActive()) {
main:interceptor/project/EditProjectInterceptor.java:285:        if (baseAction.isReportingActive() && actionName.contains(ProjectSectionStatusEnum.BUDGET.getStatus())) {
main:interceptor/project/EditProjectLp6ContributionInterceptor.java:262:          if (baseAction.isReportingActive()
main:interceptor/project/EditProjectOutcomeInterceptor.java:353:      if (baseAction.isPlanningActive()) {
main:interceptor/synthesis/CanEditCrpIndicatorsInterceptor.java:128:        if (baseAction.isReportingActive()) {
main:interceptor/synthesis/CanEditSynthesisInterceptor.java:141:        if (baseAction.isReportingActive()) {
main:validation/projects/DeliverableValidator.java:173:    if (action.isPlanningActive()) {
main:validation/projects/DeliverableValidator.java:387:        if ((action.isReportingActive())) {
main:validation/projects/DeliverableValidator.java:389:          if (action.isReportingActive() && deliverable.getDeliverableInfo(action.getActualPhase()).getStatus() != null
main:validation/projects/DeliverableValidator.java:494:        if (!action.isPOWB()) {
main:validation/projects/DeliverableValidator.java:617:          if (action.isReportingActive()) {
main:validation/projects/DeliverableValidator.java:638:            if (action.isProgressActive() && deliverable.getShfrmPriorityActions() == null) {
main:validation/projects/DeliverableValidator.java:806:      if (!action.isPOWB()) {
main:validation/projects/DeliverableValidator.java:1325:      if (action.isProgressActive()
main:validation/projects/DeliverableValidator.java:1347:      if (action.isProgressActive()
main:validation/projects/ProjectActivitiesValidator.java:157:    if (activity == null || !action.isReportingActive()) {
main:validation/projects/ProjectActivitiesValidator.java:243:      if (action.isReportingActive()) {
main:validation/projects/ProjectActivitiesValidator.java:258:      if (action.isReportingActive()) {
test:validation/projects/ProjectActivitiesValidatorCancelledTest.java:46:      public boolean isReportingActive() {
main:validation/projects/ProjectBudgetsValidator.java:186:          if ((action.isReportingActive() || action.isUpKeepActive())
main:validation/projects/ProjectExpectedStudiesValidator.java:521:        if (action.isProgressActive() && projectExpectedStudy.getProjectExpectedStudyInfo().getStatus()
main:validation/projects/ProjectExpectedStudiesValidator.java:711:    // if (action.isAWPBActive()) {
main:validation/projects/ProjectExpectedStudiesValidator.java:755:    if (!action.isPOWB()) {
main:validation/projects/ProjectExpectedStudiesValidator.java:916:        if (baseAction.isReportingActive()) {
main:validation/projects/ProjectInnovationValidator.java:1052:      if (action.isProgressActive()) {
main:validation/projects/ProjectOutcomesPandRValidator.java:120:      if (action.isReportingActive()) {
main:validation/projects/ProjectOutcomeValidator.java:152:      // if (action.isPlanningActive()) {
main:validation/projects/ProjectOutcomeValidator.java:201:        if (!action.isReportingActive()) {
main:validation/projects/ProjectOutcomeValidator.java:210:        if (action.isReportingActive() && projectMilestone.getYear() == action.getActualPhase().getYear()) {
main:validation/projects/ProjectOutcomeValidator.java:257:    if (action.isReportingActive()) {
main:validation/projects/ProjectOutcomeValidator.java:304:      if (action.isPlanningActive()) {
main:validation/projects/ProjectOutcomeValidator.java:354:    if (action.isReportingActive()) {
main:validation/projects/ProjectOutcomeValidator.java:416:    if (action.isPlanningActive()) {
main:validation/projects/ProjectOutcomeValidator.java:431:    if (action.isReportingActive()) {
main:validation/projects/ProjectPartnersValidator.java:230:    if (action.isReportingActive()) {
main:validation/projects/ProjectPartnersValidator.java:349:      if (action.isReportingActive() && project.getProjecInfoPhase(action.getActualPhase()).isProjectEditLeader()) {
main:validation/projects/ProjectSectionValidator.java:1031:    if ((action.isReportingActive() || action.isUpKeepActive())
main:validation/projects/ProjectSectionValidator.java:1137:      if (action.isProgressActive()) {
main:validation/projects/ProjectSectionValidator.java:1321:        if (action.isReportingActive() || action.isUpKeepActive()) {
main:validation/projects/ProjectSectionValidator.java:1508:      if (action.isProgressActive()) {
main:validation/projects/SafeguardValidator.java:71:      && (action.isUpKeepActive() || action.isReportingActive()) && clusterTypeId != null
main:validation/sythesis/CrpIndicatorsValidator.java:112:    if (action.isReportingActive()) {
main:validation/sythesis/SynthesisByMogValidator.java:111:    if (action.isReportingActive()) {
main:validation/sythesis/SynthesisByOutcomeValidator.java:113:    if (action.isReportingActive()) {
```
### A2. FTL locals, assigns and macro parameters that share an old name (renamed with their callers, never by the property rule)

| Kind | Site | Rename |
|---|---|---|
| `[#assign isProgressActive = action.isProgressActive()]` | `projectInnovation.ftl:113` | local `isProgressPhase` = `action.isProgressPhase()` |
| `[#local isProgressActive = action.isProgressActive()]` and its reads (16 occurrences in the file, definition included) | `innovationTemplates.ftl:136` and reads | local `isProgressPhase` |
| macro parameter `isReportingActive=false` | `deliverableListTemplate.ftl:4, 206, 272` (reads `:14, 121, 280, 350`); `homeDashboard.ftl:4, 84, 161, 245` | parameter `isReportingPhase`, definition and every caller in one batch |
| caller argument `isReportingActive=reportingActive` | `deliverableList.ftl:188, 191, 226` and every other caller of those 7 macros | `isReportingPhase=reportingPhase` |

### A3. Excluded identifiers (not uses of the old methods; never rewritten)
```
main:struts-json.xml:74:		<action name="isProgressActive"
main:database/migrations/V2_6_0_20260930_0948__RemoveCrpPlanningAndReportingActiveParameters.sql:3:-- (BaseAction.isPlanningActive() and isReportingActive()), not by these fla
main:crp/js/projects/safeguards.js:116:    if(!reportingActive) {
main:crp/js/projects/deliverables/deliverableInfo.js:618:    showComponent(isStatusExtended(statusId) && upKeepActive, $newExpectedYearBlock);
main:crp/js/projects/deliverables/deliverableInfo.js:784:  if (reportingActive) {
main:crp/js/projects/deliverables/deliverableInfo.js:814:    if((isAdmin !== true) && (reportingActive === true)){
main:crp/js/projects/projectDescription.js:287:      if(!reportingActive){
main:crp/js/projects/projectStudy.js:607:  if(reportingActive) {
main:crp/js/impactPathway/outcomes.js:169:    if(!reportingActive) {
main:global/js/global.js:39:  // isProgressActive.do answers 403 without a session user, so pages without one skip it
main:global/js/global.js:210:        url: baseURL + '/isProgressActive.do',   
main:global/macros/... reportingActiveMenu (main-menu.ftl:4) — FTL variable name; only its right-hand side changes
main:action/json/global/ProgressActiveValidationAction.java — class name
```
JS: the 9 `.js` lines read the globals `reportingActive` / `upKeepActive` defined in `javascript-constants.ftl:30-31` (listed in A1); the JS names stay. `isProgressActive` in `struts-json.xml:74` and `global.js:39, 210` is an action name and its URL. The `.sql` hit is a comment in an applied migration.

## B. Phase-name literals that decide a type (non-REST)
```
main:crp/views/projects/projectContributionCrp.ftl:539:       MARLO has no "Progress" phase description: the mid-year cycle is a
main:crp/views/admin/crpPhases.ftl:227:                        [#assign canOpenClose = ((phase.year > yearLimit) && !((phase.year == (yearLimit + 1)) && (phase.name == "P
main:global/macros/deliverableMacros.ftl:367:  [#if actualPhase.name != 'Progress']
main:global/macros/deliverableMacros.ftl:411:          [#assign isSharedCluster = (action.isSubmit(cluster.project.id)) && (actualPhase.name != 'Progress') /]
main:config/APConstants.java:379:  public static final String PROJECT_INDICATOR_PHASE_PREVIOUS_NAME = "AR";
main:config/APConstants.java:701:  public static final String POWB_ACRONYM = "AWPB";
main:action/BaseAction.java:3966:          && c.getPhase().getName().equals(APConstants.PROJECT_INDICATOR_PHASE_PREVIOUS_NAME)
main:action/BaseAction.java:3982:                && c.getPhase().getName().equals(APConstants.PROJECT_INDICATOR_PHASE_PREVIOUS_NAME)
main:action/BaseAction.java:4230:        && c.getPhase().getName().equals(APConstants.PROJECT_INDICATOR_PHASE_PREVIOUS_NAME)
main:action/BaseAction.java:4243:            && c.getPhase().getName().equals(APConstants.PROJECT_INDICATOR_PHASE_PREVIOUS_NAME)
main:action/BaseAction.java:4584:          && c.getPhase().getName().equals(APConstants.PROJECT_INDICATOR_PHASE_PREVIOUS_NAME)
main:action/BaseAction.java:4597:            && c.getPhase().getName().equals(APConstants.PROJECT_INDICATOR_PHASE_PREVIOUS_NAME)
main:action/BaseAction.java:6349:      && (this.getActualPhase().getName().equals("POWB")
main:action/BaseAction.java:6350:        || this.getActualPhase().getName().equals(APConstants.POWB_ACRONYM))) {
main:action/BaseAction.java:7741:      && (this.getActualPhase().getName().equals("POWB")
main:action/BaseAction.java:7742:        || this.getActualPhase().getName().equals(APConstants.POWB_ACRONYM))) {
main:action/annualReport/y2018/OutcomesMilestonesAction.java:427:        && ((!StringUtils.containsIgnoreCase(current.getName(), "POWB"))
main:action/annualReport/y2018/OutcomesMilestonesAction.java:428:          && (!StringUtils.containsIgnoreCase(current.getName(), APConstants.POWB_ACRONYM)))
main:action/summaries/FundingSourcesSummaryAction.java:498:            || ((this.getSelectedPhase().getName().equals("UpKeep")
main:action/summaries/FundingSourcesSummaryAction.java:499:              || this.getSelectedPhase().getName().equals("Progress"))
main:action/summaries/FundingSourcesSummaryAction.java:697:                  && (this.getSelectedPhase().getName().equals("UpKeep")
main:action/summaries/FundingSourcesSummaryAction.java:698:                    || this.getSelectedPhase().getName().equals("Progress"))
main:action/summaries/FundingSourcesSummaryAction.java:1004:            && (this.getSelectedPhase().getName().equals("UpKeep")
main:action/summaries/FundingSourcesSummaryAction.java:1005:              || this.getSelectedPhase().getName().equals("Progress"))
main:action/summaries/ProjectsFundingSourcesSummaryAction.java:611:            && (this.getSelectedPhase().getName().equals("UpKeep")
main:action/summaries/ProjectsFundingSourcesSummaryAction.java:612:              || this.getSelectedPhase().getName().equals("Progress"))
main:action/summaries/ExpectedDeliverablesSummaryAction.java:354:                && ((this.getSelectedPhase().getName() != null && this.getSelectedPhase().getName().equal
main:action/summaries/ExpectedDeliverablesSummaryAction.java:356:                    && this.getSelectedPhase().getName().equals("Progress"))))))) {
main:validation/projects/ProjectOutcomeValidator.java:193:          && (!action.getActualPhase().getName().equals("POWB")
main:validation/projects/ProjectOutcomeValidator.java:194:            && !action.getActualPhase().getName().equals(APConstants.POWB_ACRONYM))) {
main:action/summaries/ProgressReportProcessPOISummaryAction.java:1498:      fileName.append(APConstants.POWB_ACRONYM + " Year Report_");   <- not a type decision: report file name; POWB_ACRONYM stays
```
## C. Readers of the stored phases.name (consumers of DA-002)

### C1. Autosave draft file name (writer + readers)

Command: `grep -rlE "getAutoSaveFolder\(\)" marlo-web/src/main/java | xargs grep -lE "[Pp]hase\(\)\.getName\(\)|phase\.getName\(\)"` → 148 files. Each builds `<id>_<Class>_<phase.name>_<year>_<action>.json`.

```
main:action/annualReport/CrossCgiarPartnershipAction.java
main:action/annualReport/CrossCuttingDimensionAction.java
main:action/annualReport/CrpProgressAction.java
main:action/annualReport/EfficiencyAction.java
main:action/annualReport/ExternalPartnershipsAction.java
main:action/annualReport/FinancialSummaryAction.java
main:action/annualReport/FlagshipProgressAction.java
main:action/annualReport/FundingUseAction.java
main:action/annualReport/IndicatorsAction.java
main:action/annualReport/ManagementGovernanceAction.java
main:action/annualReport/MeliaAction.java
main:action/annualReport/ProgramVarianceAction.java
main:action/annualReport/RisksAction.java
main:action/annualReport/y2018/CrossCuttingDimensionAction.java
main:action/annualReport/y2018/EfficiencyAction.java
main:action/annualReport/y2018/FinancialSummaryAction.java
main:action/annualReport/y2018/FlagshipProgressAction.java
main:action/annualReport/y2018/FundingUseAction.java
main:action/annualReport/y2018/IntellectualAssetsAction.java
main:action/annualReport/y2018/ManagementGovernanceAction.java
main:action/annualReport/y2018/MonitoringEvaluationAction.java
main:action/annualReport/y2018/NarrativeAction.java
main:action/annualReport/y2018/OutcomesMilestonesAction.java
main:action/annualReport/y2018/PartnershipsAction.java
main:action/annualReport/y2018/PoliciesAction.java
main:action/annualReport/y2018/RisksAction.java
main:action/annualReport/y2018/SrfProgressAction.java
main:action/BaseAction.java
main:action/center/impactpathway/OutcomesAction.java
main:action/center/impactpathway/OutputsAction.java
main:action/center/impactpathway/ProgramImpactsAction.java
main:action/center/impactpathway/ResearchTopicsAction.java
main:action/funding/FundingSourceAction.java
main:action/impactpathway/ClusterActivitiesAction.java
main:action/impactpathway/OutcomesAction.java
main:action/json/autosave/AutoSaveWriterAction.java
main:action/powb/CrossCuttingDimensionsAction.java
main:action/powb/CRPStaffingAction.java
main:action/powb/EvidencesAction.java
main:action/powb/ExpectedCRPProgressAction.java
main:action/powb/FinancialPlanAction.java
main:action/powb/FlagshipPlansAction.java
main:action/powb/ManagementGovernanceAction.java
main:action/powb/ManagementRiskAction.java
main:action/powb/MonitoringEvaluationLearningAction.java
main:action/powb/PowbCollaborationAction.java
main:action/powb/ToCAdjustmentsAction.java
main:action/powb/y2019/ExpectedCRPProgress2019Action.java
main:action/powb/y2019/PlannedBudgetAction.java
main:action/powb/y2019/PlannedColaborationAction.java
main:action/powb/y2019/PlannedStudiesAction.java
main:action/powb/y2019/ProgramChangesAction.java
main:action/powb/y2019/ToCAdjustments2019Action.java
main:action/projects/DeliverableAction.java
main:action/projects/ProjectActivitiesAction.java
main:action/projects/ProjectBudgetByFlagshipAction.java
main:action/projects/ProjectBudgetByPartnersAction.java
main:action/projects/ProjectCenterMappingAction.java
main:action/projects/ProjectContributionToLP6Action.java
main:action/projects/ProjectDescriptionAction.java
main:action/projects/ProjectExpectedStudiesAction.java
main:action/projects/ProjectHighlightAction.java
main:action/projects/ProjectInnovationAction.java
main:action/projects/ProjectLeveragesAction.java
main:action/projects/ProjectLocationAction.java
main:action/projects/ProjectOutcomeAction.java
main:action/projects/ProjectPartnerAction.java
main:action/projects/ProjectPolicyAction.java
main:action/projects/SafeguardAction.java
main:action/publications/PublicationAction.java
main:action/superadmin/DeliverableBulkSynchronizationAction.java
main:action/superadmin/DeliverablesReplicationAction.java
main:action/superadmin/FundingSourcesReplicationAction.java
main:action/superadmin/ProjectBudgetByPartnersReplicationAction.java
main:action/superadmin/ProjectPartnersReplication.java
main:validation/annualreport/CCDimensionValidator.java
main:validation/annualreport/CrossCgiarPartnershipValidator.java
main:validation/annualreport/CrpProgressValidator.java
main:validation/annualreport/EfficiencyValidator.java
main:validation/annualreport/ExternalPartnershipsValidator.java
main:validation/annualreport/FinancialSummaryValidator.java
main:validation/annualreport/FlagshipProgressValidator.java
main:validation/annualreport/FundingUseValidator.java
main:validation/annualreport/GovernanceValidator.java
main:validation/annualreport/IndicatorsValidator.java
main:validation/annualreport/MeliaValidator.java
main:validation/annualreport/ProgramVarianceValidator.java
main:validation/annualreport/RiskValidator.java
main:validation/annualreport/y2018/CCDimension2018Validator.java
main:validation/annualreport/y2018/Efficiency2018Validator.java
main:validation/annualreport/y2018/FinancialSummary2018Validator.java
main:validation/annualreport/y2018/FlagshipProgress2018Validator.java
main:validation/annualreport/y2018/FundingUse2018Validator.java
main:validation/annualreport/y2018/Governance2018Validator.java
main:validation/annualreport/y2018/Innovations2018Validator.java
main:validation/annualreport/y2018/IntellectualAssetsValidator.java
main:validation/annualreport/y2018/MonitoringEvaluationValidator.java
main:validation/annualreport/y2018/NarrativeValidator.java
main:validation/annualreport/y2018/OutcomeMilestonesValidator.java
main:validation/annualreport/y2018/PartnershipValidator.java
main:validation/annualreport/y2018/Policies2018Validator.java
main:validation/annualreport/y2018/Publications2018Validator.java
main:validation/annualreport/y2018/Risk2018Validator.java
main:validation/annualreport/y2018/SrfProgressValidator.java
main:validation/annualreport/y2018/StudiesOICR2018Validator.java
main:validation/center/impactpathway/OutcomesValidator.java
main:validation/center/impactpathway/OutputsValidator.java
main:validation/center/impactpathway/ProgramImpactsValidator.java
main:validation/center/impactpathway/ResearchTopicsValidator.java
main:validation/center/monitoring/project/MonitoringOutcomeValidator.java
main:validation/fundingSource/FundingSourceValidator.java
main:validation/powb/CrossCuttingValidator.java
main:validation/powb/CrpStaffingValidator.java
main:validation/powb/EvidencesValidator.java
main:validation/powb/ExpectedCRPProgressValidator.java
main:validation/powb/FinancialPlanValidator.java
main:validation/powb/ManagementGovernanceValidator.java
main:validation/powb/ManagementRiskValidator.java
main:validation/powb/MonitoringEvaluationLearningValidator.java
main:validation/powb/PowbCollaborationValidator.java
main:validation/powb/ToCAdjustmentsValidator.java
main:validation/powb/y2019/PlannedBudgetValidator.java
main:validation/powb/y2019/PlannedCollaborationValidator.java
main:validation/powb/y2019/PlannedStudiesValidator.java
main:validation/powb/y2019/ProgramChangeValidator.java
main:validation/powb/y2019/ProgressOutcomesValidator.java
main:validation/powb/y2019/ToC2019Validator.java
main:validation/projects/DeliverableValidator.java
main:validation/projects/ProjectActivitiesValidator.java
main:validation/projects/ProjectBudgetsFlagshipValidator.java
main:validation/projects/ProjectBudgetsValidator.java
main:validation/projects/ProjectCaseStudyValidation.java
main:validation/projects/ProjectCCAFSOutcomeValidator.java
main:validation/projects/ProjectCenterMappingValidator.java
main:validation/projects/ProjectDescriptionValidator.java
main:validation/projects/ProjectExpectedStudiesValidator.java
main:validation/projects/ProjectHighLightValidator.java
main:validation/projects/ProjectInnovationValidator.java
main:validation/projects/ProjectLeverageValidator.java
main:validation/projects/ProjectLocationValidator.java
main:validation/projects/ProjectLP6Validator.java
main:validation/projects/ProjectOtherContributionsValidator.java
main:validation/projects/ProjectOutcomesPandRValidator.java
main:validation/projects/ProjectOutcomeValidator.java
main:validation/projects/ProjectOutputsValidator.java
main:validation/projects/ProjectPartnersValidator.java
main:validation/projects/ProjectPolicyValidator.java
main:validation/projects/SafeguardValidator.java
```
```
main:action/projects/ProjectCenterMappingAction.java:127:    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + phase.getName() +
main:action/projects/ProjectExpectedStudiesAction.java:639:      + this.getActualPhase().getName() + "_" + this.getActualPhase().getYear() + "_" + act
main:action/superadmin/DeliverableBulkSynchronizationAction.java:113:    String autoSaveFile = deliverable.getId() + "_" + composedClassName + "_" + p
main:action/superadmin/DeliverablesReplicationAction.java:160:    String autoSaveFile = deliverable.getId() + "_" + composedClassName + "_" + phase.ge
main:action/superadmin/ProjectBudgetByPartnersReplicationAction.java:96:    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + ph
main:action/superadmin/ProjectPartnersReplication.java:96:    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + phase.getName() 
main:action/json/autosave/AutoSaveWriterAction.java:231:          fileId + "_" + fileClass + "_" + phase.getName() + "_" + phase.getYear() + "_" + fil
```
### C2. REST v2 (out of scope, listed for completeness)
```
main:rest/controller/v2/controllist/items/expectedStudies/ExpectedStudiesItem.java
main:rest/controller/v2/controllist/items/expectedStudies/ExpectedStudiesOtherItem.java
main:rest/controller/v2/controllist/items/Innovations/InnovationItem.java
main:rest/controller/v2/controllist/items/FinancialSumary/FinancialSummaryItem.java
main:rest/controller/v2/controllist/items/progressTowards/ProgressTowardsItem.java
main:rest/controller/v2/controllist/items/ImpactPathway/OutcomeItem.java
main:rest/controller/v2/controllist/items/Deliverables/DeliverablesItem.java
main:rest/controller/v2/controllist/items/ImpactPathway/MilestoneItem.java
main:rest/controller/v2/controllist/items/policies/PolicyItem.java
main:rest/controller/v2/controllist/items/statusPlannedOutcomes/StatusPlannedOutcomesItem.java
main:rest/controller/v2/controllist/items/keyExternalPartnership/KeyExternalPartnershipItem.java
main:rest/controller/v2/controllist/items/actionTaken/ActionTakenItem.java
main:rest/controller/v2/controllist/items/statusPlannedOutcomes/StatusPlannedMilestonesItem.java
main:rest/controller/v2/controllist/items/Expenditures/ExpendituresItem.java
main:rest/controller/v2/controllist/items/crossCgiarCollabs/CrossCGIARCollaborationsItem.java
main:rest/controller/v2/controllist/items/ParticipantsCapDev/ParticipantsCapDevItem.java
```
### C3. Display and other readers
```
main:crp/views/home/dashboard.ftl:205:      <span class="scheduleCard__nextName">${(phase.composedName)!}</span>
main:crp/views/home/dashboard.ftl:359:  [#assign browseScope = (actualPhase.composedName)!'' /]
main:crp/views/projects/availability-projects.ftl:13:          <span ><strong>  This information is not available in this phase (${(actualPhase.compos
main:crp/views/projects/projectsList.ftl:25:[#assign phaseName = (actualPhase.composedName)!"" /]
main:crp/views/admin/crpDeliverables.ftl:72:                <p >${(actualPhase.name)!} ${(actualPhase.year)!}</p>
main:crp/views/admin/crpDeliverables.ftl:83:                      <option value="${phase.id}" >${phase.name} - ${phase.year} </option>
main:crp/views/admin/crpPhases.ftl:77:  [#assign cycName = (phase.name)!'' /]
main:crp/views/admin/crpPhases.ftl:227:                        [#assign canOpenClose = ((phase.year > yearLimit) && !((phase.year == (yearLimit + 1)) 
main:crp/views/admin/crpPhases.ftl:243:                          data-name="${(phase.name)!}"
main:crp/views/admin/crpPhases.ftl:248:                          data-search="${(((phase.name)!'') + ' ' + phase.year?c + ' ' + ((phase.description)!'
main:crp/views/admin/crpPhases.ftl:253:                          <input type="hidden" name="${customName}.name" value="${(phase.name)!}" />
main:crp/views/admin/crpPhases.ftl:263:                              <span class="cycRow__name">${(phase.name)!} ${phase.year?c}<span
main:crp/views/admin/crpPhases.ftl:335:                                <span class="sr-only">[@s.text name="crpPhases.landing.set"][@s.param]${(phase.
main:crp/views/powb2019/messages-powb2019.ftl:50:      <p class="readPrivileges">[@s.text name="message.noCycleEditable"][@s.param]${(actualPhase.comp
main:crp/views/annualReport/messages-annualReport.ftl:50:      <p class="readPrivileges">[@s.text name="message.noCycleEditable"][@s.param]${(actualPh
main:crp/views/summaries/boardSummaries.ftl:507:                <option value="${phase.id}" [#if (actualPhase.id == phase.id)!false]selected[/#if]>${p
main:crp/views/annualReport2018/messages-AR2018.ftl:50:      <p class="readPrivileges">[@s.text name="message.noCycleEditable"][@s.param]${(actualPhas
main:crp/views/powb/messages-powb.ftl:50:      <p class="readPrivileges">[@s.text name="message.noCycleEditable"][@s.param]${(actualPhase.composedName
main:global/macros/deliverableMacros.ftl:367:  [#if actualPhase.name != 'Progress']
main:global/macros/deliverableMacros.ftl:411:          [#assign isSharedCluster = (action.isSubmit(cluster.project.id)) && (actualPhase.name != 'Progr
main:global/pages/timeline-phases.ftl:79:            <span class="phasePill__name">${(phase.composedName)!}</span>
main:global/pages/timeline-phases.ftl:120:                    data-phase-search="${((phase.composedName)!)?lower_case}"
main:global/pages/timeline-phases.ftl:123:                      <span class="phaseRow__name">${(phase.composedName)!}[#if isCurrent]<em>[@s.text name=
main:global/pages/timeline-phases.ftl:148:      <span class="${(actualPhase.isReporting())?string('reporting','planning')}" style="display:none;">[#if
main:global/pages/titleGenerator.ftl:29:[#if (actualPhase.id??)!false](${(actualPhase.composedName)!})[/#if]
main:action/UnhandledExceptionAction.java:140:      message.detail("Phase", phase.getComposedName());
main:action/UnhandledExceptionAction.java:171:    if (this.getActualPhase() != null && this.getActualPhase().getComposedName() != null) {
main:action/UnhandledExceptionAction.java:172:      actionNameSubject += " - " + this.getActualPhase().getComposedName();
main:action/publications/PublicationAction.java:1679:            + ", phase: " + deliverable.getPhase().getComposedName());
main:action/projects/DeliverableAction.java:4621:            + deliverable.getId() + ", phase: " + this.getActualPhase().getComposedName());
main:action/superadmin/ProjectBudgetByPartnersReplicationAction.java:136:          "There are " + projectBudgetsNextPhase.size() + " project budgets f
main:action/superadmin/GlobalUnitCreateAction.java:833:    phaseInput.setName(StringUtils.defaultIfBlank(templatePhase.getName(), "Phase " + templateP
main:action/json/superadmin/bulkreplication/PhasesByCrpAction.java:59:            phasestMap.put("name", phase.getName());
main:action/summaries/ProgressReportProcessPOISummaryAction.java:369:        leader = project.getLeaderPerson(this.getSelectedPhase()).getUser().getCo
main:action/summaries/ProgressReportProcessPOISummaryAction.java:1022:              // poiSummary.pageLeftHeader(document, "All Clusters for " + this.
main:action/summaries/ProjectActivitiesSummaryAction.java:894:        leader = project.getLeaderPerson(this.getSelectedPhase()).getUser().getComposedN
main:action/summaries/ReportingSummaryAction.java:4935:        leader = project.getLeaderPerson(this.getSelectedPhase()).getUser().getComposedName() +
main:action/summaries/ProjectContributionLP6PDFSummaryAction.java:711:        leader = project.getLeaderPerson(this.getSelectedPhase()).getUser().getC
main:action/summaries/ProjectsDeleteFieldsSummaryAction.java:732:          contributionCRP, phase.getComposedName(), license, otherLicense});
```
