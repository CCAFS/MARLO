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

package org.cgiar.ccafs.marlo.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Hernán David Carvajal B. - CIAT/CCAFS
 */

public class LogTriggersManager {

  private static Logger LOG = LoggerFactory.getLogger(LogTriggersManager.class);

  private Connection connection;
  private String databaseName;
  private String logDatabaseName;
  private String tableName;

  public LogTriggersManager(Connection connection, String dbName, String tableName) {
    this.connection = connection;
    this.databaseName = dbName;
    this.logDatabaseName = dbName + "_history";
    this.tableName = tableName;
  }


  public void createTrigger(String triggerAction) throws SQLException {
    Statement statement = connection.createStatement();
    StringBuilder query = new StringBuilder();

    // We don't need to insert the id in the history table, that is why we remove it from the list of columns
    String columnsToInsert = this.getLogTableColumns().replace("`id`,", "");
    String valuesToInsert = this.getTriggerValues().replace("record_id, ", "").replace(", action", "");

    // Add the trigger in the production database
    statement.addBatch("USE " + databaseName + ";");

    query.append("DROP TRIGGER IF EXISTS after_" + tableName + "_" + triggerAction + "; ");
    statement.addBatch(query.toString());

    query.setLength(0);
    query.append("CREATE TRIGGER after_" + tableName + "_" + triggerAction + " ");
    query.append("AFTER " + triggerAction.toUpperCase() + " ON " + tableName);
    query.append(" FOR EACH ROW BEGIN ");

    if (triggerAction.equals("insert")) {


      // Update the last record with the same ID to set the 'active_until' column to NOW()
      query.append("UPDATE ");
      query.append(logDatabaseName);
      query.append(".");
      query.append(tableName);
      query.append(" SET active_until=NOW() WHERE record_id = NEW.`id` ");
      query.append("ORDER BY active_since DESC LIMIT 1; ");


      // Set the trigger action to 'insert'
      query.append("  INSERT INTO " + logDatabaseName + "." + tableName + "( ");
      query.append(columnsToInsert);
      query.append(") VALUES ( ");
      query.append(valuesToInsert);
      query.append(", 'insert'); ");

    } else if (triggerAction.equals("update")) {
      // If the only columns updated were the modified_by and the justification, we won't add a record in the log
      String oldColumns = this.getTableColumns(tableName, "OLD.");
      oldColumns = oldColumns.replace(", OLD.`modified_by`, OLD.`modification_justification`", "");
      String newColumns = this.getTableColumns(tableName, "NEW.");
      newColumns = newColumns.replace(", NEW.`modified_by`, NEW.`modification_justification`", "");

      query.append("DECLARE old_concat, new_concat text; ");
      query.append("SET old_concat = MD5(CONCAT_WS(',', " + oldColumns + ")); ");
      query.append("SET new_concat = MD5(CONCAT_WS(',', " + newColumns + ")); ");

      query.append("IF old_concat <> new_concat THEN ");

      // Update the last record with the same ID to set the 'active_until' column to NOW()
      query.append("UPDATE ");
      query.append(logDatabaseName);
      query.append(".");
      query.append(tableName);
      query.append(" SET active_until=NOW() WHERE record_id = NEW.`id` ");
      query.append("ORDER BY active_since DESC LIMIT 1; ");


      // If the trigger is updating the record we need to check if the record is being deleted.
      query.append("IF OLD.`is_active` = 1 AND NEW.`is_active` = 0 THEN ");

      // If the is_active column change from 1 to 0, the action is delete
      query.append("  INSERT INTO " + logDatabaseName + "." + tableName + "( ");
      query.append(columnsToInsert);
      query.append(") VALUES ( ");
      query.append(valuesToInsert);
      query.append(", 'delete'); ");

      query.append("ELSE ");

      // Otherwise the action is update
      query.append("  INSERT INTO " + logDatabaseName + "." + tableName + "( ");
      query.append(columnsToInsert);
      query.append(") VALUES ( ");
      query.append(valuesToInsert);
      query.append(", 'update'); ");
      query.append("END IF; ");

      query.append("END IF; ");
    }

    query.append("END; ");
    statement.addBatch(query.toString());

    try {
      statement.executeBatch();
    } catch (SQLException e) {
      LOG.error("Exception raised trying to create the trigger after_{}_{}.", tableName, triggerAction);
      throw e;
    } finally {
      statement.close();
    }

  }

