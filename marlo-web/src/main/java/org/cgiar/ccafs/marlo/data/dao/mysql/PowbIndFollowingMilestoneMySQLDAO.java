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

import org.cgiar.ccafs.marlo.data.dao.PowbIndFollowingMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.PowbIndFollowingMilestone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PowbIndFollowingMilestoneMySQLDAO extends AbstractMarloDAO<PowbIndFollowingMilestone, Long>
  implements PowbIndFollowingMilestoneDAO {


  @Inject
  public PowbIndFollowingMilestoneMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbIndFollowingMilestone(long powbIndFollowingMilestoneId) {
    PowbIndFollowingMilestone powbIndFollowingMilestone = this.find(powbIndFollowingMilestoneId);
    this.delete(powbIndFollowingMilestone);
  }

  @Override
  public boolean existPowbIndFollowingMilestone(long powbIndFollowingMilestoneID) {
    PowbIndFollowingMilestone powbIndFollowingMilestone = this.find(powbIndFollowingMilestoneID);
    if (powbIndFollowingMilestone == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbIndFollowingMilestone find(long id) {
    return super.find(PowbIndFollowingMilestone.class, id);

  }

  @Override
  public List<PowbIndFollowingMilestone> findAll() {
    String query = "from " + PowbIndFollowingMilestone.class.getName();
    List<PowbIndFollowingMilestone> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbIndFollowingMilestone save(PowbIndFollowingMilestone powbIndFollowingMilestone) {
    if (powbIndFollowingMilestone.getId() == null) {
      super.saveEntity(powbIndFollowingMilestone);
    } else {
      powbIndFollowingMilestone = super.update(powbIndFollowingMilestone);
    }


    return powbIndFollowingMilestone;
  }


}