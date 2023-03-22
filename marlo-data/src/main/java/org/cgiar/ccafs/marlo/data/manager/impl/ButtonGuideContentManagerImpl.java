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


import org.cgiar.ccafs.marlo.data.dao.ButtonGuideContentDAO;
import org.cgiar.ccafs.marlo.data.manager.ButtonGuideContentManager;
import org.cgiar.ccafs.marlo.data.model.ButtonGuideContent;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ButtonGuideContentManagerImpl implements ButtonGuideContentManager {


  private ButtonGuideContentDAO buttonGuideContentDAO;
  // Managers


  @Inject
  public ButtonGuideContentManagerImpl(ButtonGuideContentDAO buttonGuideContentDAO) {
    this.buttonGuideContentDAO = buttonGuideContentDAO;


  }

  @Override
  public void deleteButtonGuideContent(long buttonGuideContentId) {

    buttonGuideContentDAO.deleteButtonGuideContent(buttonGuideContentId);
  }

  @Override
  public boolean existButtonGuideContent(long buttonGuideContentID) {

    return buttonGuideContentDAO.existButtonGuideContent(buttonGuideContentID);
  }

  @Override
  public List<ButtonGuideContent> findAll() {

    return buttonGuideContentDAO.findAll();

  }

  @Override
  public ButtonGuideContent getButtonGuideContentById(long buttonGuideContentID) {

    return buttonGuideContentDAO.find(buttonGuideContentID);
  }

  @Override
  public ButtonGuideContent getButtonGuideContentByIdentifier(String identifier) {

    return buttonGuideContentDAO.getButtonGuideContentByIdentifier(identifier);
  }

  @Override
  public ButtonGuideContent saveButtonGuideContent(ButtonGuideContent buttonGuideContent) {

    return buttonGuideContentDAO.save(buttonGuideContent);
  }


}
