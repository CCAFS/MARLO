/*
 * This file is part of Managing Agricultural Research for Learning&*Outcomes Platform(MARLO).
 ** MARLO is free software:you can redistribute it and/or modify
 ** it under the terms of the GNU General Public License as published by
 ** the Free Software Foundation,either version 3 of the License,or*at your option)any later version.
 ** MARLO is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY;without even the implied warranty of*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See
 * the
 ** GNU General Public License for more details.
 ** You should have received a copy of the GNU General Public License
 ** along with MARLO.If not,see<http:// www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.MarloMessageManager;
import org.cgiar.ccafs.marlo.data.model.MarloMessage;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MarloMessagesAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;
  private static Logger logger = LoggerFactory.getLogger(MarloMessagesAction.class);
  private MarloMessageManager marloMessageManager;
  private MarloMessage message;
  private List<String> messageTypes;
  private List<String> messageHistory;
  private String lastMessage;

  @Inject
  public MarloMessagesAction(APConfig config, MarloMessageManager marloMessageManager) {
    super(config);
    this.marloMessageManager = marloMessageManager;
  }

  public String deleteMessage() {
    List<MarloMessage> messages = new ArrayList<>();
    MarloMessage messageDelete = null;;

    try {
      messages = marloMessageManager.findAll();
      if (messages != null && !messages.isEmpty()) {
        messageDelete = messages.get(0);
      }
    } catch (Exception e) {
      logger.debug(e + " error getting MARLO Messages");
    }
    if (messageDelete != null && messageDelete.getId() != null) {
      marloMessageManager.deleteMarloMessage(messageDelete.getId());
    }
    return SUCCESS;
  }

  public String getLastMessage() {
    return lastMessage;
  }

  public MarloMessage getMessage() {
    return message;
  }

  public List<String> getMessageHistory() {
    return messageHistory;
  }

  public List<String> getMessageTypes() {
    return messageTypes;
  }

  @Override
  public void prepare() throws Exception {
    messageTypes = new ArrayList<>();
    List<MarloMessage> messages = new ArrayList<>();
    List<MarloMessage> messageHistory = new ArrayList<>();

    try {
      if (marloMessageManager.getLastMessage() != null
        && marloMessageManager.getLastMessage().getMessageValue() != null) {
        lastMessage = marloMessageManager.getLastMessage().getMessageValue();
      }
      lastMessage = lastMessage.replace("strong", "b");

      if (marloMessageManager.findAllHistory() != null && !marloMessageManager.findAllHistory().isEmpty()) {
        messageHistory = marloMessageManager.findAllHistory();
      }
      /*
       * messages = marloMessageManager.findAll();
       * if (messages != null && !messages.isEmpty()) {
       * if (marloMessageManager.getLastMessage() != null
       * && marloMessageManager.getLastMessage().getMessageValue() != null) {
       * }
       * message = messages.get(0);
       * }
       */
    } catch (Exception e) {
      logger.debug(e + " error getting MARLO Messages");
    }
    messageTypes.add("General Message");

    if (this.isHttpPost()) {
      if (messages != null) {
        messages.clear();
      }
      messageTypes.clear();
    }
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (message != null) {

        // Delete previous active messages
        List<MarloMessage> messages = new ArrayList<>();
        try {
          messages = marloMessageManager.findAll();
          if (messages != null && !messages.isEmpty() && messages.get(0) != null && messages.get(0).getId() != null) {
            marloMessageManager.deleteMarloMessage(messages.get(0).getId());
          }

        } catch (Exception e) {
          logger.debug(e + " error getting MARLO Messages");
        }

        // New Activity
        MarloMessage marloMessageSave = new MarloMessage();

        if (message.getMessageValue() != null) {
          String tempMessage = message.getMessageValue();
          tempMessage = tempMessage.replace("<p>", "");
          tempMessage = tempMessage.replace("</p>", "");
          marloMessageSave.setMessageValue(tempMessage);
        }
        if (message.getMessageType() != null) {
          marloMessageSave.setMessageType(message.getMessageType());
        } else {
          marloMessageSave.setMessageType("General Message");
        }

        Date date = new Date();
        marloMessageSave.setActiveSince(date);
        marloMessageSave.setActive(true);
        marloMessageSave.setCreatedBy(this.getCurrentUser());
        marloMessageSave.setModifiedBy(this.getCurrentUser());

        marloMessageManager.saveMarloMessage(marloMessageSave);
      }

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setLastMessage(String lastMessage) {
    this.lastMessage = lastMessage;
  }

  public void setMessage(MarloMessage message) {
    this.message = message;
  }

  public void setMessageHistory(List<String> messageHistory) {
    this.messageHistory = messageHistory;
  }

  public void setMessageTypes(List<String> messageTypes) {
    this.messageTypes = messageTypes;
  }
}