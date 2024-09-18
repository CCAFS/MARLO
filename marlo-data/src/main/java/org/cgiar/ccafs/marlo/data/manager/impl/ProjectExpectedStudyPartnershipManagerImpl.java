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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPartnershipDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPartnershipsPersonDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnership;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnershipsPerson;
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
public class ProjectExpectedStudyPartnershipManagerImpl implements ProjectExpectedStudyPartnershipManager {


  private ProjectExpectedStudyPartnershipDAO projectExpectedStudyPartnershipDAO;
  private PhaseDAO phaseDAO;
  private ProjectExpectedStudyPartnershipsPersonDAO projectExpectedStudyPartnershipsPersonDAO;
  // Managers
  private ProjectPartnerManager projectPartnerManager;

  private final Logger logger = LoggerFactory.getLogger(ProjectExpectedStudyPartnershipManagerImpl.class);


  @Inject
  public ProjectExpectedStudyPartnershipManagerImpl(
    ProjectExpectedStudyPartnershipDAO projectExpectedStudyPartnershipDAO, PhaseDAO phaseDAO,
    ProjectExpectedStudyPartnershipsPersonDAO projectExpectedStudyPartnershipsPersonDAO,
    ProjectPartnerManager projectPartnerManager) {
    this.projectExpectedStudyPartnershipDAO = projectExpectedStudyPartnershipDAO;
    this.phaseDAO = phaseDAO;
    this.projectExpectedStudyPartnershipsPersonDAO = projectExpectedStudyPartnershipsPersonDAO;
    this.projectPartnerManager = projectPartnerManager;


  }

  public void addPersons(ProjectExpectedStudyPartnership projectExpectedStudyPartnership,
    Long newProjectExpectedStudyPartnershipId) {


    if (projectExpectedStudyPartnership.getPartnershipsPersons() != null) {

      for (ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson : projectExpectedStudyPartnership
        .getPartnershipsPersons()) {
        if (projectExpectedStudyPartnershipsPerson.getUser() != null
          && projectExpectedStudyPartnershipsPerson.getUser().getId() != null) {
          ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPersonSave =
            new ProjectExpectedStudyPartnershipsPerson();
          projectExpectedStudyPartnershipsPersonSave.setProjectExpectedStudyPartnership(
            this.getProjectExpectedStudyPartnershipById(newProjectExpectedStudyPartnershipId));
          projectExpectedStudyPartnershipsPersonSave.setUser(projectExpectedStudyPartnershipsPerson.getUser());

          projectExpectedStudyPartnershipsPersonDAO.save(projectExpectedStudyPartnershipsPersonSave);
        }
      }
    }

  }

