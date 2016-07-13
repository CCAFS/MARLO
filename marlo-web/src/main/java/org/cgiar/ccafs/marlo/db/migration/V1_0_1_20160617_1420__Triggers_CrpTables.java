/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning & 
 * Outcomes Platform (MARLO). * MARLO is free software: you can redistribute it and/or modify
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

package org.cgiar.ccafs.marlo.db.migration;

import org.cgiar.ccafs.marlo.db.LogDatabaseManager;
import org.cgiar.ccafs.marlo.db.LogTableManager;
import org.cgiar.ccafs.marlo.db.LogTriggersManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Garcia
 **/

public class V1_0_1_20160617_1420__Triggers_CrpTables implements JdbcMigration {

  private static Logger LOG = LoggerFactory.getLogger(V1_0_1_20160617_1420__Triggers_CrpTables.class);

  @Override
  public void migrate(Connection connection) throws Exception {
    Statement statement = connection.createStatement();
    String[] tableNames = {"crp_assumptions", "crp_cluster_of_activities", "crp_milestones", "crp_outcome_sub_idos",
      "crp_parameters", "crp_program_leaders", "crp_program_outcomes", "crp_programs", "crp_sites_leaders",
      "crp_sub_idos_contributions", "crp_users", "crps", "crps_sites_integration", "srf_cross_cutting_issues",
      "srf_idos", "srf_slo_idos", "srf_slo_indicator_targets", "srf_slo_indicators", "srf_slos", "srf_sub_idos",
      "srf_target_units", "users"};

    String query = "SELECT DATABASE() as dbName ;";
    String dbName = "";

    ResultSet rs = statement.executeQuery(query);
    if (rs.next()) {
      dbName = rs.getString("dbName");
    } else {
      String msg = "There was an error getting the current database name";
      LOG.error(msg);
      throw new Exception(msg);
    }

    LogDatabaseManager dbManager = new LogDatabaseManager(connection, dbName);
    LogTableManager tableManager = new LogTableManager(connection, dbName);
    LogTriggersManager triggerManager;

    try {

      // if (!dbManager.isLogDatabaseAvailable()) {
      // And create the database again
      dbManager.createHistoryDatabase();
      // }


      for (String tableName : tableNames) {
        try {
          dbManager.useHistoryDatabase();
          tableManager.dropLogTable(tableName);
          tableManager.createLogTable(tableName);

          triggerManager = new LogTriggersManager(connection, dbName, tableName);

          triggerManager.createTrigger("insert");
          triggerManager.createTrigger("update");
        } catch (Exception e) {
          throw e;

        }
      }
    } catch (SQLException e) {
      LOG.error("There was an error running the migration.");
      throw e;
    }

  }
}
