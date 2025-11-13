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
import org.cgiar.ccafs.marlo.data.manager.AiReportConfigurationManager;
import org.cgiar.ccafs.marlo.data.manager.UserIdeaManager;
import org.cgiar.ccafs.marlo.data.model.AiReportConfiguration;
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
  private AiReportConfigurationManager aiReportConfigurationManager;

  // Front-end
  private List<UserIdea> userIdeas;
  private UserIdea userIdea;
  private String userEmail;
  private String username;
  private List<AiReportConfiguration> reportConfigurations;

  @Inject
  public AiAction(APConfig config, UserIdeaManager userIdeaManager,
      AiReportConfigurationManager aiReportConfigurationManager) {
    super(config);
    this.userIdeaManager = userIdeaManager;
    this.aiReportConfigurationManager = aiReportConfigurationManager;
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

      if(this.getCurrentUser() != null){
        if (this.getCurrentUser() != null && this.getCurrentUser().getFirstName() != null && 
            this.getCurrentUser().getLastName() != null) {
          String fullName =
              this.getCurrentUser().getFirstName() + " " + this.getCurrentUser().getLastName();
          username = fullName;
        } else {
          username = "";
        }

        if (this.getCurrentUser() != null && this.getCurrentUser().getEmail() != null) {
          this.userEmail = this.getCurrentUser().getEmail();
        } else {
          this.userEmail = null;
        }
      }
    } catch (Exception e) {
      LOG.error("Error loading UserIdeas", e);
      userIdeas = new ArrayList<>();
      userIdea = new UserIdea();
    }

    try {
      reportConfigurations = aiReportConfigurationManager.findAll();
    } catch (Exception e) {
      LOG.error("Error loading AI report configurations", e);
      reportConfigurations = new ArrayList<>();
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
  public String getUserEmail() {
    return userEmail;
  }
  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }

  public List<AiReportConfiguration> getReportConfigurations() {
    return reportConfigurations;
  }

  public void setReportConfigurations(List<AiReportConfiguration> reportConfigurations) {
    this.reportConfigurations = reportConfigurations;
  }
}
