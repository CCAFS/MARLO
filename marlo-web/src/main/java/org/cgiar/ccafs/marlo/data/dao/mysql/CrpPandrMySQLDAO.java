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

import org.cgiar.ccafs.marlo.data.dao.CrpPandrDAO;
import org.cgiar.ccafs.marlo.data.model.CrpPandr;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CrpPandrMySQLDAO extends AbstractMarloDAO implements CrpPandrDAO {


  @Inject
  public CrpPandrMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteCrpPandr(long crpPandrId) {
    CrpPandr crpPandr = this.find(crpPandrId);
    crpPandr.setActive(false);
    return this.save(crpPandr) > 0;
  }

  @Override
  public boolean existCrpPandr(long crpPandrID) {
    CrpPandr crpPandr = this.find(crpPandrID);
    if (crpPandr == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpPandr find(long id) {
    return super.find(CrpPandr.class, id);

  }

  @Override
  public List<CrpPandr> findAll() {
    String query = "from " + CrpPandr.class.getName() + " where is_active=1";
    List<CrpPandr> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CrpPandr crpPandr) {
    if (crpPandr.getId() == null) {
      super.save(crpPandr);
    } else {
      super.update(crpPandr);
    }


    return crpPandr.getId();
  }


}