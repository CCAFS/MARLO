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

package org.cgiar.ccafs.marlo.action.annualReport.y2018;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.PartnershipValidator;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DownloadDocumentExternalPartnershipsAction extends BaseAction {


  private static final long serialVersionUID = 575869881576979848L;

  private static Logger LOG = LoggerFactory.getLogger(DownloadDocumentExternalPartnershipsAction.class);

  private PartnershipValidator validator;

  private ReportSynthesis reportSynthesis;
  private String filename;
  private String crp;

  @Inject
  public DownloadDocumentExternalPartnershipsAction(APConfig config, GlobalUnitManager crpManager) {
    super(config);
  }

  public String downloadFile() {
    if (filename != null && !filename.isEmpty() && crp != null && !crp.isEmpty()) {
      try {
        this.getClass().getClassLoader();
        // new File(this.getClass().getResource("/template/participants-template.xlsm").getFile());

        new File(".").getCanonicalPath();

        String path_ = config.getUploadsBaseFolder() + "/" + APConstants.PARTNERSHIP_FOLDER + "/" + crp + "/" + crp
          + "_" + ReportSynthesis2018SectionStatusEnum.EXTERNAL_PARTNERSHIPS.getStatus() + "/" + filename;
        path_ = path_.replace("=", "");


        /*** test ***/
        /*** fin test **/


        // path_ = path_.replace("/", File.separator);

        new FileInputStream(path_);

        new ByteArrayOutputStream();

        // inputStream = new ByteArrayInputStream(fileInput.toByteArray());

        try (BufferedInputStream in = new BufferedInputStream(new URL(path_).openStream());
          FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
          byte dataBuffer[] = new byte[1024];
          int bytesRead;
          while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            fileOutputStream.write(dataBuffer, 0, bytesRead);
          }
        } catch (IOException e) {
          System.out.println(e);
        }

      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return SUCCESS;
  }


  @Override
  public void prepare() throws Exception {

    try {
      filename = StringUtils.trim(this.getRequest().getParameter(APConstants.FILENAME));
    } catch (NumberFormatException e) {
      filename = null;
      return; // Stop here and go to execute method.
    }

    try {
      crp = StringUtils.trim(this.getRequest().getParameter(APConstants.CRP_REQUEST));
    } catch (NumberFormatException e) {
      crp = null;
      return; // Stop here and go to execute method.
    }
  }


  @Override
  public void validate() {
    if (save) {
      validator.validate(this, reportSynthesis, true);
    }
  }
}
