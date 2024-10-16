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


import org.cgiar.ccafs.marlo.data.dao.SectionStatusDAO;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class SectionStatusManagerImpl implements SectionStatusManager {


  private SectionStatusDAO sectionStatusDAO;
  // Managers


  @Inject
  public SectionStatusManagerImpl(SectionStatusDAO sectionStatusDAO) {
    this.sectionStatusDAO = sectionStatusDAO;


  }

  @Override
  public void deleteSectionStatus(long sectionStatusId) {

    sectionStatusDAO.deleteSectionStatus(sectionStatusId);
  }

  @Override
  public boolean existSectionStatus(long sectionStatusID) {

    return sectionStatusDAO.existSectionStatus(sectionStatusID);
  }

  @Override
  public List<SectionStatus> findAll() {

    return sectionStatusDAO.findAll();

  }


  @Override
  public int findAllQuantity() {

    return sectionStatusDAO.findAllQuantity();

  }


  @Override
  public List<Integer> getCompleteDeliverableListByPhase(long phase) {
    return sectionStatusDAO.getCompleteDeliverableListByPhase(phase);
  }

  @Override
  public List<SectionStatus> getSectionsStatusByReportSynthesis(long powbSynthesisID, String cycle, int year,
    Boolean upkeep, String sectionName) {
    return sectionStatusDAO.getSectionsStatusByReportSynthesis(powbSynthesisID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByCaseStudy(long caseStudyID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    return sectionStatusDAO.getSectionStatusByCaseStudy(caseStudyID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByCrpIndicators(long liaisonInstitutionID, String cycle, int year,
    Boolean upkeep, String sectionName) {
    return sectionStatusDAO.getSectionStatusByCrpIndicators(liaisonInstitutionID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByCrpProgam(long crpProgramID, String sectionName, String cylce, int year,
    Boolean upkeep) {
    return sectionStatusDAO.getSectionStatusByCrpProgam(crpProgramID, sectionName, cylce, year, upkeep);
  }

  @Override
  public SectionStatus getSectionStatusByDeliverable(long deliverableID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    return sectionStatusDAO.getSectionStatusByDeliverable(deliverableID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByFundingSource(long fundingSource, String cycle, Integer year, Boolean upkeep,
    String sectionName) {
    return sectionStatusDAO.getSectionStatusByFundingSource(fundingSource, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusById(long sectionStatusID) {

    return sectionStatusDAO.find(sectionStatusID);
  }

  @Override
  public SectionStatus getSectionStatusByIpProgram(long ipProgramID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    return sectionStatusDAO.getSectionStatusByIpProgram(ipProgramID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByPowbSynthesis(long powbSynthesisID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    return sectionStatusDAO.getSectionStatusByPowbSynthesis(powbSynthesisID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByProject(long projectID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    return sectionStatusDAO.getSectionStatusByProject(projectID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByProjectCofunded(long projectID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    return sectionStatusDAO.getSectionStatusByProjectCofunded(projectID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByProjectContributionToLP6(long projectLp6ContributionID, String cycle, int year,
    Boolean upkeep, String sectionName) {
    return sectionStatusDAO.getSectionStatusByProjectContributionToLP6(projectLp6ContributionID, cycle, year, upkeep,
      sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByProjectExpectedStudy(long expectedID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    return sectionStatusDAO.getSectionStatusByProjectExpectedStudy(expectedID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByProjectHighlight(long projectHighlightID, String cycle, int year,
    Boolean upkeep, String sectionName) {
    return sectionStatusDAO.getSectionStatusByProjectHighlight(projectHighlightID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByProjectImpacts(Long projectImpactID, String cycle, int year, Boolean upkeep,
    String sectionName) {

    return sectionStatusDAO.getSectionStatusByProjectImpacts(projectImpactID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByProjectInnovation(long projectInnovationID, String cycle, int year,
    Boolean upkeep, String sectionName) {
    return sectionStatusDAO.getSectionStatusByProjectInnovation(projectInnovationID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByProjectOutcome(long projectID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    return sectionStatusDAO.getSectionStatusByProjectOutcome(projectID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByProjectPolicy(long projectPolicyID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    return sectionStatusDAO.getSectionStatusByProjectPolicy(projectPolicyID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByReportSynthesis(long powbSynthesisID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    return sectionStatusDAO.getSectionStatusByReportSynthesis(powbSynthesisID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusBySynthesisMog(long ipProgramID, String cycle, int year, Boolean upkeep,
    String sectionName) {
    return sectionStatusDAO.getSectionStatusBySynteshisMog(ipProgramID, cycle, year, upkeep, sectionName);
  }

  @Override
  public SectionStatus saveSectionStatus(SectionStatus sectionStatus) {

    return sectionStatusDAO.save(sectionStatus);
  }

}
