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

import org.cgiar.ccafs.marlo.data.dao.CrpMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class CrpMilestoneMySQLDAO extends AbstractMarloDAO<CrpMilestone, Long> implements CrpMilestoneDAO {


  @Inject
  public CrpMilestoneMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpMilestone(long crpMilestoneId) {
    CrpMilestone crpMilestone = this.find(crpMilestoneId);
    crpMilestone.setActive(false);
    this.save(crpMilestone);
  }

  @Override
  public boolean existCrpMilestone(long crpMilestoneID) {
    CrpMilestone crpMilestone = this.find(crpMilestoneID);
    if (crpMilestone == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpMilestone find(long id) {
    return super.find(CrpMilestone.class, id);

  }

  @Override
  public List<CrpMilestone> findAll() {
    String query = "from " + CrpMilestone.class.getName() + " where is_active=1";
    List<CrpMilestone> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CrpMilestone getCrpMilestone(String composedId, CrpProgramOutcome crpProgramOutcome) {
    String query = "select distinct pp from CrpMilestone  pp "
      + "where composeID=:composeID and crpProgramOutcome.id=:crpProgramOutcomeID and active=true";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("composeID", composedId);
    createQuery.setParameter("crpProgramOutcomeID", crpProgramOutcome.getId());
    Object findSingleResult = super.findSingleResult(CrpMilestone.class, createQuery);
    CrpMilestone crpMilestone = (CrpMilestone) findSingleResult;
    if (crpMilestone != null) {
      crpMilestone = super.refreshEntity(crpMilestone);
    }

    // projectPartner.getProjectPartnerLocations().size();

    return crpMilestone;

  }

  @Override
  public CrpMilestone save(CrpMilestone crpMilestone) {
    if (crpMilestone.getId() == null) {
      super.saveEntity(crpMilestone);
    } else {
      crpMilestone = super.update(crpMilestone);
    }
    return crpMilestone;
  }

}