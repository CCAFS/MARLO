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
import org.cgiar.ccafs.marlo.data.manager.SrfIdoManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIdoManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;
import org.cgiar.ccafs.marlo.data.model.SrfSlo;
import org.cgiar.ccafs.marlo.data.model.SrfSloIdo;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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


  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ImpactRelationAction.class);
  /**
   * 
   */
  private static final long serialVersionUID = 5419809118664506772L;

  private CrpClusterKeyOutputManager crpClusterKeyOutputManager;
  private CrpClusterOfActivityManager crpClusterOfActivityManager;

  private CrpOutcomeSubIdoManager crpOutcomeSubIdoManager;
  private CrpProgramManager crpProgramManager;


  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private String flagshipId;
  private String id;
  private List<HashMap<String, Object>> relations = new ArrayList<HashMap<String, Object>>();
  private SrfIdoManager srfIdoManager;
  private SrfSloIdoManager srfSloIdoManager;
  private SrfSloManager srfSloManager;

  private SrfSubIdoManager srfSubIdoManager;

  private String type;


  @Inject
  public ImpactRelationAction(APConfig config, CrpProgramManager crpProgramManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, CrpOutcomeSubIdoManager crpOutcomeSubIdoManager,
    SrfSloIdoManager srfSloIdoManager, SrfSubIdoManager srfSubIdoManager, SrfIdoManager srfIdoManager,
    CrpClusterKeyOutputManager crpClusterKeyOutputManager, CrpClusterOfActivityManager crpClusterOfActivityManager,
    SrfSloManager srfSloManager) {
    super(config);
    this.crpProgramManager = crpProgramManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.crpOutcomeSubIdoManager = crpOutcomeSubIdoManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.srfSloIdoManager = srfSloIdoManager;
    this.srfSloManager = srfSloManager;
    this.crpClusterOfActivityManager = crpClusterOfActivityManager;
    this.srfIdoManager = srfIdoManager;
    this.crpClusterKeyOutputManager = crpClusterKeyOutputManager;

  }

  public void addRelations(CrpProgram crpProgram, CrpProgramOutcome outcome, SrfSlo srfSlo, SrfSubIdo srfSubIdo,
    SrfIdo srfIdo) {

    HashMap<String, Object> dataProgram = new HashMap<>();
    dataProgram.put("id", "F" + crpProgram.getId());
    dataProgram.put("label", crpProgram.getAcronym());
    dataProgram.put("description", crpProgram.getName());
    dataProgram.put("color", crpProgram.getColor());
    dataProgram.put("type", "F");
    dataProgram.put("order", new Integer(4));

    if (relations.stream().filter(c -> c.get("id").equals(dataProgram.get("id"))).collect(Collectors.toList())
      .isEmpty()) {
      relations.add(dataProgram);
    }
    int i = 1;
    for (CrpProgramOutcome crpProgramOutcome : crpProgram.getCrpProgramOutcomes().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {

      if (outcome == null
        || (outcome != null && outcome.getId().longValue() == crpProgramOutcome.getId().longValue())) {

        HashMap<String, Object> datacrpProgramOutcome = new HashMap<>();
        datacrpProgramOutcome.put("id", "O" + crpProgramOutcome.getId());
        datacrpProgramOutcome.put("label",
          "Outcome #" + this.getIndex(crpProgramOutcome.getCrpProgram(), crpProgramOutcome));
        datacrpProgramOutcome.put("description", crpProgramOutcome.getDescription());
        datacrpProgramOutcome.put("color", crpProgramOutcome.getCrpProgram().getColor());

        datacrpProgramOutcome.put("type", "O");
        datacrpProgramOutcome.put("order", new Integer(5));
        if (this.hasSpecificities(APConstants.CRP_IP_OUTCOME_INDICATOR)) {
          datacrpProgramOutcome.put("indicator", crpProgramOutcome.getIndicator());
        }
        relations.add(datacrpProgramOutcome);

        Set<SrfSubIdo> subIdos = new HashSet<>();

        for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcome.getCrpOutcomeSubIdos().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {
          boolean add = false;

          if (crpOutcomeSubIdo.getSrfSubIdo() != null && crpOutcomeSubIdo.getSrfSubIdo().isActive()
            && !subIdos.contains(crpOutcomeSubIdo.getSrfSubIdo())) {
            subIdos.add(crpOutcomeSubIdo.getSrfSubIdo());
            HashMap<String, Object> dataDetaiSubIDO = new HashMap<>();
            dataDetaiSubIDO.put("id", "SD" + crpOutcomeSubIdo.getSrfSubIdo().getId());
            dataDetaiSubIDO.put("label", "Sub-IDO #" + crpOutcomeSubIdo.getSrfSubIdo().getId());
            dataDetaiSubIDO.put("description", crpOutcomeSubIdo.getSrfSubIdo().getDescription());

            dataDetaiSubIDO.put("type", "SD");
            dataDetaiSubIDO.put("order", new Integer(3));

            if (relations.stream().filter(c -> c.get("id").equals(dataDetaiSubIDO.get("id")))
              .collect(Collectors.toList()).isEmpty()) {
              if (srfSubIdo == null) {
                relations.add(dataDetaiSubIDO);
                add = true;
              } else {
                if (srfSubIdo.getId().equals(crpOutcomeSubIdo.getSrfSubIdo().getId())) {
                  relations.add(dataDetaiSubIDO);
                  add = true;
                }
              }

            }

            if (add) {
              HashMap<String, Object> dataDetaiSIDO = new HashMap<>();
              dataDetaiSIDO.put("id", "IDO" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
              if (crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().isIsCrossCutting()) {
                dataDetaiSIDO.put("label", "Cross-Cutting IDO #" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
              } else {
                dataDetaiSIDO.put("label", "IDO #" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
              }
              dataDetaiSIDO.put("description", crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getDescription());
              dataDetaiSIDO.put("order", new Integer(2));
              dataDetaiSIDO.put("type", "IDO");

              if (relations.stream().filter(c -> c.get("id").equals(dataDetaiSIDO.get("id")))
                .collect(Collectors.toList()).isEmpty()) {

                if (srfIdo == null) {
                  relations.add(dataDetaiSIDO);
                } else {
                  if (srfIdo.getId().equals(crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId())) {
                    if (srfSlo == null) {
                      relations.add(dataDetaiSIDO);
                    } else {
                      if (crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getSrfSloIdos().stream()
                        .filter(c -> c.getSrfIdo().getId().longValue() == srfSlo.getId().longValue())
                        .collect(Collectors.toList()).size() > 0) {
                        relations.add(dataDetaiSIDO);
                      }
                    }
                  }
                }

              }


              for (SrfSloIdo srfSloIdo : crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getSrfSloIdos()) {

                HashMap<String, Object> dataDetaiSlo = new HashMap<>();
                dataDetaiSlo.put("id", "SLO" + srfSloIdo.getSrfSlo().getId());
                dataDetaiSlo.put("label", "SLO #" + srfSloIdo.getSrfSlo().getId());
                dataDetaiSlo.put("description", srfSloIdo.getSrfSlo().getDescription());
                dataDetaiSlo.put("order", new Integer(1));
                dataDetaiSlo.put("type", "SLO");


                if (relations.stream().filter(c -> c.get("id").equals(dataDetaiSlo.get("id")))
                  .collect(Collectors.toList()).isEmpty()) {
                  if (srfSlo == null) {
                    relations.add(dataDetaiSlo);
                  } else {
                    if (srfSlo.getId().equals(srfSloIdo.getSrfSlo().getId())) {
                      relations.add(dataDetaiSlo);
                    }
                  }

                }


              }
            }

          }
        }


        if (outcome != null) {
          int k = 1;
          Set<CrpClusterOfActivity> activities = new HashSet<>();
          for (CrpClusterKeyOutputOutcome crpClusterKeyOutput : crpProgramOutcome.getCrpClusterKeyOutputOutcomes()
            .stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
            HashMap<String, Object> dataDetailKeyOutput = new HashMap<>();
            dataDetailKeyOutput.put("id", "KO" + crpClusterKeyOutput.getCrpClusterKeyOutput().getId());

            dataDetailKeyOutput.put("label",
              "KeyOutput #" + this.getIndex(crpClusterKeyOutput.getCrpProgramOutcome().getCrpProgram(),
                crpClusterKeyOutput.getCrpClusterKeyOutput()));
            dataDetailKeyOutput.put("description", crpClusterKeyOutput.getCrpClusterKeyOutput().getKeyOutput());
            dataDetailKeyOutput.put("color", crpClusterKeyOutput.getCrpProgramOutcome().getCrpProgram().getColor());
            dataDetailKeyOutput.put("type", "KO");
            dataDetailKeyOutput.put("order", new Integer(7));
            dataDetailKeyOutput.put("order2", this.getIndex(crpClusterKeyOutput.getCrpProgramOutcome().getCrpProgram(),
              crpClusterKeyOutput.getCrpClusterKeyOutput()));

            if (relations.stream().filter(c -> c.get("id").equals(dataDetailKeyOutput.get("id")))
              .collect(Collectors.toList()).isEmpty()) {
              relations.add(dataDetailKeyOutput);
              activities.add(crpClusterKeyOutput.getCrpClusterKeyOutput().getCrpClusterOfActivity());
            }


            k++;

          }
          for (CrpClusterOfActivity crpClusterOfActivity : activities) {
            HashMap<String, Object> dataDetailOutcome = new HashMap<>();
            dataDetailOutcome.put("id", "C" + crpClusterOfActivity.getId());
            dataDetailOutcome.put("label", crpClusterOfActivity.getIdentifier());
            dataDetailOutcome.put("description", crpClusterOfActivity.getDescription());
            dataDetailOutcome.put("color", "#c0c0c0");
            dataDetailOutcome.put("type", "CoA");
            dataDetailOutcome.put("order", new Integer(6));
            if (relations.stream().filter(c -> c.get("id").equals(dataDetailOutcome.get("id")))
              .collect(Collectors.toList()).isEmpty()) {
              relations.add(dataDetailOutcome);
            }

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
        dataDetailOutcome.put("label", crpClusterOfActivity.getIdentifier());
        dataDetailOutcome.put("description", crpClusterOfActivity.getDescription());
        dataDetailOutcome.put("color", "#c0c0c0");
        dataDetailOutcome.put("type", "CoA");
        dataDetailOutcome.put("order", new Integer(6));


        relations.add(dataDetailOutcome);

        for (CrpClusterKeyOutput keyOutput : crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
          .filter(ko -> ko.isActive()).collect(Collectors.toList())) {
          HashMap<String, Object> dataDetailKeyOutput = new HashMap<>();
          dataDetailKeyOutput.put("id", "KO" + keyOutput.getId());

          dataDetailKeyOutput.put("label",
            "KeyOutput #" + this.getIndex(crpClusterOfActivity.getCrpProgram(), keyOutput));
          dataDetailKeyOutput.put("description", keyOutput.getKeyOutput());
          dataDetailKeyOutput.put("color", crpClusterOfActivity.getCrpProgram().getColor());
          dataDetailKeyOutput.put("type", "KO");
          dataDetailKeyOutput.put("order", new Integer(7));
          dataDetailKeyOutput.put("order2", this.getIndex(crpClusterOfActivity.getCrpProgram(), keyOutput));
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
    dataProgram.put("id", "F" + crpProgram.getId());
    dataProgram.put("label", crpProgram.getAcronym());
    dataProgram.put("description", crpProgram.getName());
    dataProgram.put("color", crpProgram.getColor());
    dataProgram.put("type", "F");
    dataProgram.put("order", new Integer(4));

    if (relations.stream().filter(c -> c.get("id").equals(dataProgram.get("id"))).collect(Collectors.toList())
      .isEmpty()) {
      relations.add(dataProgram);
    }


    int i = 1;


    int i1 = 1;
    int j = 1;
    for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {

      if (cluster == null
        || (cluster != null && crpClusterOfActivity.getId().longValue() == cluster.getId().longValue())) {


        HashMap<String, Object> dataDetailOutcome = new HashMap<>();
        dataDetailOutcome.put("id", "C" + crpClusterOfActivity.getId());
        dataDetailOutcome.put("label", crpClusterOfActivity.getIdentifier());
        dataDetailOutcome.put("description", crpClusterOfActivity.getDescription());
        dataDetailOutcome.put("color", "#c0c0c0");
        dataDetailOutcome.put("type", "CoA");

        dataDetailOutcome.put("order", new Integer(6));

        relations.add(dataDetailOutcome);

        for (CrpClusterKeyOutput keyOutput : crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
          .filter(ko -> ko.isActive()).collect(Collectors.toList())) {


          if (crpClusterKeyOutput == null || (crpClusterKeyOutput != null
            && crpClusterKeyOutput.getId().longValue() == keyOutput.getId().longValue())) {


            HashMap<String, Object> dataDetailKeyOutput = new HashMap<>();
            dataDetailKeyOutput.put("id", "KO" + keyOutput.getId());

            dataDetailKeyOutput.put("label",
              "KeyOutput #" + this.getIndex(crpClusterOfActivity.getCrpProgram(), keyOutput));
            dataDetailKeyOutput.put("description", keyOutput.getKeyOutput());
            dataDetailKeyOutput.put("color", crpClusterOfActivity.getCrpProgram().getColor());
            dataDetailKeyOutput.put("type", "KO");
            dataDetailKeyOutput.put("order", new Integer(7));
            dataDetailKeyOutput.put("order2", this.getIndex(crpClusterOfActivity.getCrpProgram(), keyOutput));
            j++;

            relations.add(dataDetailKeyOutput);
            for (CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome : keyOutput.getCrpClusterKeyOutputOutcomes()
              .stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
              CrpProgramOutcome crpProgramOutcome = crpClusterKeyOutputOutcome.getCrpProgramOutcome();


              HashMap<String, Object> datacrpProgramOutcome = new HashMap<>();
              datacrpProgramOutcome.put("id", "O" + crpProgramOutcome.getId());
              datacrpProgramOutcome.put("label",
                "Outcome #" + this.getIndex(crpProgramOutcome.getCrpProgram(), crpProgramOutcome));
              datacrpProgramOutcome.put("description", crpProgramOutcome.getDescription());
              datacrpProgramOutcome.put("color", crpProgramOutcome.getCrpProgram().getColor());
              datacrpProgramOutcome.put("order", new Integer(5));

              datacrpProgramOutcome.put("type", "O");

              if (relations.stream().filter(c -> c.get("id").equals(datacrpProgramOutcome.get("id")))
                .collect(Collectors.toList()).isEmpty()) {
                relations.add(datacrpProgramOutcome);
              }


              Set<SrfSubIdo> subIdos = new HashSet<>();

              for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcome.getCrpOutcomeSubIdos().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList())) {


                if (crpOutcomeSubIdo.getSrfSubIdo() != null && crpOutcomeSubIdo.getSrfSubIdo().isActive()
                  && !subIdos.contains(crpOutcomeSubIdo.getSrfSubIdo())) {
                  subIdos.add(crpOutcomeSubIdo.getSrfSubIdo());
                  HashMap<String, Object> dataDetaiSubIDO = new HashMap<>();
                  dataDetaiSubIDO.put("id", "SD" + crpOutcomeSubIdo.getSrfSubIdo().getId());
                  dataDetaiSubIDO.put("label", "Sub-IDO #" + crpOutcomeSubIdo.getSrfSubIdo().getId());
                  dataDetaiSubIDO.put("description", crpOutcomeSubIdo.getSrfSubIdo().getDescription());
                  dataDetaiSubIDO.put("order", new Integer(3));

                  dataDetaiSubIDO.put("type", "SD");
                  if (relations.stream().filter(c -> c.get("id").equals(dataDetaiSubIDO.get("id")))
                    .collect(Collectors.toList()).isEmpty()) {
                    relations.add(dataDetaiSubIDO);
                  }


                  HashMap<String, Object> dataDetaiSIDO = new HashMap<>();
                  dataDetaiSIDO.put("id", "IDO" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
                  if (crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().isIsCrossCutting()) {
                    dataDetaiSIDO.put("label",
                      "Cross-Cutting IDO #" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
                  } else {
                    dataDetaiSIDO.put("label", "IDO #" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
                  }
                  dataDetaiSIDO.put("description", crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getDescription());

                  dataDetaiSIDO.put("type", "IDO");
                  dataDetaiSIDO.put("order", new Integer(2));

                  if (relations.stream().filter(c -> c.get("id").equals(dataDetaiSIDO.get("id")))
                    .collect(Collectors.toList()).isEmpty()) {
                    relations.add(dataDetaiSIDO);
                  }


                  for (SrfSloIdo srfSloIdo : crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getSrfSloIdos()) {

                    HashMap<String, Object> dataDetaiSlo = new HashMap<>();
                    dataDetaiSlo.put("id", "SLO" + srfSloIdo.getSrfSlo().getId());
                    dataDetaiSlo.put("label", "SLO #" + srfSloIdo.getSrfSlo().getId());
                    dataDetaiSlo.put("description", srfSloIdo.getSrfSlo().getDescription());
                    dataDetaiSlo.put("order", new Integer(1));

                    dataDetaiSlo.put("type", "SLO");

                    if (relations.stream().filter(c -> c.get("id").equals(dataDetaiSlo.get("id")))
                      .collect(Collectors.toList()).isEmpty()) {
                      relations.add(dataDetaiSlo);
                    }


                  }
                }
              }
              i++;
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
    Set<CrpOutcomeSubIdo> outcomesSubIdos = new HashSet<>();

    switch (type) {
      case "F":
        CrpProgram crpProgram = crpProgramManager.getCrpProgramById(Long.parseLong(id.replace("F", "")));
        this.addRelations(crpProgram, null, null, null, null);

        break;

      case "O":
        CrpProgramOutcome crpProgramOutcome =
          crpProgramOutcomeManager.getCrpProgramOutcomeById(Long.parseLong(id.replace("O", "")));
        this.addRelations(crpProgramOutcome.getCrpProgram(), crpProgramOutcome, null, null, null);

        break;
      case "SD":

        SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoById(Long.parseLong(id.replace("SD", "")));
        for (CrpOutcomeSubIdo outcomeSubIdo : srfSubIdo.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          outcomesSubIdos.add(outcomeSubIdo);
          crpPrograms.add(outcomeSubIdo.getCrpProgramOutcome().getCrpProgram());
        }

        for (CrpProgram myProgram : crpPrograms) {
          if (flagshipId == null) {
            CrpOutcomeSubIdo subIdoOutcome = outcomesSubIdos.stream()
              .filter(c -> c.isActive()
                && c.getCrpProgramOutcome().getCrpProgram().getId().longValue() == myProgram.getId().longValue())
              .collect(Collectors.toList()).get(0);
            this.addRelations(myProgram, subIdoOutcome.getCrpProgramOutcome(), null, subIdoOutcome.getSrfSubIdo(),
              null);
          } else {
            if (Long.parseLong(flagshipId) == myProgram.getId().longValue()) {
              CrpOutcomeSubIdo subIdoOutcome = outcomesSubIdos.stream()
                .filter(c -> c.isActive()
                  && c.getCrpProgramOutcome().getCrpProgram().getId().longValue() == myProgram.getId().longValue())
                .collect(Collectors.toList()).get(0);
              this.addRelations(myProgram, subIdoOutcome.getCrpProgramOutcome(), null, subIdoOutcome.getSrfSubIdo(),
                null);
            }
          }

        }
        break;

      case "IDO":

        SrfIdo srfIdo = srfIdoManager.getSrfIdoById(Long.parseLong(id.replace("IDO", "")));
        for (SrfSubIdo srfSubIdoElement : srfIdo.getSrfSubIdos().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          for (CrpOutcomeSubIdo outcomeSubIdo : srfSubIdoElement.getCrpOutcomeSubIdos().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList())) {
            outcomesSubIdos.add(outcomeSubIdo);
            crpPrograms.add(outcomeSubIdo.getCrpProgramOutcome().getCrpProgram());
          }


        }

        for (CrpProgram myProgram : crpPrograms) {
          if (flagshipId == null) {
            List<CrpOutcomeSubIdo> subIdoOutcomes = outcomesSubIdos.stream()
              .filter(c -> c.isActive()
                && c.getCrpProgramOutcome().getCrpProgram().getId().longValue() == myProgram.getId().longValue())
              .collect(Collectors.toList());
            for (CrpOutcomeSubIdo crpOutcomeSubIdo : subIdoOutcomes) {
              this.addRelations(myProgram, crpOutcomeSubIdo.getCrpProgramOutcome(), null,
                crpOutcomeSubIdo.getSrfSubIdo(), srfIdo);
            }

          } else {
            if (Long.parseLong(flagshipId) == myProgram.getId().longValue()) {
              List<CrpOutcomeSubIdo> subIdoOutcomes = outcomesSubIdos.stream()
                .filter(c -> c.isActive()
                  && c.getCrpProgramOutcome().getCrpProgram().getId().longValue() == myProgram.getId().longValue())
                .collect(Collectors.toList());
              for (CrpOutcomeSubIdo crpOutcomeSubIdo : subIdoOutcomes) {
                this.addRelations(myProgram, crpOutcomeSubIdo.getCrpProgramOutcome(), null,
                  crpOutcomeSubIdo.getSrfSubIdo(), srfIdo);
              }
            }
          }

        }


        break;
      case "SLO":
        SrfSlo srfSlo = srfSloManager.getSrfSloById(Long.parseLong(id.replace("SLO", "")));

        for (SrfSloIdo srfSloIdo : srfSlo.getSrfSloIdos().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {

          for (SrfSubIdo srIdo : srfSloIdo.getSrfIdo().getSrfSubIdos().stream().filter(c -> c.isActive())
            .collect(Collectors.toList())) {
            srfSloIdo.getSrfIdo();
            for (CrpOutcomeSubIdo outcomeSubIdo : srIdo.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive())
              .collect(Collectors.toList())) {
              outcomesSubIdos.add(outcomeSubIdo);
              crpPrograms.add(outcomeSubIdo.getCrpProgramOutcome().getCrpProgram());
            }


          }
        }


        for (CrpProgram myProgram : crpPrograms) {
          if (flagshipId == null) {
            List<CrpOutcomeSubIdo> subIdoOutcomes = outcomesSubIdos.stream()
              .filter(c -> c.isActive()
                && c.getCrpProgramOutcome().getCrpProgram().getId().longValue() == myProgram.getId().longValue())
              .collect(Collectors.toList());
            for (CrpOutcomeSubIdo crpOutcomeSubIdo : subIdoOutcomes) {
              this.addRelations(myProgram, crpOutcomeSubIdo.getCrpProgramOutcome(), srfSlo, null, null);
            }

          } else {
            if (Long.parseLong(flagshipId) == myProgram.getId().longValue()) {
              List<CrpOutcomeSubIdo> subIdoOutcomes = outcomesSubIdos.stream()
                .filter(c -> c.isActive()
                  && c.getCrpProgramOutcome().getCrpProgram().getId().longValue() == myProgram.getId().longValue())
                .collect(Collectors.toList());
              for (CrpOutcomeSubIdo crpOutcomeSubIdo : subIdoOutcomes) {
                this.addRelations(myProgram, crpOutcomeSubIdo.getCrpProgramOutcome(), srfSlo, null, null);
              }
            }
          }

        }

        break;

      case "CoA":
        CrpClusterOfActivity crpClusterOfActivity =
          crpClusterOfActivityManager.getCrpClusterOfActivityById(Long.parseLong(id.replace("C", "")));
        this.addRelationsCluster(crpClusterOfActivity.getCrpProgram(), crpClusterOfActivity, null);

        break;

      case "KO":
        CrpClusterKeyOutput crpClusterKeyOutput =
          crpClusterKeyOutputManager.getCrpClusterKeyOutputById(Long.parseLong(id.replace("KO", "")));
        this.addRelationsCluster(crpClusterKeyOutput.getCrpClusterOfActivity().getCrpProgram(),
          crpClusterKeyOutput.getCrpClusterOfActivity(), crpClusterKeyOutput);

        break;
      default:
        break;
    }

    Collections.sort(relations, new Comparator<HashMap<String, Object>>() {

      @Override
      public int compare(HashMap<String, Object> one, HashMap<String, Object> two) {
        int compareTO = (one.get("order").toString().compareTo(two.get("order").toString()));

        if (compareTO == 0) {
          if (one.containsKey("order2") && two.containsKey("order2")) {
            return new Integer(one.get("order2").toString()).compareTo(new Integer(two.get("order2").toString()));
          } else {

            if (Integer.parseInt(one.get("order").toString()) == 3 || Integer.parseInt(one.get("order").toString()) == 1
              || Integer.parseInt(one.get("order").toString()) == 2) {
              return 0;
            }
            return one.get("label").toString().compareTo(two.get("label").toString());
          }

        } else {
          return compareTO;
        }
      }
    });

    return SUCCESS;


  }

  public int getIndex(CrpProgram crpProgram, CrpClusterKeyOutput crpClusterKeyOutput) {


    List<CrpClusterKeyOutput> crpClusterKeyOutputs = new ArrayList<>();
    for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {
      crpClusterKeyOutputs.addAll(
        crpClusterOfActivity.getCrpClusterKeyOutputs().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    }
    int index = crpClusterKeyOutputs.indexOf(crpClusterKeyOutput) + 1;
    // .indexOf(programOutcome) + 1;
    return index;
  }

  public int getIndex(CrpProgram crpProgram, CrpProgramOutcome programOutcome) {
    int index = crpProgram.getCrpProgramOutcomes().stream().filter(c -> c.isActive()).collect(Collectors.toList())
      .indexOf(programOutcome) + 1;
    return index;
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
      LOG.error("There was an exception trying to parse the   type = {} ",
        StringUtils.trim(((String[]) parameters.get(APConstants.TYPE))[0]));

    }


    try {
      flagshipId = (StringUtils.trim(((String[]) parameters.get(APConstants.FLAGSHIP_ID))[0]));
      if (flagshipId.isEmpty()) {
        flagshipId = null;
      }
    } catch (Exception e) {
      LOG.error("There was an exception trying to parse the   FLAGSHIP_ID = {} ",
        StringUtils.trim(((String[]) parameters.get(APConstants.FLAGSHIP_ID))[0]));
      flagshipId = null;
    }
  }

  public void setRelations(List<HashMap<String, Object>> relations) {
    this.relations = relations;
  }


}

