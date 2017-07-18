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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerDAO;
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

public class ProjectPartnerMySQLDAO implements ProjectPartnerDAO {

  private StandardDAO dao;

  @Inject
  public ProjectPartnerMySQLDAO(StandardDAO dao) {
    this.dao = dao;
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
        ProjectPartnerLocation partnerLocation = new ProjectPartnerLocation();
        partnerLocation.setInstitutionLocation(institutioLocation);
        partnerLocation.setActive(true);
        partnerLocation.setActiveSince(new Date());
        partnerLocation.setCreatedBy(projectPartner.getCreatedBy());
        partnerLocation.setModificationJustification(projectPartner.getModificationJustification());
        partnerLocation.setModifiedBy(projectPartner.getCreatedBy());
        partnerLocation.setProjectPartner(projectPartner);
        dao.save(partnerLocation);
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
        projectPartnerPersonAdd.setActiveSince(projectPartnerPerson.getActiveSince());
        projectPartnerPersonAdd.setCreatedBy(projectPartnerPerson.getCreatedBy());
        projectPartnerPersonAdd.setModificationJustification(projectPartnerPerson.getModificationJustification());
        projectPartnerPersonAdd.setModifiedBy(projectPartnerPerson.getCreatedBy());
        projectPartnerPersonAdd.setProjectPartner(projectPartnerAdd);
        projectPartnerPersonAdd.setContactType(projectPartnerPersonAdd.getContactType());
        projectPartnerPersonAdd.setUser(projectPartnerPersonAdd.getUser());
        dao.save(projectPartnerPersonAdd);
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
    Phase phase = dao.find(Phase.class, next.getId());
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
      dao.save(projectPartnerAdd);
      this.addPersons(projectPartner, projectPartnerAdd);
      this.addOffices(projectPartner, projectPartnerAdd);
    } else {
      if (phase.getEditable() != null && phase.getEditable()) {
        for (ProjectPartner projectPartnerPrev : partners) {
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
  public boolean deleteProjectPartner(long projectPartnerId) {
    ProjectPartner projectPartner = this.find(projectPartnerId);
    projectPartner.setActive(false);
    long result = this.save(projectPartner);

    if (projectPartner.getPhase().getNext() != null) {
      this.deletProjectPartnerPhase(projectPartner.getPhase().getNext(), projectPartner.getProject().getId(),
        projectPartner);
    }
    return result > 0;
  }

  public void deletProjectPartnerPhase(Phase next, long projecID, ProjectPartner projectPartner) {
    Phase phase = dao.find(Phase.class, next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<ProjectPartner> partners = phase.getPartners().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && projectPartner.getInstitution().getId().longValue() == c.getInstitution().getId().longValue())
        .collect(Collectors.toList());
      for (ProjectPartner partner : partners) {
        this.deleteProjectPartner(partner.getId());
      }
    } else {
      if (phase.getNext() != null) {
        this.deletProjectPartnerPhase(phase.getNext(), projecID, projectPartner);
      }
    }


  }

  @Override
  public boolean existProjectPartner(long projectPartnerID) {
    ProjectPartner projectPartner = this.find(projectPartnerID);
    if (projectPartner == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPartner find(long id) {
    return dao.find(ProjectPartner.class, id);

  }

  @Override
  public List<ProjectPartner> findAll() {
    String query = "from " + ProjectPartner.class.getName() + " where is_active=1";
    List<ProjectPartner> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public long save(ProjectPartner projectPartner) {
    if (projectPartner.getId() == null) {
      dao.save(projectPartner);
    } else {
      dao.update(projectPartner);
    }

    if (projectPartner.getPhase().getNext() != null) {
      this.addProjectPartnerDAO(projectPartner.getPhase().getNext(), projectPartner.getProject().getId(),
        projectPartner);
    }
    return projectPartner.getId();
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
          dao.find(ProjectPartnerLocation.class, projectPartnerLocation.getId());
        projectPartnerLocationDB.setActive(false);
        dao.update(projectPartnerLocationDB);
      }
    }
    if (projectPartner.getSelectedLocations() != null) {
      for (InstitutionLocation institutionLocation : projectPartner.getSelectedLocations()) {
        LocElement locElement = null;
        String queryLoc = "from " + LocElement.class.getName() + " where iso_alpha_2='"
          + institutionLocation.getLocElement().getIsoAlpha2() + "'";
        List<LocElement> listLoc = dao.findAll(queryLoc);
        if (listLoc.size() > 0) {
          locElement = listLoc.get(0);
        }
        if (projectPartnerPrev.getProjectPartnerLocations().stream()
          .filter(
            c -> c.isActive() && c.getInstitutionLocation().getLocElement().getId().equals(listLoc.get(0).getId()))
          .collect(Collectors.toList()).isEmpty()) {
          ProjectPartnerLocation partnerLocation = new ProjectPartnerLocation();


          String query = "from " + InstitutionLocation.class.getName() + "  where institution_id="
            + projectPartner.getInstitution().getId() + " and loc_element_id= " + locElement.getId();
          List<InstitutionLocation> list = dao.findAll(query);
          if (list.size() > 0) {
            partnerLocation.setInstitutionLocation(list.get(0));
          }
          partnerLocation.setActive(true);
          partnerLocation.setActiveSince(new Date());
          partnerLocation.setCreatedBy(projectPartner.getCreatedBy());
          partnerLocation.setModificationJustification("");
          partnerLocation.setModifiedBy(projectPartner.getModifiedBy());
          partnerLocation.setProjectPartner(projectPartnerPrev);
          dao.save(partnerLocation);
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
        .filter(c -> c.getUser().getId().equals(partnerPerson.getUser().getId())
          && c.getContactType().equals(partnerPerson.getContactType()))
        .collect(Collectors.toList()).isEmpty()) {
        partnerPerson.setActive(false);
        dao.update(partnerPerson);
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
          dao.update(partnerPersonAdd);

        }
      }
    }
  }


}