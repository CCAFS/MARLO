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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.InstitutionLocationDAO;
import org.cgiar.ccafs.marlo.data.dao.LocElementDAO;
import org.cgiar.ccafs.marlo.data.dao.PartnerDivisionDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerContributionDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerLocationDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerPersonDAO;
import org.cgiar.ccafs.marlo.data.manager.InstitutionLocationManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectPartnerManagerImpl implements ProjectPartnerManager {


  private ProjectPartnerDAO projectPartnerDAO;
  // Managers

  private PhaseDAO phaseDAO;
  private ProjectPartnerPersonDAO projectPartnerPersonDAO;
  private ProjectPartnerLocationDAO projectPartnerLocationDAO;
  private ProjectPartnerContributionDAO projectPartnerContributionDAO;
  private PartnerDivisionDAO partnerDivisionDAO;

  private LocElementDAO locElementDAO;
  private LocElementManager locElementManager;
  private InstitutionLocationManager institutionLocationManager;

  private InstitutionLocationDAO institutionDAO;


  public ProjectPartnerManagerImpl() {
    // TODO Auto-generated constructor stub
  }

  @Inject
  public ProjectPartnerManagerImpl(ProjectPartnerDAO projectPartnerDAO, PhaseDAO phaseDAO,
    ProjectPartnerPersonDAO projectPartnerPersonDAO, ProjectPartnerLocationDAO projectPartnerLocationDAO,
    LocElementDAO locElementDAO, InstitutionLocationDAO institutionDAO, LocElementManager locElementManager,
    ProjectPartnerContributionDAO projectPartnerContributionDAO, InstitutionLocationManager institutionLocationManager,
    PartnerDivisionDAO partnerDivisionDAO) {
    this.projectPartnerDAO = projectPartnerDAO;
    this.phaseDAO = phaseDAO;
    this.projectPartnerPersonDAO = projectPartnerPersonDAO;
    this.projectPartnerLocationDAO = projectPartnerLocationDAO;
    this.locElementDAO = locElementDAO;
    this.institutionDAO = institutionDAO;
    this.locElementManager = locElementManager;
    this.projectPartnerContributionDAO = projectPartnerContributionDAO;
    this.institutionLocationManager = institutionLocationManager;
    this.partnerDivisionDAO = partnerDivisionDAO;
  }

  /**
   * clone the contributors
   * 
   * @param projectPartner Partner original
   * @param projectPartnerAdd Partner new
   */

  private void addContributors(ProjectPartner projectPartner, ProjectPartner projectPartnerAdd, Phase phase) {

    if (projectPartner.getPartnerContributors() != null) {
      for (ProjectPartnerContribution partnerContribution : projectPartner.getPartnerContributors()) {

        ProjectPartnerContribution projectPartnerContribution = new ProjectPartnerContribution();
        projectPartnerContribution.setProjectPartner(projectPartnerAdd);
        projectPartnerContribution.setProjectPartnerContributor(projectPartnerDAO.getPartnerPhase(phase,
          projectPartner.getProject(), partnerContribution.getProjectPartnerContributor().getInstitution()));

        if (projectPartnerContribution.getProjectPartnerContributor() != null) {
          projectPartnerContributionDAO.save(projectPartnerContribution);
        }
      }
    }
  }

  /**
   * clone the offices
   * 
   * @param projectPartner Partner original
   * @param projectPartnerAdd Partner new
   */

  private void addOffices(ProjectPartner projectPartner, ProjectPartner projectPartnerAdd) {

    if (projectPartner.getSelectedLocations() != null) {
      for (InstitutionLocation institutioLocation : projectPartner.getSelectedLocations()) {
        LocElement locElement = locElementDAO.findISOCode(institutioLocation.getLocElement().getIsoAlpha2());
        InstitutionLocation institutionLocationDB =
          institutionDAO.findByLocation(locElement.getId(), projectPartner.getInstitution().getId());
        if (institutionLocationDB != null) {
          ProjectPartnerLocation partnerLocation = new ProjectPartnerLocation();
          partnerLocation.setInstitutionLocation(institutionLocationDB);
          partnerLocation.setProjectPartner(projectPartnerAdd);
          projectPartnerLocationDAO.save(partnerLocation);
        }
      }
    }
  }


  /**
   * clone the persons
   * 
   * @param projectPartner Partner original
   * @param projectPartnerAdd Partner new
   */

  private void addPersons(ProjectPartner projectPartner, Long newPartern) {

    if (projectPartner.getPartnerPersons() != null) {
      for (ProjectPartnerPerson projectPartnerPerson : projectPartner.getPartnerPersons()) {
        ProjectPartnerPerson projectPartnerPersonAdd = new ProjectPartnerPerson();
        projectPartnerPersonAdd.setProjectPartner(projectPartnerDAO.find(newPartern));
        projectPartnerPersonAdd.setContactType(projectPartnerPerson.getContactType());
        projectPartnerPersonAdd.setUser(projectPartnerPerson.getUser());

        if (projectPartnerPerson.getPartnerDivision() != null
          && projectPartnerPerson.getPartnerDivision().getId() != null) {
          projectPartnerPersonAdd
            .setPartnerDivision(partnerDivisionDAO.find(projectPartnerPerson.getPartnerDivision().getId()));
        }

        if (projectPartnerPersonAdd.getUser() != null && projectPartnerPersonAdd.getUser().getId() != null) {
          projectPartnerPersonAdd = projectPartnerPersonDAO.save(projectPartnerPersonAdd);
        }

        int a = 0;
      }
    }
  }

  /**
   * clone or update the partner for next phases
   * 
   * @param next the next phase to clone
   * @param projecID the project id we ar working
   * @param projectPartner the project partner to clone
   */
  private void addProjectPartnerDAO(Phase next, long projecID, ProjectPartner projectPartner) {
    Phase phase = phaseDAO.find(next.getId());
    List<ProjectPartner> partners = phase.getPartners().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
        && projectPartner.getInstitution().getId().longValue() == c.getInstitution().getId().longValue())
      .collect(Collectors.toList());

    if (partners.isEmpty()) {
      ProjectPartner projectPartnerAdd = new ProjectPartner();
      projectPartnerAdd.setInstitution(projectPartner.getInstitution());
      projectPartnerAdd.setPhase(phase);
      projectPartnerAdd.setResponsibilities(projectPartner.getResponsibilities());
      projectPartnerAdd.setProject(projectPartner.getProject());
      projectPartnerAdd.setSubDepartment(projectPartner.getSubDepartment());
      projectPartnerAdd = projectPartnerDAO.save(projectPartnerAdd);

      if (projectPartnerAdd.getId() != null) {

        this.addPersons(projectPartner, projectPartnerAdd.getId());
        this.addContributors(projectPartner, projectPartnerAdd, phase);
        this.addOffices(projectPartner, projectPartnerAdd);
      }

    } else {

      for (ProjectPartner projectPartnerPrev : partners) {
        projectPartnerPrev.setResponsibilities(projectPartner.getResponsibilities());
        projectPartnerPrev.setSubDepartment(projectPartner.getSubDepartment());
        projectPartnerPrev = projectPartnerDAO.save(projectPartnerPrev);
        this.updateUsers(projectPartnerPrev, projectPartner);
        this.updateLocations(projectPartnerPrev, projectPartner);
        this.updateContributors(projectPartnerPrev, projectPartner, phase);
      }

    }


    if (phase.getNext() != null) {
      this.addProjectPartnerDAO(phase.getNext(), projecID, projectPartner);

    }


  }

  private ProjectPartner copyPartner(Phase next, long projecID, ProjectPartner projectPartner) {
    Phase phase = phaseDAO.find(next.getId());
    List<ProjectPartner> partners = phase.getPartners().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
        && projectPartner.getInstitution().getId().longValue() == c.getInstitution().getId().longValue())
      .collect(Collectors.toList());

    if (partners.isEmpty()) {
      ProjectPartner projectPartnerAdd = new ProjectPartner();
      projectPartnerAdd.setInstitution(projectPartner.getInstitution());
      projectPartnerAdd.setPhase(phase);
      projectPartnerAdd.setResponsibilities(projectPartner.getResponsibilities());
      projectPartnerAdd.setProject(projectPartner.getProject());
      projectPartnerAdd = projectPartnerDAO.save(projectPartnerAdd);


      this.addPersons(projectPartner, projectPartnerAdd.getId());
      this.addContributors(projectPartner, projectPartnerAdd, phase);
      this.addOffices(projectPartner, projectPartnerAdd);

      return projectPartnerAdd;
    }
    return null;


  }

  @Override
  public ProjectPartner copyPartner(ProjectPartner projectPartner, Phase phase) {

    return this.copyPartner(phase, projectPartner.getProject().getId(), projectPartner);

  }

  @Override
  public void deleteProjectPartner(long projectPartnerId) {


    ProjectPartner projectPartner = this.getProjectPartnerById(projectPartnerId);
    projectPartnerDAO.deleteProjectPartner(projectPartnerId);
    for (ProjectPartnerContribution projectPartnerContribution : projectPartner.getProjectPartnerContributions()
      .stream().filter(c -> c.isActive()).collect(Collectors.toList())) {

      projectPartnerContributionDAO.deleteProjectPartnerContribution(projectPartnerContribution.getId());
    }
    for (ProjectPartnerPerson projectPartnerPerson : projectPartner.getProjectPartnerPersons().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {
      projectPartnerPersonDAO.deleteProjectPartnerPerson(projectPartnerPerson.getId());
    }
    for (ProjectPartnerLocation projectPartnerLocation : projectPartner.getProjectPartnerLocations().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {

      projectPartnerLocationDAO.deleteProjectPartnerLocation(projectPartnerLocation.getId());
    }
    Phase currentPhase = phaseDAO.find(projectPartner.getPhase().getId());

    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (projectPartner.getPhase().getNext() != null && projectPartner.getPhase().getNext().getNext() != null) {
    // Phase upkeepPhase = projectPartner.getPhase().getNext().getNext();
    // if (upkeepPhase != null) {
    // this.deletProjectPartnerPhase(upkeepPhase, projectPartner.getProject().getId(), projectPartner);
    // }
    // }
    // }

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {

      if (projectPartner.getPhase().getNext() != null) {
        this.deletProjectPartnerPhase(projectPartner.getPhase().getNext(), projectPartner.getProject().getId(),
          projectPartner);
      }
    }

  }


  public void deletProjectPartnerPhase(Phase next, long projecID, ProjectPartner projectPartner) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPartner> partners = phase.getPartners().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
        && projectPartner.getInstitution().getId().longValue() == c.getInstitution().getId().longValue())
      .collect(Collectors.toList());
    for (ProjectPartner partner : partners) {

      for (ProjectPartnerContribution projectPartnerContribution : partner.getProjectPartnerContributions().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {
        projectPartnerContributionDAO.deleteProjectPartnerContribution(projectPartnerContribution.getId());
      }
      for (ProjectPartnerPerson projectPartnerPerson : partner.getProjectPartnerPersons().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {
        projectPartnerPersonDAO.deleteProjectPartnerPerson(projectPartnerPerson.getId());
      }
      for (ProjectPartnerLocation projectPartnerLocation : partner.getProjectPartnerLocations().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {
        projectPartnerLocationDAO.deleteProjectPartnerLocation(projectPartnerLocation.getId());
      }

      projectPartnerDAO.deleteProjectPartner(partner.getId());
    }

    if (phase.getNext() != null) {
      this.deletProjectPartnerPhase(phase.getNext(), projecID, projectPartner);
    }


  }

  @Override
  public boolean existProjectPartner(long projectPartnerID) {

    return projectPartnerDAO.existProjectPartner(projectPartnerID);
  }

  @Override
  public List<ProjectPartner> findAll() {

    return projectPartnerDAO.findAll();

  }

  @Override
  public List<ProjectPartner> findAllByPhaseProject(long projectId, long phaseId) {

    return projectPartnerDAO.findAllByPhaseProject(projectId, phaseId);

  }

  @Override
  public List<ProjectPartner> findAllByPhaseProjectAndInstitution(long projectId, long phaseId, long institutionId) {

    return projectPartnerDAO.findAllByPhaseProjectAndInstitution(projectId, phaseId, institutionId);

  }

  @Override
  public ProjectPartner getProjectPartnerById(long projectPartnerID) {

    return projectPartnerDAO.find(projectPartnerID);
  }

  @Override
  public ProjectPartner getProjectPartnerByIdAndEagerFetchLocations(long projectPartnerID) {
    return projectPartnerDAO.getProjectPartnerByIdAndEagerFetchLocations(projectPartnerID);
  }

  @Override
  public List<ProjectPartner> getProjectPartnersForProjectWithActiveProjectPartnerPersons(long projectId) {
    return projectPartnerDAO.getProjectPartnersForProjectWithActiveProjectPartnerPersons(projectId);
  }

  @Override
  public List<ProjectPartner> getProjectPartnersForProjectWithActiveProjectPhasePartnerPersons(long projectId,
    long phaseId) {
    return projectPartnerDAO.getProjectPartnersForProjectWithActiveProjectPhasePartnerPersons(projectId, phaseId);
  }

  @Override
  public ProjectPartner saveProjectPartner(ProjectPartner projectPartner) {

    ProjectPartner resultPartner = projectPartnerDAO.save(projectPartner);
    Phase currentPhase = phaseDAO.find(projectPartner.getPhase().getId());

    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = projectPartner.getPhase().getNext().getNext();
    // if (upkeepPhase != null) {
    // this.addProjectPartnerDAO(upkeepPhase, projectPartner.getProject().getId(), projectPartner);
    // }
    // }
    // }

    // if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
    if (projectPartner.getPhase().getNext() != null) {
      this.addProjectPartnerDAO(projectPartner.getPhase().getNext(), projectPartner.getProject().getId(),
        projectPartner);
    }
    // }


    return resultPartner;

  }

  /**
   * check the offices and updated
   * 
   * @param projectPartnerPrev partern to update
   * @param projectPartner partner modified
   */
  private void updateContributors(ProjectPartner projectPartnerPrev, ProjectPartner projectPartner, Phase phase) {
    for (ProjectPartnerContribution projectPartnerContribution : projectPartnerPrev.getProjectPartnerContributions()
      .stream().filter(c -> c.isActive()).collect(Collectors.toList())) {

      if (projectPartner.getPartnerContributors() == null || projectPartner.getPartnerContributors().stream()
        .filter(c -> c.getProjectPartnerContributor().getInstitution().getId()
          .equals(projectPartnerContribution.getProjectPartnerContributor().getInstitution().getId()))
        .collect(Collectors.toList()).isEmpty()) {

        projectPartnerContributionDAO.deleteProjectPartnerContribution(projectPartnerContribution.getId());

      }
    }
    if (projectPartner.getPartnerContributors() != null) {
      for (ProjectPartnerContribution projectPartnerContribution : projectPartner.getPartnerContributors()) {

        if (projectPartnerPrev.getProjectPartnerContributions().stream()
          .filter(c -> c.isActive() && c.getProjectPartnerContributor().getInstitution().getId()
            .equals(projectPartnerContribution.getProjectPartnerContributor().getInstitution().getId()))
          .collect(Collectors.toList()).isEmpty()) {
          ProjectPartnerContribution projectPartnerContributionAdd = new ProjectPartnerContribution();
          projectPartnerContributionAdd.setProjectPartner(projectPartnerPrev);
          projectPartnerContributionAdd.setProjectPartnerContributor(projectPartnerDAO.getPartnerPhase(phase,
            projectPartner.getProject(), projectPartnerContribution.getProjectPartnerContributor().getInstitution()));
          if (projectPartnerContributionAdd.getProjectPartnerContributor() != null) {
            projectPartnerContributionDAO.save(projectPartnerContributionAdd);
          }

        }
      }
    }
  }


  /**
   * check the offices and updated
   * 
   * @param projectPartnerPrev partern to update
   * @param projectPartner partner modified
   */
  private void updateLocations(ProjectPartner projectPartnerPrev, ProjectPartner projectPartner) {
    for (ProjectPartnerLocation projectPartnerLocation : projectPartnerPrev.getProjectPartnerLocations().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {

      if (projectPartner.getSelectedLocations() == null || projectPartner.getSelectedLocations().stream()
        .filter(c -> c.getLocElement().getIsoAlpha2()
          .equals(projectPartnerLocation.getInstitutionLocation().getLocElement().getIsoAlpha2()))
        .collect(Collectors.toList()).isEmpty()) {

        projectPartnerLocationDAO.deleteProjectPartnerLocation(projectPartnerLocation.getId());
      }
    }
    if (projectPartner.getSelectedLocations() != null) {
      for (InstitutionLocation institutionLocation : projectPartner.getSelectedLocations()) {
        LocElement locElement =
          locElementManager.getLocElementByISOCode(institutionLocation.getLocElement().getIsoAlpha2());

        if (locElement != null && projectPartner.getInstitution() != null
          && projectPartnerPrev.getProjectPartnerLocations().stream()
            .filter(c -> c.isActive() && c.getInstitutionLocation().getLocElement().getId().equals(locElement.getId()))
            .collect(Collectors.toList()).isEmpty()) {
          ProjectPartnerLocation partnerLocation = new ProjectPartnerLocation();


          partnerLocation.setInstitutionLocation(
            institutionLocationManager.findByLocation(locElement.getId(), projectPartner.getInstitution().getId()));


          partnerLocation.setProjectPartner(projectPartnerPrev);
          projectPartnerLocationDAO.save(partnerLocation);
        }
      }
    }
  }

  /**
   * check the partners persons and updated
   * 
   * @param projectPartnerPrev partern to update
   * @param projectPartner partner modified
   */
  private void updateUsers(ProjectPartner projectPartnerPrev, ProjectPartner projectPartner) {
    for (ProjectPartnerPerson partnerPerson : projectPartnerPrev.getProjectPartnerPersons().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {
      if (projectPartner.getPartnerPersons() == null || projectPartner.getPartnerPersons().stream()
        .filter(c -> partnerPerson.getUser() != null && partnerPerson.getUser().getId() != null && c.getUser() != null
          && c.getUser().getId() != null && c.getUser().getId().equals(partnerPerson.getUser().getId())
          && c.getContactType() != null && c.getContactType().equals(partnerPerson.getContactType()))
        .collect(Collectors.toList()).isEmpty()) {
        projectPartnerPersonDAO.deleteProjectPartnerPerson(partnerPerson.getId());
      }
    }
    if (projectPartner.getPartnerPersons() != null) {
      for (ProjectPartnerPerson partnerPerson : projectPartner.getPartnerPersons()) {
        if (projectPartnerPrev.getProjectPartnerPersons().stream()
          .filter(c -> c.isActive() && c.getUser() != null && partnerPerson.getUser() != null
            && partnerPerson.getUser().getId() != null && c.getUser().getId() != null
            && c.getUser().getId().equals(partnerPerson.getUser().getId()))
          .collect(Collectors.toList()).isEmpty()) {

          ProjectPartnerPerson partnerPersonAdd = new ProjectPartnerPerson();
          ProjectPartner partner = new ProjectPartner();
          partner.setId(projectPartnerPrev.getId());
          partnerPersonAdd.setProjectPartner(partner);
          partnerPersonAdd.setContactType(partnerPerson.getContactType());
          partnerPersonAdd.setUser(partnerPerson.getUser());
          if (partnerPerson.getPartnerDivision() != null && partnerPerson.getPartnerDivision().getId() != null) {
            partnerPersonAdd.setPartnerDivision(partnerDivisionDAO.find(partnerPerson.getPartnerDivision().getId()));
          }
          if (partnerPersonAdd.getUser() != null && partnerPersonAdd.getUser().getId() != null) {
            projectPartnerPersonDAO.save(partnerPersonAdd);
          }
        }
      }
    }
  }

}
