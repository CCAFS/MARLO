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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceDivisionManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInfoManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceLocationsManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerDivisionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingSourceDivision;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * FundingSourceReplicationAction:
 * 
 * @author Kenji Tanaka - CIAT/CCAFS
 */
public class FundingSourcesReplicationAction extends BaseAction {

  private static final long serialVersionUID = 6392973543544674655L;

  private static Logger logger = LoggerFactory.getLogger(FundingSourcesReplicationAction.class);

  // Managers
  private GlobalUnitManager globalUnitManager;
  private PhaseManager phaseManager;
  private ProjectPartnerManager projectPartnerManager;
  private FundingSourceManager fundingSourceManager;
  private FundingSourceInfoManager fundingSourceInfoManager;
  private FundingSourceBudgetManager fundingSourceBudgetManager;
  private FundingSourceInstitutionManager fundingSourceInstitutionManager;
  private FundingSourceDivisionManager fundingSourceDivisionManager;
  private ProjectBudgetManager projectBudgetManager;
  private FundingSourceLocationsManager fundingSourceLocationsManager;

  // Variables
  private String entityByPhaseList;
  private List<GlobalUnit> crps;
  private long selectedPhaseID;
  private Phase phase;
  private FundingSource fundingSource;

  @Inject
  public FundingSourcesReplicationAction(APConfig config, PhaseManager phaseManager,
    GlobalUnitManager globalUnitManager, FundingSourceManager fundingSourceManager,
    ProjectPartnerManager projectPartnerManager, FundingSourceInfoManager fundingSourceInfoManager,
    FundingSourceBudgetManager fundingSourceBudgetManager,
    FundingSourceInstitutionManager fundingSourceInstitutionManager,
    FundingSourceDivisionManager fundingSourceDivisionManager, PartnerDivisionManager partnerDivisionManager,
    ProjectBudgetManager projectBudgetManager, FundingSourceLocationsManager fundingSourceLocationsManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.projectPartnerManager = projectPartnerManager;
    this.fundingSourceManager = fundingSourceManager;
    this.fundingSourceInfoManager = fundingSourceInfoManager;
    this.fundingSourceBudgetManager = fundingSourceBudgetManager;
    this.fundingSourceInstitutionManager = fundingSourceInstitutionManager;
    this.fundingSourceDivisionManager = fundingSourceDivisionManager;
    this.projectBudgetManager = projectBudgetManager;
    this.fundingSourceLocationsManager = fundingSourceLocationsManager;
  }


