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

import java.util.Collections;

import com.google.inject.Inject;
import com.pusher.rest.Pusher;
import com.pusher.rest.data.PresenceUser;
import com.pusher.rest.data.Result;
import com.pusher.rest.data.Result.Status;

public class SendPusher {

  private String apiKey;
  private final String appId = "225127";
  private final String apiSecret = "07c95a64b08e84a50449";
  private APConfig config;

  @Inject
  public SendPusher(APConfig config) {
    this.config = config;
    apiKey = config.getPushApiKey();
  }

  public String autenticate(String socketID, String channel, User user) {
    Pusher pusher = new Pusher(appId, apiKey, apiSecret);
    PresenceUser prenceUser = new PresenceUser(user.getId(), user);
    return pusher.authenticate(socketID, channel, prenceUser);

  }

  public boolean sendPush(String channel, String event, String message) {
    Pusher pusher = new Pusher(appId, apiKey, apiSecret);
    pusher.setEncrypted(true);
    Result result = pusher.trigger(channel, event, Collections.singletonMap("message", message));
    return result.getStatus().compareTo(Status.SUCCESS) == 0;

  }

}
