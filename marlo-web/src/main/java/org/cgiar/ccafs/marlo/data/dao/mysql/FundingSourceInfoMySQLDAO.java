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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.FundingSourceInfoDAO;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class FundingSourceInfoMySQLDAO implements FundingSourceInfoDAO {

  private StandardDAO dao;

  @Inject
  public FundingSourceInfoMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteFundingSourceInfo(long fundingSourceInfoId) {
    FundingSourceInfo fundingSourceInfo = this.find(fundingSourceInfoId);
    return dao.delete(fundingSourceInfo);
  }

  @Override
  public boolean existFundingSourceInfo(long fundingSourceInfoID) {
    FundingSourceInfo fundingSourceInfo = this.find(fundingSourceInfoID);
    if (fundingSourceInfo == null) {
      return false;
    }
    return true;

  }

  @Override
  public FundingSourceInfo find(long id) {
    return dao.find(FundingSourceInfo.class, id);

  }

  @Override
  public List<FundingSourceInfo> findAll() {
    String query = "from " + FundingSourceInfo.class.getName();
    List<FundingSourceInfo> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(FundingSourceInfo fundingSourceInfo) {
    if (fundingSourceInfo.getId() == null) {
      dao.save(fundingSourceInfo);
    } else {
      dao.update(fundingSourceInfo);
    }

    if (fundingSourceInfo.getPhase().getDescription().equals(APConstants.PLANNING)) {
      if (fundingSourceInfo.getPhase().getNext() != null) {
        this.saveInfoPhase(fundingSourceInfo.getPhase().getNext(), fundingSourceInfo.getFundingSource().getId(),
          fundingSourceInfo);
      }
    }

    return fundingSourceInfo.getId();
  }

  public void saveInfoPhase(Phase next, long fundingSourceID, FundingSourceInfo fundingSourceInfo) {
    Phase phase = dao.find(Phase.class, next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<FundingSourceInfo> fundingSourcesInfos = phase.getFundingSourceInfo().stream()
        .filter(c -> c.getFundingSource().getId().longValue() == fundingSourceID).collect(Collectors.toList());
      if (!fundingSourcesInfos.isEmpty()) {
        for (FundingSourceInfo fundingSourceInfoPhase : fundingSourcesInfos) {
          fundingSourceInfoPhase.updateFundingSourceInfo(fundingSourceInfo);
          dao.update(fundingSourceInfoPhase);
        }
      } else {
        if (fundingSourceInfo.getEndDate() != null) {
          Calendar cal = Calendar.getInstance();
          cal.setTime(fundingSourceInfo.getEndDate());
          if (cal.get(Calendar.YEAR) >= phase.getYear()) {
            FundingSourceInfo fundingSourceInfoAdd = new FundingSourceInfo();
            fundingSourceInfoAdd.setFundingSource(fundingSourceInfo.getFundingSource());
            fundingSourceInfoAdd.updateFundingSourceInfo(fundingSourceInfo);
            fundingSourceInfoAdd.setPhase(phase);
            dao.save(fundingSourceInfoAdd);
          }
        }
      }

    }
    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), fundingSourceID, fundingSourceInfo);
    }
  }


}