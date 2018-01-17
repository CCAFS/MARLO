/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.IpElementManager;
import org.cgiar.ccafs.marlo.data.manager.IpIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.IpProjectIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpElementType;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.IpProgramElement;
import org.cgiar.ccafs.marlo.data.model.IpProjectContribution;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocusPrev;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.utils.HistoryDifference;
import org.cgiar.ccafs.marlo.validation.projects.ProjectCCAFSOutcomeValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/***
 * Christian Garcia
 */
public class ProjectCCAFSOutcomesAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -6970673687247245375L;
  private List<IpElement> midOutcomes;
  private List<IpElement> midOutcomesSelected;


  private List<IpProgram> projectFocusList;


  private List<IpElement> previousOutputs;
  private List<IpIndicator> previousIndicators;
  private List<Integer> allYears;
  private ProjectCCAFSOutcomeValidator ccafsOutcomeValidator;
  private HistoryComparator historyComparator;

  private long projectID;


  private Project project;


  private String transaction;

  private AuditLogManager auditLogManager;
  // Managers
  private ProjectManager projectManager;


  private CrpProgramManager crpProgramManager;


  private CrpManager crpManager;
  private IpElementManager ipElementManager;

  private IpIndicatorManager ipIndicatorManager;


  private Crp loggedCrp;
  private IpProjectIndicatorManager ipProjectIndicatorManager;


  @Inject
  public ProjectCCAFSOutcomesAction(APConfig config, ProjectManager projectManager, CrpProgramManager crpProgramManager,
    IpElementManager ipElementManager, CrpManager crpManager, AuditLogManager auditLogManager,
    IpProjectIndicatorManager ipProjectIndicatorManager, IpIndicatorManager ipIndicatorManager,
    HistoryComparator historyComparator, ProjectCCAFSOutcomeValidator ccafsOutcomeValidator) {
    super(config);
    this.crpProgramManager = crpProgramManager;
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.ipElementManager = ipElementManager;
    this.auditLogManager = auditLogManager;
    this.historyComparator = historyComparator;
    this.ipProjectIndicatorManager = ipProjectIndicatorManager;
    this.ccafsOutcomeValidator = ccafsOutcomeValidator;
    this.ipIndicatorManager = ipIndicatorManager;
  }

  public String calculateAcumulativeTarget(int year, IpProjectIndicator id) {

    int acumulative = 0;

    try {
      for (IpProjectIndicator indicators : project.getProjectIndicators()) {
        if (indicators != null) {
          if (id.getIpIndicator().getIpIndicator() != null) {
            if (indicators.getYear() <= year && indicators.getIpIndicator().getIpIndicator().getId().longValue() == id
              .getIpIndicator().getIpIndicator().getId().longValue()) {
              if (indicators.getTarget() == null) {
                indicators.setTarget("0");
              }
              if (indicators.getTarget() != null) {
                if (!indicators.getTarget().equals("")) {
                  try {
                    acumulative = acumulative + Integer.parseInt(indicators.getTarget());
                  } catch (NumberFormatException e) {
                    return "Cannot be Calculated";
                  }
                }
              }
            }
          } else {
            if (indicators.getYear() <= year && indicators.getIpIndicator() != null
              && indicators.getIpIndicator().getId() != null
              && indicators.getIpIndicator().getId().longValue() == id.getIpIndicator().getId().longValue()) {
              if (indicators.getTarget() == null) {
                indicators.setTarget("0");
              }
              if (indicators.getTarget() != null) {

                if (!indicators.getTarget().equals("")) {
                  try {
                    acumulative = acumulative + Integer.parseInt(indicators.getTarget());
                  } catch (NumberFormatException e) {
                    return "Cannot be Calculated";
                  }
                }
              }
            }
          }
        }


      }
    } catch (Exception e) {
      return "Cannot be Calculated";
    }
    return String.valueOf(acumulative);
  }

  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
    }

    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    } else {
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();

    return SUCCESS;
  }


  public boolean containsOutput(long outputID, long outcomeID) {
    if (project.getMogs() != null) {
      for (IpElement output : project.getMogs()) {

        IpElement outputDB = ipElementManager.getIpElementById(output.getId());
        if (outputDB != null && outputDB.getId().longValue() == outputID) {


          return true;

        }
      }
    }
    return false;
  }


  public List<Integer> getAllYears() {
    return allYears;
  }


  private Path getAutoSaveFilePath() {
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public IpProjectIndicator getIndicator(long indicatorID, long midOutcome, int year) {

    int index = this.getIndicatorIndex(indicatorID, midOutcome, year);

    if (index >= 0) {
      return project.getProjectIndicators().get(index);
    }
    return new IpProjectIndicator();
  }


  public int getIndicatorIndex(long indicatorID, long midOutcome, int year) {


    if (project.getProjectIndicators() != null) {
      int i = 0;
      for (IpProjectIndicator ipProjectIndicator : project.getProjectIndicators()) {
        if (ipProjectIndicator.getIpIndicator() != null) {
          if (ipProjectIndicator.getIpIndicator().getIpIndicator() != null) {
            if (ipProjectIndicator.getIpIndicator().getIpIndicator().getId().longValue() == indicatorID
              && ipProjectIndicator.getIpIndicator().getIpElement().getId().longValue() == midOutcome
              && year == ipProjectIndicator.getYear()) {
              return i;
            }

          } else {
            if (ipProjectIndicator.getIpIndicator().getId().longValue() == indicatorID
              && Long.parseLong(String.valueOf(ipProjectIndicator.getOutcomeId())) == midOutcome
              && year == ipProjectIndicator.getYear()) {
              return i;
            }
          }
        }

        i++;
      }

    } else {
      project.setProjectIndicators(new ArrayList<>());
    }
    IpProjectIndicator ipProjectIndicator = new IpProjectIndicator();


    project.getProjectIndicators().add(ipProjectIndicator);
    return project.getProjectIndicators().size() - 1;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public List<IpElement> getMidOutcomeOutputs(long midOutcomeID) {
    List<IpElement> outputs = new ArrayList<>();
    IpElement midOutcome = ipElementManager.getIpElementById(midOutcomeID);

    if (this.isRegionalOutcome(midOutcome)) {
      List<IpElement> mogs = new ArrayList<>();

      List<IpElement> translatedOf =
        ipElementManager.getIPElementsRelated(midOutcome.getId().intValue(), APConstants.ELEMENT_RELATION_TRANSLATION);

      for (IpElement fsOutcome : translatedOf) {
        mogs.addAll(ipElementManager.getIPElementsByParent(fsOutcome, APConstants.ELEMENT_RELATION_CONTRIBUTION));
        for (IpElement mog : mogs) {
          if (!outputs.contains(mog)) {
            outputs.add(mog);
          }
        }
      }
    } else {
      outputs = ipElementManager.getIPElementsByParent(midOutcome, APConstants.ELEMENT_RELATION_CONTRIBUTION);

    }

    List<IpElement> elements = new ArrayList<>();
    elements.addAll(outputs);
    for (IpElement ipElement : elements) {
      if (!this.containsOutput(ipElement.getId(), midOutcomeID)) {
        outputs.remove(ipElement);
      }
    }
    return outputs;

  }

  public List<IpElement> getMidOutcomes() {
    return midOutcomes;
  }

  private void getMidOutcomesByIndicators() {
    for (IpIndicator indicator : project.getIndicators()) {
      IpElement midoutcome = indicator.getIpElement();
      if (!midOutcomesSelected.contains(midoutcome)) {
        String description = midoutcome.getIpProgram().getAcronym() + " - "
          + this.getText("planning.activityImpactPathways.outcome2019") + ": " + midoutcome.getDescription();
        midoutcome.setDescription(description);
        if (midoutcome.getIpProgram() != null) {
          // midOutcomesSelected.add(midoutcome);
        }

      }
    }
  }


  private void getMidOutcomesByOutputs() {
    for (IpElement output : project.getOutputs()) {

      List<IpElement> contributesTo = new ArrayList<>();
      List<IpProgramElement> programElements = output.getIpProgramElements().stream()
        .filter(c -> c.isActive()
          && c.getIpProgramElementRelationType().getId().intValue() == APConstants.ELEMENT_RELATION_CONTRIBUTION)
        .collect(Collectors.toList());
      for (IpProgramElement ipProgramElement : programElements) {
        contributesTo.add(ipProgramElement.getIpElement());
      }

      for (IpElement parent : contributesTo) {
        IpElement midoutcome = ipElementManager.getIpElementById(parent.getId());
        if (!midOutcomesSelected.contains(midoutcome)) {
          String description = midoutcome.getComposedId() + ": " + midoutcome.getDescription();
          midoutcome.setDescription(description);
          if (midoutcome.getIpProgram() != null) {
            midOutcomesSelected.add(midoutcome);
          }

        }
      }
    }
  }

  private void getMidOutcomesByProjectFocuses() {
    boolean isGlobalProject;
    isGlobalProject =
      projectFocusList.contains(new IpProgram(Long.parseLong(String.valueOf(APConstants.GLOBAL_PROGRAM))));

    IpElementType midOutcomeType =
      new IpElementType(Long.parseLong(String.valueOf(APConstants.ELEMENT_TYPE_OUTCOME2019)));
    for (IpProgram program : projectFocusList) {

      if (!isGlobalProject && program.isFlagshipProgram()) {
        continue;
      }

      List<IpElement> elements =
        program.getIpElements().stream().filter(c -> c.isActive()).collect(Collectors.toList());

      for (int i = 0; i < elements.size(); i++) {
        IpElement midOutcome = elements.get(i);
        if (this.isValidMidoutcome(midOutcome)) {
          midOutcome.setDescription(midOutcome.getComposedId() + ": " + midOutcome.getDescription());
          midOutcomes.add(midOutcome);
        }

      }
    }
  }

  public List<IpElement> getMidOutcomesSelected() {
    return midOutcomesSelected;
  }


  public int getMidOutcomeYear() {
    return APConstants.MID_OUTCOME_YEAR;
  }


  public int getMOGIndex(IpElement mog) {
    int index = 0;
    List<IpElement> allMOGs = ipElementManager.findAll().stream()
      .filter(c -> c.getIpProgram().getId().longValue() == mog.getIpProgram().getId().longValue()
        && mog.getIpElementType().getId().longValue() == c.getIpElementType().getId().longValue())
      .collect(Collectors.toList());

    for (int i = 0; i < allMOGs.size(); i++) {
      if (allMOGs.get(i).getId() == mog.getId()) {
        return (i + 1);
      }
    }

    return index;
  }


  public List<IpIndicator> getPreviousIndicators() {
    return previousIndicators;
  }


  public List<IpElement> getPreviousOutputs() {
    return previousOutputs;
  }

  public Project getProject() {
    return project;
  }

  public List<IpProgram> getProjectFocusList() {
    return projectFocusList;
  }


  public long getProjectID() {
    return projectID;
  }

  public String getTransaction() {
    return transaction;
  }

  public boolean isRegionalOutcome(IpElement outcome) {

    List<IpElement> translatedOf =
      ipElementManager.getIPElementsRelated(outcome.getId().intValue(), APConstants.ELEMENT_RELATION_TRANSLATION);
    return !translatedOf.isEmpty();
  }


  /**
   * The regional midOutcomes only can be selected if they are translation of
   * an outcome that belongs to the project focuses.
   * 
   * @param midOutcome - The element to evaluate
   * @return True if the midOutcome belongs to a flagship.
   *         True if the midOutcome is regional but is translated from an
   *         outcome that belongs to some of the project focuses.
   *         False otherwise.
   */
  private boolean isValidMidoutcome(IpElement midOutcome) {
    //
    List<IpElement> translatedOf =
      ipElementManager.getIPElementsRelated(midOutcome.getId().intValue(), APConstants.ELEMENT_RELATION_TRANSLATION);
    if (!translatedOf.isEmpty()) {
      for (IpElement parentElement : translatedOf) {
        if (projectFocusList.contains(parentElement.getIpProgram())) {
          return true;
        }
      }
    } else {
      // Is a flagship midOutcome
      return true;
    }

    return false;
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();


    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    midOutcomes = new ArrayList<>();
    midOutcomesSelected = new ArrayList<>();
    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    project = projectManager.getProjectById(projectID);


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);

      if (history != null) {
        project = history;
        List<HistoryDifference> differences = new ArrayList<>();
        Map<String, String> specialList = new HashMap<>();
        int i = 0;
        project.setProjectIndicators(
          project.getIpProjectIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        for (IpProjectIndicator projectOutcomePandr : project.getProjectIndicators()) {
          int[] index = new int[1];
          index[0] = i;
          differences.addAll(historyComparator.getDifferencesList(projectOutcomePandr, transaction, specialList,
            "project.projectIndicators[" + i + "]", "project", 1));
          i++;
        }

        this.setDifferences(differences);
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      project = projectManager.getProjectById(projectID);
    }
    // Get all years
    allYears = project.getProjecInfoPhase(this.getActualPhase()).getAllYears();
    allYears.add(this.getMidOutcomeYear());

    projectFocusList = new ArrayList<>();


    if (project != null) {
      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
 	      reader.close();
 	
        AutoSaveReader autoSaveReader = new AutoSaveReader();
        project = (Project) autoSaveReader.readFromJson(jReader);
      

        Project projectDB = projectManager.getProjectById(projectID);
        project.setProjectInfo(projectDB.getProjecInfoPhase(this.getActualPhase()));
        if (project.getProjectIndicators() == null) {
          project.setProjectIndicators(new ArrayList<IpProjectIndicator>());
        } else {
          for (IpProjectIndicator ipProjectIndicator : project.getProjectIndicators()) {

            try {
              if (ipProjectIndicator != null && ipProjectIndicator.getId() != null) {
                IpProjectIndicator ipProjectIndicatorDB =
                  ipProjectIndicatorManager.getIpProjectIndicatorById(ipProjectIndicator.getId());
                ipProjectIndicator.setOutcomeId(ipProjectIndicatorDB.getOutcomeId());
                if (ipProjectIndicator.getIpIndicator() != null) {
                  ipProjectIndicator
                    .setIpIndicator(ipIndicatorManager.getIpIndicatorById(ipProjectIndicator.getIpIndicator().getId()));
                }
              }
            } catch (Exception e) {
              e.printStackTrace();
            }


          }
        }

        this.setDraft(true);
      } else {
        project.setProjectIndicators(
          project.getIpProjectIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        this.setDraft(false);
      }
    }

    /* logic for save */

    Project projectDB = projectManager.getProjectById(projectID);
    List<ProjectFocusPrev> focusPrevs =
      projectDB.getProjectFocusPrevs().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    for (ProjectFocusPrev projectFocusPrev : focusPrevs) {
      projectFocusList.add(projectFocusPrev.getIpProgram());
    }
    List<IpProjectContribution> ipProjectContributions =
      projectDB.getIpProjectContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    project.setOutputs(new ArrayList<>());
    for (IpProjectContribution ipProjectContribution : ipProjectContributions) {
      project.getOutputs().add(ipProjectContribution.getIpElementByMidOutcomeId());
    }
    project.setMogs(new ArrayList<>());
    for (IpProjectContribution ipProjectContribution : ipProjectContributions) {
      project.getMogs().add(ipProjectContribution.getIpElementByMogId());
    }

    List<IpProjectIndicator> ipProjectIndicators =
      projectDB.getIpProjectIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList());

    project.setIndicators(new ArrayList<>());
    for (IpProjectIndicator ipProjectIndicator : ipProjectIndicators) {
      project.getIndicators().add(ipIndicatorManager.getIpIndicatorById(ipProjectIndicator.getIpIndicator().getId()));
    }
    this.getMidOutcomesByProjectFocuses();

    // Get all the midOutcomes selected
    this.getMidOutcomesByOutputs();

    // Get all the midOutcomes selected through the indicators
    this.getMidOutcomesByIndicators();

    this.removeOutcomesAlreadySelected();

    for (IpElement ipElement : midOutcomesSelected) {
      IpElement ipElementDB = ipElementManager.getIpElementById(ipElement.getId());
      ipElement
        .setIndicators(ipElementDB.getIpIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      List<IpIndicator> indicators = new ArrayList<>();
      indicators.addAll(ipElement.getIndicators());
      ipElement.getIndicators().clear();


      for (IpIndicator ipIndicator : indicators) {


        if (project.getIndicators().contains(ipIndicator)
          || project.getIndicators().contains(ipIndicator.getIpIndicator())) {
          ipElement.getIndicators().add(ipIndicator);
        }
      }

    }


    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_CCFASOUTCOME_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      if (project.getClusterActivities() != null) {
        project.getClusterActivities().clear();

      }
    }


  }


  private void removeOutcomesAlreadySelected() {
    for (int i = 0; i < midOutcomes.size(); i++) {
      if (midOutcomesSelected.contains(midOutcomes.get(i))) {
        midOutcomes.remove(i);
        i--;
      }
    }
  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      Project projectDB = projectManager.getProjectById(project.getId());
      project.setActive(true);
      project.setCreatedBy(projectDB.getCreatedBy());
      project.setActiveSince(projectDB.getActiveSince());
      Path path = this.getAutoSaveFilePath();


      for (IpProjectIndicator ipProjectIndicator : project.getProjectIndicators()) {

        if (ipProjectIndicator != null) {
          if (ipProjectIndicator.getId() == null || ipProjectIndicator.getId() == -1) {
            ipProjectIndicator.setActive(true);
            ipProjectIndicator.setCreatedBy(this.getCurrentUser());
            ipProjectIndicator.setModifiedBy(this.getCurrentUser());
            ipProjectIndicator.setModificationJustification(this.getJustification());
            ipProjectIndicator.setActiveSince(new Date());


            ipProjectIndicator.setId(null);
            ipProjectIndicator.setProject(project);

          } else {
            IpProjectIndicator projectIndicatorDB =
              ipProjectIndicatorManager.getIpProjectIndicatorById(ipProjectIndicator.getId());
            ipProjectIndicator.setActive(true);
            ipProjectIndicator.setCreatedBy(projectIndicatorDB.getCreatedBy());
            ipProjectIndicator.setModifiedBy(this.getCurrentUser());
            ipProjectIndicator.setModificationJustification(this.getJustification());
            ipProjectIndicator.setDescription(projectIndicatorDB.getDescription());
            ipProjectIndicator.setGender(projectIndicatorDB.getGender());

            ipProjectIndicator.setYear(projectIndicatorDB.getYear());
            ipProjectIndicator.setProject(project);
            ipProjectIndicator.setActiveSince(projectIndicatorDB.getActiveSince());
            ipProjectIndicator.setIpIndicator(projectIndicatorDB.getIpIndicator());

          }

          if (ipProjectIndicator.getIpIndicator() != null) {
            ipProjectIndicatorManager.saveIpProjectIndicator(ipProjectIndicator);
          }

        }

      }

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_CCFASOTUCOME_RELATION);
      project = projectManager.getProjectById(projectID);
      project.setActiveSince(new Date());
      projectManager.saveProject(project, this.getActionName(), relationsName);


      if (path.toFile().exists()) {
        path.toFile().delete();
      }


      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();

        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }

        } else {
          this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        return SUCCESS;
      } else {
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }
    }
    return NOT_AUTHORIZED;
  }


  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMidOutcomes(List<IpElement> midOutcomes) {
    this.midOutcomes = midOutcomes;
  }

  public void setMidOutcomesSelected(List<IpElement> midOutcomesSelected) {
    this.midOutcomesSelected = midOutcomesSelected;
  }


  public void setPreviousIndicators(List<IpIndicator> previousIndicators) {
    this.previousIndicators = previousIndicators;
  }

  public void setPreviousOutputs(List<IpElement> previousOutputs) {
    this.previousOutputs = previousOutputs;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectFocusList(List<IpProgram> projectFocusList) {
    this.projectFocusList = projectFocusList;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      ccafsOutcomeValidator.validate(this, project, true);

    }
  }
}
