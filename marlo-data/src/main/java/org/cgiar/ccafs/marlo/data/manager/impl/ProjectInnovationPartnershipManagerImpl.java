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
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationPartnershipDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationPartnershipPersonDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnership;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnershipPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationPartnershipManagerImpl implements ProjectInnovationPartnershipManager {

  private ProjectInnovationPartnershipDAO projectInnovationPartnershipDAO;
  private ProjectInnovationPartnershipPersonDAO projectInnovationPartnershipPersonDAO;
  private PhaseDAO phaseDAO;
  private ProjectPartnerManager projectPartnerManager;

  private final Logger logger = LoggerFactory.getLogger(ProjectInnovationPartnershipManagerImpl.class);

  @Inject
  public ProjectInnovationPartnershipManagerImpl(ProjectInnovationPartnershipDAO projectInnovationPartnershipDAO,
    ProjectInnovationPartnershipPersonDAO projectInnovationPartnershipPersonDAO, PhaseDAO phaseDAO,
    ProjectPartnerManager projectPartnerManager) {
    this.projectInnovationPartnershipDAO = projectInnovationPartnershipDAO;
    this.projectInnovationPartnershipPersonDAO = projectInnovationPartnershipPersonDAO;
    this.phaseDAO = phaseDAO;
    this.projectPartnerManager = projectPartnerManager;
  }

  public void addPersons(ProjectInnovationPartnership projectInnovationPartnership,
    Long newProjectInnovationPartnershipId) {


    if (projectInnovationPartnership.getPartnershipPersons() != null) {

      for (ProjectInnovationPartnershipPerson projectInnovationPartnershipsPerson : projectInnovationPartnership
        .getPartnershipPersons()) {
        if (projectInnovationPartnershipsPerson.getUser() != null
          && projectInnovationPartnershipsPerson.getUser().getId() != null) {
          ProjectInnovationPartnershipPerson projectInnovationPartnershipsPersonSave =
            new ProjectInnovationPartnershipPerson();
          projectInnovationPartnershipsPersonSave.setProjectInnovationPartnership(
            this.getProjectInnovationPartnershipById(newProjectInnovationPartnershipId));
          projectInnovationPartnershipsPersonSave.setUser(projectInnovationPartnershipsPerson.getUser());

          projectInnovationPartnershipPersonDAO.save(projectInnovationPartnershipsPersonSave);
        }
      }
    }

  }

  @Override
  public void deleteProjectInnovationPartnership(long projectInnovationPartnershipId) {

    ProjectInnovationPartnership projectInnovationPartnership =
      this.getProjectInnovationPartnershipById(projectInnovationPartnershipId);
    Phase currentPhase = projectInnovationPartnership.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteProjectInnovationPartnershipPhase(projectInnovationPartnership.getPhase().getNext(),
        projectInnovationPartnership.getProjectInnovation().getId(), projectInnovationPartnership);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectInnovationPartnershipPhase(upkeepPhase,
            projectInnovationPartnership.getProjectInnovation().getId(), projectInnovationPartnership);
        }
      }
    }

    projectInnovationPartnershipDAO.deleteProjectInnovationPartnership(projectInnovationPartnershipId);
  }


  public void deleteProjectInnovationPartnershipPhase(Phase next, long innovationID,
    ProjectInnovationPartnership projectInnovationPartnership) {
    if (projectInnovationPartnership != null) {
      Phase phase = phaseDAO.find(next.getId());

      if (phase.getProjectInnovationPartnerships() != null) {
        List<ProjectInnovationPartnership> projectInnovationPartnershipList =
          phase.getProjectInnovationPartnerships().stream()
            .filter(c -> c.isActive() && c.getProjectInnovation().getId().longValue() == innovationID
              && c.getInstitution() != null && c != null && c.getInstitution().getId() != null
              && projectInnovationPartnership != null && projectInnovationPartnership.getInstitution() != null
              && projectInnovationPartnership.getInstitution().getId() != null
              && projectInnovationPartnership.getProjectInnovationPartnerType() != null
              && c.getInstitution().getId().equals(projectInnovationPartnership.getInstitution().getId())
              && c.getProjectInnovationPartnerType().getId()
                .equals(projectInnovationPartnership.getProjectInnovationPartnerType().getId()))
            .collect(Collectors.toList());

        for (ProjectInnovationPartnership projectInnovationPartnershipDB : projectInnovationPartnershipList) {

          if (projectInnovationPartnershipDB.getProjectInnovationPartnershipPersons() != null) {
            List<ProjectInnovationPartnershipPerson> projectInnovationPartnershipsPersons =
              new ArrayList<>(projectInnovationPartnershipDB.getProjectInnovationPartnershipPersons().stream()
                .filter(dup -> dup.isActive()).collect(Collectors.toList()));
            for (ProjectInnovationPartnershipPerson projectInnovationPartnershipsPerson : projectInnovationPartnershipsPersons) {
              this.projectInnovationPartnershipPersonDAO
                .deleteProjectInnovationPartnershipPerson(projectInnovationPartnershipsPerson.getId());
            }
          }
          if (projectInnovationPartnershipDB != null && projectInnovationPartnershipDB.getId() != null) {
            projectInnovationPartnershipDAO.deleteProjectInnovationPartnership(projectInnovationPartnershipDB.getId());
          }
        }

        if (phase.getNext() != null) {
          this.deleteProjectInnovationPartnershipPhase(phase.getNext(), innovationID, projectInnovationPartnership);
        }
      }
    }
  }

  @Override
  public boolean existProjectInnovationPartnership(long projectInnovationPartnershipID) {

    return projectInnovationPartnershipDAO.existProjectInnovationPartnership(projectInnovationPartnershipID);
  }

  @Override
  public List<ProjectInnovationPartnership> findAll() {

    return projectInnovationPartnershipDAO.findAll();

  }

  @Override
  public List<ProjectInnovationPartnership> findByInnovation(long innovationID) {
    return projectInnovationPartnershipDAO.findByInnovation(innovationID);
  }

  @Override
  public List<ProjectInnovationPartnership> findByInnovationAndPhase(long innovationID, long phaseId) {
    return projectInnovationPartnershipDAO.findByInnovationAndPhase(innovationID, phaseId);
  }

  @Override
  public ProjectInnovationPartnership getProjectInnovationPartnershipById(long projectInnovationPartnershipID) {

    return projectInnovationPartnershipDAO.find(projectInnovationPartnershipID);
  }

  /**
   * @param institutionId
   * @return
   */
  public List<User> getUserList(Long institutionId, Long projectID, Phase phase) {

    List<User> users = new ArrayList<>();

    List<ProjectPartner> partnersTmp = new ArrayList<>();
    try {
      partnersTmp = projectPartnerManager.findAllByPhaseProjectAndInstitution(projectID, phase.getId(), institutionId);

    } catch (Exception e) {
      logger.error("unable to get partners");
    }

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
  public ProjectInnovationPartnership
    saveProjectInnovationPartnership(ProjectInnovationPartnership projectInnovationPartnership) {


    ProjectInnovationPartnership projectInnovationPartnershipTemp =
      projectInnovationPartnershipDAO.save(projectInnovationPartnership);
    Phase phase = phaseDAO.find(projectInnovationPartnershipTemp.getPhase().getId());

    if (projectInnovationPartnershipTemp != null && projectInnovationPartnershipTemp.getProjectInnovation() != null) {

      List<ProjectInnovationPartnership> projectInnovationPartnershipCustom = null;

      try {
        projectInnovationPartnershipCustom =
          this.findByInnovation(projectInnovationPartnership.getProjectInnovation().getId());
      } catch (Exception e) {
        // TODO: handle exception
        logger.error(e.getMessage());
      }


      if (phase.getDescription().equals(APConstants.PLANNING)) {
        if (phase.getNext() != null) {
          this.saveProjectInnovationPartnershipPhase(projectInnovationPartnershipTemp.getPhase().getNext(),
            projectInnovationPartnershipTemp.getProjectInnovation().getId(), projectInnovationPartnership,
            projectInnovationPartnershipCustom);
        }
      }

      if (phase.getDescription().equals(APConstants.REPORTING)) {
        if (phase.getNext() != null && phase.getNext().getNext() != null) {
          Phase upkeepPhase = phase.getNext().getNext();
          if (upkeepPhase != null) {
            this.saveProjectInnovationPartnershipPhase(upkeepPhase,
              projectInnovationPartnershipTemp.getProjectInnovation().getId(), projectInnovationPartnership,
              projectInnovationPartnershipCustom);
          }
        }
      }


    }

    return projectInnovationPartnershipTemp;
  }

  public void saveProjectInnovationPartnershipPhase(Phase next, long innovationID,
    ProjectInnovationPartnership projectInnovationPartnership,
    List<ProjectInnovationPartnership> ProjectInnovationPartnershipCustom) {

    Phase phase = phaseDAO.find(next.getId());
    List<ProjectInnovationPartnership> projectInnovationPartnerships = null;

    if (innovationID != 0 && projectInnovationPartnership != null
      && projectInnovationPartnership.getInstitution() != null
      && projectInnovationPartnership.getInstitution().getId() != null
      && projectInnovationPartnership.getProjectInnovationPartnerType() != null) {
      if (ProjectInnovationPartnershipCustom != null && !ProjectInnovationPartnershipCustom.isEmpty()) {

        projectInnovationPartnerships = ProjectInnovationPartnershipCustom.stream()
          .filter(c -> c.isActive() && c.getProjectInnovation() != null
            && c.getProjectInnovation().getId().equals(innovationID) && c.getInstitution() != null
            && c.getInstitution().getId().equals(projectInnovationPartnership.getInstitution().getId())
            && c.getProjectInnovationPartnerType() != null
            && c.getProjectInnovationPartnerType().getId()
              .equals(projectInnovationPartnership.getProjectInnovationPartnerType().getId())
            && c.getPhase().getId().equals(phase.getId()))
          .collect(Collectors.toList());
      }
    }

    if (projectInnovationPartnerships != null && projectInnovationPartnerships.isEmpty()) {

      if (projectInnovationPartnership != null && projectInnovationPartnership.getProjectInnovationPartnerType() != null
        && projectInnovationPartnership.getProjectInnovationPartnerType().getId() != null
        && projectInnovationPartnership.getProjectInnovationPartnerType().getId()
          .equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE)) {

        List<ProjectInnovationPartnership> checkProjectInnovationPartnerships = null;
        if (ProjectInnovationPartnershipCustom != null && !ProjectInnovationPartnershipCustom.isEmpty()) {
          checkProjectInnovationPartnerships = ProjectInnovationPartnershipCustom.stream()
            .filter(c -> c.isActive() && c.getProjectInnovationPartnerType().getId().equals(innovationID)
              && c.getProjectInnovationPartnerType().getId()
                .equals(projectInnovationPartnership.getProjectInnovationPartnerType().getId())
              && c.getPhase().getId().equals(phase.getId()))
            .collect(Collectors.toList());

        }

        if (checkProjectInnovationPartnerships != null && !checkProjectInnovationPartnerships.isEmpty()) {
          if (checkProjectInnovationPartnerships.get(0) != null
            && checkProjectInnovationPartnerships.get(0).getInstitution() != null
            && checkProjectInnovationPartnerships.get(0).getInstitution().getId() != null
            && projectInnovationPartnership.getInstitution() != null && !checkProjectInnovationPartnerships.get(0)
              .getInstitution().getId().equals(projectInnovationPartnership.getInstitution().getId())) {

            checkProjectInnovationPartnerships.get(0).setInstitution(projectInnovationPartnership.getInstitution());

            ProjectInnovationPartnership projectInnovationPartnershipUp =
              projectInnovationPartnershipDAO.save(checkProjectInnovationPartnerships.get(0));

            if (projectInnovationPartnershipUp != null
              && projectInnovationPartnershipUp.getProjectInnovationPartnershipPersons() != null) {

              List<ProjectInnovationPartnershipPerson> trueList = new ArrayList<>();
              List<ProjectInnovationPartnershipPerson> list =
                new ArrayList<>(projectInnovationPartnershipUp.getProjectInnovationPartnershipPersons().stream()
                  .filter(p -> p.isActive()).collect(Collectors.toList()));

              if (list != null) {
                for (ProjectInnovationPartnershipPerson projectInnovationPartnershipsPerson : list) {

                  if (trueList.isEmpty()) {
                    trueList.add(projectInnovationPartnershipsPerson);
                  } else {
                    boolean addUser = true;
                    for (ProjectInnovationPartnershipPerson projectInnovationPartnershipsPerson2 : trueList) {

                      if (projectInnovationPartnershipsPerson2 != null
                        && projectInnovationPartnershipsPerson2.getUser() != null
                        && projectInnovationPartnershipsPerson2.getUser().getId()
                          .equals(projectInnovationPartnershipsPerson.getUser().getId())) {
                        projectInnovationPartnershipPersonDAO
                          .deleteProjectInnovationPartnershipPerson(projectInnovationPartnershipsPerson.getId());
                      }
                    }
                    if (addUser) {
                      trueList.add(projectInnovationPartnershipsPerson);
                    }
                  }
                }
              }

              List<ProjectInnovationPartnershipPerson> personList = new ArrayList<>();
              if (projectInnovationPartnershipUp != null && projectInnovationPartnershipUp.getInstitution() != null
                && projectInnovationPartnershipUp.getProjectInnovation() != null
                && projectInnovationPartnershipUp.getProjectInnovation().getProject() != null) {
                List<User> dUsers = this.getUserList(projectInnovationPartnershipUp.getInstitution().getId(),
                  projectInnovationPartnershipUp.getProjectInnovation().getProject().getId(), phase);

                if (trueList != null) {
                  for (ProjectInnovationPartnershipPerson projectInnovationPartnershipsPerson2 : trueList) {
                    boolean addUser = false;
                    for (User user : dUsers) {

                      if (user.getId().equals(projectInnovationPartnershipsPerson2.getUser().getId())) {
                        addUser = true;
                        break;
                      }
                    }

                    if (addUser) {
                      personList.add(projectInnovationPartnershipsPerson2);
                    } else {
                      projectInnovationPartnershipPersonDAO
                        .deleteProjectInnovationPartnershipPerson(projectInnovationPartnershipsPerson2.getId());
                    }

                  }
                }
              }

            }

            if (projectInnovationPartnershipUp != null
              && projectInnovationPartnershipUp.getProjectInnovationPartnerType() != null
              && projectInnovationPartnershipUp.getProjectInnovationPartnerType().getId() != null) {
              this.updatePersons(projectInnovationPartnershipUp, projectInnovationPartnership,
                projectInnovationPartnershipUp.getProjectInnovationPartnerType().getId());
            }


          }

        } else {
          ProjectInnovationPartnership projectInnovationPartnershipAdd = new ProjectInnovationPartnership();

          projectInnovationPartnershipAdd.setProjectInnovation(projectInnovationPartnership.getProjectInnovation());
          projectInnovationPartnershipAdd.setInstitution(projectInnovationPartnership.getInstitution());
          projectInnovationPartnershipAdd
            .setProjectInnovationPartnerType(projectInnovationPartnership.getProjectInnovationPartnerType());
          projectInnovationPartnershipAdd.setActive(true);
          projectInnovationPartnershipAdd.setActiveSince(new Date());
          projectInnovationPartnershipAdd.setPhase(phase);
          projectInnovationPartnershipAdd = projectInnovationPartnershipDAO.save(projectInnovationPartnershipAdd);
          if (projectInnovationPartnershipAdd.getId() != null) {
            this.addPersons(projectInnovationPartnership, projectInnovationPartnershipAdd.getId());
          }
        }
      } else {

        ProjectInnovationPartnership projectInnovationPartnershipAdd = new ProjectInnovationPartnership();

        projectInnovationPartnershipAdd.setProjectInnovation(projectInnovationPartnership.getProjectInnovation());
        projectInnovationPartnershipAdd.setInstitution(projectInnovationPartnership.getInstitution());
        projectInnovationPartnershipAdd
          .setProjectInnovationPartnerType(projectInnovationPartnership.getProjectInnovationPartnerType());
        projectInnovationPartnershipAdd.setActive(true);
        projectInnovationPartnershipAdd.setActiveSince(new Date());
        projectInnovationPartnershipAdd.setPhase(phase);
        projectInnovationPartnershipAdd = projectInnovationPartnershipDAO.save(projectInnovationPartnershipAdd);
        if (projectInnovationPartnershipAdd.getId() != null) {
          this.addPersons(projectInnovationPartnership, projectInnovationPartnershipAdd.getId());
        }

      }

    } else {

      ProjectInnovationPartnership dp = null;
      if (projectInnovationPartnerships != null && projectInnovationPartnerships.get(0) != null) {
        dp = projectInnovationPartnerships.get(0);
      }

      if (dp != null && dp.getProjectInnovationPartnershipPersons() != null) {


        List<ProjectInnovationPartnershipPerson> trueList = new ArrayList<>();

        List<ProjectInnovationPartnershipPerson> list = new ArrayList<>(
          dp.getProjectInnovationPartnershipPersons().stream().filter(p -> p.isActive()).collect(Collectors.toList()));

        if (list != null) {
          for (ProjectInnovationPartnershipPerson projectInnovationPartnershipsPerson : list) {

            if (trueList.isEmpty()) {
              trueList.add(projectInnovationPartnershipsPerson);
            } else {
              boolean addUser = true;
              for (ProjectInnovationPartnershipPerson projectInnovationPartnershipsPerson2 : trueList) {

                if (projectInnovationPartnershipsPerson != null && projectInnovationPartnershipsPerson2 != null
                  && projectInnovationPartnershipsPerson2.getUser() != null && projectInnovationPartnershipsPerson2
                    .getUser().getId().equals(projectInnovationPartnershipsPerson.getUser().getId())) {
                  projectInnovationPartnershipPersonDAO
                    .deleteProjectInnovationPartnershipPerson(projectInnovationPartnershipsPerson.getId());
                }
              }
              if (addUser) {
                trueList.add(projectInnovationPartnershipsPerson);
              }
            }
          }
        }

        List<ProjectInnovationPartnershipPerson> personList = new ArrayList<>();
        if (dp.getInstitution() != null && dp.getProjectInnovation().getProject() != null) {
          List<User> dUsers =
            this.getUserList(dp.getInstitution().getId(), dp.getProjectInnovation().getProject().getId(), phase);

          for (ProjectInnovationPartnershipPerson projectInnovationPartnershipsPerson2 : trueList) {
            boolean addUser = false;
            for (User user : dUsers) {

              if (user != null && projectInnovationPartnershipsPerson2 != null
                && projectInnovationPartnershipsPerson2.getUser() != null
                && user.getId().equals(projectInnovationPartnershipsPerson2.getUser().getId())) {
                addUser = true;
                break;
              }

            }

            if (addUser) {
              personList.add(projectInnovationPartnershipsPerson2);
            } else {
              projectInnovationPartnershipPersonDAO
                .deleteProjectInnovationPartnershipPerson(projectInnovationPartnershipsPerson2.getId());
            }

          }
        }

      }
      if (projectInnovationPartnerships != null && projectInnovationPartnerships.get(0) != null
        && projectInnovationPartnerships.get(0).getProjectInnovationPartnerType() != null
        && projectInnovationPartnerships.get(0).getProjectInnovationPartnerType().getId() != null) {
        this.updatePersons(projectInnovationPartnerships.get(0), projectInnovationPartnership,
          projectInnovationPartnerships.get(0).getProjectInnovationPartnerType().getId());
      }
    }


    if (phase.getNext() != null) {
      this.saveProjectInnovationPartnershipPhase(phase.getNext(), innovationID, projectInnovationPartnership,
        ProjectInnovationPartnershipCustom);
    }
  }

  public void updatePersons(ProjectInnovationPartnership projectInnovationPartnershipUp,
    ProjectInnovationPartnership projectInnovationPartnership, Long projectInnovationPartnerType) {


    List<ProjectInnovationPartnershipPerson> projectInnovationPartnershipsPersonPrev =
      new ArrayList<ProjectInnovationPartnershipPerson>(projectInnovationPartnershipUp
        .getProjectInnovationPartnershipPersons().stream().filter(dup -> dup.isActive()).collect(Collectors.toList()));

    for (ProjectInnovationPartnershipPerson projectInnovationPartnershipsPerson : projectInnovationPartnershipsPersonPrev) {
      if (projectInnovationPartnership.getPartnershipPersons() == null
        || projectInnovationPartnership.getPartnershipPersons().stream()
          .filter(c -> projectInnovationPartnershipsPerson.getUser() != null
            && projectInnovationPartnershipsPerson.getUser().getId() != null && c.getUser() != null
            && c.getUser().getId() != null
            && c.getUser().getId().equals(projectInnovationPartnershipsPerson.getUser().getId()))
          .collect(Collectors.toList()).isEmpty()) {
        projectInnovationPartnershipPersonDAO
          .deleteProjectInnovationPartnershipPerson(projectInnovationPartnershipsPerson.getId());
      }


    }

    // This process is for Other Partnership Delete function
    if (projectInnovationPartnership.getPartnershipPersons() != null) {
      for (ProjectInnovationPartnershipPerson projectInnovationPartnershipsPersonDel : projectInnovationPartnership
        .getPartnershipPersons()) {
        if (projectInnovationPartnershipsPersonDel.getId() != null) {
          if (projectInnovationPartnershipsPersonDel.getUser() == null
            || projectInnovationPartnershipsPersonDel.getUser().getId() == null) {
            projectInnovationPartnershipPersonDAO
              .deleteProjectInnovationPartnershipPerson(projectInnovationPartnershipsPersonDel.getId());
          }
        }
      }
    }

    if (projectInnovationPartnership.getPartnershipPersons() != null) {

      for (ProjectInnovationPartnershipPerson projectInnovationPartnershipsPerson : projectInnovationPartnership
        .getPartnershipPersons()) {

        List<ProjectInnovationPartnershipPerson> projectInnovationPartnershipsPersons =
          projectInnovationPartnershipsPersonPrev.stream()
            .filter(dup -> projectInnovationPartnershipsPerson.getUser() != null
              && projectInnovationPartnershipsPerson.getUser().getId() != null && dup.getUser() != null
              && dup.getUser().getId() != null
              && dup.getUser().getId().equals(projectInnovationPartnershipsPerson.getUser().getId()))
            .collect(Collectors.toList());

        if (projectInnovationPartnershipsPersons == null || projectInnovationPartnershipsPersons.isEmpty()) {

          if (projectInnovationPartnershipsPerson.getUser() != null
            && projectInnovationPartnershipsPerson.getUser().getId() != null) {
            ProjectInnovationPartnershipPerson projectInnovationPartnershipsPersonSave =
              new ProjectInnovationPartnershipPerson();

            projectInnovationPartnershipsPersonSave.setUser(projectInnovationPartnershipsPerson.getUser());
            projectInnovationPartnershipsPersonSave.setProjectInnovationPartnership(projectInnovationPartnershipUp);

            projectInnovationPartnershipPersonDAO.save(projectInnovationPartnershipsPersonSave);
          }
        }
      }
    }
  }

}