  @Override
  public void deleteProjectExpectedStudyPartnership(long projectExpectedStudyPartnershipId) {


    ProjectExpectedStudyPartnership projectExpectedStudyPartnership =
      this.getProjectExpectedStudyPartnershipById(projectExpectedStudyPartnershipId);
    Phase currentPhase = projectExpectedStudyPartnership.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyPartnershipPhase(projectExpectedStudyPartnership.getPhase().getNext(),
        projectExpectedStudyPartnership.getProjectExpectedStudy().getId(), projectExpectedStudyPartnership);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyPartnershipPhase(upkeepPhase,
            projectExpectedStudyPartnership.getProjectExpectedStudy().getId(), projectExpectedStudyPartnership);
        }
      }
    }


    projectExpectedStudyPartnershipDAO.deleteProjectExpectedStudyPartnership(projectExpectedStudyPartnershipId);
  }

  public void deleteProjectExpectedStudyPartnershipPhase(Phase next, long expectedID,
    ProjectExpectedStudyPartnership projectExpectedStudyPartnership) {
    if (projectExpectedStudyPartnership != null) {
      Phase phase = phaseDAO.find(next.getId());

      if (phase.getProjectExpectedStudyPartnerships() != null) {
        List<ProjectExpectedStudyPartnership> projectExpectedStudyPartnershipList =
          phase.getProjectExpectedStudyPartnerships().stream()
            .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
              && c.getInstitution() != null && c != null && c.getInstitution().getId() != null
              && projectExpectedStudyPartnership != null && projectExpectedStudyPartnership.getInstitution() != null
              && projectExpectedStudyPartnership.getInstitution().getId() != null
              && projectExpectedStudyPartnership.getProjectExpectedStudyPartnerType() != null
              && c.getInstitution().getId().equals(projectExpectedStudyPartnership.getInstitution().getId())
              && c.getProjectExpectedStudyPartnerType().getId()
                .equals(projectExpectedStudyPartnership.getProjectExpectedStudyPartnerType().getId()))
            .collect(Collectors.toList());

        for (ProjectExpectedStudyPartnership projectExpectedStudyPartnershipDB : projectExpectedStudyPartnershipList) {

          if (projectExpectedStudyPartnershipDB.getProjectExpectedStudyPartnershipsPersons() != null) {
            List<ProjectExpectedStudyPartnershipsPerson> projectExpectedStudyPartnershipsPersons =
              new ArrayList<>(projectExpectedStudyPartnershipDB.getProjectExpectedStudyPartnershipsPersons().stream()
                .filter(dup -> dup.isActive()).collect(Collectors.toList()));
            for (ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson : projectExpectedStudyPartnershipsPersons) {
              this.projectExpectedStudyPartnershipsPersonDAO
                .deleteProjectExpectedStudyPartnershipsPerson(projectExpectedStudyPartnershipsPerson.getId());
            }
          }
          if (projectExpectedStudyPartnershipDB != null && projectExpectedStudyPartnershipDB.getId() != null) {
            projectExpectedStudyPartnershipDAO
              .deleteProjectExpectedStudyPartnership(projectExpectedStudyPartnershipDB.getId());
          }
        }

        if (phase.getNext() != null) {
          this.deleteProjectExpectedStudyPartnershipPhase(phase.getNext(), expectedID, projectExpectedStudyPartnership);
        }
      }
    }
  }

  @Override
  public boolean existProjectExpectedStudyPartnership(long projectExpectedStudyPartnershipID) {

    return projectExpectedStudyPartnershipDAO.existProjectExpectedStudyPartnership(projectExpectedStudyPartnershipID);
  }

  @Override
  public List<ProjectExpectedStudyPartnership> findAll() {

    return projectExpectedStudyPartnershipDAO.findAll();

  }

  @Override
  public List<ProjectExpectedStudyPartnership> findByExpected(long expectedIdd) {

    return projectExpectedStudyPartnershipDAO.findByExpected(expectedIdd);

  }


  @Override
  public List<ProjectExpectedStudyPartnership> findByExpectedAndPhase(long expectedId, long phaseId) {

    return projectExpectedStudyPartnershipDAO.findByExpectedAndPhase(expectedId, phaseId);

  }

  @Override
  public ProjectExpectedStudyPartnership
    getProjectExpectedStudyPartnershipById(long projectExpectedStudyPartnershipID) {

    return projectExpectedStudyPartnershipDAO.find(projectExpectedStudyPartnershipID);
  }

  /**
   * HJ 08/01/2019
   * 
   * @param institutionId
   * @return
   */
  public List<User> getUserList(Long institutionId, Long projectID, Phase phase) {

    List<User> users = new ArrayList<>();

    /*
     * cgamboa 26/04/2024 the query has been optimized
     * List<ProjectPartner> partnersTmp = projectPartnerManager.findAll().stream()
     * .filter(pp -> pp.isActive() && pp.getProject().getId().equals(projectID)
     * && pp.getPhase().getId().equals(phase.getId()) && pp.getInstitution().getId().equals(institutionId))
     * .collect(Collectors.toList());
     */

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
  public ProjectExpectedStudyPartnership
    saveProjectExpectedStudyPartnership(ProjectExpectedStudyPartnership projectExpectedStudyPartnership) {


    ProjectExpectedStudyPartnership projectExpectedStudyPartnershipTemp =
      projectExpectedStudyPartnershipDAO.save(projectExpectedStudyPartnership);
    Phase phase = phaseDAO.find(projectExpectedStudyPartnershipTemp.getPhase().getId());

    if (projectExpectedStudyPartnershipTemp != null
      && projectExpectedStudyPartnershipTemp.getProjectExpectedStudy() != null) {


      // cgamboa 26/04/2024 the query has been optimized to be used in the replication process

      List<ProjectExpectedStudyPartnership> projectExpectedStudyPartnershipCustom = null;


      try {
        projectExpectedStudyPartnershipCustom =
          this.findByExpected(projectExpectedStudyPartnership.getProjectExpectedStudy().getId());
      } catch (Exception e) {
        // TODO: handle exception
        logger.error(e.getMessage());
      }


      if (phase.getDescription().equals(APConstants.PLANNING)) {
        if (phase.getNext() != null) {
          this.saveProjectExpectedStudyPartnershipPhase(projectExpectedStudyPartnershipTemp.getPhase().getNext(),
            projectExpectedStudyPartnershipTemp.getProjectExpectedStudy().getId(), projectExpectedStudyPartnership,
            projectExpectedStudyPartnershipCustom);
        }
      }

      if (phase.getDescription().equals(APConstants.REPORTING)) {
        if (phase.getNext() != null && phase.getNext().getNext() != null) {
          Phase upkeepPhase = phase.getNext().getNext();
          if (upkeepPhase != null) {
            this.saveProjectExpectedStudyPartnershipPhase(upkeepPhase,
              projectExpectedStudyPartnershipTemp.getProjectExpectedStudy().getId(), projectExpectedStudyPartnership,
              projectExpectedStudyPartnershipCustom);
          }
        }
      }


    }

    return projectExpectedStudyPartnershipTemp;

  }


  public void saveProjectExpectedStudyPartnershipPhase(Phase next, long expectedID,
    ProjectExpectedStudyPartnership projectExpectedStudyPartnership,
    List<ProjectExpectedStudyPartnership> ProjectExpectedStudyPartnershipCustom) {

    Phase phase = phaseDAO.find(next.getId());
    List<ProjectExpectedStudyPartnership> projectExpectedStudyPartnerships = null;

    if (expectedID != 0 && projectExpectedStudyPartnership != null
      && projectExpectedStudyPartnership.getInstitution() != null
      && projectExpectedStudyPartnership.getInstitution().getId() != null
      && projectExpectedStudyPartnership.getProjectExpectedStudyPartnerType() != null) {
      if (ProjectExpectedStudyPartnershipCustom != null && !ProjectExpectedStudyPartnershipCustom.isEmpty()) {
        // cgamboa 26/04/2024 the query has been optimized to be used in the replication process
        projectExpectedStudyPartnerships = ProjectExpectedStudyPartnershipCustom.stream()
          .filter(c -> c.isActive() && c.getProjectExpectedStudy() != null
            && c.getProjectExpectedStudy().getId().equals(expectedID) && c.getInstitution() != null
            && c.getInstitution().getId().equals(projectExpectedStudyPartnership.getInstitution().getId())
            && c.getProjectExpectedStudyPartnerType() != null
            && c.getProjectExpectedStudyPartnerType().getId()
              .equals(projectExpectedStudyPartnership.getProjectExpectedStudyPartnerType().getId())
            && c.getPhase().getId().equals(phase.getId()))
          .collect(Collectors.toList());
      }
    }

    if (projectExpectedStudyPartnerships != null && projectExpectedStudyPartnerships.isEmpty()) {

      if (projectExpectedStudyPartnership != null
        && projectExpectedStudyPartnership.getProjectExpectedStudyPartnerType() != null
        && projectExpectedStudyPartnership.getProjectExpectedStudyPartnerType().getId() != null
        && projectExpectedStudyPartnership.getProjectExpectedStudyPartnerType().getId()
          .equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE)) {

        List<ProjectExpectedStudyPartnership> checkProjectExpectedStudyPartnerships = null;
        if (ProjectExpectedStudyPartnershipCustom != null && !ProjectExpectedStudyPartnershipCustom.isEmpty()) {
          checkProjectExpectedStudyPartnerships = ProjectExpectedStudyPartnershipCustom.stream()
            .filter(c -> c.isActive() && c.getProjectExpectedStudyPartnerType().getId().equals(expectedID)
              && c.getProjectExpectedStudyPartnerType().getId()
                .equals(projectExpectedStudyPartnership.getProjectExpectedStudyPartnerType().getId())
              && c.getPhase().getId().equals(phase.getId()))
            .collect(Collectors.toList());

        }

        if (checkProjectExpectedStudyPartnerships != null && !checkProjectExpectedStudyPartnerships.isEmpty()) {
          if (checkProjectExpectedStudyPartnerships.get(0) != null
            && checkProjectExpectedStudyPartnerships.get(0).getInstitution() != null
            && checkProjectExpectedStudyPartnerships.get(0).getInstitution().getId() != null
            && projectExpectedStudyPartnership.getInstitution() != null && !checkProjectExpectedStudyPartnerships.get(0)
              .getInstitution().getId().equals(projectExpectedStudyPartnership.getInstitution().getId())) {

            checkProjectExpectedStudyPartnerships.get(0)
              .setInstitution(projectExpectedStudyPartnership.getInstitution());

            ProjectExpectedStudyPartnership projectExpectedStudyPartnershipUp =
              projectExpectedStudyPartnershipDAO.save(checkProjectExpectedStudyPartnerships.get(0));

            if (projectExpectedStudyPartnershipUp != null
              && projectExpectedStudyPartnershipUp.getProjectExpectedStudyPartnershipsPersons() != null) {

              List<ProjectExpectedStudyPartnershipsPerson> trueList = new ArrayList<>();
              List<ProjectExpectedStudyPartnershipsPerson> list =
                new ArrayList<>(projectExpectedStudyPartnershipUp.getProjectExpectedStudyPartnershipsPersons().stream()
                  .filter(p -> p.isActive()).collect(Collectors.toList()));

              if (list != null) {
                for (ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson : list) {

                  if (trueList.isEmpty()) {
                    trueList.add(projectExpectedStudyPartnershipsPerson);
                  } else {
                    boolean addUser = true;
                    for (ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson2 : trueList) {

                      if (projectExpectedStudyPartnershipsPerson2 != null
                        && projectExpectedStudyPartnershipsPerson2.getUser() != null
                        && projectExpectedStudyPartnershipsPerson2.getUser().getId()
                          .equals(projectExpectedStudyPartnershipsPerson.getUser().getId())) {
                        projectExpectedStudyPartnershipsPersonDAO
                          .deleteProjectExpectedStudyPartnershipsPerson(projectExpectedStudyPartnershipsPerson.getId());
                      }
                    }
                    if (addUser) {
                      trueList.add(projectExpectedStudyPartnershipsPerson);
                    }
                  }
                }
              }

              List<ProjectExpectedStudyPartnershipsPerson> personList = new ArrayList<>();
              if (projectExpectedStudyPartnershipUp != null
                && projectExpectedStudyPartnershipUp.getInstitution() != null
                && projectExpectedStudyPartnershipUp.getProjectExpectedStudy() != null
                && projectExpectedStudyPartnershipUp.getProjectExpectedStudy().getProject() != null) {
                List<User> dUsers = this.getUserList(projectExpectedStudyPartnershipUp.getInstitution().getId(),
                  projectExpectedStudyPartnershipUp.getProjectExpectedStudy().getProject().getId(), phase);

                if (trueList != null) {
                  for (ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson2 : trueList) {
                    boolean addUser = false;
                    for (User user : dUsers) {

                      if (user.getId().equals(projectExpectedStudyPartnershipsPerson2.getUser().getId())) {
                        addUser = true;
                        break;
                      }
                    }

                    if (addUser) {
                      personList.add(projectExpectedStudyPartnershipsPerson2);
                    } else {
                      projectExpectedStudyPartnershipsPersonDAO
                        .deleteProjectExpectedStudyPartnershipsPerson(projectExpectedStudyPartnershipsPerson2.getId());
                    }

                  }
                }
              }

            }

            if (projectExpectedStudyPartnershipUp != null
              && projectExpectedStudyPartnershipUp.getProjectExpectedStudyPartnerType() != null
              && projectExpectedStudyPartnershipUp.getProjectExpectedStudyPartnerType().getId() != null) {
              this.updatePersons(projectExpectedStudyPartnershipUp, projectExpectedStudyPartnership,
                projectExpectedStudyPartnershipUp.getProjectExpectedStudyPartnerType().getId());
            }


          }

        } else {
          ProjectExpectedStudyPartnership projectExpectedStudyPartnershipAdd = new ProjectExpectedStudyPartnership();

          projectExpectedStudyPartnershipAdd
            .setProjectExpectedStudy(projectExpectedStudyPartnership.getProjectExpectedStudy());
          projectExpectedStudyPartnershipAdd.setInstitution(projectExpectedStudyPartnership.getInstitution());
          projectExpectedStudyPartnershipAdd
            .setProjectExpectedStudyPartnerType(projectExpectedStudyPartnership.getProjectExpectedStudyPartnerType());
          projectExpectedStudyPartnershipAdd.setActive(true);
          projectExpectedStudyPartnershipAdd.setActiveSince(new Date());
          projectExpectedStudyPartnershipAdd.setPhase(phase);
          projectExpectedStudyPartnershipAdd =
            projectExpectedStudyPartnershipDAO.save(projectExpectedStudyPartnershipAdd);
          if (projectExpectedStudyPartnershipAdd.getId() != null) {
            this.addPersons(projectExpectedStudyPartnership, projectExpectedStudyPartnershipAdd.getId());
          }
        }
      } else {

        ProjectExpectedStudyPartnership projectExpectedStudyPartnershipAdd = new ProjectExpectedStudyPartnership();

        projectExpectedStudyPartnershipAdd
          .setProjectExpectedStudy(projectExpectedStudyPartnership.getProjectExpectedStudy());
        projectExpectedStudyPartnershipAdd.setInstitution(projectExpectedStudyPartnership.getInstitution());
        projectExpectedStudyPartnershipAdd
          .setProjectExpectedStudyPartnerType(projectExpectedStudyPartnership.getProjectExpectedStudyPartnerType());
        projectExpectedStudyPartnershipAdd.setActive(true);
        projectExpectedStudyPartnershipAdd.setActiveSince(new Date());
        projectExpectedStudyPartnershipAdd.setPhase(phase);
        projectExpectedStudyPartnershipAdd =
          projectExpectedStudyPartnershipDAO.save(projectExpectedStudyPartnershipAdd);
        if (projectExpectedStudyPartnershipAdd.getId() != null) {
          this.addPersons(projectExpectedStudyPartnership, projectExpectedStudyPartnershipAdd.getId());
        }

      }

    } else {

      ProjectExpectedStudyPartnership dp = null;
      if (projectExpectedStudyPartnerships != null && projectExpectedStudyPartnerships.get(0) != null) {
        dp = projectExpectedStudyPartnerships.get(0);
      }

      if (dp != null && dp.getProjectExpectedStudyPartnershipsPersons() != null) {


        List<ProjectExpectedStudyPartnershipsPerson> trueList = new ArrayList<>();

        List<ProjectExpectedStudyPartnershipsPerson> list =
          new ArrayList<>(dp.getProjectExpectedStudyPartnershipsPersons().stream().filter(p -> p.isActive())
            .collect(Collectors.toList()));

        if (list != null) {
          for (ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson : list) {

            if (trueList.isEmpty()) {
              trueList.add(projectExpectedStudyPartnershipsPerson);
            } else {
              boolean addUser = true;
              for (ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson2 : trueList) {

                if (projectExpectedStudyPartnershipsPerson != null && projectExpectedStudyPartnershipsPerson2 != null
                  && projectExpectedStudyPartnershipsPerson2.getUser() != null
                  && projectExpectedStudyPartnershipsPerson2.getUser().getId()
                    .equals(projectExpectedStudyPartnershipsPerson.getUser().getId())) {
                  projectExpectedStudyPartnershipsPersonDAO
                    .deleteProjectExpectedStudyPartnershipsPerson(projectExpectedStudyPartnershipsPerson.getId());
                }
              }
              if (addUser) {
                trueList.add(projectExpectedStudyPartnershipsPerson);
              }
            }
          }
        }

        List<ProjectExpectedStudyPartnershipsPerson> personList = new ArrayList<>();
        if (dp.getInstitution() != null && dp.getProjectExpectedStudy().getProject() != null) {
          List<User> dUsers =
            this.getUserList(dp.getInstitution().getId(), dp.getProjectExpectedStudy().getProject().getId(), phase);

          for (ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson2 : trueList) {
            boolean addUser = false;
            for (User user : dUsers) {

              if (user != null && projectExpectedStudyPartnershipsPerson2 != null
                && projectExpectedStudyPartnershipsPerson2.getUser() != null
                && user.getId().equals(projectExpectedStudyPartnershipsPerson2.getUser().getId())) {
                addUser = true;
                break;
              }

            }

            if (addUser) {
              personList.add(projectExpectedStudyPartnershipsPerson2);
            } else {
              projectExpectedStudyPartnershipsPersonDAO
                .deleteProjectExpectedStudyPartnershipsPerson(projectExpectedStudyPartnershipsPerson2.getId());
            }

          }
        }

      }
      if (projectExpectedStudyPartnerships != null && projectExpectedStudyPartnerships.get(0) != null
        && projectExpectedStudyPartnerships.get(0).getProjectExpectedStudyPartnerType() != null
        && projectExpectedStudyPartnerships.get(0).getProjectExpectedStudyPartnerType().getId() != null) {
        this.updatePersons(projectExpectedStudyPartnerships.get(0), projectExpectedStudyPartnership,
          projectExpectedStudyPartnerships.get(0).getProjectExpectedStudyPartnerType().getId());
      }
    }


    if (phase.getNext() != null) {
      this.saveProjectExpectedStudyPartnershipPhase(phase.getNext(), expectedID, projectExpectedStudyPartnership,
        ProjectExpectedStudyPartnershipCustom);
    }
  }


  public void updatePersons(ProjectExpectedStudyPartnership projectExpectedStudyPartnershipUp,
    ProjectExpectedStudyPartnership projectExpectedStudyPartnership, Long projectExpectedStudyPartnerTypeType) {


    List<ProjectExpectedStudyPartnershipsPerson> projectExpectedStudyPartnershipsPersonPrev =
      new ArrayList<ProjectExpectedStudyPartnershipsPerson>(
        projectExpectedStudyPartnershipUp.getProjectExpectedStudyPartnershipsPersons().stream()
          .filter(dup -> dup.isActive()).collect(Collectors.toList()));

    for (ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson : projectExpectedStudyPartnershipsPersonPrev) {
      if (projectExpectedStudyPartnership.getPartnershipsPersons() == null
        || projectExpectedStudyPartnership.getPartnershipsPersons().stream()
          .filter(c -> projectExpectedStudyPartnershipsPerson.getUser() != null
            && projectExpectedStudyPartnershipsPerson.getUser().getId() != null && c.getUser() != null
            && c.getUser().getId() != null
            && c.getUser().getId().equals(projectExpectedStudyPartnershipsPerson.getUser().getId()))
          .collect(Collectors.toList()).isEmpty()) {
        projectExpectedStudyPartnershipsPersonDAO
          .deleteProjectExpectedStudyPartnershipsPerson(projectExpectedStudyPartnershipsPerson.getId());
      }


    }

    // This process is for Other Partnership Delete function
    if (projectExpectedStudyPartnership.getPartnershipsPersons() != null) {
      for (ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPersonDel : projectExpectedStudyPartnership
        .getPartnershipsPersons()) {
        if (projectExpectedStudyPartnershipsPersonDel.getId() != null) {
          if (projectExpectedStudyPartnershipsPersonDel.getUser() == null
            || projectExpectedStudyPartnershipsPersonDel.getUser().getId() == null) {
            projectExpectedStudyPartnershipsPersonDAO
              .deleteProjectExpectedStudyPartnershipsPerson(projectExpectedStudyPartnershipsPersonDel.getId());
          }
        }
      }
    }

    if (projectExpectedStudyPartnership.getPartnershipsPersons() != null) {

      for (ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson : projectExpectedStudyPartnership
        .getPartnershipsPersons()) {

        List<ProjectExpectedStudyPartnershipsPerson> projectExpectedStudyPartnershipsPersons =
          projectExpectedStudyPartnershipsPersonPrev.stream()
            .filter(dup -> projectExpectedStudyPartnershipsPerson.getUser() != null
              && projectExpectedStudyPartnershipsPerson.getUser().getId() != null && dup.getUser() != null
              && dup.getUser().getId() != null
              && dup.getUser().getId().equals(projectExpectedStudyPartnershipsPerson.getUser().getId()))
            .collect(Collectors.toList());

        if (projectExpectedStudyPartnershipsPersons == null || projectExpectedStudyPartnershipsPersons.isEmpty()) {

          if (projectExpectedStudyPartnershipsPerson.getUser() != null
            && projectExpectedStudyPartnershipsPerson.getUser().getId() != null) {
            ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPersonSave =
              new ProjectExpectedStudyPartnershipsPerson();

            projectExpectedStudyPartnershipsPersonSave.setUser(projectExpectedStudyPartnershipsPerson.getUser());
            projectExpectedStudyPartnershipsPersonSave
              .setProjectExpectedStudyPartnership(projectExpectedStudyPartnershipUp);

            projectExpectedStudyPartnershipsPersonDAO.save(projectExpectedStudyPartnershipsPersonSave);
          }

        }

      }

    }

  }


}
