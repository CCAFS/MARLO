/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.LiaisonInstitutionDAO;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;

import java.util.List;

import com.google.inject.Inject;

public class LiaisonInstitutionMySQLDAO implements LiaisonInstitutionDAO {

  private StandardDAO dao;

  @Inject
  public LiaisonInstitutionMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteLiaisonInstitution(long liaisonInstitutionId) {
    LiaisonInstitution liaisonInstitution = this.find(liaisonInstitutionId);

    return dao.delete(liaisonInstitution);
  }

  @Override
  public boolean existLiaisonInstitution(long liaisonInstitutionID) {
    LiaisonInstitution liaisonInstitution = this.find(liaisonInstitutionID);
    if (liaisonInstitution == null) {
      return false;
    }
    return true;

  }

  @Override
  public LiaisonInstitution find(long id) {
    return dao.find(LiaisonInstitution.class, id);

  }

  @Override
  public List<LiaisonInstitution> findAll() {
    String query = "from " + LiaisonInstitution.class.getName() + " where is_active=1";
    List<LiaisonInstitution> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(LiaisonInstitution liaisonInstitution) {
    if (liaisonInstitution.getId() == null) {
      dao.save(liaisonInstitution);
    } else {
      dao.update(liaisonInstitution);
    }


    return liaisonInstitution.getId();
  }


}