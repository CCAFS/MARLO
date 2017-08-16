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

import org.cgiar.ccafs.marlo.data.dao.LiaisonInstitutionDAO;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class LiaisonInstitutionMySQLDAO extends AbstractMarloDAO<LiaisonInstitution, Long> implements LiaisonInstitutionDAO {


  @Inject
  public LiaisonInstitutionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteLiaisonInstitution(long liaisonInstitutionId) {
    LiaisonInstitution liaisonInstitution = this.find(liaisonInstitutionId);

    liaisonInstitution.setActive(false);
    return this.save(liaisonInstitution) > 0;

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
    return super.find(LiaisonInstitution.class, id);

  }

  @Override
  public List<LiaisonInstitution> findAll() {
    String query = "from " + LiaisonInstitution.class.getName();
    List<LiaisonInstitution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public LiaisonInstitution findByAcronym(String acronym) {
    String query = "from " + LiaisonInstitution.class.getName() + " where acronym='" + acronym + "'";
    List<LiaisonInstitution> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }


  @Override
  public long save(LiaisonInstitution liaisonInstitution) {
    if (liaisonInstitution.getId() == null) {
      liaisonInstitution.setActive(true);
      super.saveEntity(liaisonInstitution);
    } else {
      super.update(liaisonInstitution);
    }


    return liaisonInstitution.getId();
  }


}