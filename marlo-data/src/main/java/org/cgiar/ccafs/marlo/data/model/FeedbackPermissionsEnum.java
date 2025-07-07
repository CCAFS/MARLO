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

package org.cgiar.ccafs.marlo.data.model;

import java.util.Arrays;
import java.util.Optional;

public enum FeedbackPermissionsEnum {

  CAN_MANAGE_FEEDBACK("can_react_comments"), CAN_LEAVE_COMMENTS("can_leave_comments"),
  CAN_APPROVE_COMMENTS("can_approve_comments"), CAN_TRACK_COMMENTS("can_track_comments");

  public static Optional<FeedbackPermissionsEnum> fromValue(String value) {
    return Arrays.stream(values()).filter(permission -> permission.getValue().equalsIgnoreCase(value)).findFirst();
  }

  private final String value;

  FeedbackPermissionsEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}