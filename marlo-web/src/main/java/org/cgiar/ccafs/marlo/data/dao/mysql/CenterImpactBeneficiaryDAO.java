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

import org.cgiar.ccafs.marlo.data.dao.ICenterImpactBeneficiaryDAO;
import org.cgiar.ccafs.marlo.data.model.CenterImpactBeneficiary;

import java.util.List;

import com.google.inject.Inject;

public class CenterImpactBeneficiaryDAO implements ICenterImpactBeneficiaryDAO {

  private StandardDAO dao;

  @Inject
  public CenterImpactBeneficiaryDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteResearchImpactBeneficiary(long researchImpactBeneficiaryId) {
    CenterImpactBeneficiary researchImpactBeneficiary = this.find(researchImpactBeneficiaryId);
    researchImpactBeneficiary.setActive(false);
    return this.save(researchImpactBeneficiary) > 0;
  }

  @Override
  public boolean existResearchImpactBeneficiary(long researchImpactBeneficiaryID) {
    CenterImpactBeneficiary researchImpactBeneficiary = this.find(researchImpactBeneficiaryID);
    if (researchImpactBeneficiary == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterImpactBeneficiary find(long id) {
    return dao.find(CenterImpactBeneficiary.class, id);

  }

  @Override
  public List<CenterImpactBeneficiary> findAll() {
    String query = "from " + CenterImpactBeneficiary.class.getName();
    List<CenterImpactBeneficiary> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterImpactBeneficiary> getResearchImpactBeneficiarysByUserId(long userId) {
    String query = "from " + CenterImpactBeneficiary.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterImpactBeneficiary researchImpactBeneficiary) {
    if (researchImpactBeneficiary.getId() == null) {
      dao.save(researchImpactBeneficiary);
    } else {
      dao.update(researchImpactBeneficiary);
    }
    return researchImpactBeneficiary.getId();
  }


}