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

import org.cgiar.ccafs.marlo.data.dao.SrfSloDAO;
import org.cgiar.ccafs.marlo.data.model.SrfSlo;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class SrfSloMySQLDAO extends AbstractMarloDAO<SrfSlo, Long> implements SrfSloDAO {


  @Inject
  public SrfSloMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteSrfSlo(long srfSloId) {
    SrfSlo srfSlo = this.find(srfSloId);
    srfSlo.setActive(false);
    return this.save(srfSlo) > 0;
  }

  @Override
  public boolean existSrfSlo(long srfSloID) {
    SrfSlo srfSlo = this.find(srfSloID);
    if (srfSlo == null) {
      return false;
    }
    return true;

  }

  @Override
  public SrfSlo find(long id) {
    return super.find(SrfSlo.class, id);

  }

  @Override
  public List<SrfSlo> findAll() {
    String query = "from " + SrfSlo.class.getName() + " where is_active=1";
    List<SrfSlo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(SrfSlo srfSlo) {
    if (srfSlo.getId() == null) {
      super.saveEntity(srfSlo);
    } else {
      super.update(srfSlo);
    }
    return srfSlo.getId();
  }


}