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


package org.cgiar.ccafs.marlo.data.dao.impl;

import org.cgiar.ccafs.marlo.data.dao.ICenterBeneficiaryDAO;
import org.cgiar.ccafs.marlo.data.model.CenterBeneficiary;

import java.util.List;

import com.google.inject.Inject;

public class CenterBeneficiaryDAO implements ICenterBeneficiaryDAO {

  private StandardDAO dao;

  @Inject
  public CenterBeneficiaryDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteBeneficiary(long beneficiaryId) {
    CenterBeneficiary beneficiary = this.find(beneficiaryId);
    beneficiary.setActive(false);
    return this.save(beneficiary) > 0;
  }

  @Override
  public boolean existBeneficiary(long beneficiaryID) {
    CenterBeneficiary beneficiary = this.find(beneficiaryID);
    if (beneficiary == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterBeneficiary find(long id) {
    return dao.find(CenterBeneficiary.class, id);

  }

  @Override
  public List<CenterBeneficiary> findAll() {
    String query = "from " + CenterBeneficiary.class.getName();
    List<CenterBeneficiary> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterBeneficiary> getBeneficiarysByUserId(long userId) {
    String query = "from " + CenterBeneficiary.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterBeneficiary beneficiary) {
    if (beneficiary.getId() == null) {
      dao.save(beneficiary);
    } else {
      dao.update(beneficiary);
    }
    return beneficiary.getId();
  }


}