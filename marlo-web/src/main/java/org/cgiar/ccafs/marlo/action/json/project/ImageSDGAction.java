package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.SDGContributionManager;
import org.cgiar.ccafs.marlo.data.manager.SdgManager;
import org.cgiar.ccafs.marlo.data.model.SDGContribution;
import org.cgiar.ccafs.marlo.data.model.Sdg;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageSDGAction extends BaseAction {

  private final Logger logger = LoggerFactory.getLogger(ImageSDGAction.class);
  private SDGContributionManager SDGContributionManager;
  private SdgManager sdgManager;

  private long sdgContributionID;
  private InputStream imageStream;
  private String imageURL;
  private String imagePath;

  private Map<String, Object> imageProperties;

  @Inject
  public ImageSDGAction(APConfig config, SDGContributionManager SDGContributionManager, SdgManager sdgManager) {
    super(config);
    this.SDGContributionManager = SDGContributionManager;
    this.sdgManager = sdgManager;
  }

  @Override
  public String execute() throws Exception {
    this.getInfo();
    return SUCCESS;
  }

  public Map<String, Object> getImageProperties() {
    return imageProperties;
  }

  public InputStream getImageStream() {
    return imageStream;
  }

  private void getInfo() throws FileNotFoundException {
    SDGContribution SDGContribution = SDGContributionManager.getSDGContributionById(sdgContributionID);
    if (SDGContribution != null && SDGContribution.getSdg() != null && SDGContribution.getSdg().getId() != null) {
      Sdg sdg = sdgManager.getSDGById(SDGContribution.getSdg().getId());
      if (sdg != null && sdg.getIcon() != null) {
        // D:\MARLO-PROJECT - 4.5\MARLO\marlo-web\src\main\webapp\global\images\sdg\E_SDG-goals_Goal-01.png

        // String imagePath = this.getBaseUrl() + "/global/images/sdg/" + sdg.getIcon();
        imagePath = "global/images/sdg/" + sdg.getIcon();

        File imageFile = this.searchForImage(imagePath);
        System.out.println("imageFile " + imageFile);
        if (imageFile != null) {
          imageStream = new FileInputStream(imageFile);
        }
      }
    }
  }

  public String image() throws Exception {
    this.getInfo();
    imageProperties.put(this.getBaseUrl() + "adsoluteURL", imagePath);
    imageProperties.put("relativeURL", imagePath);
    return SUCCESS;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    sdgContributionID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.PARTNER_REQUEST_ID).getMultipleValues()[0]));
  }

  private File searchForImage(String searchParam) {
    System.out.println("search param " + searchParam);
    File file = new File(searchParam);
    if (file.exists() && file.isFile()) {
      return file;
    }
    return null;
  }


  public void setImageProperties(Map<String, Object> imageProperties) {
    this.imageProperties = imageProperties;
  }

  public void setImageStream(InputStream imageStream) {
    this.imageStream = imageStream;
  }
}