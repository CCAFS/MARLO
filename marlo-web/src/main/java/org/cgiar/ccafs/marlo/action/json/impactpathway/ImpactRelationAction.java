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


package org.cgiar.ccafs.marlo.action.json.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterKeyOutputManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterOfActivityManager;
import org.cgiar.ccafs.marlo.data.manager.CrpOutcomeSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIdoManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.SrfSloIdo;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImpactRelationAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 5419809118664506772L;
  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ImpactRelationAction.class);

  private String id;
  private String type;
  private List<HashMap<String, Object>> relations = new ArrayList<HashMap<String, Object>>();


  private CrpProgramManager crpProgramManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private CrpClusterOfActivityManager crpClusterOfActivityManager;
  private CrpClusterKeyOutputManager crpClusterKeyOutputManager;
  private CrpOutcomeSubIdoManager crpOutcomeSubIdoManager;
  private SrfSubIdoManager srfSubIdoManager;
  private SrfSloIdoManager srfSloIdoManager;

  @Inject
  public ImpactRelationAction(APConfig config, CrpProgramManager crpProgramManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, CrpOutcomeSubIdoManager crpOutcomeSubIdoManager,
    SrfSloIdoManager srfSloIdoManager, SrfSubIdoManager srfSubIdoManager,
    CrpClusterKeyOutputManager crpClusterKeyOutputManager, CrpClusterOfActivityManager crpClusterOfActivityManager) {
    super(config);
    this.crpProgramManager = crpProgramManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.crpOutcomeSubIdoManager = crpOutcomeSubIdoManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.srfSloIdoManager = srfSloIdoManager;
    this.crpClusterOfActivityManager = crpClusterOfActivityManager;
    this.crpClusterKeyOutputManager = crpClusterKeyOutputManager;

  }

  public void addRelations(CrpProgram crpProgram, CrpProgramOutcome outcome) {

    HashMap<String, Object> dataProgram = new HashMap<>();
    dataProgram.put("id", "F" + crpProgram.getId());
    dataProgram.put("label", crpProgram.getAcronym());
    dataProgram.put("description", crpProgram.getName());
    dataProgram.put("color", crpProgram.getColor());
    dataProgram.put("type", "F");
    relations.add(dataProgram);
    int i = 1;
    for (CrpProgramOutcome crpProgramOutcome : crpProgram.getCrpProgramOutcomes().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {

      if (outcome == null
        || (outcome != null && outcome.getId().longValue() == crpProgramOutcome.getId().longValue())) {

        HashMap<String, Object> datacrpProgramOutcome = new HashMap<>();
        datacrpProgramOutcome.put("id", "O" + crpProgramOutcome.getId());
        datacrpProgramOutcome.put("label", "Outcome #" + i);
        datacrpProgramOutcome.put("description", crpProgramOutcome.getDescription());
        datacrpProgramOutcome.put("color", crpProgramOutcome.getCrpProgram().getColor());

        datacrpProgramOutcome.put("type", "O");
        relations.add(datacrpProgramOutcome);

        Set<SrfSubIdo> subIdos = new HashSet<>();

        for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcome.getCrpOutcomeSubIdos().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {


          if (crpOutcomeSubIdo.getSrfSubIdo() != null && crpOutcomeSubIdo.getSrfSubIdo().isActive()
            && !subIdos.contains(crpOutcomeSubIdo.getSrfSubIdo())) {
            subIdos.add(crpOutcomeSubIdo.getSrfSubIdo());
            HashMap<String, Object> dataDetaiSubIDO = new HashMap<>();
            dataDetaiSubIDO.put("id", "SD" + crpOutcomeSubIdo.getSrfSubIdo().getId());
            dataDetaiSubIDO.put("label", "SubIDO #" + crpOutcomeSubIdo.getSrfSubIdo().getId());
            dataDetaiSubIDO.put("description", crpOutcomeSubIdo.getSrfSubIdo().getDescription());

            dataDetaiSubIDO.put("type", "SD");

            relations.add(dataDetaiSubIDO);


            HashMap<String, Object> dataDetaiSIDO = new HashMap<>();
            dataDetaiSIDO.put("id", "IDO" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
            dataDetaiSIDO.put("label", "IDO #" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
            dataDetaiSIDO.put("description", crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getDescription());

            dataDetaiSIDO.put("type", "IDO");

            relations.add(dataDetaiSIDO);


            for (SrfSloIdo srfSloIdo : crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getSrfSloIdos()) {

              HashMap<String, Object> dataDetaiSlo = new HashMap<>();
              dataDetaiSlo.put("id", "SLO" + srfSloIdo.getSrfSlo().getId());
              dataDetaiSlo.put("label", "SLO #" + srfSloIdo.getSrfSlo().getId());
              dataDetaiSlo.put("description", srfSloIdo.getSrfSlo().getDescription());

              dataDetaiSlo.put("type", "SLO");

              relations.add(dataDetaiSlo);


            }
          }
        }


        if (outcome != null) {

          Set<CrpClusterOfActivity> activities = new HashSet<>();
          for (CrpClusterKeyOutputOutcome crpClusterKeyOutput : crpProgramOutcome.getCrpClusterKeyOutputOutcomes()
            .stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
            HashMap<String, Object> dataDetailKeyOutput = new HashMap<>();
            dataDetailKeyOutput.put("id", "KO" + crpClusterKeyOutput.getCrpClusterKeyOutput().getId());

            dataDetailKeyOutput.put("label",
              "KeyOutput " + crpClusterKeyOutput.getCrpClusterKeyOutput().getComposedName());
            dataDetailKeyOutput.put("description", crpClusterKeyOutput.getCrpClusterKeyOutput().getKeyOutput());
            dataDetailKeyOutput.put("color", crpClusterKeyOutput.getCrpProgramOutcome().getCrpProgram().getColor());
            dataDetailKeyOutput.put("type", "KO");


            relations.add(dataDetailKeyOutput);
            activities.add(crpClusterKeyOutput.getCrpClusterKeyOutput().getCrpClusterOfActivity());


          }
          for (CrpClusterOfActivity crpClusterOfActivity : activities) {
            HashMap<String, Object> dataDetailOutcome = new HashMap<>();
            dataDetailOutcome.put("id", "C" + crpClusterOfActivity.getId());
            dataDetailOutcome.put("label", "CoA #" + crpClusterOfActivity.getComposedName());
            dataDetailOutcome.put("description", crpClusterOfActivity.getComposedName());
            dataDetailOutcome.put("color", "#c0c0c0");
            dataDetailOutcome.put("type", "CoA");
            relations.add(dataDetailOutcome);
          }

        }
      }
      i++;
    }


    if (outcome == null) {
      int i1 = 1;
      int j = 1;
      for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {

        HashMap<String, Object> dataDetailOutcome = new HashMap<>();
        dataDetailOutcome.put("id", "C" + crpClusterOfActivity.getId());
        dataDetailOutcome.put("label", "CoA #" + i1);
        dataDetailOutcome.put("description", crpClusterOfActivity.getComposedName());
        dataDetailOutcome.put("color", "#c0c0c0");
        dataDetailOutcome.put("type", "CoA");


        relations.add(dataDetailOutcome);

        for (CrpClusterKeyOutput keyOutput : crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
          .filter(ko -> ko.isActive()).collect(Collectors.toList())) {
          HashMap<String, Object> dataDetailKeyOutput = new HashMap<>();
          dataDetailKeyOutput.put("id", "KO" + keyOutput.getId());

          dataDetailKeyOutput.put("label", "KeyOutput #" + j);
          dataDetailKeyOutput.put("description", keyOutput.getKeyOutput());
          dataDetailKeyOutput.put("color", crpClusterOfActivity.getCrpProgram().getColor());
          dataDetailKeyOutput.put("type", "KO");
          j++;

          relations.add(dataDetailKeyOutput);
        }


        i1++;
      }
    }
  }


  public void addRelationsCluster(CrpProgram crpProgram, CrpClusterOfActivity cluster,
    CrpClusterKeyOutput crpClusterKeyOutput) {

    HashMap<String, Object> dataProgram = new HashMap<>();
    dataProgram.put("id", crpProgram.getId());
    dataProgram.put("label", crpProgram.getAcronym());
    dataProgram.put("description", crpProgram.getName());
    dataProgram.put("color", crpProgram.getColor());
    dataProgram.put("type", "F");
    relations.add(dataProgram);
    int i = 1;


    int i1 = 1;
    int j = 1;
    for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {

      if (cluster == null
        || (cluster != null && crpClusterOfActivity.getId().longValue() == cluster.getId().longValue())) {


        HashMap<String, Object> dataDetailOutcome = new HashMap<>();
        dataDetailOutcome.put("id", "C" + crpClusterOfActivity.getId());
        dataDetailOutcome.put("label", "CoA #" + i1);
        dataDetailOutcome.put("description", crpClusterOfActivity.getComposedName());
        dataDetailOutcome.put("color", "#c0c0c0");
        dataDetailOutcome.put("type", "CoA");


        relations.add(dataDetailOutcome);

        for (CrpClusterKeyOutput keyOutput : crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
          .filter(ko -> ko.isActive()).collect(Collectors.toList())) {


          if (crpClusterKeyOutput == null || (crpClusterKeyOutput != null
            && crpClusterKeyOutput.getId().longValue() == keyOutput.getId().longValue())) {


            HashMap<String, Object> dataDetailKeyOutput = new HashMap<>();
            dataDetailKeyOutput.put("id", "KO" + keyOutput.getId());

            dataDetailKeyOutput.put("label", "KeyOutput #" + j);
            dataDetailKeyOutput.put("description", keyOutput.getKeyOutput());
            dataDetailKeyOutput.put("color", crpClusterOfActivity.getCrpProgram().getColor());
            dataDetailKeyOutput.put("type", "KO");
            j++;

            relations.add(dataDetailKeyOutput);
            for (CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome : keyOutput.getCrpClusterKeyOutputOutcomes()
              .stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
              CrpProgramOutcome crpProgramOutcome = crpClusterKeyOutputOutcome.getCrpProgramOutcome();


              HashMap<String, Object> datacrpProgramOutcome = new HashMap<>();
              datacrpProgramOutcome.put("id", "O" + crpProgramOutcome.getId());
              datacrpProgramOutcome.put("label", "Outcome #" + i);
              datacrpProgramOutcome.put("description", crpProgramOutcome.getDescription());
              datacrpProgramOutcome.put("color", crpProgramOutcome.getCrpProgram().getColor());

              datacrpProgramOutcome.put("type", "O");
              relations.add(datacrpProgramOutcome);

              Set<SrfSubIdo> subIdos = new HashSet<>();

              for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcome.getCrpOutcomeSubIdos().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList())) {


                if (crpOutcomeSubIdo.getSrfSubIdo() != null && crpOutcomeSubIdo.getSrfSubIdo().isActive()
                  && !subIdos.contains(crpOutcomeSubIdo.getSrfSubIdo())) {
                  subIdos.add(crpOutcomeSubIdo.getSrfSubIdo());
                  HashMap<String, Object> dataDetaiSubIDO = new HashMap<>();
                  dataDetaiSubIDO.put("id", "SD" + crpOutcomeSubIdo.getSrfSubIdo().getId());
                  dataDetaiSubIDO.put("label", "SubIDO #" + crpOutcomeSubIdo.getSrfSubIdo().getId());
                  dataDetaiSubIDO.put("description", crpOutcomeSubIdo.getSrfSubIdo().getDescription());

                  dataDetaiSubIDO.put("type", "SD");

                  relations.add(dataDetaiSubIDO);


                  HashMap<String, Object> dataDetaiSIDO = new HashMap<>();
                  dataDetaiSIDO.put("id", "IDO" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
                  dataDetaiSIDO.put("label", "IDO #" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
                  dataDetaiSIDO.put("description", crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getDescription());

                  dataDetaiSIDO.put("type", "IDO");

                  relations.add(dataDetaiSIDO);


                  for (SrfSloIdo srfSloIdo : crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getSrfSloIdos()) {

                    HashMap<String, Object> dataDetaiSlo = new HashMap<>();
                    dataDetaiSlo.put("id", "SLO" + srfSloIdo.getSrfSlo().getId());
                    dataDetaiSlo.put("label", "SLO #" + srfSloIdo.getSrfSlo().getId());
                    dataDetaiSlo.put("description", srfSloIdo.getSrfSlo().getDescription());

                    dataDetaiSlo.put("type", "SLO");

                    relations.add(dataDetaiSlo);


                  }
                }
              }
            }
          }
        }

        i1++;


      }
    }


  }

  @Override
  public String execute() throws Exception {

    Set<CrpProgram> crpPrograms = new HashSet<>();
    switch (type) {
      case "F":
        CrpProgram crpProgram = crpProgramManager.getCrpProgramById(Long.parseLong(id.replace("F", "")));
        this.addRelations(crpProgram, null);

        break;
      /**
       * TODO :Refactor
       */
      case "O":
        CrpProgramOutcome crpProgramOutcome =
          crpProgramOutcomeManager.getCrpProgramOutcomeById(Long.parseLong(id.replace("O", "")));
        this.addRelations(crpProgramOutcome.getCrpProgram(), crpProgramOutcome);

        break;
      case "SD":
        CrpOutcomeSubIdo crpOutcomeSubIdo =
          crpOutcomeSubIdoManager.getCrpOutcomeSubIdoById(Long.parseLong(id.replace("SD", "")));
        this.addRelations(crpOutcomeSubIdo.getCrpProgramOutcome().getCrpProgram(), null);

        break;

      case "IDO":

        SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoById(Long.parseLong(id.replace("IDO", "")));
        for (CrpOutcomeSubIdo outcomeSubIdo : srfSubIdo.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          crpPrograms.add(outcomeSubIdo.getCrpProgramOutcome().getCrpProgram());
        }

        for (CrpProgram myProgram : crpPrograms) {
          this.addRelations(myProgram, null);
        }


        break;
      case "SLO":
        SrfSloIdo srfSloIdo = srfSloIdoManager.getSrfSloIdoById(Long.parseLong(id.replace("SLO", "")));
        for (SrfSubIdo srIdo : srfSloIdo.getSrfIdo().getSrfSubIdos().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          for (CrpOutcomeSubIdo outcomeSubIdo : srIdo.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive())
            .collect(Collectors.toList())) {
            crpPrograms.add(outcomeSubIdo.getCrpProgramOutcome().getCrpProgram());
          }


        }
        for (CrpProgram myProgram : crpPrograms) {
          this.addRelations(myProgram, null);
        }

        break;

      /**
       * TODO :Refactor
       */
      case "CoA":
        CrpClusterOfActivity crpClusterOfActivity =
          crpClusterOfActivityManager.getCrpClusterOfActivityById(Long.parseLong(id.replace("C", "")));
        this.addRelationsCluster(crpClusterOfActivity.getCrpProgram(), crpClusterOfActivity, null);

        break;
      /**
       * TODO :Refactor
       */

      case "KO":
        CrpClusterKeyOutput crpClusterKeyOutput =
          crpClusterKeyOutputManager.getCrpClusterKeyOutputById(Long.parseLong(id.replace("KO", "")));
        this.addRelationsCluster(crpClusterKeyOutput.getCrpClusterOfActivity().getCrpProgram(),
          crpClusterKeyOutput.getCrpClusterOfActivity(), crpClusterKeyOutput);

        break;
      default:
        break;
    }

    return SUCCESS;


  }


  public List<HashMap<String, Object>> getRelations() {
    return relations;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    // Validating parameters.

    id = "";

    try {
      id = (StringUtils.trim(((String[]) parameters.get(APConstants.ID))[0]));
    } catch (Exception e) {
      LOG.error("There was an exception trying to parse the   id = {} ",
        StringUtils.trim(((String[]) parameters.get(APConstants.ID))[0]));

    }

    try {
      type = (StringUtils.trim(((String[]) parameters.get(APConstants.TYPE))[0]));
    } catch (Exception e) {
      LOG.error("There was an exception trying to parse the   id = {} ",
        StringUtils.trim(((String[]) parameters.get(APConstants.TYPE))[0]));

    }
  }

  public void setRelations(List<HashMap<String, Object>> relations) {
    this.relations = relations;
  }


}

