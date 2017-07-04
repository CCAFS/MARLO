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

package org.cgiar.ccafs.marlo.action.json.autosave;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConfig;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.service.IUserManager;
import org.cgiar.ccafs.marlo.utils.APConstants;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.inject.Inject;
import com.opensymphony.xwork2.Action;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class AutoSaveWriterAction extends BaseAction {


  private static final long serialVersionUID = 2904862716714197942L;


  private String autoSave[];

  private Map<String, Object> status;

  private IUserManager userService;

  @Inject
  public AutoSaveWriterAction(APConfig config, IUserManager userService) {

    super(config);
    this.userService = userService;
  }


  @Override
  public String execute() throws Exception {


    String fileId = "";
    String fileClass = "";
    String nameClass = "";
    String fileAction = "";

    status = new HashMap<String, Object>();

    if (autoSave.length > 0) {

      Gson gson = new Gson();
      byte ptext[] = autoSave[0].getBytes(ISO_8859_1);
      String value = new String(ptext, UTF_8);

      @SuppressWarnings("unchecked")

      LinkedTreeMap<String, Object> result = gson.fromJson(value, LinkedTreeMap.class);

      String userModifiedBy = fileId = (String) result.get("modifiedBy.id");
      if (result.containsKey("id")) {
        fileId = (String) result.get("id");
      } else {
        fileId = "XX";
      }

      if (result.containsKey("className")) {
        String ClassName = (String) result.get("className");
        String[] composedClassName = ClassName.split("\\.");
        nameClass = ClassName;
        fileClass = composedClassName[composedClassName.length - 1];
      }

      if (result.containsKey("actionName")) {
        fileAction = (String) result.get("actionName");
        fileAction = fileAction.replace("/", "_");
      }
      ArrayList<String> keysRemove = new ArrayList<>();

      for (Map.Entry<String, Object> entry : result.entrySet()) {
        if (entry.getKey().contains("__")) {
          keysRemove.add(entry.getKey());
        }
      }
      for (String string : keysRemove) {
        result.remove(string);
      }
      Date generatedDate = new Date();
      result.put("activeSince", generatedDate);

      String jSon = gson.toJson(result);

      if (nameClass.equals(CenterOutcome.class.getName())) {
        jSon = jSon.replaceAll("outcome\\.", "");
      }
      if (nameClass.equals(CenterOutput.class.getName())) {
        jSon = jSon.replaceAll("output\\.", "");
      }
      if (nameClass.equals(CenterProject.class.getName())) {
        jSon = jSon.replaceAll("project\\.", "");
      }
      if (nameClass.equals(CenterDeliverable.class.getName())) {
        jSon = jSon.replaceAll("deliverable\\.", "");
      }

      try {

        String fileName = fileId + "_" + fileClass + "_" + fileAction + ".json";
        String pathFile = config.getAutoSaveFolder();
        System.out.println(pathFile);
        Path path = Paths.get(pathFile);

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
        SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
        status.put("modifiedBy", userService.getUser(Long.parseLong(userModifiedBy)).getComposedCompleteName());
        status.put("activeSince", dt.format(generatedDate));
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
