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

import org.cgiar.ccafs.marlo.data.dao.ScalingReadinessDAO;
import org.cgiar.ccafs.marlo.data.model.ScalingReadiness;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ScalingReadinessMySQLDAO extends AbstractMarloDAO<ScalingReadiness, Long> implements ScalingReadinessDAO {


  @Inject
  public ScalingReadinessMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteScalingReadiness(long scalingReadinessId) {
    ScalingReadiness scalingReadiness = this.find(scalingReadinessId);
    scalingReadiness.setActive(false);
    this.update(scalingReadiness);
  }

  @Override
  public boolean existScalingReadiness(long scalingReadinessID) {
    ScalingReadiness scalingReadiness = this.find(scalingReadinessID);
    if (scalingReadiness == null) {
      return false;
    }
    return true;

  }

  @Override
  public ScalingReadiness find(long id) {
    return super.find(ScalingReadiness.class, id);

  }

  @Override
  public List<ScalingReadiness> findAll() {
    String query = "from " + ScalingReadiness.class.getName() + " where is_active=1";
    List<ScalingReadiness> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ScalingReadiness save(ScalingReadiness scalingReadiness) {
    if (scalingReadiness.getId() == null) {
      super.saveEntity(scalingReadiness);
    } else {
      scalingReadiness = super.update(scalingReadiness);
    }


    return scalingReadiness;
  }


}