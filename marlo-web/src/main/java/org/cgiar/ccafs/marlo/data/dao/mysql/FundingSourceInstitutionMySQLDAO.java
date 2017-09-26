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
import org.cgiar.ccafs.marlo.data.dao.FundingSourceInstitutionDAO;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class FundingSourceInstitutionMySQLDAO implements FundingSourceInstitutionDAO {

  private StandardDAO dao;

  @Inject
  public FundingSourceInstitutionMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  public void cloneFundingSourceInstitution(FundingSourceInstitution fundingSourceInstitutionAdd,
    FundingSourceInstitution fundingSourceInstitution, Phase phase) {
    fundingSourceInstitutionAdd.setFundingSource(fundingSourceInstitution.getFundingSource());
    fundingSourceInstitutionAdd.setInstitution(fundingSourceInstitution.getInstitution());
    fundingSourceInstitutionAdd.setPhase(phase);

  }

  @Override
  public boolean deleteFundingSourceInstitution(long fundingSourceInstitutionId) {
    FundingSourceInstitution fundingSourceInstitution = this.find(fundingSourceInstitutionId);
    boolean result = dao.delete(fundingSourceInstitution);
    Phase currentPhase = dao.find(Phase.class, fundingSourceInstitution.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (fundingSourceInstitution.getPhase().getNext() != null) {
        this.deletFundingSourceInstitutionPhase(fundingSourceInstitution.getPhase().getNext(),
          fundingSourceInstitution.getFundingSource().getId(), fundingSourceInstitution);
      }
    }

    return result;


  }

  public void deletFundingSourceInstitutionPhase(Phase next, long fundingSourceID,
    FundingSourceInstitution fundingSourceInstitution) {
    Phase phase = dao.find(Phase.class, next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<FundingSourceInstitution> institutions = phase.getFundingSourceInstitutions().stream()
        .filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
          && c.getInstitution().getId().equals(fundingSourceInstitution.getInstitution().getId()))
        .collect(Collectors.toList());
      for (FundingSourceInstitution fundingSourceInstitutionDB : institutions) {
        dao.delete(fundingSourceInstitutionDB);
      }
    }
    if (phase.getNext() != null) {
      this.deletFundingSourceInstitutionPhase(phase.getNext(), fundingSourceID, fundingSourceInstitution);

    }
  }

  @Override
  public boolean existFundingSourceInstitution(long fundingSourceInstitutionID) {
    FundingSourceInstitution fundingSourceInstitution = this.find(fundingSourceInstitutionID);
    if (fundingSourceInstitution == null) {
      return false;
    }
    return true;

  }

  @Override
  public FundingSourceInstitution find(long id) {
    return dao.find(FundingSourceInstitution.class, id);

  }

  @Override
  public List<FundingSourceInstitution> findAll() {
    String query = "from " + FundingSourceInstitution.class.getName() + " where is_active=1";
    List<FundingSourceInstitution> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(FundingSourceInstitution fundingSourceInstitution) {
    if (fundingSourceInstitution.getId() == null) {
      dao.save(fundingSourceInstitution);
    } else {
      dao.update(fundingSourceInstitution);
    }

    Phase currentPhase = dao.find(Phase.class, fundingSourceInstitution.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (fundingSourceInstitution.getPhase().getNext() != null) {
        this.saveFundingSourceInstitutionPhase(fundingSourceInstitution.getPhase().getNext(),
          fundingSourceInstitution.getFundingSource().getId(), fundingSourceInstitution);
      }
    }

    return fundingSourceInstitution.getId();
  }

  public void saveFundingSourceInstitutionPhase(Phase next, long fundingSourceID,
    FundingSourceInstitution fundingSourceInstitution) {
    Phase phase = dao.find(Phase.class, next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<FundingSourceInstitution> institutions = phase.getFundingSourceInstitutions().stream()
        .filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
          && c.getInstitution().getId().equals(fundingSourceInstitution.getInstitution().getId()))
        .collect(Collectors.toList());
      if (institutions.isEmpty()) {
        FundingSourceInstitution fundingSourceInstitutionAdd = new FundingSourceInstitution();
        this.cloneFundingSourceInstitution(fundingSourceInstitutionAdd, fundingSourceInstitution, phase);
        dao.save(fundingSourceInstitutionAdd);
      }

    }
    if (phase.getNext() != null) {
      this.saveFundingSourceInstitutionPhase(phase.getNext(), fundingSourceID, fundingSourceInstitution);
    }


  }
}