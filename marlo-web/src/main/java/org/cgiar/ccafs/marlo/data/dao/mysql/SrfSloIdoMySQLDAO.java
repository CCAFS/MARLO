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

import org.cgiar.ccafs.marlo.data.dao.SrfSloIdoDAO;
import org.cgiar.ccafs.marlo.data.model.SrfSloIdo;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class SrfSloIdoMySQLDAO extends AbstractMarloDAO<SrfSloIdo, Long> implements SrfSloIdoDAO {


  @Inject
  public SrfSloIdoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteSrfSloIdo(long srfSloIdoId) {
    SrfSloIdo srfSloIdo = this.find(srfSloIdoId);
    srfSloIdo.setActive(false);
    this.save(srfSloIdo);
  }

  @Override
  public boolean existSrfSloIdo(long srfSloIdoID) {
    SrfSloIdo srfSloIdo = this.find(srfSloIdoID);
    if (srfSloIdo == null) {
      return false;
    }
    return true;

  }

  @Override
  public SrfSloIdo find(long id) {
    return super.find(SrfSloIdo.class, id);

  }

  @Override
  public List<SrfSloIdo> findAll() {
    String query = "from " + SrfSloIdo.class.getName() + " where is_active=1";
    List<SrfSloIdo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public SrfSloIdo save(SrfSloIdo srfSloIdo) {
    if (srfSloIdo.getId() == null) {
      super.saveEntity(srfSloIdo);
    } else {
      srfSloIdo = super.update(srfSloIdo);
    }
    return srfSloIdo;
  }


}