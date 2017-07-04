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
package org.cgiar.ccafs.marlo.data.service.impl;


import org.cgiar.ccafs.marlo.data.dao.ICenterBeneficiaryTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterBeneficiaryType;
import org.cgiar.ccafs.marlo.data.service.ICenterBeneficiaryTypeManager;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterBeneficiaryTypeManager implements ICenterBeneficiaryTypeManager {


  private ICenterBeneficiaryTypeDAO beneficiaryTypeDAO;

  // Managers


  @Inject
  public CenterBeneficiaryTypeManager(ICenterBeneficiaryTypeDAO beneficiaryTypeDAO) {
    this.beneficiaryTypeDAO = beneficiaryTypeDAO;


  }

  @Override
  public boolean deleteBeneficiaryType(long beneficiaryTypeId) {

    return beneficiaryTypeDAO.deleteBeneficiaryType(beneficiaryTypeId);
  }

  @Override
  public boolean existBeneficiaryType(long beneficiaryTypeID) {

    return beneficiaryTypeDAO.existBeneficiaryType(beneficiaryTypeID);
  }

  @Override
  public List<CenterBeneficiaryType> findAll() {

    return beneficiaryTypeDAO.findAll();

  }

  @Override
  public CenterBeneficiaryType getBeneficiaryTypeById(long beneficiaryTypeID) {

    return beneficiaryTypeDAO.find(beneficiaryTypeID);
  }

  @Override
  public List<CenterBeneficiaryType> getBeneficiaryTypesByUserId(Long userId) {
    return beneficiaryTypeDAO.getBeneficiaryTypesByUserId(userId);
  }

  @Override
  public long saveBeneficiaryType(CenterBeneficiaryType beneficiaryType) {

    return beneficiaryTypeDAO.save(beneficiaryType);
  }


}
