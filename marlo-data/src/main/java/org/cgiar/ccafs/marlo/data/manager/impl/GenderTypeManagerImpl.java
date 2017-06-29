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


import org.cgiar.ccafs.marlo.data.dao.GenderTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.GenderTypeManager;
import org.cgiar.ccafs.marlo.data.model.GenderType;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class GenderTypeManagerImpl implements GenderTypeManager {


  private GenderTypeDAO genderTypeDAO;
  // Managers


  @Inject
  public GenderTypeManagerImpl(GenderTypeDAO genderTypeDAO) {
    this.genderTypeDAO = genderTypeDAO;


  }

  @Override
  public boolean deleteGenderType(long genderTypeId) {

    return genderTypeDAO.deleteGenderType(genderTypeId);
  }

  @Override
  public boolean existGenderType(long genderTypeID) {

    return genderTypeDAO.existGenderType(genderTypeID);
  }

  @Override
  public List<GenderType> findAll() {

    return genderTypeDAO.findAll();

  }

  @Override
  public GenderType getGenderTypeById(long genderTypeID) {

    return genderTypeDAO.find(genderTypeID);
  }

  @Override
  public long saveGenderType(GenderType genderType) {

    return genderTypeDAO.save(genderType);
  }


}
