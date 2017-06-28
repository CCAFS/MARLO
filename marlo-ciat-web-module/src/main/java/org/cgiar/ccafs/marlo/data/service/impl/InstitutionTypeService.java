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


import org.cgiar.ccafs.marlo.data.dao.IInstitutionTypeDAO;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.service.IInstitutionTypeService;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class InstitutionTypeService implements IInstitutionTypeService {


  private IInstitutionTypeDAO institutionTypeDAO;

  // Managers


  @Inject
  public InstitutionTypeService(IInstitutionTypeDAO institutionTypeDAO) {
    this.institutionTypeDAO = institutionTypeDAO;


  }

  @Override
  public boolean deleteInstitutionType(long institutionTypeId) {

    return institutionTypeDAO.deleteInstitutionType(institutionTypeId);
  }

  @Override
  public boolean existInstitutionType(long institutionTypeID) {

    return institutionTypeDAO.existInstitutionType(institutionTypeID);
  }

  @Override
  public List<InstitutionType> findAll() {

    return institutionTypeDAO.findAll();

  }

  @Override
  public InstitutionType getInstitutionTypeById(long institutionTypeID) {

    return institutionTypeDAO.find(institutionTypeID);
  }

  @Override
  public List<InstitutionType> getInstitutionTypesByUserId(Long userId) {
    return institutionTypeDAO.getInstitutionTypesByUserId(userId);
  }

  @Override
  public long saveInstitutionType(InstitutionType institutionType) {

    return institutionTypeDAO.save(institutionType);
  }


}
