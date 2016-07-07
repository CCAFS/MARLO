/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.json.autosave;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.inject.Inject;
import com.opensymphony.xwork2.Action;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class AutoSaveWriterAction extends BaseAction {


  private static final long serialVersionUID = 2904862716714197942L;


  private String autoSave[];

  private Map<String, Object> status;


  @Inject
  public AutoSaveWriterAction(APConfig config) {
    super(config);
  }


  @Override
  public String execute() throws Exception {

    String fileId = "";
    String fileClass = "";
    status = new HashMap<String, Object>();

    if (autoSave.length > 0) {

      Gson gson = new Gson();

      @SuppressWarnings("unchecked")
      LinkedTreeMap<String, Object> result = gson.fromJson(autoSave[0], LinkedTreeMap.class);


      if (result.containsKey("id")) {
        fileId = (String) result.get("id");
      } else {
        fileId = "XX";
      }

      if (result.containsKey("className")) {
        String ClassName = (String) result.get("className");
        String[] composedClassName = ClassName.split("\\.");
        fileClass = composedClassName[composedClassName.length - 1];
      }

      result.put("activeSince", new Date());

      String jSon = gson.toJson(result);

      try {

        String fileName = fileId + "_" + fileClass + ".json";

        Path path = Paths.get(config.getAutoSaveFolder());

        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
          File file = new File(config.getAutoSaveFolder() + fileName);
          Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
          out.append(jSon).append("\r\n");;

          out.flush();
          out.close();
        } else {
          Files.createDirectories(path);
          File file = new File(config.getAutoSaveFolder() + fileName);
          Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
          out.append(jSon).append("\r\n");;

          out.flush();
          out.close();
        }
        status.put("status", true);
      } catch (IOException e) {
        status.put("status", false);
        e.printStackTrace();
      } catch (Exception e) {
        status.put("status", false);
        e.printStackTrace();
      }

    }

    return Action.SUCCESS;
  }

  public Map<String, Object> getStatus() {
    return status;
  }

  @Override
  public void prepare() throws Exception {

    Map<String, Object> parameters = this.getParameters();
    autoSave = (String[]) parameters.get(APConstants.AUTOSAVE_REQUEST);

  }

  public void setStatus(Map<String, Object> status) {
    this.status = status;
  }


}
