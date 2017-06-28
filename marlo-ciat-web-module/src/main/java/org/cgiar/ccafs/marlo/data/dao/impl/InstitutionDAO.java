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

import org.cgiar.ccafs.marlo.data.dao.IInstitutionDAO;
import org.cgiar.ccafs.marlo.data.model.Institution;

import java.util.List;

import com.google.inject.Inject;

public class InstitutionDAO implements IInstitutionDAO {

  private StandardDAO dao;

  @Inject
  public InstitutionDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteInstitution(long institutionId) {
    Institution institution = this.find(institutionId);
    return dao.delete(institution);
  }

  @Override
  public boolean existInstitution(long institutionID) {
    Institution institution = this.find(institutionID);
    if (institution == null) {
      return false;
    }
    return true;

  }

  @Override
  public Institution find(long id) {
    return dao.find(Institution.class, id);

  }

  @Override
  public List<Institution> findAll() {
    String query = "from " + Institution.class.getName();
    List<Institution> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<Institution> getInstitutionsByUserId(long userId) {
    String query = "from " + Institution.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(Institution institution) {
    if (institution.getId() == null) {
      dao.save(institution);
    } else {
      dao.update(institution);
    }
    return institution.getId();
  }


}