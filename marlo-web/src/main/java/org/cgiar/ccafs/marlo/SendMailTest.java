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


package org.cgiar.ccafs.marlo;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.action.summaries.ReportingSummaryAction;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

public class SendMailTest extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 8724519305479344311L;

  @Inject
  SendMailS send;
  @Inject
  ReportingSummaryAction reportingSummaryAction;

  @Inject
  public SendMailTest(APConfig config) {
    super(config);
    // TODO Auto-generated constructor stub
  }

  @Override
  public String execute() throws Exception {

    // TODO Auto-generated method stub
    try {


      ByteBuffer buffer = null;
      String fileName = null;
      String contentType = null;


      reportingSummaryAction.setSelectedYear(2017);
      //
      reportingSummaryAction.setSession(this.getSession());
      reportingSummaryAction.setSelectedCycle("Planning");
      reportingSummaryAction.setProjectID(60);
      reportingSummaryAction.execute();
      // // Getting the file data.
      //
      buffer = ByteBuffer.wrap(reportingSummaryAction.getBytesPDF());
      fileName = this.getFileName();
      contentType = "application/pdf";
      //

      System.out.println("OK!");
      send.send("MARLOSupport@cgiar.org", "c.d.garcia@cgiar.org", "MARLOSupport@cgiar.org", "Prueba", "Message test",
        buffer.array(), contentType, fileName, true);

    } catch (Exception e) {

      e.printStackTrace();
    }
    return SUCCESS;


  }

  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("Full_Project_Report-");
    fileName.append("ccafs" + "-");
    fileName.append("P" + 60 + "-");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
    return fileName.toString();

  }
}
