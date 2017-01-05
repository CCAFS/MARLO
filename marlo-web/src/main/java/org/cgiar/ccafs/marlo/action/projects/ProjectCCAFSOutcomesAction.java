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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.IpElementManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpElementType;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.IpProgramElement;
import org.cgiar.ccafs.marlo.data.model.IpProjectContribution;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;
import org.cgiar.ccafs.marlo.data.model.IpRelationship;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocusPrev;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
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


  private long projectID;


  private Project project;


  // Managers
  private ProjectManager projectManager;


  private CrpProgramManager crpProgramManager;


  private CrpManager crpManager;
  private IpElementManager ipElementManager;

  private Crp loggedCrp;


  @Inject
  public ProjectCCAFSOutcomesAction(APConfig config, ProjectManager projectManager, CrpProgramManager crpProgramManager,
    IpElementManager ipElementManager, CrpManager crpManager) {
    super(config);
    this.crpProgramManager = crpProgramManager;
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.ipElementManager = ipElementManager;
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


  public IpProjectIndicator getIndicator(long indicatorID, long midOutcome, int year) {

    int index = this.getIndicatorIndex(indicatorID, midOutcome, year);

    if (index >= 0) {
      return project.getProjectIndicators().get(index);
    }
    return null;
  }

  public int getIndicatorIndex(long indicatorID, long midOutcome, int year) {


    if (project.getProjectIndicators() != null) {
      int i = 0;
      for (IpProjectIndicator ipProjectIndicator : project.getProjectIndicators()) {
        if (ipProjectIndicator.getIpIndicator().getIpIndicator() != null) {
          if (ipProjectIndicator.getIpIndicator().getIpIndicator().getId().longValue() == indicatorID
            && ipProjectIndicator.getIpIndicator().getIpElement().getId().longValue() == midOutcome
            && year == ipProjectIndicator.getYear()) {
            return i;
          }

        } else {
          if (ipProjectIndicator.getIpIndicator().getId().longValue() == indicatorID
            && ipProjectIndicator.getIpIndicator().getIpElement().getId().longValue() == midOutcome
            && year == ipProjectIndicator.getYear()) {
            return i;
          }
        }
        i++;
      }

    }
    return -1;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public List<IpElement> getMidOutcomeOutputs(long midOutcomeID) {
    List<IpElement> outputs = new ArrayList<>();
    IpElement midOutcome = ipElementManager.getIpElementById(midOutcomeID);

    if (this.isRegionalOutcome(midOutcome)) {
      List<IpElement> mogs = new ArrayList<>();

      List<IpElement> translatedOf = new ArrayList<>();
      List<IpProgramElement> programElements = midOutcome.getIpProgramElements().stream()
        .filter(c -> c.isActive()
          && c.getIpProgramElementRelationType().getId().intValue() == APConstants.ELEMENT_RELATION_TRANSLATION)
        .collect(Collectors.toList());

      for (IpProgramElement ipProgramElement : programElements) {
        translatedOf.add(ipProgramElement.getIpElement());
      }


      for (IpElement fsOutcome : translatedOf) {

        List<IpElement> contributesTo = new ArrayList<>();
        List<IpRelationship> programElementsMogs = fsOutcome.getIpRelationshipsForParentId().stream()
          .filter(
            c -> Integer.parseInt(String.valueOf(c.getRelationTypeId())) == APConstants.ELEMENT_RELATION_CONTRIBUTION)
          .collect(Collectors.toList());
        for (IpRelationship ipRelationship : programElementsMogs) {
          contributesTo.add(ipRelationship.getIpElementsByChildId());
        }

        mogs.addAll(contributesTo);
        for (IpElement mog : mogs) {
          if (!outputs.contains(mog)) {
            outputs.add(mog);
          }
        }
      }
    } else {

      List<IpElement> contributesTo = new ArrayList<>();

      List<IpRelationship> programElementsMogs = midOutcome.getIpRelationshipsForParentId().stream()
        .filter(
          c -> Integer.parseInt(String.valueOf(c.getRelationTypeId())) == APConstants.ELEMENT_RELATION_CONTRIBUTION)
        .collect(Collectors.toList());
      for (IpRelationship ipRelationship : programElementsMogs) {
        contributesTo.add(ipRelationship.getIpElementsByChildId());
      }

      outputs = contributesTo;
    }


    List<IpElement> elements = new ArrayList<>();
    elements.addAll(outputs);
    outputs = new ArrayList<>();
    for (IpElement ipElement : elements) {
      IpElement ipElementDB = ipElementManager.getIpElementById(ipElement.getId());
      if (this.containsOutput(ipElementDB.getId().longValue(), midOutcomeID)) {

        outputs.add(ipElement);
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

  public boolean isRegionalOutcome(IpElement outcome) {

    List<IpElement> translatedOf = new ArrayList<>();
    List<IpProgramElement> programElements = outcome.getIpProgramElements().stream()
      .filter(c -> c.isActive()
        && c.getIpProgramElementRelationType().getId().intValue() == APConstants.ELEMENT_RELATION_TRANSLATION)
      .collect(Collectors.toList());

    for (IpProgramElement ipProgramElement : programElements) {
      translatedOf.add(ipProgramElement.getIpElement());
    }
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
    List<IpElement> translatedOf = new ArrayList<>();
    List<IpProgramElement> programElements = midOutcome.getIpProgramElements().stream()
      .filter(c -> c.isActive()
        && c.getIpProgramElementRelationType().getId().intValue() == APConstants.ELEMENT_RELATION_TRANSLATION)
      .collect(Collectors.toList());
    for (IpProgramElement ipProgramElement : programElements) {
      translatedOf.add(ipProgramElement.getIpElement());
    }

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

    // Get all years
    allYears = project.getAllYears();
    allYears.add(this.getMidOutcomeYear());

    projectFocusList = new ArrayList<>();

    List<ProjectFocusPrev> focusPrevs =
      project.getProjectFocusPrevs().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    for (ProjectFocusPrev projectFocusPrev : focusPrevs) {
      projectFocusList.add(projectFocusPrev.getIpProgram());
    }
    List<IpProjectContribution> ipProjectContributions =
      project.getIpProjectContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    project.setOutputs(new ArrayList<>());
    for (IpProjectContribution ipProjectContribution : ipProjectContributions) {
      project.getOutputs().add(ipProjectContribution.getIpElementByMidOutcomeId());
    }
    project.setMogs(new ArrayList<>());
    for (IpProjectContribution ipProjectContribution : ipProjectContributions) {
      project.getMogs().add(ipProjectContribution.getIpElementByMogId());
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

    }
    List<IpProjectIndicator> ipProjectIndicators =
      project.getIpProjectIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList());

    project.setIndicators(new ArrayList<>());
    for (IpProjectIndicator ipProjectIndicator : ipProjectIndicators) {
      project.getIndicators().add(ipProjectIndicator.getIpIndicator());
    }
    /* logic for save */
    project.setProjectIndicators(
      project.getIpProjectIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

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
}
