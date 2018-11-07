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

import org.cgiar.ccafs.marlo.data.dao.PowbProgramChangeDAO;
import org.cgiar.ccafs.marlo.data.model.PowbProgramChange;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PowbProgramChangeMySQLDAO extends AbstractMarloDAO<PowbProgramChange, Long> implements PowbProgramChangeDAO {


  @Inject
  public PowbProgramChangeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbProgramChange(long powbProgramChangeId) {
    PowbProgramChange powbProgramChange = this.find(powbProgramChangeId);
    powbProgramChange.setActive(false);
    this.update(powbProgramChange);
  }

  @Override
  public boolean existPowbProgramChange(long powbProgramChangeID) {
    PowbProgramChange powbProgramChange = this.find(powbProgramChangeID);
    if (powbProgramChange == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbProgramChange find(long id) {
    return super.find(PowbProgramChange.class, id);

  }

  @Override
  public List<PowbProgramChange> findAll() {
    String query = "from " + PowbProgramChange.class.getName() + " where is_active=1";
    List<PowbProgramChange> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbProgramChange save(PowbProgramChange powbProgramChange) {
    if (powbProgramChange.getId() == null) {
      super.saveEntity(powbProgramChange);
    } else {
      powbProgramChange = super.update(powbProgramChange);
    }


    return powbProgramChange;
  }


}