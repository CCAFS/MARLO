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

import org.cgiar.ccafs.marlo.data.dao.CgiarCrossCuttingMarkerDAO;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class CgiarCrossCuttingMarkerMySQLDAO extends AbstractMarloDAO<CgiarCrossCuttingMarker, Long>
  implements CgiarCrossCuttingMarkerDAO {


  @Inject
  public CgiarCrossCuttingMarkerMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCgiarCrossCuttingMarker(long cgiarCrossCuttingMarkerId) {
    CgiarCrossCuttingMarker cgiarCrossCuttingMarker = this.find(cgiarCrossCuttingMarkerId);
    this.delete(cgiarCrossCuttingMarker.getId());
  }

  @Override
  public boolean existCgiarCrossCuttingMarker(long cgiarCrossCuttingMarkerID) {
    CgiarCrossCuttingMarker cgiarCrossCuttingMarker = this.find(cgiarCrossCuttingMarkerID);
    if (cgiarCrossCuttingMarker == null) {
      return false;
    }
    return true;

  }

  @Override
  public CgiarCrossCuttingMarker find(long id) {
    return super.find(CgiarCrossCuttingMarker.class, id);

  }

  @Override
  public List<CgiarCrossCuttingMarker> findAll() {
    String query = "from " + CgiarCrossCuttingMarker.class.getName();
    List<CgiarCrossCuttingMarker> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CgiarCrossCuttingMarker save(CgiarCrossCuttingMarker cgiarCrossCuttingMarker) {
    if (cgiarCrossCuttingMarker.getId() == null) {
      super.saveEntity(cgiarCrossCuttingMarker);
    } else {
      cgiarCrossCuttingMarker = super.update(cgiarCrossCuttingMarker);
    }


    return cgiarCrossCuttingMarker;
  }


}