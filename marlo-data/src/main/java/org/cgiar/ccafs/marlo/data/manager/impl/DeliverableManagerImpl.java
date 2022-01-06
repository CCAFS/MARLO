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


import org.cgiar.ccafs.marlo.data.dao.CrpClusterKeyOutputDAO;
import org.cgiar.ccafs.marlo.data.dao.CrpClusterOfActivityDAO;
import org.cgiar.ccafs.marlo.data.dao.DeliverableDAO;
import org.cgiar.ccafs.marlo.data.dao.DeliverableInfoDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dto.DeliverableHomeDTO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableProgram;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressDeliverable;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressDeliverableDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableManagerImpl implements DeliverableManager {

  private PhaseDAO phaseDAO;

  private DeliverableDAO deliverableDAO;
  private DeliverableInfoDAO deliverableInfoDAO;
  private CrpClusterOfActivityDAO crpClusterOfActivityDAO;
  private CrpClusterKeyOutputDAO crpClusterKeyOutputDAO;

  // Managers
  private PhaseManager phaseManager;
  private CrpProgramManager crpProgramManager;
  private ProjectFocusManager projectFocusManager;
  private ProjectManager projectManager;
  private ReportSynthesisManager reportSynthesisManager;

  @Inject
  public DeliverableManagerImpl(DeliverableDAO deliverableDAO, PhaseDAO phaseDAO, DeliverableInfoDAO deliverableInfoDAO,
    CrpClusterOfActivityDAO crpClusterOfActivityDAO, CrpClusterKeyOutputDAO crpClusterKeyOutputDAO,
    PhaseManager phaseManager, CrpProgramManager crpProgramManager, ProjectFocusManager projectFocusManager,
    ProjectManager projectManager, ReportSynthesisManager reportSynthesisManager) {
    this.deliverableDAO = deliverableDAO;
    this.phaseDAO = phaseDAO;
    this.deliverableInfoDAO = deliverableInfoDAO;
    this.crpClusterOfActivityDAO = crpClusterOfActivityDAO;
    this.crpClusterKeyOutputDAO = crpClusterKeyOutputDAO;
    this.phaseManager = phaseManager;
    this.crpProgramManager = crpProgramManager;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }

  @Override
  public Deliverable copyDeliverable(Deliverable deliverable, Phase phase) {


    DeliverableInfo deliverableInfo = new DeliverableInfo();
    deliverableInfo.updateDeliverableInfo(deliverable.getDeliverableInfo());

    if (deliverableInfo.getCrpClusterKeyOutput() != null
      && deliverableInfo.getCrpClusterKeyOutput().getCrpClusterOfActivity() != null) {
      CrpClusterKeyOutput crpClusterKeyOutput =
        crpClusterKeyOutputDAO.find(deliverableInfo.getCrpClusterKeyOutput().getId());
      CrpClusterOfActivity crpClusterOfActivity =
        crpClusterOfActivityDAO.getCrpClusterOfActivityByIdentifierFlagshipAndPhase(
          crpClusterKeyOutput.getCrpClusterOfActivity().getIdentifier(),
          crpClusterKeyOutput.getCrpClusterOfActivity().getCrpProgram().getId(), phase.getId());
      List<CrpClusterKeyOutput> clusterKeyOutputs = crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
        .filter(c -> c.isActive() && c.getComposeID().equals(deliverableInfo.getCrpClusterKeyOutput().getComposeID()))
        .collect(Collectors.toList());
      if (!clusterKeyOutputs.isEmpty()) {
        deliverableInfo.setCrpClusterKeyOutput(clusterKeyOutputs.get(0));
      }
    }

    deliverableInfo.setPhase(phase);
    deliverableInfoDAO.save(deliverableInfo);
    return deliverableInfo.getDeliverable();
  }

  @Override
  public void deleteDeliverable(long deliverableId) {

    deliverableDAO.deleteDeliverable(deliverableId);
  }

  @Override
  public boolean existDeliverable(long deliverableID) {

    return deliverableDAO.existDeliverable(deliverableID);
  }

  @Override
  public List<Deliverable> findAll() {

    return deliverableDAO.findAll();

  }

  @Override
  public Deliverable getDeliverableById(long deliverableID) {

    return deliverableDAO.find(deliverableID);
  }

  @Override
  public List<Deliverable> getDeliverablesByParameters(Phase phase, boolean filterPhaseYear, boolean filterParticipants,
    Boolean filterPublications) {
    return deliverableDAO.getDeliverablesByParameters(phase, filterPhaseYear, filterParticipants, filterPublications);
  }

  @Override
  public List<Deliverable> getDeliverablesByPhase(long phase) {
    return deliverableDAO.getDeliverablesByPhase(phase);
  }

  @Override
  public List<Deliverable> getDeliverablesByProjectAndPhase(Long phaseId, Long projectId) {
    return deliverableDAO.getDeliverablesByProjectAndPhase(phaseId, projectId);
  }

  @Override
  public List<DeliverableHomeDTO> getDeliverablesByProjectAndPhaseHome(Long phaseId, Long projectId) {
    return deliverableDAO.getDeliverablesByProjectAndPhaseHome(phaseId.longValue(), projectId.longValue());
  }

  @Override
  public List<Deliverable> getDeliverablesLeadByInstitution(long institutionId, long phaseId) {
    return deliverableDAO.getDeliverablesLeadByInstitution(institutionId, phaseId);
  }

  @Override
  public List<Deliverable> getDeliverablesLeadByUser(long userId, long phaseId) {
    return deliverableDAO.getDeliverablesLeadByUser(userId, phaseId);
  }

  @Override
  public List<Deliverable> getDeliverablesList(LiaisonInstitution liaisonInstitution, Phase phase) {
    Phase phaseDB = phaseManager.getPhaseById(phase.getId());
    List<Deliverable> deliverables = new ArrayList<>();

    if (crpProgramManager.isFlagship(liaisonInstitution)) {
      // Fill Project Deliverables of the current flagship
      if (projectFocusManager.findAll() != null) {
        List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
          .filter(pf -> pf.isActive() && pf.getCrpProgram().getId().equals(liaisonInstitution.getCrpProgram().getId())
            && pf.getPhase() != null && pf.getPhase().getId().equals(phaseDB.getId()))
          .collect(Collectors.toList()));

        for (ProjectFocus focus : projectFocus) {
          Project project = projectManager.getProjectById(focus.getProject().getId());
          List<Deliverable> plannedDeliverables = new ArrayList<>(project.getDeliverables().stream()
            .filter(d -> d.isActive() && d.getDeliverableInfo(phaseDB) != null && d.getDeliverableInfo().isCompleted()
              && d.getDeliverableInfo().getDeliverableType() != null
              && d.getDeliverableInfo().getDeliverableType().getId() == 63)
            .collect(Collectors.toList()));
          for (Deliverable deliverable : plannedDeliverables) {
            deliverable.getDeliverableInfo(phaseDB);
            deliverable.setUsers(deliverable.getUsers(phaseDB));
            deliverable.setDissemination(deliverable.getDissemination(phaseDB));
            deliverable.setPublication(deliverable.getPublication(phaseDB));
            deliverable.setMetadataElements(deliverable.getMetadataElements(phaseDB));
            deliverables.add(deliverable);
          }
        }
      }

      // Fill Supplementary Deliverables
      List<DeliverableProgram> deliverablePrograms = liaisonInstitution.getCrpProgram().getDeliverablePrograms()
        .stream()
        .filter(dp -> dp.isActive() && dp.getPhase().equals(phaseDB) && dp.getDeliverable() != null
          && dp.getDeliverable().isActive() && dp.getDeliverable().getDeliverableInfo(phaseDB) != null
          && dp.getDeliverable().getDeliverableInfo().isRequiredToComplete()
          && dp.getDeliverable().getDeliverableInfo().getDeliverableType() != null
          && dp.getDeliverable().getDeliverableInfo().getDeliverableType().getId() != 63)
        .collect(Collectors.toList());

      if (deliverablePrograms != null && !deliverablePrograms.isEmpty()) {
        for (DeliverableProgram deliverableProgram : deliverablePrograms) {
          Deliverable deliverable = deliverableProgram.getDeliverable();
          deliverable.getDeliverableInfo(phaseDB);
          deliverable.setUsers(deliverable.getUsers(phaseDB));
          deliverable.setDissemination(deliverable.getDissemination(phaseDB));
          deliverable.setPublication(deliverable.getPublication(phaseDB));
          deliverable.setMetadataElements(deliverable.getMetadataElements(phaseDB));
          deliverables.add(deliverable);
        }
      }
    } else {
      // Fill Project Deliverables of the PMU, removing flagship deletions
      List<LiaisonInstitution> liaisonInstitutions = phaseDB.getCrp().getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

      List<ReportSynthesisFlagshipProgressDeliverableDTO> flagshipPlannedList =
        this.getFpPlannedList(liaisonInstitutions, phaseDB, liaisonInstitution, true);

      for (ReportSynthesisFlagshipProgressDeliverableDTO reportSynthesisFlagshipProgressDeliverableDTO : flagshipPlannedList) {

        Deliverable deliverable = reportSynthesisFlagshipProgressDeliverableDTO.getDeliverable();
        deliverable.getDeliverableInfo(phaseDB);
        deliverable.setUsers(deliverable.getUsers(phaseDB));
        deliverable.setDissemination(deliverable.getDissemination(phaseDB));
        deliverable.setPublication(deliverable.getPublication(phaseDB));
        deliverable.setMetadataElements(deliverable.getMetadataElements(phaseDB));
        deliverable.setSelectedFlahsgips(new ArrayList<>());
        // sort selected flagships
        if (reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions() != null
          && !reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions().isEmpty()) {
          reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions()
            .sort((l1, l2) -> l1.getCrpProgram().getAcronym().compareTo(l2.getCrpProgram().getAcronym()));
        }
        deliverable.getSelectedFlahsgips()
          .addAll(reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions());
        deliverables.add(deliverable);

      }
    }
    deliverables.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
    return deliverables;
  }

  @Override
  public List<Deliverable> getDeliverableSynthesis(long phaseId, boolean prp) {
    return this.deliverableDAO.getDeliverableSynthesis(null, phaseId, prp);
  }

  @Override
  public List<Deliverable> getDeliverableSynthesis(long projectId, long phaseId, boolean prp) {
    return this.deliverableDAO.getDeliverableSynthesis(projectId, phaseId, prp);
  }

  /**
   * Method to fill the list of deliverables (only publications or only Grey literature, depending on the
   * getPublications parameter), selected by flagships.
   * 
   * @param flagshipsLiaisonInstitutions
   * @param getPublications
   * @return
   */
  public List<ReportSynthesisFlagshipProgressDeliverableDTO> getFpPlannedList(
    List<LiaisonInstitution> flagshipsLiaisonInstitutions, Phase phaseDB, LiaisonInstitution pmuInstitution,
    boolean getPublications) {
    List<ReportSynthesisFlagshipProgressDeliverableDTO> flagshipPlannedList = new ArrayList<>();

    if (this.findAll() != null) {

      // Get global unit deliverables
      List<Deliverable> deliverables = new ArrayList<>(this.findAll().stream()
        .filter(d -> d.isActive() && d.getDeliverableInfo(phaseDB) != null
          && d.getDeliverableInfo().isRequiredToComplete() && d.getDeliverableInfo().getDeliverableType() != null
          && (getPublications ? d.getDeliverableInfo().getDeliverableType().getId() == 63
            : d.getDeliverableInfo().getDeliverableType().getId() != 63))
        .collect(Collectors.toList()));

      // Fill all deliverables of the global unit
      for (Deliverable deliverable : deliverables) {
        ReportSynthesisFlagshipProgressDeliverableDTO dto = new ReportSynthesisFlagshipProgressDeliverableDTO();
        if (deliverable.getProject() != null) {
          deliverable.getProject().setProjectInfo(deliverable.getProject().getProjecInfoPhase(phaseDB));
          if (deliverable.getProject().getProjectInfo().getAdministrative() != null
            && deliverable.getProject().getProjectInfo().getAdministrative()) {
            dto.setLiaisonInstitutions(new ArrayList<>());
            dto.getLiaisonInstitutions().add(pmuInstitution);
          } else {
            List<ProjectFocus> projectFocuses = new ArrayList<>(deliverable.getProject().getProjectFocuses().stream()
              .filter(pf -> pf.isActive() && pf.getPhase().getId().equals(phaseDB.getId()))
              .collect(Collectors.toList()));
            List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
            for (ProjectFocus projectFocus : projectFocuses) {
              liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
                .filter(li -> li.isActive() && li.getCrpProgram() != null
                  && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                  && li.getCrp() != null && li.getCrp().equals(phaseDB.getCrp()))
                .collect(Collectors.toList()));
            }
            dto.setLiaisonInstitutions(liaisonInstitutions);
          }
        } else {
          // Fill Supplementary Deliverable programs
          List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
          List<DeliverableProgram> deliverablePrograms = deliverable.getDeliverablePrograms().stream()
            .filter(dp -> dp.isActive() && dp.getPhase().equals(phaseDB)).collect(Collectors.toList());
          for (DeliverableProgram deliverableProgram : deliverablePrograms) {
            liaisonInstitutions.addAll(deliverableProgram.getCrpProgram().getLiaisonInstitutions().stream()
              .filter(li -> li.isActive() && li.getCrpProgram() != null
                && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                && li.getCrp() != null && li.getCrp().equals(phaseDB.getCrp()))
              .collect(Collectors.toList()));

          }
          if (liaisonInstitutions != null && !liaisonInstitutions.isEmpty()) {
            dto.setLiaisonInstitutions(liaisonInstitutions);
          } else {
            dto.setLiaisonInstitutions(new ArrayList<>());
            dto.getLiaisonInstitutions().add(pmuInstitution);
          }
        }

        dto.setDeliverable(deliverable);
        flagshipPlannedList.add(dto);
      }

      // Get deleted deliverables
      List<ReportSynthesisFlagshipProgressDeliverable> flagshipProgressDeliverables = new ArrayList<>();
      for (LiaisonInstitution liaisonInstitution : flagshipsLiaisonInstitutions) {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phaseDB.getId(), liaisonInstitution.getId());
        if (reportSynthesis != null) {
          if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress()
              .getReportSynthesisFlagshipProgressDeliverables() != null) {
              List<ReportSynthesisFlagshipProgressDeliverable> reportSynthesisFlagshipProgressDeliverables =
                new ArrayList<>(
                  reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressDeliverables()
                    .stream().filter(s -> s.isActive()).collect(Collectors.toList()));
              if (reportSynthesisFlagshipProgressDeliverables != null
                || !reportSynthesisFlagshipProgressDeliverables.isEmpty()) {
                for (ReportSynthesisFlagshipProgressDeliverable reportSynthesisFlagshipProgressDeliverable : reportSynthesisFlagshipProgressDeliverables) {
                  flagshipProgressDeliverables.add(reportSynthesisFlagshipProgressDeliverable);
                }
              }
            }
          }
        }
      }

      // Get list of deliverables to remove
      List<ReportSynthesisFlagshipProgressDeliverableDTO> removeList = new ArrayList<>();
      for (ReportSynthesisFlagshipProgressDeliverableDTO dto : flagshipPlannedList) {

        List<LiaisonInstitution> removeLiaison = new ArrayList<>();
        for (LiaisonInstitution liaisonInstitution : dto.getLiaisonInstitutions()) {
          ReportSynthesis reportSynthesis =
            reportSynthesisManager.findSynthesis(phaseDB.getId(), liaisonInstitution.getId());
          if (reportSynthesis != null) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {

              ReportSynthesisFlagshipProgressDeliverable flagshipProgressDeliverableNew =
                new ReportSynthesisFlagshipProgressDeliverable();
              flagshipProgressDeliverableNew = new ReportSynthesisFlagshipProgressDeliverable();
              flagshipProgressDeliverableNew.setDeliverable(dto.getDeliverable());
              flagshipProgressDeliverableNew
                .setReportSynthesisFlagshipProgress(reportSynthesis.getReportSynthesisFlagshipProgress());

              if (flagshipProgressDeliverables.contains(flagshipProgressDeliverableNew)) {
                removeLiaison.add(liaisonInstitution);
              }
            }
          }
        }

        for (LiaisonInstitution li : removeLiaison) {
          dto.getLiaisonInstitutions().remove(li);
        }

        if (dto.getLiaisonInstitutions().isEmpty()) {
          removeList.add(dto);
        }
      }

      // Remove Deliverables unselected by flagships
      for (ReportSynthesisFlagshipProgressDeliverableDTO i : removeList) {
        flagshipPlannedList.remove(i);
      }


    }
    /*
     * String deliverableIds = flagshipPlannedList.stream()
     * .filter(d -> d != null && d.getDeliverable() != null && d.getDeliverable().getId() != null)
     * .map(d -> String.valueOf(d.getDeliverable().getId())).collect(Collectors.joining(", "));
     */
    return flagshipPlannedList;
  }

  public List<ReportSynthesisFlagshipProgressDeliverableDTO> getPMU2020DeliverableList(Phase phaseDB,
    LiaisonInstitution pmuInstitution, boolean getPublications) {
    // Get global unit deliverables
    List<ReportSynthesisFlagshipProgressDeliverableDTO> flagshipPlannedList = new ArrayList<>();

    List<Deliverable> deliverables = this.getDeliverableSynthesis(phaseDB.getId(), getPublications);

    // Fill all deliverables of the global unit
    for (Deliverable deliverable : deliverables) {
      ReportSynthesisFlagshipProgressDeliverableDTO dto = new ReportSynthesisFlagshipProgressDeliverableDTO();
      if (deliverable.getProject() != null) {
        deliverable.getProject().setProjectInfo(deliverable.getProject().getProjecInfoPhase(phaseDB));
        if (deliverable.getProject().getProjectInfo().getAdministrative() != null
          && deliverable.getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(pmuInstitution);
        } else {
          List<ProjectFocus> projectFocuses = new ArrayList<>(deliverable.getProject().getProjectFocuses().stream()
            .filter(pf -> pf.isActive() && pf.getPhase().getId().equals(phaseDB.getId())).collect(Collectors.toList()));
          List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
          for (ProjectFocus projectFocus : projectFocuses) {
            liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
              .filter(li -> li.isActive() && li.getCrpProgram() != null
                && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                && li.getCrp() != null && li.getCrp().equals(phaseDB.getCrp()))
              .collect(Collectors.toList()));
          }
          dto.setLiaisonInstitutions(liaisonInstitutions);
        }
      } else {
        // Fill Supplementary Deliverable programs
        List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
        List<DeliverableProgram> deliverablePrograms = deliverable.getDeliverablePrograms().stream()
          .filter(dp -> dp.isActive() && dp.getPhase().equals(phaseDB)).collect(Collectors.toList());
        for (DeliverableProgram deliverableProgram : deliverablePrograms) {
          liaisonInstitutions.addAll(deliverableProgram.getCrpProgram().getLiaisonInstitutions().stream()
            .filter(li -> li.isActive() && li.getCrpProgram() != null
              && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
              && li.getCrp() != null && li.getCrp().equals(phaseDB.getCrp()))
            .collect(Collectors.toList()));

        }
        if (liaisonInstitutions != null && !liaisonInstitutions.isEmpty()) {
          dto.setLiaisonInstitutions(liaisonInstitutions);
        } else {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(pmuInstitution);
        }
      }

      dto.setDeliverable(deliverable);
      flagshipPlannedList.add(dto);
    }

    String deliverableIds = flagshipPlannedList.stream()
      .filter(d -> d != null && d.getDeliverable() != null && d.getDeliverable().getId() != null)
      .map(d -> String.valueOf(d.getDeliverable().getId())).collect(Collectors.joining(", "));

    return flagshipPlannedList;
  }

  @Override
  public List<Deliverable> getPublicationsByPhase(long phase) {

    return deliverableDAO.getPublicationsByPhase(phase);
  }

  @Override
  public List<Deliverable> getPublicationsList(LiaisonInstitution liaisonInstitution, Phase phase) {
    Phase phaseDB = phaseManager.getPhaseById(phase.getId());
    List<Deliverable> deliverables = new ArrayList<>();

    if (crpProgramManager.isFlagship(liaisonInstitution)) {
      // Fill Project Deliverables of the current flagship
      if (projectFocusManager.findAll() != null) {
        List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
          .filter(pf -> pf.isActive() && pf.getCrpProgram().getId().equals(liaisonInstitution.getCrpProgram().getId())
            && pf.getPhase() != null && pf.getPhase().getId().equals(phaseDB.getId()) && pf.getProject() != null
            && pf.getProject().getId() != null)
          .collect(Collectors.toList()));

        for (ProjectFocus focus : projectFocus) {
          Project project = projectManager.getProjectById(focus.getProject().getId());
          List<Deliverable> plannedDeliverables = new ArrayList<>(project.getDeliverables().stream()
            .filter(d -> d.isActive() && d.getDeliverableInfo(phaseDB) != null && d.getDeliverableInfo().isCompleted()
              && d.getDeliverableInfo().getDeliverableType() != null
              && d.getDeliverableInfo().getDeliverableType().getId() == 63)
            .collect(Collectors.toList()));
          for (Deliverable deliverable : plannedDeliverables) {
            deliverable.getDeliverableInfo(phaseDB);
            deliverable.setUsers(deliverable.getUsers(phaseDB));
            deliverable.setDissemination(deliverable.getDissemination(phaseDB));
            deliverable.setPublication(deliverable.getPublication(phaseDB));
            deliverable.setMetadataElements(deliverable.getMetadataElements(phaseDB));
            deliverables.add(deliverable);
          }
        }
      }

      // Fill Supplementary Deliverables
      List<DeliverableProgram> deliverablePrograms = liaisonInstitution.getCrpProgram().getDeliverablePrograms()
        .stream()
        .filter(dp -> dp.isActive() && dp.getPhase().equals(phaseDB) && dp.getDeliverable() != null
          && dp.getDeliverable().isActive() && dp.getDeliverable().getDeliverableInfo(phaseDB) != null
          && dp.getDeliverable().getDeliverableInfo().isRequiredToComplete()
          && dp.getDeliverable().getDeliverableInfo().getDeliverableType() != null
          && dp.getDeliverable().getDeliverableInfo().getDeliverableType().getId() == 63)
        .collect(Collectors.toList());

      if (deliverablePrograms != null && !deliverablePrograms.isEmpty()) {
        for (DeliverableProgram deliverableProgram : deliverablePrograms) {
          Deliverable deliverable = deliverableProgram.getDeliverable();
          deliverable.getDeliverableInfo(phaseDB);
          deliverable.setUsers(deliverable.getUsers(phaseDB));
          deliverable.setDissemination(deliverable.getDissemination(phaseDB));
          deliverable.setPublication(deliverable.getPublication(phaseDB));
          deliverable.setMetadataElements(deliverable.getMetadataElements(phaseDB));
          deliverables.add(deliverable);
        }
      }
    } else {
      // Fill Project Deliverables of the PMU, removing flagship deletions
      List<ReportSynthesisFlagshipProgressDeliverableDTO> flagshipPlannedList =
        this.getPMU2020DeliverableList(phaseDB, liaisonInstitution, true);

      for (ReportSynthesisFlagshipProgressDeliverableDTO reportSynthesisFlagshipProgressDeliverableDTO : flagshipPlannedList) {

        Deliverable deliverable = reportSynthesisFlagshipProgressDeliverableDTO.getDeliverable();
        deliverable.getDeliverableInfo(phaseDB);
        deliverable.setUsers(deliverable.getUsers(phaseDB));
        deliverable.setDissemination(deliverable.getDissemination(phaseDB));
        deliverable.setPublication(deliverable.getPublication(phaseDB));
        deliverable.setMetadataElements(deliverable.getMetadataElements(phaseDB));
        deliverable.setSelectedFlahsgips(new ArrayList<>());
        // sort selected flagships
        if (reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions() != null
          && !reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions().isEmpty()) {
          reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions()
            .sort((l1, l2) -> l1.getCrpProgram().getAcronym().compareTo(l2.getCrpProgram().getAcronym()));
        }
        deliverable.getSelectedFlahsgips()
          .addAll(reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions());
        deliverables.add(deliverable);

      }
    }
    deliverables.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
    return deliverables;
  }

  @Override
  public List<Deliverable> getSynthesisPublicationsList(LiaisonInstitution liaisonInstitution, Phase phase,
    boolean prp) {
    Phase phaseDB = phaseManager.getPhaseById(phase.getId());
    List<Deliverable> deliverables = new ArrayList<>();

    if (crpProgramManager.isFlagship(liaisonInstitution)) {
      // Fill Project Deliverables of the current flagship
      List<ProjectFocus> projectFocus = CollectionUtils.emptyIfNull(projectFocusManager.findAll()).stream()
        .filter(pf -> pf.isActive() && pf.getCrpProgram().getId().equals(liaisonInstitution.getCrpProgram().getId())
          && pf.getPhase() != null && pf.getPhase().getId().equals(phaseDB.getId()) && pf.getProject() != null
          && pf.getProject().getId() != null)
        .collect(Collectors.toList());

      for (ProjectFocus focus : projectFocus) {
        Project project = projectManager.getProjectById(focus.getProject().getId());
        List<Deliverable> plannedProjectDeliverables =
          this.getDeliverableSynthesis(project.getId(), phase.getId(), prp);

        for (Deliverable deliverable : plannedProjectDeliverables) {
          deliverable.getDeliverableInfo(phaseDB);
          deliverable.setUsers(deliverable.getUsers(phaseDB));
          deliverable.setDissemination(deliverable.getDissemination(phaseDB));
          deliverable.setPublication(deliverable.getPublication(phaseDB));
          deliverable.setMetadataElements(deliverable.getMetadataElements(phaseDB));
          deliverables.add(deliverable);
        }
      }

      // Fill Supplementary Deliverables
      List<DeliverableProgram> deliverablePrograms =
        liaisonInstitution.getCrpProgram().getDeliverablePrograms().stream()
          .filter(dp -> dp.isActive() && dp.getPhase().equals(phaseDB) && dp.getDeliverable() != null
            && dp.getDeliverable().isActive() && dp.getDeliverable().getDeliverableInfo(phaseDB) != null
            && dp.getDeliverable().getDeliverableInfo().isRequiredToComplete()
            && dp.getDeliverable().getDeliverableInfo().getDeliverableType() != null
            && dp.getDeliverable().getDeliverableInfo().getDeliverableType().getId() != null
            && (prp ? dp.getDeliverable().getDeliverableInfo().getDeliverableType().getId() == 63
              : dp.getDeliverable().getDeliverableInfo().getDeliverableType().getId() != 63))
          .collect(Collectors.toList());

      if (deliverablePrograms != null && !deliverablePrograms.isEmpty()) {
        for (DeliverableProgram deliverableProgram : deliverablePrograms) {
          Deliverable deliverable = deliverableProgram.getDeliverable();
          deliverable.getDeliverableInfo(phaseDB);
          deliverable.setUsers(deliverable.getUsers(phaseDB));
          deliverable.setDissemination(deliverable.getDissemination(phaseDB));
          deliverable.setPublication(deliverable.getPublication(phaseDB));
          deliverable.setMetadataElements(deliverable.getMetadataElements(phaseDB));
          deliverables.add(deliverable);
        }
      }
    } else {
      List<ReportSynthesisFlagshipProgressDeliverableDTO> flagshipPlannedList =
        this.getPMU2020DeliverableList(phaseDB, liaisonInstitution, prp);

      for (ReportSynthesisFlagshipProgressDeliverableDTO reportSynthesisFlagshipProgressDeliverableDTO : flagshipPlannedList) {
        Deliverable deliverable = reportSynthesisFlagshipProgressDeliverableDTO.getDeliverable();
        deliverable.getDeliverableInfo(phaseDB);
        deliverable.setUsers(deliverable.getUsers(phaseDB));
        deliverable.setDissemination(deliverable.getDissemination(phaseDB));
        deliverable.setPublication(deliverable.getPublication(phaseDB));
        deliverable.setMetadataElements(deliverable.getMetadataElements(phaseDB));
        deliverable.setSelectedFlahsgips(new ArrayList<>());
        // sort selected flagships
        if (reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions() != null
          && !reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions().isEmpty()) {
          reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions()
            .sort((l1, l2) -> l1.getCrpProgram().getAcronym().compareTo(l2.getCrpProgram().getAcronym()));
        }
        deliverable.getSelectedFlahsgips()
          .addAll(reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions());
        deliverables.add(deliverable);

      }
    }

    deliverables.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));

    return deliverables;
  }

  @Override
  public Boolean isDeliverableExcluded(Long deliverableId, Long phaseId) {

    return deliverableDAO.isDeliverableExcluded(deliverableId, phaseId);
  }

  @Override
  public Deliverable saveDeliverable(Deliverable deliverable) {

    return deliverableDAO.save(deliverable);
  }

  @Override
  public Deliverable saveDeliverable(Deliverable deliverable, String section, List<String> relationsName, Phase phase) {
    Deliverable resultDeliverable = deliverableDAO.save(deliverable, section, relationsName, phase);
    return resultDeliverable;
  }

  public void saveDeliverablePhase(Phase next, long deliverableID, Deliverable deliverable) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableInfo> deliverablesInfo = phase.getDeliverableInfos().stream()
      .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID)
      .collect(Collectors.toList());

    if (deliverablesInfo == null || deliverablesInfo.isEmpty()) {
      deliverablesInfo = new ArrayList<>();
      deliverablesInfo.add(new DeliverableInfo());
    }

    for (DeliverableInfo deliverableInfo : deliverablesInfo) {
      deliverableInfo.updateDeliverableInfo(deliverable.getDeliverableInfo());

      if (deliverableInfo.getCrpClusterKeyOutput() != null && deliverableInfo.getCrpClusterKeyOutput().getId() != null
        && deliverableInfo.getCrpClusterKeyOutput() != null) {

        CrpClusterKeyOutput crpClusterKeyOutput =
          crpClusterKeyOutputDAO.find(deliverableInfo.getCrpClusterKeyOutput().getId());

        CrpClusterOfActivity crpClusterOfActivity =
          crpClusterOfActivityDAO.getCrpClusterOfActivityByIdentifierFlagshipAndPhase(
            crpClusterKeyOutput.getCrpClusterOfActivity().getIdentifier(),
            crpClusterKeyOutput.getCrpClusterOfActivity().getCrpProgram().getId(), phase.getId());
        if (crpClusterOfActivity != null) {
          List<CrpClusterKeyOutput> clusterKeyOutputs = crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
            .filter(c -> c.isActive() && c.getComposeID().equals(crpClusterKeyOutput.getComposeID()))
            .collect(Collectors.toList());
          if (!clusterKeyOutputs.isEmpty()) {
            deliverableInfo.setCrpClusterKeyOutput(clusterKeyOutputs.get(0));
          } else {
            deliverableInfo.setCrpClusterKeyOutput(null);
          }
        } else {
          deliverableInfo.setCrpClusterKeyOutput(null);
        }

      }

      deliverableInfo.setPhase(phase);
      deliverableInfoDAO.save(deliverableInfo);
    }


    if (phase.getNext() != null) {
      this.saveDeliverablePhase(phase.getNext(), deliverableID, deliverable);
    }
  }

}
