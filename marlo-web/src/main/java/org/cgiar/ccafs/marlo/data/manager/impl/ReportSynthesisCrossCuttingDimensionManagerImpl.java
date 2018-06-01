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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrossCuttingDimensionDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableIntellectualAssetManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCuttingDimensionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingAssetDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimensionAsset;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimensionInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingInnovationDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisCrossCuttingDimensionManagerImpl implements ReportSynthesisCrossCuttingDimensionManager {


  private ReportSynthesisCrossCuttingDimensionDAO reportSynthesisCrossCuttingDimensionDAO;
  // Managers
  private ReportSynthesisManager reportSynthesisManager;
  private PhaseManager phaseManager;
  private ProjectInnovationManager projectInnovationManager;
  private DeliverableIntellectualAssetManager deliverableIntellectualAssetManager;

  @Inject
  public ReportSynthesisCrossCuttingDimensionManagerImpl(
    ReportSynthesisCrossCuttingDimensionDAO reportSynthesisCrossCuttingDimensionDAO,
    ReportSynthesisManager reportSynthesisManager, PhaseManager phaseManager,
    ProjectInnovationManager projectInnovationManager,
    DeliverableIntellectualAssetManager deliverableIntellectualAssetManager) {
    this.reportSynthesisCrossCuttingDimensionDAO = reportSynthesisCrossCuttingDimensionDAO;
    this.reportSynthesisManager = reportSynthesisManager;
    this.phaseManager = phaseManager;
    this.projectInnovationManager = projectInnovationManager;
    this.deliverableIntellectualAssetManager = deliverableIntellectualAssetManager;
  }

  @Override
  public void deleteReportSynthesisCrossCuttingDimension(long reportSynthesisCrossCuttingDimensionId) {

    reportSynthesisCrossCuttingDimensionDAO
      .deleteReportSynthesisCrossCuttingDimension(reportSynthesisCrossCuttingDimensionId);
  }

  @Override
  public boolean existReportSynthesisCrossCuttingDimension(long reportSynthesisCrossCuttingDimensionID) {

    return reportSynthesisCrossCuttingDimensionDAO
      .existReportSynthesisCrossCuttingDimension(reportSynthesisCrossCuttingDimensionID);
  }

  @Override
  public List<ReportSynthesisCrossCuttingDimension> findAll() {

    return reportSynthesisCrossCuttingDimensionDAO.findAll();

  }

  @Override
  public List<ReportSynthesisCrossCuttingDimension> getFlagshipCCDimensions(List<LiaisonInstitution> lInstitutions,
    long phaseID) {

    List<ReportSynthesisCrossCuttingDimension> crossCuttingDimensions = new ArrayList<>();

    for (LiaisonInstitution liaisonInstitution : lInstitutions) {

      ReportSynthesisCrossCuttingDimension crossCuttingDimension = new ReportSynthesisCrossCuttingDimension();
      ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());

      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisCrpProgress() != null) {
          crossCuttingDimension = reportSynthesisFP.getReportSynthesisCrossCuttingDimension();
        }
      } else {
        ReportSynthesis synthesis = new ReportSynthesis();
        synthesis.setPhase(phaseManager.getPhaseById(phaseID));
        synthesis.setLiaisonInstitution(liaisonInstitution);
        crossCuttingDimension.setReportSynthesis(synthesis);
      }
      crossCuttingDimensions.add(crossCuttingDimension);
    }

    return crossCuttingDimensions;


  }


  @Override
  public List<ReportSynthesisCrossCuttingAssetDTO> getPlannedAssetsList(List<LiaisonInstitution> lInstitutions,
    long phaseID, GlobalUnit loggedCrp, LiaisonInstitution liaisonInstitutionPMU) {

    List<ReportSynthesisCrossCuttingAssetDTO> assetPlannedList = new ArrayList<>();
    Phase phase = phaseManager.getPhaseById(phaseID);

    if (deliverableIntellectualAssetManager.findAll() != null) {
      List<DeliverableIntellectualAsset> intellectualAssets =
        new ArrayList<>(deliverableIntellectualAssetManager.findAll()
          .stream().filter(ps -> ps.getDeliverable().isActive() && ps.getPhase() != null
            && ps.getPhase().getId() == phaseID && ps.getHasPatentPvp() != null && ps.getHasPatentPvp())
          .collect(Collectors.toList()));

      for (DeliverableIntellectualAsset intellectualAsset : intellectualAssets) {
        ReportSynthesisCrossCuttingAssetDTO dto = new ReportSynthesisCrossCuttingAssetDTO();


        intellectualAsset.getDeliverable().getProject()
          .setProjectInfo(intellectualAsset.getDeliverable().getProject().getProjecInfoPhase(phase));
        dto.setDeliverableIntellectualAsset(intellectualAsset);
        if (intellectualAsset.getDeliverable().getProject().getProjectInfo().getAdministrative() != null
          && intellectualAsset.getDeliverable().getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(liaisonInstitutionPMU);
        } else {
          List<ProjectFocus> projectFocuses =
            new ArrayList<>(intellectualAsset.getDeliverable().getProject().getProjectFocuses().stream()
              .filter(pf -> pf.isActive() && pf.getPhase().getId() == phaseID).collect(Collectors.toList()));
          List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
          for (ProjectFocus projectFocus : projectFocuses) {
            liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
              .filter(li -> li.isActive() && li.getCrpProgram() != null
                && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
              .collect(Collectors.toList()));
          }
          dto.setLiaisonInstitutions(liaisonInstitutions);
        }

        assetPlannedList.add(dto);

      }
      List<ReportSynthesisCrossCuttingDimensionAsset> reportAssets = new ArrayList<>();


      for (LiaisonInstitution liaisonInstitution : lInstitutions) {
        ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
        if (reportSynthesisFP != null) {
          if (reportSynthesisFP.getReportSynthesisCrossCuttingDimension() != null) {
            if (reportSynthesisFP.getReportSynthesisCrossCuttingDimension()
              .getReportSynthesisCrossCuttingDimensionAssets() != null) {
              List<ReportSynthesisCrossCuttingDimensionAsset> assets = new ArrayList<>(reportSynthesisFP
                .getReportSynthesisCrossCuttingDimension().getReportSynthesisCrossCuttingDimensionAssets().stream()
                .filter(s -> s.isActive()).collect(Collectors.toList()));
              if (assets != null || !assets.isEmpty()) {
                for (ReportSynthesisCrossCuttingDimensionAsset synthesisCrossCuttingDimensionAsset : assets) {
                  reportAssets.add(synthesisCrossCuttingDimensionAsset);
                }
              }
            }
          }
        }

      }

      List<ReportSynthesisCrossCuttingAssetDTO> removeList = new ArrayList<>();
      for (ReportSynthesisCrossCuttingAssetDTO dto : assetPlannedList) {

        List<LiaisonInstitution> removeLiaison = new ArrayList<>();
        for (LiaisonInstitution liaisonInstitution : dto.getLiaisonInstitutions()) {
          ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
          if (reportSynthesisFP != null) {
            if (reportSynthesisFP.getReportSynthesisCrossCuttingDimension() != null) {

              ReportSynthesisCrossCuttingDimensionAsset reportAssetNew =
                new ReportSynthesisCrossCuttingDimensionAsset();
              reportAssetNew = new ReportSynthesisCrossCuttingDimensionAsset();
              reportAssetNew.setDeliverableIntellectualAsset(dto.getDeliverableIntellectualAsset());
              reportAssetNew
                .setReportSynthesisCrossCuttingDimension(reportSynthesisFP.getReportSynthesisCrossCuttingDimension());

              if (reportAssets.contains(reportAssetNew)) {
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


      for (ReportSynthesisCrossCuttingAssetDTO i : removeList) {
        assetPlannedList.remove(i);
      }

    }

    return assetPlannedList;
  }

  @Override
  public List<ReportSynthesisCrossCuttingInnovationDTO> getPlannedInnovationList(List<LiaisonInstitution> lInstitutions,
    long phaseID, GlobalUnit loggedCrp, LiaisonInstitution liaisonInstitutionPMU) {

    List<ReportSynthesisCrossCuttingInnovationDTO> innovationPlannedList = new ArrayList<>();
    Phase phase = phaseManager.getPhaseById(phaseID);

    if (projectInnovationManager.findAll() != null) {
      List<ProjectInnovation> projectInnovations = new ArrayList<>(projectInnovationManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getProjectInnovationInfo(phase) != null
          && ps.getProjectInnovationInfo(phase).getPhase().getId() == phaseID
          && ps.getProjectInnovationInfo(phase).getYear() == phase.getYear())
        .collect(Collectors.toList()));

      for (ProjectInnovation projectInnovation : projectInnovations) {
        ReportSynthesisCrossCuttingInnovationDTO dto = new ReportSynthesisCrossCuttingInnovationDTO();
        if (projectInnovation.getProjectInnovationInfo(phase) != null) {


          projectInnovation.getProject().setProjectInfo(projectInnovation.getProject().getProjecInfoPhase(phase));
          dto.setProjectInnovation(projectInnovation);
          if (projectInnovation.getProject().getProjectInfo().getAdministrative() != null
            && projectInnovation.getProject().getProjectInfo().getAdministrative()) {
            dto.setLiaisonInstitutions(new ArrayList<>());
            dto.getLiaisonInstitutions().add(liaisonInstitutionPMU);
          } else {
            List<ProjectFocus> projectFocuses = new ArrayList<>(projectInnovation.getProject().getProjectFocuses()
              .stream().filter(pf -> pf.isActive() && pf.getPhase().getId() == phaseID).collect(Collectors.toList()));
            List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
            for (ProjectFocus projectFocus : projectFocuses) {
              liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
                .filter(li -> li.isActive() && li.getCrpProgram() != null
                  && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
                .collect(Collectors.toList()));
            }
            dto.setLiaisonInstitutions(liaisonInstitutions);
          }

          innovationPlannedList.add(dto);
        }
      }
      List<ReportSynthesisCrossCuttingDimensionInnovation> reportInnovations = new ArrayList<>();


      for (LiaisonInstitution liaisonInstitution : lInstitutions) {
        ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
        if (reportSynthesisFP != null) {
          if (reportSynthesisFP.getReportSynthesisCrossCuttingDimension() != null) {
            if (reportSynthesisFP.getReportSynthesisCrossCuttingDimension()
              .getReportSynthesisCrossCuttingDimensionInnovations() != null) {
              List<ReportSynthesisCrossCuttingDimensionInnovation> innovations = new ArrayList<>(reportSynthesisFP
                .getReportSynthesisCrossCuttingDimension().getReportSynthesisCrossCuttingDimensionInnovations().stream()
                .filter(s -> s.isActive()).collect(Collectors.toList()));
              if (innovations != null || !innovations.isEmpty()) {
                for (ReportSynthesisCrossCuttingDimensionInnovation synthesisCrossCuttingDimensionInnovation : innovations) {
                  reportInnovations.add(synthesisCrossCuttingDimensionInnovation);
                }
              }
            }
          }
        }

      }

      List<ReportSynthesisCrossCuttingInnovationDTO> removeList = new ArrayList<>();
      for (ReportSynthesisCrossCuttingInnovationDTO dto : innovationPlannedList) {

        List<LiaisonInstitution> removeLiaison = new ArrayList<>();
        for (LiaisonInstitution liaisonInstitution : dto.getLiaisonInstitutions()) {
          ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
          if (reportSynthesisFP != null) {
            if (reportSynthesisFP.getReportSynthesisCrossCuttingDimension() != null) {

              ReportSynthesisCrossCuttingDimensionInnovation reportInnovationNew =
                new ReportSynthesisCrossCuttingDimensionInnovation();
              reportInnovationNew = new ReportSynthesisCrossCuttingDimensionInnovation();
              reportInnovationNew.setProjectInnovation(dto.getProjectInnovation());
              reportInnovationNew
                .setReportSynthesisCrossCuttingDimension(reportSynthesisFP.getReportSynthesisCrossCuttingDimension());

              if (reportInnovations.contains(reportInnovationNew)) {
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


      for (ReportSynthesisCrossCuttingInnovationDTO i : removeList) {
        innovationPlannedList.remove(i);
      }

    }

    return innovationPlannedList;
  }

  @Override
  public ReportSynthesisCrossCuttingDimension
    getReportSynthesisCrossCuttingDimensionById(long reportSynthesisCrossCuttingDimensionID) {

    return reportSynthesisCrossCuttingDimensionDAO.find(reportSynthesisCrossCuttingDimensionID);
  }

  @Override
  public CrossCuttingDimensionTableDTO getTableC(Phase phase, GlobalUnit globalUnit) {

    CrossCuttingDimensionTableDTO tableC = new CrossCuttingDimensionTableDTO();

    List<Deliverable> deliverables = new ArrayList<>();
    List<DeliverableInfo> deliverableList = new ArrayList<>();
    int iGenderPrincipal = 0;
    int iGenderSignificant = 0;
    int iGenderNa = 0;
    int iYouthPrincipal = 0;
    int iYouthSignificant = 0;
    int iYouthNa = 0;
    int iCapDevPrincipal = 0;
    int iCapDevSignificant = 0;
    int iCapDevNa = 0;


    for (GlobalUnitProject globalUnitProject : globalUnit.getGlobalUnitProjects().stream()
      .filter(p -> p.isActive() && p.getProject() != null && p.getProject().isActive()
        && (p.getProject().getProjecInfoPhase(phase) != null
          && p.getProject().getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
          || p.getProject().getProjecInfoPhase(phase) != null && p.getProject().getProjectInfo().getStatus()
            .intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))
      .collect(Collectors.toList())) {

      for (Deliverable deliverable : globalUnitProject.getProject().getDeliverables().stream()
        .filter(d -> d.isActive() && d.getProject() != null && d.getProject().isActive()
          && d.getProject().getGlobalUnitProjects().stream()
            .filter(gup -> gup.isActive() && gup.getGlobalUnit().getId().equals(globalUnit.getId()))
            .collect(Collectors.toList()).size() > 0
          && d.getDeliverableInfo(phase) != null && d.getDeliverableInfo(phase).getStatus() != null
          && ((d.getDeliverableInfo(phase).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId())
            && (d.getDeliverableInfo(phase).getYear() >= phase.getYear()
              || (d.getDeliverableInfo(phase).getNewExpectedYear() != null
                && d.getDeliverableInfo(phase).getNewExpectedYear().intValue() >= phase.getYear())))
            || (d.getDeliverableInfo(phase).getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())
              && (d.getDeliverableInfo(phase).getNewExpectedYear() != null
                && d.getDeliverableInfo(phase).getNewExpectedYear().intValue() == phase.getYear()))
            || (d.getDeliverableInfo(phase).getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Cancelled.getStatusId())
              && (d.getDeliverableInfo(phase).getYear() == phase.getYear()
                || (d.getDeliverableInfo(phase).getNewExpectedYear() != null
                  && d.getDeliverableInfo(phase).getNewExpectedYear().intValue() == phase.getYear()))))
          && (d.getDeliverableInfo(phase).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())
            || d.getDeliverableInfo(phase).getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Complete.getStatusId())
            || d.getDeliverableInfo(phase).getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Cancelled.getStatusId())))
        .collect(Collectors.toList())) {
        deliverables.add(deliverable);
      }

    }


    if (deliverables != null && !deliverables.isEmpty()) {
      for (Deliverable deliverable : deliverables) {
        DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(phase);
        if (deliverableInfo.isActive()) {
          deliverableList.add(deliverableInfo);
          boolean bGender = false;
          boolean bYouth = false;
          boolean bCapDev = false;
          if (deliverableInfo.getCrossCuttingNa() != null && deliverableInfo.getCrossCuttingNa()) {
            iGenderNa++;
            iYouthNa++;
            iCapDevNa++;
          } else {
            // Gender
            if (deliverableInfo.getCrossCuttingGender() != null && deliverableInfo.getCrossCuttingGender()) {
              bGender = true;
              if (deliverableInfo.getCrossCuttingScoreGender() != null
                && deliverableInfo.getCrossCuttingScoreGender() == 1) {
                iGenderSignificant++;
              } else if (deliverableInfo.getCrossCuttingScoreGender() != null
                && deliverableInfo.getCrossCuttingScoreGender() == 2) {
                iGenderPrincipal++;
              } else if (deliverableInfo.getCrossCuttingScoreGender() == null) {
                iGenderNa++;
              }
            }

            // Youth
            if (deliverableInfo.getCrossCuttingYouth() != null && deliverableInfo.getCrossCuttingYouth()) {
              bYouth = true;
              if (deliverableInfo.getCrossCuttingScoreYouth() != null
                && deliverableInfo.getCrossCuttingScoreYouth() == 1) {
                iYouthSignificant++;
              } else if (deliverableInfo.getCrossCuttingScoreYouth() != null
                && deliverableInfo.getCrossCuttingScoreYouth() == 2) {
                iYouthPrincipal++;
              } else if (deliverableInfo.getCrossCuttingScoreYouth() == null) {
                iYouthNa++;
              }
            }

            // CapDev
            if (deliverableInfo.getCrossCuttingCapacity() != null && deliverableInfo.getCrossCuttingCapacity()) {
              bCapDev = true;
              if (deliverableInfo.getCrossCuttingScoreCapacity() != null
                && deliverableInfo.getCrossCuttingScoreCapacity() == 1) {
                iCapDevSignificant++;
              } else if (deliverableInfo.getCrossCuttingScoreCapacity() != null
                && deliverableInfo.getCrossCuttingScoreCapacity() == 2) {
                iCapDevPrincipal++;
              } else if (deliverableInfo.getCrossCuttingScoreCapacity() == null) {
                iCapDevNa++;
              }
            }

            if (!bGender) {
              iGenderNa++;
            }
            if (!bYouth) {
              iYouthNa++;
            }
            if (!bCapDev) {
              iCapDevNa++;
            }
          }
        }
      }

      int iDeliverableCount = deliverableList.size();

      tableC.setTotal(iDeliverableCount);

      double dGenderPrincipal = (iGenderPrincipal * 100.0) / iDeliverableCount;
      double dGenderSignificant = (iGenderSignificant * 100.0) / iDeliverableCount;
      double dGenderNa = (iGenderNa * 100.0) / iDeliverableCount;
      double dYouthPrincipal = (iYouthPrincipal * 100.0) / iDeliverableCount;
      double dYouthSignificant = (iYouthSignificant * 100.0) / iDeliverableCount;
      double dYouthNa = (iYouthNa * 100.0) / iDeliverableCount;
      double dCapDevPrincipal = (iCapDevPrincipal * 100.0) / iDeliverableCount;
      double dCapDevSignificant = (iCapDevSignificant * 100.0) / iDeliverableCount;
      double dCapDevNa = (iCapDevNa * 100.0) / iDeliverableCount;


      // Gender
      tableC.setGenderPrincipal(iGenderPrincipal);
      tableC.setGenderSignificant(iGenderSignificant);
      tableC.setGenderScored(iGenderNa);

      tableC.setPercentageGenderPrincipal(dGenderPrincipal);
      tableC.setPercentageGenderSignificant(dGenderSignificant);
      tableC.setPercentageGenderNotScored(dGenderNa);
      // Youth
      tableC.setYouthPrincipal(iYouthPrincipal);
      tableC.setYouthSignificant(iYouthSignificant);
      tableC.setYouthScored(iYouthNa);

      tableC.setPercentageYouthPrincipal(dYouthPrincipal);
      tableC.setPercentageYouthSignificant(dYouthSignificant);
      tableC.setPercentageYouthNotScored(dYouthNa);
      // CapDev
      tableC.setCapDevPrincipal(iCapDevPrincipal);
      tableC.setCapDevSignificant(iCapDevSignificant);
      tableC.setCapDevScored(iCapDevNa);

      tableC.setPercentageCapDevPrincipal(dCapDevPrincipal);
      tableC.setPercentageCapDevSignificant(dCapDevSignificant);
      tableC.setPercentageCapDevNotScored(dCapDevNa);


    }


    tableC.setDeliverableList(deliverableList);
    return tableC;

  }

  @Override
  public ReportSynthesisCrossCuttingDimension saveReportSynthesisCrossCuttingDimension(
    ReportSynthesisCrossCuttingDimension reportSynthesisCrossCuttingDimension) {

    return reportSynthesisCrossCuttingDimensionDAO.save(reportSynthesisCrossCuttingDimension);
  }


}
