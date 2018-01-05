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

/**
 * 
 */
package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ICenterLeaderDAO;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;


/**
 * Modified by @author nmatovu last on Oct 10, 2016
 */
@Named
public class CenterLeaderDAO extends AbstractMarloDAO<CenterLeader, Long> implements ICenterLeaderDAO {


  @Inject
  public CenterLeaderDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }


  @Override
  public void deleteResearchLeader(long researchLeaderId) {
    CenterLeader researchLeader = this.find(researchLeaderId);
    researchLeader.setActive(false);
    this.save(researchLeader);
  }


  @Override
  public boolean existResearchLeader(long researchLeaderId) {
    CenterLeader researchLeader = this.find(researchLeaderId);
    if (researchLeader == null) {
      return false;
    }
    return true;
  }


  @Override
  public CenterLeader find(long researchLeaderId) {
    return super.find(CenterLeader.class, researchLeaderId);
  }

  @Override
  public List<CenterLeader> findAll() {
    String query = "from " + CenterLeader.class.getName() + " where is_active=1";
    List<CenterLeader> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public CenterLeader save(CenterLeader researchLeader) {
    if (researchLeader.getId() == null) {
      super.saveEntity(researchLeader);
    } else {
      researchLeader = super.update(researchLeader);
    }
    return researchLeader;
  }

}
