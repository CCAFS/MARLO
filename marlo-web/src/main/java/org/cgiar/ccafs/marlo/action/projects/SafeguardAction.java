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
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SafeguardsManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.Safeguards;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.validation.projects.SafeguardValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeguardAction extends BaseAction {


  private static final long serialVersionUID = -793652591843623397L;


  private static final Logger LOG = LoggerFactory.getLogger(SafeguardAction.class);

  // Managers
  private ProjectManager projectManager;
  private ProjectInfoManager projectInfoManagerManager;
  private FileDBManager fileDBManager;
  private AuditLogManager auditLogManager;
  private SafeguardsManager safeguardsManager;

  private String transaction;
  private HistoryComparator historyComparator;

  // Front-end
  private long projectID;
  private long safeguardID;
  private GlobalUnit loggedCrp;
  private Project project;
  private Project projectDB;
  private File file;
  private File fileReporting;
  private String fileContentType;
  private String fileFileName;
  private String fileReportingFileName;
  private SafeguardValidator validator;
  private Safeguards safeguard;

  @Inject
  public SafeguardAction(APConfig config, ProjectManager projectManager, UserManager userManager,
    SectionStatusManager sectionStatusManager, FileDBManager fileDBManager, AuditLogManager auditLogManager,
    SafeguardValidator validator, HistoryComparator historyComparator, ProjectInfoManager projectInfoManagerManager,
    SafeguardsManager safeguardsManager) {
    super(config);
    this.projectManager = projectManager;
    this.projectInfoManagerManager = projectInfoManagerManager;
    this.projectManager = projectManager;
    this.validator = validator;
    this.auditLogManager = auditLogManager;
    this.fileDBManager = fileDBManager;
    this.historyComparator = historyComparator;
    this.safeguardsManager = safeguardsManager;
  }

  /**
   * In this method it is checked if there is a draft file and it is eliminated
   * 
   * @return Sucess always
   */
  @Override
  public String cancel() {

    // get the path auto save
    Path path = this.getAutoSaveFilePath();
    // if file exist
    if (path.toFile().exists()) {
      // delete the file
      boolean fileDeleted = path.toFile().delete();
    }

    // Set the action No draft
    this.setDraft(false);

    // Put succes message to front
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


  /**
   * Return the absolute path where the bilateral contract is or should be located.
   * 
   * @return complete path where the image is stored
   */
  private String getAnnualReportAbsolutePath() {
    return config.getUploadsBaseFolder() + File.separator + this.getAnualReportRelativePath() + File.separator;
  }

  private String getAnualReportRelativePath() {
    return config.getProjectsBaseFolder(loggedCrp.getAcronym()) + File.separator + project.getId() + File.separator
      + config.getAnualReportFolder();
  }

  public String getAnualReportURL() {
    return config.getDownloadURL() + "/" + this.getAnualReportRelativePath().replace('\\', '/');
  }

  /**
   * The name of the autosave file is constructed and the path is searched
   * 
   * @return Auto save file path
   */
  private Path getAutoSaveFilePath() {
    // get the class simple name
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public String getBaseLineFileURL(String safeguardID) {
    String path = config.getDownloadURL() + "/file.do?" + this.getBaseLineFileUrlPath(safeguardID).replace('\\', '/');
    return path;
  }

  public String getBaseLineFileUrlPath(String safeguardID) {
    return "crp=" + this.getActualPhase().getCrp().getAcronym() + "&category=projects&id=" + safeguardID;
  }

  /**
   * Return the absolute path where the bilateral contract is or should be located.
   * 
   * @return complete path where the image is stored
   */
  private String getBilateralContractAbsolutePath() {
    return config.getUploadsBaseFolder() + File.separator + this.getBilateralProposalRelativePath() + File.separator;
  }

  public String getBilateralContractURL() {
    return config.getDownloadURL() + "/" + this.getBilateralProposalRelativePath().replace('\\', '/');
  }

  private String getBilateralProposalRelativePath() {


    return config.getProjectsBaseFolder(loggedCrp.getAcronym()) + File.separator + project.getId() + File.separator
      + config.getBilateralProjectContractProposalFolder() + File.separator;
  }

  public File getFile() {
    return file;
  }

  public String getFileContentType() {
    return fileContentType;
  }

  public String getFileFileName() {
    return fileFileName;
  }

  public File getFileReporting() {
    return fileReporting;
  }

  public String getFileReportingFileName() {
    return fileReportingFileName;
  }

  /**
   * This method returns an array of flagship ids depending on the project.flagships attribute.
   * 
   * @return an array of integers.
   */
  public long[] getFlagshipIds() {

    List<CrpProgram> projectFocuses = project.getFlagships();

    if (projectFocuses != null && !projectFocuses.isEmpty()) {
      long[] ids = new long[projectFocuses.size()];
      for (int c = 0; c < ids.length; c++) {
        ids[c] = projectFocuses.get(c).getId();
      }
      return ids;
    }
    return null;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }


  public long[] getRegionsIds() {

    List<CrpProgram> projectFocuses = project.getRegions();

    if (projectFocuses != null && !projectFocuses.isEmpty()) {
      long[] ids = new long[projectFocuses.size()];
      for (int c = 0; c < ids.length; c++) {
        ids[c] = projectFocuses.get(c).getId();
      }
      return ids;
    }
    return null;
  }


  public Safeguards getSafeguard() {
    return safeguard;
  }


  public long getSafeguardID() {
    return safeguardID;
  }


  public String getTransaction() {
    return transaction;
  }


  private String getWorkplanRelativePath() {

    return config.getProjectsBaseFolder(loggedCrp.getAcronym()) + File.separator + project.getId() + File.separator
      + config.getProjectWorkplanFolder() + File.separator;
  }


  public String getWorkplanURL() {
    return config.getDownloadURL() + "/" + this.getWorkplanRelativePath().replace('\\', '/');
  }

  /**
   * Return the absolute path where the work plan is or should be located.
   * 
   * @param workplan name
   * @return complete path where the image is stored
   */
  private String getWorplansAbsolutePath() {
    return config.getUploadsBaseFolder() + File.separator + this.getWorkplanRelativePath() + File.separator;
  }


  @Override
  public void prepare() throws Exception {

    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
      // safeguardID =
      // Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.SAFEGUARD_REQUEST_ID)));
    } catch (Exception e) {
      LOG.error("unable to parse projectID", e);
      /**
       * Original code swallows the exception and didn't even log it. Now we at least log it,
       * but we need to revisit to see if we should continue processing or re-throw the exception.
       */
    }

    // We check that you have a TRANSACTION_ID to know if it is history version

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      // auditLogManager.getHistory Bring us the history with the transaction id
      Project history = (Project) auditLogManager.getHistory(transaction);


      // In case there are some relationships that are displayed on the front in a particular field, add to this list by
      // passing the name of the relationship and the name of the attribute with which it is displayed on the front
      Map<String, String> specialList = new HashMap<>();

      // We load the differences of this version with the previous version

      this.setDifferences(new ArrayList<>());
      try {
        this.getDifferences().addAll(historyComparator.getDifferencesList(
          history.getProjecInfoPhase(this.getActualPhase()), transaction, specialList, "project.projectInfo", "", 1));
      } catch (Exception e) {

      }

      if (history != null) {
        project = history;

      } else {
        // not a valid transatacion
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      // get project info for DB
      project = projectManager.getProjectById(projectID);
    }

    if (project != null) {

      // We validate if there is a draft version
      // Get the path
      Path path = this.getAutoSaveFilePath();
      // validate if file exist and user has enabled auto-save funcionallity
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;
        // instace de buffer from file
        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();

        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();

        // instance class AutoSaveReader (made by US)
        AutoSaveReader autoSaveReader = new AutoSaveReader();

        // We read the JSON serialized by the front-end and cast it to the object

        project = (Project) autoSaveReader.readFromJson(jReader);
        // We load some BD objects, since the draft only keeps IDs and some data is shown with a different labe
        Project projectDb = projectManager.getProjectById(project.getId());
        project.getProjectInfo().setPhase(projectDb.getProjecInfoPhase(this.getActualPhase()).getPhase());

        this.setDraft(true);
      } else {
        // DB version
        this.setDraft(false);

        // Load the DB information and adjust it to the structures with which the front end
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        if (project.getProjectInfo() == null) {
          project.setProjectInfo(new ProjectInfo());
        } else {
          List<Safeguards> safeguardsList = new ArrayList<>();
          if (safeguardsManager.findAll() != null) {
            safeguardsList = safeguardsManager.findAll().stream()
              .filter(s -> s != null && s.isActive() && s.getProject() != null && s.getProject().getId() != null
                && s.getProject().getId().equals(this.getProject().getId()) && s.getPhase() != null
                && s.getPhase().getId() != null && s.getPhase().getId().equals(this.getActualPhase().getId()))
              .collect(Collectors.toList());
          }
          if (safeguardsList != null && !safeguardsList.isEmpty()) {
            safeguard = safeguardsList.get(0);
          } else {

            // Create a new safeguard object and record if not exist
            safeguard = new Safeguards();
            safeguard.setProject(project);
            safeguard.setPhase(this.getActualPhase());
            safeguard.setHasFile(false);
            safeguard.setActive(true);

            safeguard = safeguardsManager.saveSafeguards(safeguard);
          }

          if (safeguard != null && safeguard.getFile() != null && safeguard.getFile().getId() != null) {
            safeguard.setHasFile(true);
          } else {
            safeguard.setHasFile(false);
          }
        }

      }
    }

    projectDB = projectManager.getProjectById(projectID);

    // The base permission is established for the current section

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_DESCRIPTION_BASE_PERMISSION, params));

    /*
     * If it is post, the lists are cleaned, this is done because there is a bug of struts, if this is not done it does
     * not delete the items deleted by the user
     */
    if (this.isHttpPost()) {
      project.getProjecInfoPhase(this.getActualPhase()).setLiaisonInstitution(null);
    }

  }

  @Override
  public String save() {

    if (this.hasPermission("canEdit")) {

      // Saving project and add relations we want to save on the history
      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      this.getActionMessages();
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
      // no permissions to edit
      return NOT_AUTHORIZED;
    }

  }


  public void setFile(File file) {
    this.file = file;
  }


  public void setFileContentType(String fileContentType) {
    this.fileContentType = fileContentType;
  }


  public void setFileFileName(String fileFileName) {
    this.fileFileName = fileFileName;
  }

  public void setFileReporting(File fileReporting) {
    this.fileReporting = fileReporting;
  }


  public void setFileReportingFileName(String fileReportingFileName) {
    this.fileReportingFileName = fileReportingFileName;
  }


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setSafeguard(Safeguards safeguard) {
    this.safeguard = safeguard;
  }

  public void setSafeguardID(long safeguardID) {
    this.safeguardID = safeguardID;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    this.setInvalidFields(new HashMap<>());
    // if is saving call the validator to check for the missing fields
    if (save) {
      validator.validate(this, project, safeguard, true);
    }
  }

}