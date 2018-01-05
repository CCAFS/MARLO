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


package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendPusher;

import java.util.HashMap;

import javax.inject.Inject;

public class MessagesAction extends BaseAction {

  private static final long serialVersionUID = 6893229051875972168L;
  private String message;


  private String diffTime;

  @Inject
  public MessagesAction(APConfig config) {
    super(config);

  }

  @Override
  public String execute() throws Exception {
    HashMap<String, String> message = new HashMap<>();
    message.put("message", this.message);
    message.put("diffTime", diffTime);

    SendPusher sendPusher = new SendPusher(config);
    sendPusher.sendPush("presence-global", "client-system-reset", message);
    return SUCCESS;
  }

  public String getDiffTime() {
    return diffTime;
  }


  public String getMessage() {
    return message;
  }

  public void setDiffTime(String diffTime) {
    this.diffTime = diffTime;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
