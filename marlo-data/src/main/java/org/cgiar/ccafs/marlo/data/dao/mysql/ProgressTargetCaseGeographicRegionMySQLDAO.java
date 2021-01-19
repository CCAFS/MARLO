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

import org.cgiar.ccafs.marlo.data.dao.ProgressTargetCaseGeographicRegionDAO;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicRegion;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProgressTargetCaseGeographicRegionMySQLDAO
  extends AbstractMarloDAO<ProgressTargetCaseGeographicRegion, Long> implements ProgressTargetCaseGeographicRegionDAO {


  @Inject
  public ProgressTargetCaseGeographicRegionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProgressTargetCaseGeographicRegion(long progressTargetCaseGeographicRegionId) {
    ProgressTargetCaseGeographicRegion progressTargetCaseGeographicRegion =
      this.find(progressTargetCaseGeographicRegionId);
    this.update(progressTargetCaseGeographicRegion);
  }

  @Override
  public boolean existProgressTargetCaseGeographicRegion(long progressTargetCaseGeographicRegionID) {
    ProgressTargetCaseGeographicRegion progressTargetCaseGeographicRegion =
      this.find(progressTargetCaseGeographicRegionID);
    if (progressTargetCaseGeographicRegion == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProgressTargetCaseGeographicRegion find(long id) {
    return super.find(ProgressTargetCaseGeographicRegion.class, id);

  }

  @Override
  public List<ProgressTargetCaseGeographicRegion> findAll() {
    String query = "from " + ProgressTargetCaseGeographicRegion.class.getName();
    List<ProgressTargetCaseGeographicRegion> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProgressTargetCaseGeographicRegion> findGeographicRegionByTargetCase(long targetCaseID) {
    String query =
      "from " + ProgressTargetCaseGeographicRegion.class.getName() + " where progress_target_case_id = " + targetCaseID;
    List<ProgressTargetCaseGeographicRegion> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();
  }

  @Override
  public ProgressTargetCaseGeographicRegion
    save(ProgressTargetCaseGeographicRegion progressTargetCaseGeographicRegion) {
    if (progressTargetCaseGeographicRegion.getId() == null) {
      super.saveEntity(progressTargetCaseGeographicRegion);
    } else {
      progressTargetCaseGeographicRegion = super.update(progressTargetCaseGeographicRegion);
    }


    return progressTargetCaseGeographicRegion;
  }


}