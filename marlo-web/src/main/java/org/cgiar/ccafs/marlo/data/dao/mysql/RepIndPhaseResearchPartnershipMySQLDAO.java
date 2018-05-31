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

import org.cgiar.ccafs.marlo.data.dao.RepIndPhaseResearchPartnershipDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndPhaseResearchPartnership;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndPhaseResearchPartnershipMySQLDAO extends AbstractMarloDAO<RepIndPhaseResearchPartnership, Long>
  implements RepIndPhaseResearchPartnershipDAO {


  @Inject
  public RepIndPhaseResearchPartnershipMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndPhaseResearchPartnership(long repIndPhaseResearchPartnershipId) {
    RepIndPhaseResearchPartnership repIndPhaseResearchPartnership = this.find(repIndPhaseResearchPartnershipId);
    this.delete(repIndPhaseResearchPartnership);
  }

  @Override
  public boolean existRepIndPhaseResearchPartnership(long repIndPhaseResearchPartnershipID) {
    RepIndPhaseResearchPartnership repIndPhaseResearchPartnership = this.find(repIndPhaseResearchPartnershipID);
    if (repIndPhaseResearchPartnership == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndPhaseResearchPartnership find(long id) {
    return super.find(RepIndPhaseResearchPartnership.class, id);

  }

  @Override
  public List<RepIndPhaseResearchPartnership> findAll() {
    String query = "from " + RepIndPhaseResearchPartnership.class.getName();
    List<RepIndPhaseResearchPartnership> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndPhaseResearchPartnership save(RepIndPhaseResearchPartnership repIndPhaseResearchPartnership) {
    if (repIndPhaseResearchPartnership.getId() == null) {
      super.saveEntity(repIndPhaseResearchPartnership);
    } else {
      repIndPhaseResearchPartnership = super.update(repIndPhaseResearchPartnership);
    }


    return repIndPhaseResearchPartnership;
  }


}