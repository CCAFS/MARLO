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


package org.cgiar.ccafs.marlo.utils;

import org.cgiar.ccafs.marlo.data.model.User;

import java.util.HashMap;

import com.pusher.rest.Pusher;
import com.pusher.rest.data.PresenceUser;
import com.pusher.rest.data.Result;
import com.pusher.rest.data.Result.Status;

public class SendPusher {

  private String apiKey;
  private String appId;
  private String apiSecret;
  private APConfig config;

  public SendPusher(APConfig config) {
    this.config = config;
    apiKey = config.getPushApiKey();
    appId = config.getPushAppId();
    apiSecret = config.getPushKeySecret();
  }

  public String autenticate(String socketID, String channel, User user, String idSession, String color) {
    Pusher pusher = new Pusher(appId, apiKey, apiSecret);
    HashMap<String, Object> userInfo = new HashMap<>();

    userInfo.put("name", user.getComposedCompleteName());

    userInfo.put("color", color);

    PresenceUser prenceUser = new PresenceUser(idSession, userInfo);
    return pusher.authenticate(socketID, channel, prenceUser);

  }


  public boolean sendPush(String channel, String event, HashMap<String, String> message) {
    Pusher pusher = new Pusher(appId, apiKey, apiSecret);
    pusher.setEncrypted(true);
    Result result = pusher.trigger(channel, event, message);
    return result.getStatus().compareTo(Status.SUCCESS) == 0;

  }

}
