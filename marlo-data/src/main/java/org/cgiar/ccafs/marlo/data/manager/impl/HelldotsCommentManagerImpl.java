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

import org.cgiar.ccafs.marlo.data.dao.HelldotsCommentDAO;
import org.cgiar.ccafs.marlo.data.manager.HelldotsCommentManager;
import org.cgiar.ccafs.marlo.data.model.HelldotsComment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

@Named
public class HelldotsCommentManagerImpl implements HelldotsCommentManager {

  private HelldotsCommentDAO helldotsCommentDAO;

  @Inject
  public HelldotsCommentManagerImpl(HelldotsCommentDAO helldotsCommentDAO) {
    this.helldotsCommentDAO = helldotsCommentDAO;
  }

  @Override
  public HelldotsComment find(long id) {
    return helldotsCommentDAO.find(id);
  }

  @Override
  public List<HelldotsComment> findAllActive() {
    return helldotsCommentDAO.findAllActive();
  }

  @Override
  public HelldotsComment findByCommentId(String commentId) {
    return helldotsCommentDAO.findByCommentId(commentId);
  }

  @Override
  public List<HelldotsComment> findByPage(String page) {
    return helldotsCommentDAO.findByPage(page);
  }

  @Override
  @Transactional
  public HelldotsComment save(HelldotsComment helldotsComment) {
    return helldotsCommentDAO.save(helldotsComment);
  }
}
