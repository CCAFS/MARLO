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

import org.cgiar.ccafs.marlo.data.dao.PowbCollaborationGlobalUnitDAO;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationGlobalUnit;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PowbCollaborationGlobalUnitMySQLDAO extends AbstractMarloDAO<PowbCollaborationGlobalUnit, Long> implements PowbCollaborationGlobalUnitDAO {


  @Inject
  public PowbCollaborationGlobalUnitMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbCollaborationGlobalUnit(long powbCollaborationGlobalUnitId) {
    PowbCollaborationGlobalUnit powbCollaborationGlobalUnit = this.find(powbCollaborationGlobalUnitId);
    powbCollaborationGlobalUnit.setActive(false);
    this.update(powbCollaborationGlobalUnit);
  }

  @Override
  public boolean existPowbCollaborationGlobalUnit(long powbCollaborationGlobalUnitID) {
    PowbCollaborationGlobalUnit powbCollaborationGlobalUnit = this.find(powbCollaborationGlobalUnitID);
    if (powbCollaborationGlobalUnit == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbCollaborationGlobalUnit find(long id) {
    return super.find(PowbCollaborationGlobalUnit.class, id);

  }

  @Override
  public List<PowbCollaborationGlobalUnit> findAll() {
    String query = "from " + PowbCollaborationGlobalUnit.class.getName() + " where is_active=1";
    List<PowbCollaborationGlobalUnit> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbCollaborationGlobalUnit save(PowbCollaborationGlobalUnit powbCollaborationGlobalUnit) {
    if (powbCollaborationGlobalUnit.getId() == null) {
      super.saveEntity(powbCollaborationGlobalUnit);
    } else {
      powbCollaborationGlobalUnit = super.update(powbCollaborationGlobalUnit);
    }


    return powbCollaborationGlobalUnit;
  }


}