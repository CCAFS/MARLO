/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.InstitutionTypeDAO;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;

import java.util.List;

import com.google.inject.Inject;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class InstitutionTypeMySQLDAO implements InstitutionTypeDAO {

  private StandardDAO dao;

  @Inject
  public InstitutionTypeMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteInstitutionType(long institutionTypeId) {
    InstitutionType institutionType = this.find(institutionTypeId);
    return dao.delete(institutionType);
  }

  @Override
  public boolean existInstitutionType(long institutionTypeId) {
    InstitutionType institutionType = this.find(institutionTypeId);
    if (institutionType == null) {
      return false;
    }
    return true;
  }

  @Override
  public InstitutionType find(long id) {
    return dao.find(InstitutionType.class, id);
  }

  @Override
  public List<InstitutionType> findAll() {
    String query = "from " + InstitutionType.class.getName();
    List<InstitutionType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public long save(InstitutionType institutionType) {
    if (institutionType.getId() == null) {
      dao.save(institutionType);
    } else {
      dao.update(institutionType);
    }
    return institutionType.getId();
  }

}
