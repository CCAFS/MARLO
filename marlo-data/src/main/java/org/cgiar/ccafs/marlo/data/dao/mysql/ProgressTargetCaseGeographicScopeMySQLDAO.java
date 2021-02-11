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

import org.cgiar.ccafs.marlo.data.dao.ProgressTargetCaseGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicScope;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProgressTargetCaseGeographicScopeMySQLDAO extends AbstractMarloDAO<ProgressTargetCaseGeographicScope, Long>
  implements ProgressTargetCaseGeographicScopeDAO {


  @Inject
  public ProgressTargetCaseGeographicScopeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProgressTargetCaseGeographicScope(long progressTargetCaseGeographicScopeId) {
    ProgressTargetCaseGeographicScope progressTargetCaseGeographicScope =
      this.find(progressTargetCaseGeographicScopeId);
    this.delete(progressTargetCaseGeographicScope);
  }

  @Override
  public boolean existProgressTargetCaseGeographicScope(long progressTargetCaseGeographicScopeID) {
    ProgressTargetCaseGeographicScope progressTargetCaseGeographicScope =
      this.find(progressTargetCaseGeographicScopeID);
    if (progressTargetCaseGeographicScope == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProgressTargetCaseGeographicScope find(long id) {
    return super.find(ProgressTargetCaseGeographicScope.class, id);

  }

  @Override
  public List<ProgressTargetCaseGeographicScope> findAll() {
    String query = "from " + ProgressTargetCaseGeographicScope.class.getName();
    List<ProgressTargetCaseGeographicScope> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProgressTargetCaseGeographicScope> findGeographicScopeByTargetCase(long targetCaseID) {
    String query =
      "from " + ProgressTargetCaseGeographicScope.class.getName() + " where progress_target_case_id = " + targetCaseID;
    List<ProgressTargetCaseGeographicScope> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();
  }

  @Override
  public ProgressTargetCaseGeographicScope save(ProgressTargetCaseGeographicScope progressTargetCaseGeographicScope) {
    if (progressTargetCaseGeographicScope.getId() == null) {
      super.saveEntity(progressTargetCaseGeographicScope);
    } else {
      progressTargetCaseGeographicScope = super.update(progressTargetCaseGeographicScope);
    }


    return progressTargetCaseGeographicScope;
  }


}