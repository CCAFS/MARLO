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

import org.cgiar.ccafs.marlo.data.dao.SrfSubIdoDAO;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class SrfSubIdoMySQLDAO extends AbstractMarloDAO<SrfSubIdo, Long> implements SrfSubIdoDAO {


  @Inject
  public SrfSubIdoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteSrfSubIdo(long srfSubIdoId) {
    SrfSubIdo srfSubIdo = this.find(srfSubIdoId);
    srfSubIdo.setActive(false);
    this.save(srfSubIdo);
  }

  @Override
  public boolean existSrfSubIdo(long srfSubIdoID) {
    SrfSubIdo srfSubIdo = this.find(srfSubIdoID);
    if (srfSubIdo == null) {
      return false;
    }
    return true;

  }

  @Override
  public SrfSubIdo find(long id) {
    return super.find(SrfSubIdo.class, id);

  }

  @Override
  public List<SrfSubIdo> findAll() {
    String query = "from " + SrfSubIdo.class.getName() + " where is_active=1";
    List<SrfSubIdo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public SrfSubIdo save(SrfSubIdo srfSubIdo) {
    if (srfSubIdo.getId() == null) {
      super.saveEntity(srfSubIdo);
    } else {
      srfSubIdo = super.update(srfSubIdo);
    }
    return srfSubIdo;
  }


}