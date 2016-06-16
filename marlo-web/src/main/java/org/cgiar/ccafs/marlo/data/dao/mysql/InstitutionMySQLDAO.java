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

import org.cgiar.ccafs.marlo.data.dao.InstitutionDAO;
import org.cgiar.ccafs.marlo.data.model.Institution;

import java.util.List;

import com.google.inject.Inject;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class InstitutionMySQLDAO implements InstitutionDAO {

  private StandardDAO dao;

  @Inject
  public InstitutionMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteInstitution(long institutionId) {
    Institution institution = this.find(institutionId);
    return dao.delete(institution);
  }

  @Override
  public boolean existInstitution(long institutionId) {
    Institution institution = this.find(institutionId);
    if (institution == null) {
      return false;
    }
    return true;
  }

  @Override
  public Institution find(long id) {
    return dao.find(Institution.class, id);
  }

  @Override
  public List<Institution> findAll() {
    String query = "from " + Institution.class.getName();
    List<Institution> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public long save(Institution institution) {
    if (institution.getId() == null) {
      dao.save(institution);
    } else {
      dao.update(institution);
    }
    return institution.getId();
  }

}
