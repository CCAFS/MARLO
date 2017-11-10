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


package org.cgiar.ccafs.marlo.action.center.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendPusher;

import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.inject.Inject;

public class PusherAutentication extends BaseAction {


  private static final long serialVersionUID = -5699784342330829319L;


  public String socketID;
  public String channel;
  public HashMap<String, Object> jsonString;


  public SendPusher sendPusher;

  @Inject
  public PusherAutentication(APConfig config, SendPusher sendPusher) {

    super(config);
    this.sendPusher = sendPusher;
  }

  @Override
  public String execute() throws Exception {
    socketID = this.getRequest().getParameter("socket_id");
    channel = this.getRequest().getParameter("channel_name");
    jsonString = new HashMap<>();
    /*
     * HashMap<String, String> message = new HashMap<>();
     * message.put("message", "Holi vamos a reinciar!");
     * message.put("diffTime", "30");
     * sendPusher.sendPush("presence-global", "system-reset", message);
     */

    String jsonReturn = sendPusher.autenticate(socketID, channel, this.getCurrentUser(),
      this.getRequest().getSession().getId(), this.getSession().get("color").toString());
    Gson gson = new GsonBuilder().create();
    jsonString = gson.fromJson(jsonReturn, HashMap.class);
    return SUCCESS;
  }

  public HashMap<String, Object> getJsonString() {
    return jsonString;
  }


}
