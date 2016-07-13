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

import org.cgiar.ccafs.marlo.data.dao.CrpsSiteIntegrationDAO;
import org.cgiar.ccafs.marlo.data.model.CrpsSiteIntegration;

import java.util.List;

import com.google.inject.Inject;

public class CrpsSiteIntegrationMySQLDAO implements CrpsSiteIntegrationDAO {

  private StandardDAO dao;

  @Inject
  public CrpsSiteIntegrationMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpsSiteIntegration(long crpsSiteIntegrationId) {
    CrpsSiteIntegration crpsSiteIntegration = this.find(crpsSiteIntegrationId);
    crpsSiteIntegration.setActive(false);
    return this.save(crpsSiteIntegration) > 0;
  }

  @Override
  public boolean existCrpsSiteIntegration(long crpsSiteIntegrationID) {
    CrpsSiteIntegration crpsSiteIntegration = this.find(crpsSiteIntegrationID);
    if (crpsSiteIntegration == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpsSiteIntegration find(long id) {
    return dao.find(CrpsSiteIntegration.class, id);

  }

  @Override
  public List<CrpsSiteIntegration> findAll() {
    String query = "from " + CrpsSiteIntegration.class.getName() + " where is_active=1";
    List<CrpsSiteIntegration> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CrpsSiteIntegration crpsSiteIntegration) {
    if (crpsSiteIntegration.getId() == null) {
      dao.save(crpsSiteIntegration);
    } else {
      dao.update(crpsSiteIntegration);
    }
    return crpsSiteIntegration.getId();
  }


}