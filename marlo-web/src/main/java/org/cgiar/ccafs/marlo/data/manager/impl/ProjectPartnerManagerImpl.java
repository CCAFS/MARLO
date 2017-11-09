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
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
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
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectPartnerManagerImpl implements ProjectPartnerManager {


  private ProjectPartnerDAO projectPartnerDAO;
  // Managers

  private PhaseDAO phaseDAO;
  private ProjectPartnerPersonDAO projectPartnerPersonDAO;
  private ProjectPartnerLocationDAO projectPartnerLocationDAO;
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
    InstitutionLocationManager institutionLocationManager) {
    this.projectPartnerDAO = projectPartnerDAO;
    this.phaseDAO = phaseDAO;
    this.projectPartnerPersonDAO = projectPartnerPersonDAO;
    this.projectPartnerLocationDAO = projectPartnerLocationDAO;
    this.locElementDAO = locElementDAO;
    this.institutionDAO = institutionDAO;
    this.locElementManager = locElementManager;
    this.institutionLocationManager = institutionLocationManager;
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
        ProjectPartnerLocation partnerLocation = new ProjectPartnerLocation();
        partnerLocation.setInstitutionLocation(institutionLocationDB);
        partnerLocation.setActive(true);
        partnerLocation.setActiveSince(new Date());
        partnerLocation.setCreatedBy(projectPartner.getCreatedBy());
        partnerLocation.setModificationJustification(projectPartner.getModificationJustification());
        partnerLocation.setModifiedBy(projectPartner.getCreatedBy());
        partnerLocation.setProjectPartner(projectPartnerAdd);
        projectPartnerLocationDAO.save(partnerLocation);
      }
    }
  }


  /**
   * clone the persons
   * 
   * @param projectPartner Partner original
   * @param projectPartnerAdd Partner new
   */

  private void addPersons(ProjectPartner projectPartner, ProjectPartner projectPartnerAdd) {

    if (projectPartner.getPartnerPersons() != null) {
      for (ProjectPartnerPerson projectPartnerPerson : projectPartner.getPartnerPersons()) {
        ProjectPartnerPerson projectPartnerPersonAdd = new ProjectPartnerPerson();
        projectPartnerPersonAdd.setActive(true);
        projectPartnerPersonAdd.setActiveSince(projectPartner.getActiveSince());
        projectPartnerPersonAdd.setCreatedBy(projectPartner.getCreatedBy());
        projectPartnerPersonAdd.setModificationJustification(projectPartner.getModificationJustification());
        projectPartnerPersonAdd.setModifiedBy(projectPartner.getCreatedBy());
        projectPartnerPersonAdd.setProjectPartner(projectPartnerAdd);
        projectPartnerPersonAdd.setContactType(projectPartnerPerson.getContactType());
        projectPartnerPersonAdd.setUser(projectPartnerPerson.getUser());
        projectPartnerPersonDAO.save(projectPartnerPersonAdd);
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
    if (phase.getEditable() != null && phase.getEditable() && partners.isEmpty()) {
      ProjectPartner projectPartnerAdd = new ProjectPartner();
      projectPartnerAdd.setActive(true);
      projectPartnerAdd.setActiveSince(projectPartner.getActiveSince());
      projectPartnerAdd.setCreatedBy(projectPartner.getCreatedBy());
      projectPartnerAdd.setInstitution(projectPartner.getInstitution());
      projectPartnerAdd.setModificationJustification(projectPartner.getModificationJustification());
      projectPartnerAdd.setModifiedBy(projectPartner.getModifiedBy());
      projectPartnerAdd.setPhase(phase);
      projectPartnerAdd.setResponsibilities(projectPartner.getResponsibilities());
      projectPartnerAdd.setProject(projectPartner.getProject());
      projectPartnerDAO.save(projectPartnerAdd);

      if (projectPartnerAdd.getId() != null) {
        projectPartnerAdd = projectPartnerDAO.find(projectPartnerAdd.getId());
        this.addPersons(projectPartner, projectPartnerAdd);
        this.addOffices(projectPartner, projectPartnerAdd);
      }

    } else {
      if (phase.getEditable() != null && phase.getEditable()) {
        for (ProjectPartner projectPartnerPrev : partners) {
          projectPartnerPrev.setResponsibilities(projectPartner.getResponsibilities());
          projectPartnerDAO.save(projectPartnerPrev);
          this.updateUsers(projectPartnerPrev, projectPartner);
          this.updateLocations(projectPartnerPrev, projectPartner);
        }
      }
    }

    if (phase.getNext() != null) {
      this.addProjectPartnerDAO(phase.getNext(), projecID, projectPartner);
    }


  }

  @Override
  public void deleteProjectPartner(long projectPartnerId) {

    projectPartnerDAO.deleteProjectPartner(projectPartnerId);
    ProjectPartner projectPartner = this.getProjectPartnerById(projectPartnerId);
    Phase currentPhase = phaseDAO.find(projectPartner.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {

      if (projectPartner.getPhase().getNext() != null) {
        this.deletProjectPartnerPhase(projectPartner.getPhase().getNext(), projectPartner.getProject().getId(),
          projectPartner);
      }
    }
   

  }

  public void deletProjectPartnerPhase(Phase next, long projecID, ProjectPartner projectPartner) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<ProjectPartner> partners = phase.getPartners().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && projectPartner.getInstitution().getId().longValue() == c.getInstitution().getId().longValue())
        .collect(Collectors.toList());
      for (ProjectPartner partner : partners) {
        partner.setActive(false);
        projectPartnerDAO.save(partner);
      }
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
  public ProjectPartner saveProjectPartner(ProjectPartner projectPartner) {

    ProjectPartner resultPartner = projectPartnerDAO.save(projectPartner);
    Phase currentPhase = phaseDAO.find(projectPartner.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectPartner.getPhase().getNext() != null) {
        this.addProjectPartnerDAO(projectPartner.getPhase().getNext(), projectPartner.getProject().getId(),
          projectPartner);
      }
    }
    return resultPartner;

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

        ProjectPartnerLocation projectPartnerLocationDB =
          projectPartnerLocationDAO.find(projectPartnerLocation.getId());
        projectPartnerLocationDB.setActive(false);
        projectPartnerLocationDAO.save(projectPartnerLocationDB);
      }
    }
    if (projectPartner.getSelectedLocations() != null) {
      for (InstitutionLocation institutionLocation : projectPartner.getSelectedLocations()) {
        LocElement locElement =
          locElementManager.getLocElementByISOCode(institutionLocation.getLocElement().getIsoAlpha2());

        if (projectPartnerPrev.getProjectPartnerLocations().stream()
          .filter(c -> c.isActive() && c.getInstitutionLocation().getLocElement().getId().equals(locElement.getId()))
          .collect(Collectors.toList()).isEmpty()) {
          ProjectPartnerLocation partnerLocation = new ProjectPartnerLocation();


          partnerLocation.setInstitutionLocation(
            institutionLocationManager.findByLocation(locElement.getId(), projectPartner.getInstitution().getId()));


          partnerLocation.setActive(true);
          partnerLocation.setActiveSince(new Date());
          partnerLocation.setCreatedBy(projectPartner.getCreatedBy());
          partnerLocation.setModificationJustification("");
          partnerLocation.setModifiedBy(projectPartner.getModifiedBy());
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
        .filter(c -> partnerPerson.getUser() != null && c.getUser().getId().equals(partnerPerson.getUser().getId())
          && c.getContactType().equals(partnerPerson.getContactType()))
        .collect(Collectors.toList()).isEmpty()) {
        partnerPerson.setActive(false);
        projectPartnerPersonDAO.save(partnerPerson);
      }
    }
    if (projectPartner.getPartnerPersons() != null) {
      for (ProjectPartnerPerson partnerPerson : projectPartner.getPartnerPersons()) {
        if (projectPartnerPrev.getProjectPartnerPersons().stream()
          .filter(c -> c.isActive() && c.getUser().equals(partnerPerson.getUser())).collect(Collectors.toList())
          .isEmpty()) {

          ProjectPartnerPerson partnerPersonAdd = new ProjectPartnerPerson();
          ProjectPartner partner = new ProjectPartner();
          partner.setId(projectPartnerPrev.getId());
          partnerPersonAdd.setProjectPartner(partner);
          partnerPersonAdd.setModifiedBy(projectPartnerPrev.getModifiedBy());
          partnerPersonAdd.setActive(true);
          partnerPersonAdd.setActiveSince(projectPartnerPrev.getActiveSince());
          partnerPersonAdd.setContactType(partnerPerson.getContactType());
          partnerPersonAdd.setModificationJustification(projectPartnerPrev.getModificationJustification());
          partnerPersonAdd.setUser(partnerPerson.getUser());
          partnerPersonAdd.setCreatedBy(projectPartnerPrev.getCreatedBy());
          projectPartnerPersonDAO.save(partnerPersonAdd);

        }
      }
    }
  }

}
