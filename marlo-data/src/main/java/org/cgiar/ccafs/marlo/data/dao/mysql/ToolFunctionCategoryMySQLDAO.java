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

import org.cgiar.ccafs.marlo.data.dao.ToolFunctionCategoryDAO;
import org.cgiar.ccafs.marlo.data.model.ToolFunctionCategory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ToolFunctionCategoryMySQLDAO extends AbstractMarloDAO<ToolFunctionCategory, Long> implements ToolFunctionCategoryDAO {


  @Inject
  public ToolFunctionCategoryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteToolFunctionCategory(long toolFunctionCategoryId) {
    ToolFunctionCategory toolFunctionCategory = this.find(toolFunctionCategoryId);
    toolFunctionCategory.setActive(false);
    this.update(toolFunctionCategory);
  }

  @Override
  public boolean existToolFunctionCategory(long toolFunctionCategoryID) {
    ToolFunctionCategory toolFunctionCategory = this.find(toolFunctionCategoryID);
    if (toolFunctionCategory == null) {
      return false;
    }
    return true;

  }

  @Override
  public ToolFunctionCategory find(long id) {
    return super.find(ToolFunctionCategory.class, id);

  }

  @Override
  public List<ToolFunctionCategory> findAll() {
    String query = "from " + ToolFunctionCategory.class.getName() + " where is_active=1";
    List<ToolFunctionCategory> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ToolFunctionCategory save(ToolFunctionCategory toolFunctionCategory) {
    if (toolFunctionCategory.getId() == null) {
      super.saveEntity(toolFunctionCategory);
    } else {
      toolFunctionCategory = super.update(toolFunctionCategory);
    }


    return toolFunctionCategory;
  }


}