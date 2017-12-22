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
import org.cgiar.ccafs.marlo.data.dao.FundingSourceInstitutionDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInstitutionManager;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class FundingSourceInstitutionManagerImpl implements FundingSourceInstitutionManager {


  private PhaseDAO phaseDAO;

  private FundingSourceInstitutionDAO fundingSourceInstitutionDAO;
  // Managers


  @Inject
  public FundingSourceInstitutionManagerImpl(FundingSourceInstitutionDAO fundingSourceInstitutionDAO,
    PhaseDAO phaseDAO) {
    this.fundingSourceInstitutionDAO = fundingSourceInstitutionDAO;
    this.phaseDAO = phaseDAO;

  }

  public void cloneFundingSourceInstitution(FundingSourceInstitution fundingSourceInstitutionAdd,
    FundingSourceInstitution fundingSourceInstitution, Phase phase) {
    fundingSourceInstitutionAdd.setFundingSource(fundingSourceInstitution.getFundingSource());
    fundingSourceInstitutionAdd.setInstitution(fundingSourceInstitution.getInstitution());
    fundingSourceInstitutionAdd.setPhase(phase);

  }

  @Override
  public void deleteFundingSourceInstitution(long fundingSourceInstitutionId) {
    FundingSourceInstitution fundingSourceInstitution =
      this.getFundingSourceInstitutionById(fundingSourceInstitutionId);
    fundingSourceInstitutionDAO.deleteFundingSourceInstitution(fundingSourceInstitutionId);

    Phase currentPhase = phaseDAO.find(fundingSourceInstitution.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (fundingSourceInstitution.getPhase().getNext() != null) {
        this.deletFundingSourceInstitutionPhase(fundingSourceInstitution.getPhase().getNext(),
          fundingSourceInstitution.getFundingSource().getId(), fundingSourceInstitution);
      }
    }
  }

  public void deletFundingSourceInstitutionPhase(Phase next, long fundingSourceID,
    FundingSourceInstitution fundingSourceInstitution) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<FundingSourceInstitution> institutions = phase.getFundingSourceInstitutions().stream()
        .filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
          && c.getInstitution().getId().equals(fundingSourceInstitution.getInstitution().getId()))
        .collect(Collectors.toList());
      for (FundingSourceInstitution fundingSourceInstitutionDB : institutions) {
        fundingSourceInstitutionDAO.deleteFundingSourceInstitution(fundingSourceInstitutionDB.getId());
      }
    }
    if (phase.getNext() != null) {
      this.deletFundingSourceInstitutionPhase(phase.getNext(), fundingSourceID, fundingSourceInstitution);

    }

  }

  @Override
  public boolean existFundingSourceInstitution(long fundingSourceInstitutionID) {

    return fundingSourceInstitutionDAO.existFundingSourceInstitution(fundingSourceInstitutionID);
  }

  @Override
  public List<FundingSourceInstitution> findAll() {

    return fundingSourceInstitutionDAO.findAll();

  }

  @Override
  public FundingSourceInstitution getFundingSourceInstitutionById(long fundingSourceInstitutionID) {

    return fundingSourceInstitutionDAO.find(fundingSourceInstitutionID);
  }

  @Override
  public FundingSourceInstitution saveFundingSourceInstitution(FundingSourceInstitution fundingSourceInstitution) {

    FundingSourceInstitution fuInstitution = fundingSourceInstitutionDAO.save(fundingSourceInstitution);
    Phase currentPhase = phaseDAO.find(fundingSourceInstitution.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (fundingSourceInstitution.getPhase().getNext() != null) {
        this.saveFundingSourceInstitutionPhase(fundingSourceInstitution.getPhase().getNext(),
          fundingSourceInstitution.getFundingSource().getId(), fundingSourceInstitution);
      }
    }


    return fuInstitution;
  }

  public void saveFundingSourceInstitutionPhase(Phase next, long fundingSourceID,
    FundingSourceInstitution fundingSourceInstitution) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<FundingSourceInstitution> institutions = phase.getFundingSourceInstitutions().stream()
        .filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
          && c.getInstitution().getId().equals(fundingSourceInstitution.getInstitution().getId()))
        .collect(Collectors.toList());
      if (institutions.isEmpty()) {
        FundingSourceInstitution fundingSourceInstitutionAdd = new FundingSourceInstitution();
        this.cloneFundingSourceInstitution(fundingSourceInstitutionAdd, fundingSourceInstitution, phase);
        fundingSourceInstitutionDAO.save(fundingSourceInstitutionAdd);
      }

    }
    if (phase.getNext() != null) {
      this.saveFundingSourceInstitutionPhase(phase.getNext(), fundingSourceID, fundingSourceInstitution);
    }


  }
}
