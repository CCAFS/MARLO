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

package org.cgiar.ccafs.marlo.action.ai;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.UserIdeaManager;
import org.cgiar.ccafs.marlo.data.model.UserIdea;
import org.cgiar.ccafs.marlo.utils.APConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AiAction extends BaseAction {

  private static final long serialVersionUID = 1329042468240291639L;

  private static final Logger LOG = LoggerFactory.getLogger(AiAction.class);

  // Managers
  private UserIdeaManager userIdeaManager;

  // Front-end
  private List<UserIdea> userIdeas;
  private UserIdea userIdea;

  @Inject
  public AiAction(APConfig config, UserIdeaManager userIdeaManager) {
    super(config);
    this.userIdeaManager = userIdeaManager;
  }

  @Override
  public void prepare() {

    try {
      // userIdeas = userIdeaManager.findAll();

      if (userIdeas != null && !userIdeas.isEmpty()) {
        userIdea = userIdeas.get(0);
      } else {
        userIdea = new UserIdea();
      }
    } catch (Exception e) {
      LOG.error("Error loading UserIdeas", e);
      userIdeas = new ArrayList<>();
      userIdea = new UserIdea();
    }

    if (this.isHttpPost()) {
      if (userIdeas != null) {
        userIdeas.clear();
      }
    }
  }

  @Override
  public String save() {
    if (userIdea != null) {
      userIdea = userIdeaManager.saveUserIdea(userIdea);
    }

    if (this.getUrl() == null || this.getUrl().isEmpty()) {
      // check if there are missing field
      Collection<String> messages = this.getActionMessages();
      if (!this.getInvalidFields().isEmpty()) {
        this.setActionMessages(null);
        // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
        List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
        for (String key : keys) {
          this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
        }
      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
      }
      return SUCCESS;
    } else {
      // No messages to next page

      this.addActionMessage("");
      this.setActionMessages(null);
      // redirect the url select by user
      return REDIRECT;
    }
  }

  @Override
  public void validate() {
    this.setInvalidFields(new HashMap<>());
    // if is saving call the validator to check for the missing fields
    if (save) {
    }
  }

  public List<UserIdea> getUserIdeas() {
    return userIdeas;
  }

  public void setUserIdeas(List<UserIdea> userIdeas) {
    this.userIdeas = userIdeas;
  }

  public UserIdea getUserIdea() {
    return userIdea;
  }

  public void setUserIdea(UserIdea userIdea) {
    this.userIdea = userIdea;
  }
}