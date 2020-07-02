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

import org.cgiar.ccafs.marlo.data.dao.ProjectImpactsCategoriesDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectImpactsCategories;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectImpactsCategoriesMySQLDAO extends AbstractMarloDAO<ProjectImpactsCategories, Long> implements ProjectImpactsCategoriesDAO {


  @Inject
  public ProjectImpactsCategoriesMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectImpactsCategories(long projectImpactsCategoriesId) {
    ProjectImpactsCategories projectImpactsCategories = this.find(projectImpactsCategoriesId);
    projectImpactsCategories.setActive(false);
    this.update(projectImpactsCategories);
  }

  @Override
  public boolean existProjectImpactsCategories(long projectImpactsCategoriesID) {
    ProjectImpactsCategories projectImpactsCategories = this.find(projectImpactsCategoriesID);
    if (projectImpactsCategories == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectImpactsCategories find(long id) {
    return super.find(ProjectImpactsCategories.class, id);

  }

  @Override
  public List<ProjectImpactsCategories> findAll() {
    String query = "from " + ProjectImpactsCategories.class.getName() + " where is_active=1";
    List<ProjectImpactsCategories> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectImpactsCategories save(ProjectImpactsCategories projectImpactsCategories) {
    if (projectImpactsCategories.getId() == null) {
      super.saveEntity(projectImpactsCategories);
    } else {
      projectImpactsCategories = super.update(projectImpactsCategories);
    }


    return projectImpactsCategories;
  }


}