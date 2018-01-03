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

import org.cgiar.ccafs.marlo.data.dao.InstitutionTypeDAO;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class InstitutionTypeMySQLDAO extends AbstractMarloDAO<InstitutionType, Long> implements InstitutionTypeDAO {


  @Inject
  public InstitutionTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteInstitutionType(long institutionTypeId) {
    InstitutionType institutionType = this.find(institutionTypeId);

    super.delete(institutionType);
  }

  @Override
  public boolean existInstitutionType(long institutionTypeID) {
    InstitutionType institutionType = this.find(institutionTypeID);
    if (institutionType == null) {
      return false;
    }
    return true;

  }

  @Override
  public InstitutionType find(long id) {
    return super.find(InstitutionType.class, id);

  }

  @Override
  public List<InstitutionType> findAll() {
    String query = "from " + InstitutionType.class.getName() + "";
    List<InstitutionType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public InstitutionType save(InstitutionType institutionType) {
    if (institutionType.getId() == null) {
      super.saveEntity(institutionType);
    } else {
      institutionType = super.update(institutionType);
    }


    return institutionType;
  }


}