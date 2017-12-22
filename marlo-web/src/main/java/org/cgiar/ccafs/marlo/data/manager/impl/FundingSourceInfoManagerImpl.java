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
import org.cgiar.ccafs.marlo.data.dao.FundingSourceInfoDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInfoManager;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class FundingSourceInfoManagerImpl implements FundingSourceInfoManager {


  private FundingSourceInfoDAO fundingSourceInfoDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public FundingSourceInfoManagerImpl(FundingSourceInfoDAO fundingSourceInfoDAO, PhaseDAO phaseDAO) {
    this.fundingSourceInfoDAO = fundingSourceInfoDAO;
    this.phaseDAO = phaseDAO;

  }

  @Override
  public void deleteFundingSourceInfo(long fundingSourceInfoId) {

    fundingSourceInfoDAO.deleteFundingSourceInfo(fundingSourceInfoId);
  }

  @Override
  public boolean existFundingSourceInfo(long fundingSourceInfoID) {

    return fundingSourceInfoDAO.existFundingSourceInfo(fundingSourceInfoID);
  }

  @Override
  public List<FundingSourceInfo> findAll() {

    return fundingSourceInfoDAO.findAll();

  }

  @Override
  public FundingSourceInfo getFundingSourceInfoById(long fundingSourceInfoID) {

    return fundingSourceInfoDAO.find(fundingSourceInfoID);
  }

  @Override
  public FundingSourceInfo saveFundingSourceInfo(FundingSourceInfo fundingSourceInfo) {

    FundingSourceInfo sourceInfo = fundingSourceInfoDAO.save(fundingSourceInfo);
    Phase phase = phaseDAO.find(fundingSourceInfo.getPhase().getId());
    if (phase.getDescription().equals(APConstants.PLANNING)) {
      if (fundingSourceInfo.getPhase().getNext() != null) {
        this.saveInfoPhase(fundingSourceInfo.getPhase().getNext(), fundingSourceInfo.getFundingSource().getId(),
          fundingSourceInfo);
      }
    }
    return sourceInfo;
  }

  public void saveInfoPhase(Phase next, long fundingSourceID, FundingSourceInfo fundingSourceInfo) {
    Phase phase = phaseDAO.find(next.getId());
    Calendar cal = Calendar.getInstance();
    if (fundingSourceInfo.getEndDate() != null) {
      cal.setTime(fundingSourceInfo.getEndDate());
    }


    if (phase.getEditable() != null && phase.getEditable()) {
      List<FundingSourceInfo> fundingSourcesInfos = phase.getFundingSourceInfo().stream()
        .filter(c -> c.getFundingSource().getId().longValue() == fundingSourceID).collect(Collectors.toList());
      if (!fundingSourcesInfos.isEmpty()) {
        for (FundingSourceInfo fundingSourceInfoPhase : fundingSourcesInfos) {
          fundingSourceInfoPhase.updateFundingSourceInfo(fundingSourceInfo);
          if (fundingSourceInfo.getEndDate() != null) {
            if (cal.get(Calendar.YEAR) < phase.getYear()) {

              fundingSourceInfoDAO.deleteFundingSourceInfo(fundingSourceInfoPhase.getId());
            } else {
              fundingSourceInfoDAO.save(fundingSourceInfoPhase);
            }
          }


        }
      } else {
        if (fundingSourceInfo.getEndDate() != null) {

          if (cal.get(Calendar.YEAR) >= phase.getYear()) {
            FundingSourceInfo fundingSourceInfoAdd = new FundingSourceInfo();
            fundingSourceInfoAdd.setFundingSource(fundingSourceInfo.getFundingSource());
            fundingSourceInfoAdd.updateFundingSourceInfo(fundingSourceInfo);
            fundingSourceInfoAdd.setPhase(phase);
            fundingSourceInfoDAO.save(fundingSourceInfoAdd);

          }


        }
      }

    }
    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), fundingSourceID, fundingSourceInfo);
    }
  }

}
