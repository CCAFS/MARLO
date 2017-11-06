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


import org.cgiar.ccafs.marlo.data.dao.InstitutionTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class InstitutionTypeManagerImpl implements InstitutionTypeManager {


  private InstitutionTypeDAO institutionTypeDAO;
  // Managers


  @Inject
  public InstitutionTypeManagerImpl(InstitutionTypeDAO institutionTypeDAO) {
    this.institutionTypeDAO = institutionTypeDAO;


  }

  @Override
  public void deleteInstitutionType(long institutionTypeId) {

    institutionTypeDAO.deleteInstitutionType(institutionTypeId);
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
  public InstitutionType saveInstitutionType(InstitutionType institutionType) {

    return institutionTypeDAO.save(institutionType);
  }


}
