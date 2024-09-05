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

import org.cgiar.ccafs.marlo.data.dao.RelatedAllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.model.RelatedAllianceLever;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RelatedAllianceLeverMySQLDAO extends AbstractMarloDAO<RelatedAllianceLever, Long>
  implements RelatedAllianceLeverDAO {


  @Inject
  public RelatedAllianceLeverMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRelatedAllianceLever(long relatedAllianceLeverId) {
    RelatedAllianceLever relatedAllianceLever = this.find(relatedAllianceLeverId);
    relatedAllianceLever.setActive(false);
    this.update(relatedAllianceLever);
  }

  @Override
  public boolean existRelatedAllianceLever(long relatedAllianceLeverID) {
    RelatedAllianceLever relatedAllianceLever = this.find(relatedAllianceLeverID);
    if (relatedAllianceLever == null) {
      return false;
    }
    return true;

  }

  @Override
  public RelatedAllianceLever find(long id) {
    return super.find(RelatedAllianceLever.class, id);

  }

  @Override
  public List<RelatedAllianceLever> findAll() {
    String query = "from " + RelatedAllianceLever.class.getName() + " where is_active=1";
    List<RelatedAllianceLever> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public List<RelatedAllianceLever> findAllByPhase(long phaseId) {
    String query = "from " + RelatedAllianceLever.class.getName() + " where is_active=1 and id_phase=" + phaseId;
    List<RelatedAllianceLever> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RelatedAllianceLever save(RelatedAllianceLever relatedAllianceLever) {
    if (relatedAllianceLever.getId() == null) {
      super.saveEntity(relatedAllianceLever);
    } else {
      relatedAllianceLever = super.update(relatedAllianceLever);
    }


    return relatedAllianceLever;
  }


}