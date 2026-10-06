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

import java.util.Date;

/**
 * The filters of System Admin -> Emails over email_logs. Not an entity: every field left null does not filter.
 * Rows logged before the global unit and the source were recorded have neither, so each of those two filters can
 * also ask for the rows without one.
 */
public class EmailLogSearch {

  /** The column the results are ordered by; anything else falls back to the date. */
  public enum Order {
    DATE, SUBJECT, TO, GLOBAL_UNIT, SOURCE, TRIED
  }

  // true for the sent emails, false for the ones not sent (a row whose outcome was never recorded included).
  private Boolean sent;
  private Long globalUnitId;
  private boolean withoutGlobalUnit;
  private String sourceAction;
  private boolean withoutSourceAction;
  // Inclusive lower bound and exclusive upper bound of date_email.
  private Date from;
  private Date until;
  // Matched against the subject and the recipients.
  private String text;
  private Order order = Order.DATE;
  private boolean ascending;

  public Date getFrom() {
    return from;
  }

  public Long getGlobalUnitId() {
    return globalUnitId;
  }

  public Order getOrder() {
    return order;
  }

  public Boolean getSent() {
    return sent;
  }

  public String getSourceAction() {
    return sourceAction;
  }

  public String getText() {
    return text;
  }

  public Date getUntil() {
    return until;
  }

  public boolean isAscending() {
    return ascending;
  }

  public boolean isWithoutGlobalUnit() {
    return withoutGlobalUnit;
  }

  public boolean isWithoutSourceAction() {
    return withoutSourceAction;
  }

  public void setAscending(boolean ascending) {
    this.ascending = ascending;
  }

  public void setFrom(Date from) {
    this.from = from;
  }

  public void setGlobalUnitId(Long globalUnitId) {
    this.globalUnitId = globalUnitId;
  }

  public void setOrder(Order order) {
    this.order = order == null ? Order.DATE : order;
  }

  public void setSent(Boolean sent) {
    this.sent = sent;
  }

  public void setSourceAction(String sourceAction) {
    this.sourceAction = sourceAction;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setUntil(Date until) {
    this.until = until;
  }

  public void setWithoutGlobalUnit(boolean withoutGlobalUnit) {
    this.withoutGlobalUnit = withoutGlobalUnit;
  }

  public void setWithoutSourceAction(boolean withoutSourceAction) {
    this.withoutSourceAction = withoutSourceAction;
  }
}
