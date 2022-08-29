package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.TimelineManager;
import org.cgiar.ccafs.marlo.data.model.Timeline;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimelineInformationServiceAction extends BaseAction {

  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(TimelineInformationServiceAction.class);
  private TimelineManager timelineManager;
  Map<String, Object> timelineMap;

  @Inject
  public TimelineInformationServiceAction(APConfig config, TimelineManager timelineManager) {
    super(config);
    this.timelineManager = timelineManager;
  }

  @Override
  public String execute() throws Exception {
    List<Timeline> timelineList = new ArrayList<>();
    timelineMap = new HashMap<String, Object>();

    try {
      timelineList = timelineManager.findAll();
      if (timelineList != null) {
        for (Timeline timelineItem : timelineList) {
          if (timelineItem.getDescription() != null) {
            timelineMap.put("description", timelineItem.getDescription());
          } else {
            timelineMap.put("description", "");
          }
          if (timelineItem.getStartDate() != null) {
            timelineMap.put("startDate", timelineItem.getStartDate());
          } else {
            timelineMap.put("startDate", "");
          }
          if (timelineItem.getEndDate() != null) {
            timelineMap.put("endDate", timelineItem.getEndDate());
          } else {
            timelineMap.put("endDate", "");
          }
          if (timelineItem.getOrder() != null) {
            timelineMap.put("orderIndex", timelineItem.getOrder());
          } else {
            timelineMap.put("orderIndex", "");
          }

        }
      }
    } catch (Exception e) {
      logger.error("unable to get timeline items", e);
    }


    return SUCCESS;
  }


  public Map<String, Object> getTimelineMap() {
    return timelineMap;
  }


  @Override
  public void prepare() throws Exception {
  }


  public void setTimelineMap(Map<String, Object> timelineMap) {
    this.timelineMap = timelineMap;
  }


}
