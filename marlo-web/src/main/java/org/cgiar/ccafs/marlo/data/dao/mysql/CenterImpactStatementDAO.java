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

import org.cgiar.ccafs.marlo.data.dao.ICenterImpactStatementDAO;
import org.cgiar.ccafs.marlo.data.model.CenterImpactStatement;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CenterImpactStatementDAO extends AbstractMarloDAO<CenterImpactStatement, Long>
  implements ICenterImpactStatementDAO {


  @Inject
  public CenterImpactStatementDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteResearchImpactStatement(long researchImpactStatementId) {
    CenterImpactStatement researchImpactStatement = this.find(researchImpactStatementId);
    researchImpactStatement.setActive(false);
    this.save(researchImpactStatement);
  }

  @Override
  public boolean existResearchImpactStatement(long researchImpactStatementID) {
    CenterImpactStatement researchImpactStatement = this.find(researchImpactStatementID);
    if (researchImpactStatement == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterImpactStatement find(long id) {
    return super.find(CenterImpactStatement.class, id);

  }

  @Override
  public List<CenterImpactStatement> findAll() {
    String query = "from " + CenterImpactStatement.class.getName();
    List<CenterImpactStatement> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterImpactStatement> getResearchImpactStatementsByUserId(long userId) {
    String query = "from " + CenterImpactStatement.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterImpactStatement save(CenterImpactStatement researchImpactStatement) {
    if (researchImpactStatement.getId() == null) {
      super.saveEntity(researchImpactStatement);
    } else {
      researchImpactStatement = super.update(researchImpactStatement);
    }
    return researchImpactStatement;
  }

  @Override
  public CenterImpactStatement save(CenterImpactStatement researchImpactStatement, String actionName, List<String> relationsName) {
    if (researchImpactStatement.getId() == null) {
      super.saveEntity(researchImpactStatement, actionName, relationsName);
    } else {
      researchImpactStatement = super.update(researchImpactStatement, actionName, relationsName);
    }
    return researchImpactStatement;
  }


}