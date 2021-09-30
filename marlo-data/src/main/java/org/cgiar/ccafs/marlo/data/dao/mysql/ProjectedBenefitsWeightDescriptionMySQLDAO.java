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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ProjectedBenefitsWeightDescriptionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectedBenefitsWeightDescription;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectedBenefitsWeightDescriptionMySQLDAO
  extends AbstractMarloDAO<ProjectedBenefitsWeightDescription, Long> implements ProjectedBenefitsWeightDescriptionDAO {

  @Inject
  public ProjectedBenefitsWeightDescriptionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public ProjectedBenefitsWeightDescription find(long id) {
    return super.find(ProjectedBenefitsWeightDescription.class, id);
  }

  @Override
  public List<ProjectedBenefitsWeightDescription> findAll() {
    String query = "from " + ProjectedBenefitsWeightDescription.class.getName();
    List<ProjectedBenefitsWeightDescription> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }


}
