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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides a Context for AuditLog operations which can be used by for example the Hibernate Event Listener framework.
 * 
 * @author GrantL
 */
public final class AuditLogContext {

  private Set<Map<String, Object>> inserts = new HashSet<>();
  private Set<Map<String, Object>> updates = new HashSet<>();
  private Set<Map<String, Object>> deletes = new HashSet<>();

  private String actionName;

  private List<String> relationsNames;


  public AuditLogContext() {

  }


  public String getActionName() {
    return actionName;
  }


  public Set<Map<String, Object>> getDeletes() {
    return deletes;
  }


  public Set<Map<String, Object>> getInserts() {
    return inserts;
  }


  public List<String> getRelationsNames() {
    return relationsNames;
  }


  public Set<Map<String, Object>> getUpdates() {
    return updates;
  }


  public void setActionName(String actionName) {
    this.actionName = actionName;
  }


  public void setDeletes(Set<Map<String, Object>> deletes) {
    this.deletes = deletes;
  }


  public void setInserts(Set<Map<String, Object>> inserts) {
    this.inserts = inserts;
  }


  public void setRelationsNames(List<String> relationsNames) {
    this.relationsNames = relationsNames;
  }

  public void setUpdates(Set<Map<String, Object>> updates) {
    this.updates = updates;
  }


}
