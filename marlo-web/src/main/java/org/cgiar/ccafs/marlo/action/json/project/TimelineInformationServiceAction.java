package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.TimelineManager;
import org.cgiar.ccafs.marlo.data.model.Timeline;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimelineInformationServiceAction extends BaseAction {

  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(TimelineInformationServiceAction.class);
  private TimelineManager timelineManager;
  private List<Map<String, Object>> information;


  @Inject
  public TimelineInformationServiceAction(APConfig config, TimelineManager timelineManager) {
    super(config);
    this.timelineManager = timelineManager;
  }

  @Override
  public String execute() throws Exception {
    List<Timeline> timelineList = new ArrayList<>();
    Map<String, Object> timelineMap;
    information = new ArrayList<Map<String, Object>>();

    try {
      timelineList = timelineManager.findAll();

      try {
        timelineList = timelineList.stream()
          .sorted(Comparator.comparing(Timeline::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList());
      } catch (Exception e) {
        logger.error("unable to order timeline items", e);
      }

      if (timelineList != null) {
        int count = 0;
        for (Timeline timelineItem : timelineList) {
          timelineMap = new HashMap<String, Object>();
          if (timelineItem.getDescription() != null) {
            timelineMap.put("description", timelineItem.getDescription());
          } else {
            timelineMap.put("description", "");
          }
          if (timelineItem.getStartDate() != null) {
            timelineMap.put("startDate", (timelineItem.getStartDate().toString()));
          } else {
            timelineMap.put("startDate", "");
          }
          if (timelineItem.getEndDate() != null) {
            timelineMap.put("endDate", (timelineItem.getEndDate().toString()));
          } else {
            timelineMap.put("endDate", "");
          }
          if (timelineItem.getOrder() != null) {
            timelineMap.put("orderIndex", timelineItem.getOrder());
          } else {
            timelineMap.put("orderIndex", "");
          }
          information.add(count, timelineMap);
          count++;
        }
      }
    } catch (Exception e) {
      logger.error("unable to get timeline items", e);
    }


    return SUCCESS;
  }

  public List<Map<String, Object>> getInformation() {
    return information;
  }

  /**
   * Parse date to String and removes the hour information
   * 
   * @param dateString Date to String parameter
   * @return String
   * @exception StringIndexOutOfBoundsException if is not possible parse the date or cut
   */
  public String parseDate(String dateString) {
    try {
      if (!dateString.isEmpty()) {
        dateString = dateString.substring(0, 10);
      }
    } catch (StringIndexOutOfBoundsException e) {
      logger.error("unable to parse date", e);
    }
    return dateString;
  }

  @Override
  public void prepare() throws Exception {
  }

  public void setInformation(List<Map<String, Object>> information) {
    this.information = information;
  }

}