  public void deleteTrigger(String triggerAction) throws SQLException {
    Statement statement = connection.createStatement();
    StringBuilder query = new StringBuilder();

    // Add the trigger in the production database
    statement.addBatch("USE " + databaseName + ";");

    query.append("DROP TRIGGER IF EXISTS after_" + tableName + "_" + triggerAction + "; ");
    statement.addBatch(query.toString());

    try {
      statement.executeBatch();
    } catch (SQLException e) {
      LOG.error("Exception raised trying to create the trigger after_{}_{}.", tableName, triggerAction);
      throw e;
    } finally {
      statement.close();
    }
  }

  /**
   * This method returns a String with all the columns that has the table referenced in the property
   * tableName.
   * 
   * @return a string with the columns present in the table tableName.
   * @throws SQLException
   */
  private String getLogTableColumns() throws SQLException {
    Statement statement = connection.createStatement();

    StringBuilder query = new StringBuilder();
    query.append("SELECT GROUP_CONCAT( DISTINCT CONCAT('`', column_name, '`') SEPARATOR ', ') as columnNames ");
    query.append("FROM INFORMATION_SCHEMA.COLUMNS ");
    query.append("WHERE TABLE_SCHEMA='");
    query.append(logDatabaseName);
    query.append("' AND TABLE_NAME = '");
    query.append(tableName);
    query.append("'; ");

    try {
      ResultSet rs = statement.executeQuery(query.toString());
      if (rs.next()) {
        return rs.getString("columnNames");
      }
    } catch (SQLException e) {
      LOG.error("Exception raised trying to get the columns of the table {}.", tableName);
      throw e;
    } finally {
      statement.close();
    }

    return null;
  }

  /**
   * This method returns a String with the columns of the table called tableName separated by comma.
   * If the parameter prefix is not null the column name will be preceded by its value.
   * For example:
   * prefix -> "OLD."
   * returns: " OLD.`column1`, OLD.`column2` "
   * 
   * @return a String with the columns of the table separated by comma.
   */
  private String getTableColumns(String tableName, String prefix) throws SQLException {
    Statement statement = connection.createStatement();

    StringBuilder query = new StringBuilder();
    query.append("SELECT GROUP_CONCAT( DISTINCT ");
    query.append("          CONCAT('" + prefix + "`', column_name, '`') ");
    query.append("       SEPARATOR ', ') as tableValues ");
    query.append("FROM INFORMATION_SCHEMA.COLUMNS ");
    query.append("WHERE TABLE_SCHEMA='");
    query.append(databaseName);
    query.append("' AND TABLE_NAME = '");
    query.append(tableName);
    query.append("'; ");

    try {
      ResultSet rs = statement.executeQuery(query.toString());
      if (rs.next()) {
        return rs.getString("tableValues");
      }
    } catch (SQLException e) {
      LOG.error("Exception raised trying to get the columns of the table {}.", tableName);
      throw e;
    } finally {
      statement.close();
    }

    return null;
  }

  /**
   * This method returns the values to be inserted by the trigger that populates the history tables. Keep in mind that
   * the 'action' column is filled by the trigger in charge of insert the values.
   * 
   * @param triggerAction - ['insert', 'update']
   * @return an string with the values to be inserted by the trigger.
   * @throws SQLException
   */
  private String getTriggerValues() throws SQLException {
    Statement statement = connection.createStatement();

    StringBuilder query = new StringBuilder();
    query.append("SELECT GROUP_CONCAT( DISTINCT ");
    query.append("          CASE column_name ");
    query.append("          WHEN 'active_since' THEN 'NOW()' ");
    query.append("          WHEN 'active_until' THEN 'NULL' ");
    query.append("          WHEN 'record_id' THEN 'record_id' ");
    query.append("          WHEN 'action' THEN 'action' ");
    query.append("          ELSE CONCAT('NEW.`', column_name, '`') ");
    query.append("          END ");
    query.append("       SEPARATOR ', ') as tableValues ");
    query.append("FROM INFORMATION_SCHEMA.COLUMNS ");
    query.append("WHERE TABLE_SCHEMA='");
    query.append(logDatabaseName);
    query.append("' AND TABLE_NAME = '");
    query.append(tableName);
    query.append("'; ");

    try {
      ResultSet rs = statement.executeQuery(query.toString());
      if (rs.next()) {
        return rs.getString("tableValues");
      }
    } catch (SQLException e) {
      LOG.error("Exception raised trying to get the columns of the table {}.", tableName);
      throw e;
    } finally {
      statement.close();
    }

    return null;
  }

}
