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

import org.cgiar.ccafs.marlo.data.dao.IntellectualPropertyRightsInstitutionDAO;
import org.cgiar.ccafs.marlo.data.model.IntellectualPropertyRightsInstitution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class IntellectualPropertyRightsInstitutionMySQLDAO extends AbstractMarloDAO<IntellectualPropertyRightsInstitution, Long> implements IntellectualPropertyRightsInstitutionDAO {


  @Inject
  public IntellectualPropertyRightsInstitutionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteIntellectualPropertyRightsInstitution(long intellectualPropertyRightsInstitutionId) {
    IntellectualPropertyRightsInstitution intellectualPropertyRightsInstitution = this.find(intellectualPropertyRightsInstitutionId);
    intellectualPropertyRightsInstitution.setActive(false);
    this.update(intellectualPropertyRightsInstitution);
  }

  @Override
  public boolean existIntellectualPropertyRightsInstitution(long intellectualPropertyRightsInstitutionID) {
    IntellectualPropertyRightsInstitution intellectualPropertyRightsInstitution = this.find(intellectualPropertyRightsInstitutionID);
    if (intellectualPropertyRightsInstitution == null) {
      return false;
    }
    return true;

  }

  @Override
  public IntellectualPropertyRightsInstitution find(long id) {
    return super.find(IntellectualPropertyRightsInstitution.class, id);

  }

  @Override
  public List<IntellectualPropertyRightsInstitution> findAll() {
    String query = "from " + IntellectualPropertyRightsInstitution.class.getName() + " where is_active=1";
    List<IntellectualPropertyRightsInstitution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public IntellectualPropertyRightsInstitution save(IntellectualPropertyRightsInstitution intellectualPropertyRightsInstitution) {
    if (intellectualPropertyRightsInstitution.getId() == null) {
      super.saveEntity(intellectualPropertyRightsInstitution);
    } else {
      intellectualPropertyRightsInstitution = super.update(intellectualPropertyRightsInstitution);
    }


    return intellectualPropertyRightsInstitution;
  }


}