  private Path getAutoSaveFilePath() {

    // get the class simple name
    String composedClassName = fundingSource.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = fundingSource.getId() + "_" + composedClassName + "_" + phase.getName() + "_"
      + phase.getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);

  }

  public List<GlobalUnit> getCrps() {
    return crps;
  }


  public String getEntityByPhaseList() {
    return entityByPhaseList;
  }


  public long getSelectedPhaseID() {
    return selectedPhaseID;
  }


  /**
   * HJ 08/01/2019
   * 
   * @param institutionId
   * @return
   */
  public List<User> getUserList(Long institutionId, Long projectID) {

    List<User> users = new ArrayList<>();

    List<ProjectPartner> partnersTmp = projectPartnerManager.findAll().stream()
      .filter(pp -> pp.isActive() && pp.getProject().getId().equals(projectID)
        && pp.getPhase().getId().equals(this.getSelectedPhaseID()) && pp.getInstitution().getId().equals(institutionId))
      .collect(Collectors.toList());

    if (partnersTmp != null && !partnersTmp.isEmpty()) {
      ProjectPartner projectPartner = partnersTmp.get(0);
      List<ProjectPartnerPerson> partnerPersons = new ArrayList<>(
        projectPartner.getProjectPartnerPersons().stream().filter(pp -> pp.isActive()).collect(Collectors.toList()));
      for (ProjectPartnerPerson projectPartnerPerson : partnerPersons) {

        users.add(projectPartnerPerson.getUser());
      }
    }

    return users;
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();
    crps = globalUnitManager.findAll().stream().filter(c -> c.isMarlo() && c.isActive()).collect(Collectors.toList());
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (entityByPhaseList != null && !entityByPhaseList.isEmpty()) {
        logger.debug("Start replication for phase: " + selectedPhaseID);
        phase = phaseManager.getPhaseById(selectedPhaseID);

        for (String id : entityByPhaseList.trim().split(",")) {
          logger.debug("Replicating Funding source: " + id);


          fundingSource = fundingSourceManager.getFundingSourceById(new Long(id.trim()));
          if (fundingSource.getFundingSourceInfo(phase) != null) {

            // Load relations for auditlog
            List<String> relationsName = new ArrayList<>();
            relationsName.add(APConstants.FUNDING_SOURCES_BUDGETS_RELATION);
            relationsName.add(APConstants.FUNDING_SOURCES_LOCATIONS_RELATION);
            relationsName.add(APConstants.FUNDING_SOURCES_INFO);
            relationsName.add(APConstants.FUNDING_SOURCES_INSTITUTIONS_RELATION);
            relationsName.add(APConstants.FUNDING_SOURCES_DIVISIONS_RELATION);

            // Save Funding Source Budget
            fundingSource.setBudgets(fundingSource.getFundingSourceBudgets().stream()
              .filter(pb -> pb.isActive() && pb.getPhase().equals(phase)).collect(Collectors.toList()));
            if (fundingSource.getBudgets() != null) {
              List<FundingSourceBudget> fundingSourceBudgets = fundingSource.getBudgets();

              if (fundingSourceBudgets != null && !fundingSourceBudgets.isEmpty()) {
                for (FundingSourceBudget fundingSourceBudget : fundingSourceBudgets) {
                  fundingSourceBudgetManager.saveFundingSourceBudget(fundingSourceBudget);
                }
              }
            }

            // Save Funding Source Institution
            fundingSource.setInstitutions(new ArrayList<>(fundingSource.getFundingSourceInstitutions().stream()
              .filter(pb -> pb.isActive() && pb.getPhase() != null && pb.getPhase().equals(phase))
              .collect(Collectors.toList())));
            if (fundingSource.getInstitutions() != null) {
              List<FundingSourceInstitution> fundingSourceInstitutions = fundingSource.getInstitutions();

              if (fundingSourceInstitutions != null && !fundingSourceInstitutions.isEmpty()) {
                for (FundingSourceInstitution fundingSourceInstitution : fundingSourceInstitutions) {
                  fundingSourceInstitutionManager.saveFundingSourceInstitution(fundingSourceInstitution);
                }
              }
            }

            // Save Funding Source Divisions
            fundingSource.setDivisions(new ArrayList<>(fundingSource.getFundingSourceDivisions().stream()
              .filter(pb -> pb.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
            if (fundingSource.getDivisions() != null) {
              List<FundingSourceDivision> fundingSourceDivisions = fundingSource.getDivisions();

              if (fundingSourceDivisions != null && !fundingSourceDivisions.isEmpty()) {
                for (FundingSourceDivision fundingSourceDivision : fundingSourceDivisions) {
                  fundingSourceDivisionManager.saveFundingSourceDivision(fundingSourceDivision);
                }
              }
            }

            // Save Project Budget
            fundingSource.setProjectBudgetsList(fundingSource.getProjectBudgets().stream()
              .filter(pb -> pb.isActive() && pb.getProject().isActive() && pb.getPhase() != null
                && pb.getPhase().equals(phase) && pb.getProject().getProjecInfoPhase(phase) != null)
              .collect(Collectors.toList()));
            if (fundingSource.getProjectBudgetsList() != null) {
              List<ProjectBudget> projectBudgets = fundingSource.getProjectBudgetsList();

              if (projectBudgets != null && !projectBudgets.isEmpty()) {
                for (ProjectBudget projectBudget : projectBudgets) {
                  projectBudgetManager.saveProjectBudget(projectBudget);
                }
              }
            }

            // Save Funding Source Locations
            fundingSource.setFundingCountry(fundingSource.getFundingSourceLocations().stream()
              .filter(fl -> fl.isActive() && fl.getPhase().equals(phase)).collect(Collectors.toList()));
            if (fundingSource.getFundingSourceLocations() != null) {
              List<FundingSourceLocation> fundingSourceLocations = fundingSource.getFundingCountry();

              if (fundingSourceLocations != null && !fundingSourceLocations.isEmpty()) {
                for (FundingSourceLocation FundingSourceLocation : fundingSourceLocations) {
                  fundingSourceLocationsManager.saveFundingSourceLocations(FundingSourceLocation);
                }
              }
            }

            Phase previousPhase = this.phaseManager.findPreviousPhase(phase.getId());
            // Get missing has research file selection and lead center missing info from previous phase
            if (fundingSource.getFundingSourceInfo(phase) != null && previousPhase != null) {
              if (fundingSource.getFundingSourceInfo(phase).getHasFileResearch() == null) {
                Boolean fileResearch = fundingSource.getFundingSourceInfo(previousPhase).getHasFileResearch();
                fundingSource.getFundingSourceInfo(phase).setHasFileResearch(fileResearch);
              }

              if (fundingSource.getFundingSourceInfo(phase).getLeadCenter() == null
                && fundingSource.getFundingSourceInfo(previousPhase).getLeadCenter() != null) {
                Institution leadCenter = fundingSource.getFundingSourceInfo(previousPhase).getLeadCenter();
                fundingSource.getFundingSourceInfo(phase).setLeadCenter(leadCenter);
              }
            }

            // Save Funding Source Info
            fundingSource.getFundingSourceInfo(phase).setModificationJustification(this.getJustification());
            fundingSourceInfoManager.saveFundingSourceInfo(fundingSource.getFundingSourceInfo(phase));

            // Save Funding Source
            this.setModificationJustification(fundingSource);
            fundingSourceManager.saveFundingSource(fundingSource, this.getActionName(), relationsName, phase);

            // Delete autosave
            Path path = this.getAutoSaveFilePath();

            if (path.toFile().exists()) {
              path.toFile().delete();
            }


          }

        }

        logger.debug("Finished replication succesfully");
      } else {
        logger.debug("No funding sources selected");
      }
      return SUCCESS;
    } else

    {
      return NOT_AUTHORIZED;
    }
  }

  public void setCrps(List<GlobalUnit> crps) {
    this.crps = crps;
  }

  public void setEntityByPhaseList(String entityByPhaseList) {
    this.entityByPhaseList = entityByPhaseList;
  }


  public void setSelectedPhaseID(long selectedPhaseID) {
    this.selectedPhaseID = selectedPhaseID;
  }


}