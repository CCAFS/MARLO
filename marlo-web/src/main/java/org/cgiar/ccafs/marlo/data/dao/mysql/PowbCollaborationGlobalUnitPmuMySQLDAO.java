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

import org.cgiar.ccafs.marlo.data.dao.PowbCollaborationGlobalUnitPmuDAO;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationGlobalUnitPmu;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PowbCollaborationGlobalUnitPmuMySQLDAO extends AbstractMarloDAO<PowbCollaborationGlobalUnitPmu, Long> implements PowbCollaborationGlobalUnitPmuDAO {


  @Inject
  public PowbCollaborationGlobalUnitPmuMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbCollaborationGlobalUnitPmu(long powbCollaborationGlobalUnitPmuId) {
    PowbCollaborationGlobalUnitPmu powbCollaborationGlobalUnitPmu = this.find(powbCollaborationGlobalUnitPmuId);
    powbCollaborationGlobalUnitPmu.setActive(false);
    this.update(powbCollaborationGlobalUnitPmu);
  }

  @Override
  public boolean existPowbCollaborationGlobalUnitPmu(long powbCollaborationGlobalUnitPmuID) {
    PowbCollaborationGlobalUnitPmu powbCollaborationGlobalUnitPmu = this.find(powbCollaborationGlobalUnitPmuID);
    if (powbCollaborationGlobalUnitPmu == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbCollaborationGlobalUnitPmu find(long id) {
    return super.find(PowbCollaborationGlobalUnitPmu.class, id);

  }

  @Override
  public List<PowbCollaborationGlobalUnitPmu> findAll() {
    String query = "from " + PowbCollaborationGlobalUnitPmu.class.getName() + " where is_active=1";
    List<PowbCollaborationGlobalUnitPmu> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbCollaborationGlobalUnitPmu save(PowbCollaborationGlobalUnitPmu powbCollaborationGlobalUnitPmu) {
    if (powbCollaborationGlobalUnitPmu.getId() == null) {
      super.saveEntity(powbCollaborationGlobalUnitPmu);
    } else {
      powbCollaborationGlobalUnitPmu = super.update(powbCollaborationGlobalUnitPmu);
    }


    return powbCollaborationGlobalUnitPmu;
  }


}