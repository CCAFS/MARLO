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

import org.cgiar.ccafs.marlo.data.dao.IInstitutionTypeDAO;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;

import java.util.List;

import com.google.inject.Inject;

public class InstitutionTypeDAO implements IInstitutionTypeDAO {

  private StandardDAO dao;

  @Inject
  public InstitutionTypeDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteInstitutionType(long institutionTypeId) {
    InstitutionType institutionType = this.find(institutionTypeId);
    return dao.delete(institutionType);
  }

  @Override
  public boolean existInstitutionType(long institutionTypeID) {
    InstitutionType institutionType = this.find(institutionTypeID);
    if (institutionType == null) {
      return false;
    }
    return true;

  }

  @Override
  public InstitutionType find(long id) {
    return dao.find(InstitutionType.class, id);

  }

  @Override
  public List<InstitutionType> findAll() {
    String query = "from " + InstitutionType.class.getName();
    List<InstitutionType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<InstitutionType> getInstitutionTypesByUserId(long userId) {
    String query = "from " + InstitutionType.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(InstitutionType institutionType) {
    if (institutionType.getId() == null) {
      dao.save(institutionType);
    } else {
      dao.update(institutionType);
    }
    return institutionType.getId();
  }


}