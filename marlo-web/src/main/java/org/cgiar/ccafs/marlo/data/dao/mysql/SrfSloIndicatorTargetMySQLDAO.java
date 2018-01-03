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

import org.cgiar.ccafs.marlo.data.dao.SrfSloIndicatorTargetDAO;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class SrfSloIndicatorTargetMySQLDAO extends AbstractMarloDAO<SrfSloIndicatorTarget, Long> implements SrfSloIndicatorTargetDAO {


  @Inject
  public SrfSloIndicatorTargetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteSrfSloIndicatorTarget(long srfSloIndicatorTargetId) {
    SrfSloIndicatorTarget srfSloIndicatorTarget = this.find(srfSloIndicatorTargetId);
    srfSloIndicatorTarget.setActive(false);
    this.save(srfSloIndicatorTarget);
  }

  @Override
  public boolean existSrfSloIndicatorTarget(long srfSloIndicatorTargetID) {
    SrfSloIndicatorTarget srfSloIndicatorTarget = this.find(srfSloIndicatorTargetID);
    if (srfSloIndicatorTarget == null) {
      return false;
    }
    return true;

  }

  @Override
  public SrfSloIndicatorTarget find(long id) {
    return super.find(SrfSloIndicatorTarget.class, id);

  }

  @Override
  public List<SrfSloIndicatorTarget> findAll() {
    String query = "from " + SrfSloIndicatorTarget.class.getName() + " where is_active=1";
    List<SrfSloIndicatorTarget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public SrfSloIndicatorTarget save(SrfSloIndicatorTarget srfSloIndicatorTarget) {
    if (srfSloIndicatorTarget.getId() == null) {
      super.saveEntity(srfSloIndicatorTarget);
    } else {
      srfSloIndicatorTarget = super.update(srfSloIndicatorTarget);
    }
    return srfSloIndicatorTarget;
  }


}