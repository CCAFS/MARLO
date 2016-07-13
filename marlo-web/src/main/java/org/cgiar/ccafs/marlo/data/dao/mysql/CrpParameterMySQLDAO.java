/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning & 
 * Outcomes Platform (MARLO). * MARLO is free software: you can redistribute it and/or modify
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

import org.cgiar.ccafs.marlo.data.dao.CrpParameterDAO;
import org.cgiar.ccafs.marlo.data.model.CrpParameter;

import java.util.List;

import com.google.inject.Inject;

public class CrpParameterMySQLDAO implements CrpParameterDAO {

  private StandardDAO dao;

  @Inject
  public CrpParameterMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpParameter(long crpParameterId) {
    CrpParameter crpParameter = this.find(crpParameterId);
    crpParameter.setActive(false);
    return this.save(crpParameter) > 0;
  }

  @Override
  public boolean existCrpParameter(long crpParameterID) {
    CrpParameter crpParameter = this.find(crpParameterID);
    if (crpParameter == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpParameter find(long id) {
    return dao.find(CrpParameter.class, id);

  }

  @Override
  public List<CrpParameter> findAll() {
    String query = "from " + CrpParameter.class.getName() + " where is_active=1";
    List<CrpParameter> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CrpParameter crpParameter) {
    if (crpParameter.getId() == null) {
      dao.save(crpParameter);
    } else {
      dao.update(crpParameter);
    }
    return crpParameter.getId();
  }


}