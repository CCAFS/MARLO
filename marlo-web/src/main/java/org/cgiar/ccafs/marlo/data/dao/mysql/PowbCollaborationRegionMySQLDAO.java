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

import org.cgiar.ccafs.marlo.data.dao.PowbCollaborationRegionDAO;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationRegion;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PowbCollaborationRegionMySQLDAO extends AbstractMarloDAO<PowbCollaborationRegion, Long> implements PowbCollaborationRegionDAO {


  @Inject
  public PowbCollaborationRegionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbCollaborationRegion(long powbCollaborationRegionId) {
    PowbCollaborationRegion powbCollaborationRegion = this.find(powbCollaborationRegionId);
    powbCollaborationRegion.setActive(false);
    this.update(powbCollaborationRegion);
  }

  @Override
  public boolean existPowbCollaborationRegion(long powbCollaborationRegionID) {
    PowbCollaborationRegion powbCollaborationRegion = this.find(powbCollaborationRegionID);
    if (powbCollaborationRegion == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbCollaborationRegion find(long id) {
    return super.find(PowbCollaborationRegion.class, id);

  }

  @Override
  public List<PowbCollaborationRegion> findAll() {
    String query = "from " + PowbCollaborationRegion.class.getName() + " where is_active=1";
    List<PowbCollaborationRegion> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbCollaborationRegion save(PowbCollaborationRegion powbCollaborationRegion) {
    if (powbCollaborationRegion.getId() == null) {
      super.saveEntity(powbCollaborationRegion);
    } else {
      powbCollaborationRegion = super.update(powbCollaborationRegion);
    }


    return powbCollaborationRegion;
  }


}