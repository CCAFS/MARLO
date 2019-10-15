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
import org.cgiar.ccafs.marlo.data.dao.FundingSourceDivisionDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceDivisionManager;
import org.cgiar.ccafs.marlo.data.model.FundingSourceDivision;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;


@Named
public class FundingSourceDivisionManagerImpl implements FundingSourceDivisionManager {


  private PhaseDAO phaseDAO;

  private FundingSourceDivisionDAO fundingSourceDivisionDAO;
  // Managers


  @Inject
  public FundingSourceDivisionManagerImpl(FundingSourceDivisionDAO fundingSourceDivisionDAO, PhaseDAO phaseDAO) {
    this.fundingSourceDivisionDAO = fundingSourceDivisionDAO;
    this.phaseDAO = phaseDAO;

  }

  public void cloneFundingSourceDivision(FundingSourceDivision fundingSourceDivisionAdd,
    FundingSourceDivision fundingSourceDivision, Phase phase) {
    fundingSourceDivisionAdd.setFundingSource(fundingSourceDivision.getFundingSource());
    fundingSourceDivisionAdd.setDivision(fundingSourceDivision.getDivision());
    fundingSourceDivisionAdd.setPhase(phase);

  }

  @Override
  public void deleteFundingSourceDivision(long fundingSourceDivisionId) {
    FundingSourceDivision fundingSourceDivision = this.getFundingSourceDivisionById(fundingSourceDivisionId);
    fundingSourceDivisionDAO.deleteFundingSourceDivision(fundingSourceDivisionId);

    Phase currentPhase = phaseDAO.find(fundingSourceDivision.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (fundingSourceDivision.getPhase().getNext() != null) {
        this.deletFundingSourceDivisionPhase(fundingSourceDivision.getPhase().getNext(),
          fundingSourceDivision.getFundingSource().getId(), fundingSourceDivision);
      }
    }
  }

  public void deletFundingSourceDivisionPhase(Phase next, long fundingSourceID,
    FundingSourceDivision fundingSourceDivision) {
    Phase phase = phaseDAO.find(next.getId());

    List<FundingSourceDivision> institutions = phase.getFundingSourceDivisions().stream()
      .filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
        && c.getDivision().getId().equals(fundingSourceDivision.getDivision().getId()))
      .collect(Collectors.toList());
    for (FundingSourceDivision fundingSourceDivisionDB : institutions) {
      fundingSourceDivisionDAO.deleteFundingSourceDivision(fundingSourceDivisionDB.getId());
    }

    if (phase.getNext() != null) {
      this.deletFundingSourceDivisionPhase(phase.getNext(), fundingSourceID, fundingSourceDivision);

    }

  }

  @Override
  public boolean existFundingSourceDivision(long fundingSourceDivisionID) {

    return fundingSourceDivisionDAO.existFundingSourceDivision(fundingSourceDivisionID);
  }

  @Override
  public List<FundingSourceDivision> findAll() {

    return fundingSourceDivisionDAO.findAll();

  }

  @Override
  public FundingSourceDivision getFundingSourceDivisionById(long fundingSourceDivisionID) {

    return fundingSourceDivisionDAO.find(fundingSourceDivisionID);
  }

  @Override
  public FundingSourceDivision saveFundingSourceDivision(FundingSourceDivision fundingSourceDivision) {

    FundingSourceDivision fuDivision = fundingSourceDivisionDAO.save(fundingSourceDivision);
    Phase currentPhase = phaseDAO.find(fundingSourceDivision.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (fundingSourceDivision.getPhase().getNext() != null) {
        this.saveFundingSourceDivisionPhase(fundingSourceDivision.getPhase().getNext(),
          fundingSourceDivision.getFundingSource().getId(), fundingSourceDivision);
      }
    }


    return fuDivision;
  }

  public void saveFundingSourceDivisionPhase(Phase next, long fundingSourceID,
    FundingSourceDivision fundingSourceDivision) {
    Phase phase = phaseDAO.find(next.getId());

    List<FundingSourceDivision> institutions = phase.getFundingSourceDivisions().stream()
      .filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
        && c.getDivision().getId().equals(fundingSourceDivision.getDivision().getId()))
      .collect(Collectors.toList());
    if (institutions.isEmpty()) {
      FundingSourceDivision fundingSourceDivisionAdd = new FundingSourceDivision();
      this.cloneFundingSourceDivision(fundingSourceDivisionAdd, fundingSourceDivision, phase);
      fundingSourceDivisionDAO.save(fundingSourceDivisionAdd);
    }


    if (phase.getNext() != null) {
      this.saveFundingSourceDivisionPhase(phase.getNext(), fundingSourceID, fundingSourceDivision);
    }


  }
}
