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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ToolFunctionCategoryDAO;
import org.cgiar.ccafs.marlo.data.manager.ToolFunctionCategoryManager;
import org.cgiar.ccafs.marlo.data.model.ToolFunctionCategory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CCAFS
 */
@Named
public class ToolFunctionCategoryManagerImpl implements ToolFunctionCategoryManager {


  private ToolFunctionCategoryDAO toolFunctionCategoryDAO;
  // Managers


  @Inject
  public ToolFunctionCategoryManagerImpl(ToolFunctionCategoryDAO toolFunctionCategoryDAO) {
    this.toolFunctionCategoryDAO = toolFunctionCategoryDAO;


  }

  @Override
  @Transactional
  public void deleteToolFunctionCategory(long toolFunctionCategoryId) {

    toolFunctionCategoryDAO.deleteToolFunctionCategory(toolFunctionCategoryId);
  }

  @Override
  public boolean existToolFunctionCategory(long toolFunctionCategoryID) {

    return toolFunctionCategoryDAO.existToolFunctionCategory(toolFunctionCategoryID);
  }

  @Override
  public List<ToolFunctionCategory> findAll() {

    return toolFunctionCategoryDAO.findAll();

  }

  @Override
  public ToolFunctionCategory getToolFunctionCategoryById(long toolFunctionCategoryID) {

    return toolFunctionCategoryDAO.find(toolFunctionCategoryID);
  }

  @Override
  public ToolFunctionCategory saveToolFunctionCategory(ToolFunctionCategory toolFunctionCategory) {

    return toolFunctionCategoryDAO.save(toolFunctionCategory);
  }


}
