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

import org.cgiar.ccafs.marlo.data.dao.ICenterBeneficiaryTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterBeneficiaryType;

import java.util.List;

import com.google.inject.Inject;

public class CenterBeneficiaryTypeDAO implements ICenterBeneficiaryTypeDAO {

  private StandardDAO dao;

  @Inject
  public CenterBeneficiaryTypeDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteBeneficiaryType(long beneficiaryTypeId) {
    CenterBeneficiaryType beneficiaryType = this.find(beneficiaryTypeId);
    beneficiaryType.setActive(false);
    return this.save(beneficiaryType) > 0;
  }

  @Override
  public boolean existBeneficiaryType(long beneficiaryTypeID) {
    CenterBeneficiaryType beneficiaryType = this.find(beneficiaryTypeID);
    if (beneficiaryType == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterBeneficiaryType find(long id) {
    return dao.find(CenterBeneficiaryType.class, id);

  }

  @Override
  public List<CenterBeneficiaryType> findAll() {
    String query = "from " + CenterBeneficiaryType.class.getName();
    List<CenterBeneficiaryType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterBeneficiaryType> getBeneficiaryTypesByUserId(long userId) {
    String query = "from " + CenterBeneficiaryType.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterBeneficiaryType beneficiaryType) {
    if (beneficiaryType.getId() == null) {
      dao.save(beneficiaryType);
    } else {
      dao.update(beneficiaryType);
    }
    return beneficiaryType.getId();
  }


}