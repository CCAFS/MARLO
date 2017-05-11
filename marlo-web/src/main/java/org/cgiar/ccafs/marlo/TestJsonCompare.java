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

import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TestJsonCompare {


  public static void main(String[] args) {


    // BuildMyString.com generated code. Please enjoy your string responsibly.

    StringBuilder jsonNew = new StringBuilder();

    jsonNew.append("{");
    jsonNew.append("   \"test2\":true,  \"active\":true,");
    jsonNew.append("   \"activeSince\":\"Mar 8, 2017 8:14:40 AM\",");
    jsonNew.append("   \"cofinancing\":true,");
    jsonNew.append("   \"createDate\":\"Aug 23, 2014 12:54:43 PM\",");
    jsonNew.append("   \"createdBy\":{");
    jsonNew.append("      \"id\":7,");
    jsonNew.append("      \"firstName\":\"Philip\",");
    jsonNew.append("      \"lastName\":\"Thornton\",");
    jsonNew.append("      \"username\":\"pthornton\",");
    jsonNew.append("      \"email\":\"p.thornton@cgiar.org\",");
    jsonNew.append("      \"password\":\"202cb962ac59075b964b07152d234b70\",");
    jsonNew.append("      \"cgiarUser\":false");
    jsonNew.append("   },");
    jsonNew.append("   \"crossCuttingCapacity\":true,");
    jsonNew.append("   \"crossCuttingGender\":true,");
    jsonNew.append("   \"crossCuttingNa\":false,");
    jsonNew.append("   \"reporting\":true,");
    jsonNew.append("   \"crossCuttingYouth\":false,");
    jsonNew.append("   \"crp\":{");
    jsonNew.append("      \"id\":1,");
    jsonNew.append("      \"name\":\"CCAFS\",");
    jsonNew.append("      \"acronym\":\"ccafs\",");
    jsonNew.append("      \"active\":true,");
    jsonNew.append("      \"marlo\":true,");
    jsonNew.append("      \"activeSince\":\"Jun 7, 2016 2:08:48 PM\",");
    jsonNew.append("      \"modifiedBy\":{");
    jsonNew.append("         \"cgiarUser\":false");
    jsonNew.append("      },");
    jsonNew.append("      \"modificationJustification\":\"\"");
    jsonNew.append("   },");
    jsonNew.append("   \"dimension\":\"\",");
    jsonNew.append("   \"endDate\":\"Dec 31, 2017 12:00:00 AM\",");
    jsonNew.append(
      "   \"genderAnalysis\":\"The gender dimensions of vulnerability and climate impacts are fundamental components of the CCAFS programme and to understand vulnerability reduction and livelihood security from a gender perspective, there are several issues of relevance to CCAFS activities, including a recognising that relations of power and inequality mediate access to resources.\",");
    jsonNew.append("   \"id\":1,");
    jsonNew.append("   \"liaisonInstitution\":{");
    jsonNew.append("      \"id\":5,");
    jsonNew.append("      \"crpProgram\":{");
    jsonNew.append("         \"id\":87,");
    jsonNew.append("         \"crp\":{");
    jsonNew.append("            \"id\":1,");
    jsonNew.append("            \"name\":\"CCAFS\",");
    jsonNew.append("            \"acronym\":\"ccafs\",");
    jsonNew.append("            \"active\":true,");
    jsonNew.append("            \"marlo\":true,");
    jsonNew.append("            \"activeSince\":\"Jun 7, 2016 2:08:48 PM\",");
    jsonNew.append("            \"modifiedBy\":{");
    jsonNew.append("               \"cgiarUser\":false");
    jsonNew.append("            },");
    jsonNew.append("            \"modificationJustification\":\"\"");
    jsonNew.append("         },");
    jsonNew.append("         \"name\":\"Priorities and Policies for CSA\",");
    jsonNew.append("         \"acronym\":\"F1 (before F4 - Philip)\",");
    jsonNew.append("         \"programType\":1,");
    jsonNew.append("         \"active\":true,");
    jsonNew.append("         \"createdBy\":{");
    jsonNew.append("            \"cgiarUser\":false");
    jsonNew.append("         },");
    jsonNew.append("         \"activeSince\":\"Nov 30, 2016 4:08:18 AM\",");
    jsonNew.append("         \"modifiedBy\":{");
    jsonNew.append("            \"cgiarUser\":false");
    jsonNew.append("         },");
    jsonNew.append("         \"modificationJustification\":\"\",");
    jsonNew.append("         \"color\":\"#4B91D7\"");
    jsonNew.append("      },");
    jsonNew.append("      \"institution\":{");
    jsonNew.append("         \"id\":66,");
    jsonNew.append("         \"institutionType\":{");
    jsonNew.append("            \"id\":3,");
    jsonNew.append("            \"name\":\"CGIAR Center\",");
    jsonNew.append("            \"acronym\":\"CG\"");
    jsonNew.append("         },");
    jsonNew.append("         \"name\":\"International Livestock Research Institute\",");
    jsonNew.append("         \"locElement\":{");
    jsonNew.append("            \"id\":113,");
    jsonNew.append("            \"isoAlpha2\":\"KE\",");
    jsonNew.append("            \"isoNumeric\":404,");
    jsonNew.append("            \"locElementType\":{");
    jsonNew.append("               \"active\":false,");
    jsonNew.append("               \"scope\":false");
    jsonNew.append("            },");
    jsonNew.append("            \"name\":\"Kenya\",");
    jsonNew.append("            \"locElement\":{");
    jsonNew.append("               \"active\":false");
    jsonNew.append("            },");
    jsonNew.append("            \"isSiteIntegration\":true,");
    jsonNew.append("            \"active\":true,");
    jsonNew.append("            \"activeSince\":\"Jun 24, 2016 5:17:09 AM\",");
    jsonNew.append("            \"modifiedBy\":{");
    jsonNew.append("               \"cgiarUser\":false");
    jsonNew.append("            },");
    jsonNew.append("            \"modificationJustification\":\"\"");
    jsonNew.append("         },");
    jsonNew.append("         \"acronym\":\"ILRI\",");
    jsonNew.append("         \"city\":\"Nairobi\",");
    jsonNew.append("         \"websiteLink\":\"https://www.ilri.org/\",");
    jsonNew.append("         \"added\":\"Jun 20, 2016 7:50:12 AM\"");
    jsonNew.append("      },");
    jsonNew.append("      \"name\":\"Flagship 1\",");
    jsonNew.append("      \"acronym\":\"F1\",");
    jsonNew.append("      \"crp\":{");
    jsonNew.append("         \"id\":1,");
    jsonNew.append("         \"name\":\"CCAFS\",");
    jsonNew.append("         \"acronym\":\"ccafs\",");
    jsonNew.append("         \"active\":true,");
    jsonNew.append("         \"marlo\":true,");
    jsonNew.append("         \"activeSince\":\"Jun 7, 2016 2:08:48 PM\",");
    jsonNew.append("         \"modifiedBy\":{");
    jsonNew.append("            \"cgiarUser\":false");
    jsonNew.append("         },");
    jsonNew.append("         \"modificationJustification\":\"\"");
    jsonNew.append("      },");
    jsonNew.append("      \"active\":true");
    jsonNew.append("   },");
    jsonNew.append("   \"liaisonUser\":{");
    jsonNew.append("      \"id\":9,");
    jsonNew.append("      \"liaisonInstitution\":{");
    jsonNew.append("         \"id\":5,");
    jsonNew.append("         \"crpProgram\":{");
    jsonNew.append("            \"id\":87,");
    jsonNew.append("            \"crp\":{");
    jsonNew.append("               \"active\":false,");
    jsonNew.append("               \"marlo\":false");
    jsonNew.append("            },");
    jsonNew.append("            \"name\":\"Priorities and Policies for CSA\",");
    jsonNew.append("            \"acronym\":\"F1 (before F4 - Philip)\",");
    jsonNew.append("            \"programType\":1,");
    jsonNew.append("            \"active\":true,");
    jsonNew.append("            \"createdBy\":{");
    jsonNew.append("               \"cgiarUser\":false");
    jsonNew.append("            },");
    jsonNew.append("            \"activeSince\":\"Nov 30, 2016 4:08:18 AM\",");
    jsonNew.append("            \"modifiedBy\":{");
    jsonNew.append("               \"cgiarUser\":false");
    jsonNew.append("            },");
    jsonNew.append("            \"modificationJustification\":\"\",");
    jsonNew.append("            \"color\":\"#4B91D7\"");
    jsonNew.append("         },");
    jsonNew.append("         \"institution\":{");
    jsonNew.append("            \"id\":66,");
    jsonNew.append("            \"institutionType\":{");
    jsonNew.append("            },");
    jsonNew.append("            \"name\":\"International Livestock Research Institute\",");
    jsonNew.append("            \"locElement\":{");
    jsonNew.append("               \"active\":false");
    jsonNew.append("            },");
    jsonNew.append("            \"acronym\":\"ILRI\",");
    jsonNew.append("            \"city\":\"Nairobi\",");
    jsonNew.append("            \"websiteLink\":\"https://www.ilri.org/\",");
    jsonNew.append("            \"added\":\"Jun 20, 2016 7:50:12 AM\"");
    jsonNew.append("         },");
    jsonNew.append("         \"name\":\"Flagship 1\",");
    jsonNew.append("         \"acronym\":\"F1\",");
    jsonNew.append("         \"crp\":{");
    jsonNew.append("            \"id\":1,");
    jsonNew.append("            \"name\":\"CCAFS\",");
    jsonNew.append("            \"acronym\":\"ccafs\",");
    jsonNew.append("            \"active\":true,");
    jsonNew.append("            \"marlo\":true,");
    jsonNew.append("            \"activeSince\":\"Jun 7, 2016 2:08:48 PM\",");
    jsonNew.append("            \"modifiedBy\":{");
    jsonNew.append("               \"cgiarUser\":false");
    jsonNew.append("            },");
    jsonNew.append("            \"modificationJustification\":\"\"");
    jsonNew.append("         },");
    jsonNew.append("         \"active\":true");
    jsonNew.append("      },");
    jsonNew.append("      \"user\":{");
    jsonNew.append("         \"id\":7,");
    jsonNew.append("         \"firstName\":\"Philip\",");
    jsonNew.append("         \"lastName\":\"Thornton\",");
    jsonNew.append("         \"username\":\"pthornton\",");
    jsonNew.append("         \"email\":\"p.thornton@cgiar.org\",");
    jsonNew.append("         \"password\":\"202cb962ac59075b964b07152d234b70\",");
    jsonNew.append("         \"cgiarUser\":false");
    jsonNew.append("      },");
    jsonNew.append("      \"crp\":{");
    jsonNew.append("         \"id\":1,");
    jsonNew.append("         \"name\":\"CCAFS\",");
    jsonNew.append("         \"acronym\":\"ccafs\",");
    jsonNew.append("         \"active\":true,");
    jsonNew.append("         \"marlo\":true,");
    jsonNew.append("         \"activeSince\":\"Jun 7, 2016 2:08:48 PM\",");
    jsonNew.append("         \"modifiedBy\":{");
    jsonNew.append("            \"cgiarUser\":false");
    jsonNew.append("         },");
    jsonNew.append("         \"modificationJustification\":\"\"");
    jsonNew.append("      },");
    jsonNew.append("      \"active\":true");
    jsonNew.append("   },");
    jsonNew.append("   \"locationGlobal\":false,");
    jsonNew.append("   \"modificationJustification\":\"\",");
    jsonNew.append("   \"modifiedBy\":{");
    jsonNew.append("      \"id\":844,");
    jsonNew.append("      \"firstName\":\"Christian\",");
    jsonNew.append("      \"lastName\":\"Garcia\",");
    jsonNew.append("      \"username\":\"cdgarcia\",");
    jsonNew.append("      \"email\":\"c.d.garcia@cgiar.org\",");
    jsonNew.append("      \"password\":\"202cb962ac59075b964b07152d234b70\",");
    jsonNew.append("      \"cgiarUser\":false");
    jsonNew.append("   },");
    jsonNew.append("   \"noRegional\":false,");
    jsonNew.append("   \"presetDate\":\"Jan 16, 2017 11:35:41 AM\",");
    jsonNew.append("   \"projectEditLeader\":true,");
    jsonNew.append("   \"administrative\":false,");
    jsonNew.append("   \"scale\":0,");
    jsonNew.append("   \"startDate\":\"Mar 1, 2014 12:00:00 AM\",");
    jsonNew.append("   \"status\":2,");
    jsonNew.append(
      "   \"summary\":\"Through its regional scenario process and the set-up of national science-policy exchange platforms, CCAFS-WA has engaged with regional and national institutions in charge of planning agricultural development and food security. The CCAFS national science-policy exchange platforms will form the backbone for a top-down and bottom-up mainstreaming of climate change into national development frameworks by 1/ catalyzing multi-scale, participatory identification of priorities and knowledge gaps using appropriate tools and approaches to define priority investments, and 2/ learning from participatory action research in selected districts of three pilot countries, while constituent communities benefit from technical and political support from the national platforms. This systemic framework for integrated climate impact assessments and adaptation planning will produce site-specific contextual insights and scalable evidences to guide policy design and decision-making processes. This will rapidly increase the adaptive capacity of people and institutions across scales, allowing for intelligently targeted investments in agriculture and food security sectors.\\r\\n\\r\\nc\",");
    jsonNew.append(
      "   \"title\":\"(ICRISAT WA) Capacitating science-policy exchange platforms to mainstream climate change into national agricultural and food security policy plans b\",");
    jsonNew.append("   \"type\":\"CCAFS_COFUNDED\"");
    jsonNew.append("}");


    // BuildMyString.com generated code. Please enjoy your string responsibly.

    StringBuilder jsonOlder = new StringBuilder();

    jsonOlder.append("{");
    jsonOlder.append("   \"active\":true,");
    jsonOlder.append("   \"activeSince\":\"Mar 8, 2017 8:14:29 AM\",");
    jsonOlder.append("   \"cofinancing\":true,");
    jsonOlder.append("   \"createDate\":\"Aug 23, 2014 12:54:43 PM\",");
    jsonOlder.append("   \"createdBy\":{");
    jsonOlder.append("      \"id\":7,");
    jsonOlder.append("      \"firstName\":\"Philip\",");
    jsonOlder.append("      \"lastName\":\"Thornton\",");
    jsonOlder.append("      \"username\":\"pthornton\",");
    jsonOlder.append("      \"email\":\"p.thornton@cgiar.org\",");
    jsonOlder.append("      \"password\":\"202cb962ac59075b964b07152d234b70\",");
    jsonOlder.append("      \"cgiarUser\":false");
    jsonOlder.append("   },");
    jsonOlder.append("   \"crossCuttingCapacity\":true,");
    jsonOlder.append("   \"crossCuttingGender\":true,");
    jsonOlder.append("   \"crossCuttingNa\":false,");
    jsonOlder.append("   \"reporting\":true,");
    jsonOlder.append("   \"crossCuttingYouth\":false,");
    jsonOlder.append("   \"crp\":{");
    jsonOlder.append("      \"id\":1,");
    jsonOlder.append("      \"name\":\"CCAFS\",");
    jsonOlder.append("      \"acronym\":\"ccafs\",");
    jsonOlder.append("      \"active\":true,");
    jsonOlder.append("      \"marlo\":true,");
    jsonOlder.append("      \"activeSince\":\"Jun 7, 2016 2:08:48 PM\",");
    jsonOlder.append("      \"modifiedBy\":{");
    jsonOlder.append("         \"cgiarUser\":false");
    jsonOlder.append("      },");
    jsonOlder.append("      \"modificationJustification\":\"\"");
    jsonOlder.append("   },");
    jsonOlder.append("   \"dimension\":\"\",");
    jsonOlder.append("   \"endDate\":\"Dec 31, 2017 12:00:00 AM\",");
    jsonOlder.append(
      "   \"genderAnalysis\":\"The gender dimensions of vulnerability and climate impacts are fundamental components of the CCAFS programme and to understand vulnerability reduction and livelihood security from a gender perspective, there are several issues of relevance to CCAFS activities, including a recognising that relations of power and inequality mediate access to resources.\",");
    jsonOlder.append("   \"id\":1,");
    jsonOlder.append("   \"liaisonInstitution\":{");
    jsonOlder.append("      \"id\":5,");
    jsonOlder.append("      \"crpProgram\":{");
    jsonOlder.append("         \"id\":87,");
    jsonOlder.append("         \"crp\":{");
    jsonOlder.append("            \"id\":1,");
    jsonOlder.append("            \"name\":\"CCAFS\",");
    jsonOlder.append("            \"acronym\":\"ccafs\",");
    jsonOlder.append("            \"active\":true,");
    jsonOlder.append("            \"marlo\":true,");
    jsonOlder.append("            \"activeSince\":\"Jun 7, 2016 2:08:48 PM\",");
    jsonOlder.append("            \"modifiedBy\":{");
    jsonOlder.append("               \"cgiarUser\":false");
    jsonOlder.append("            },");
    jsonOlder.append("            \"modificationJustification\":\"\"");
    jsonOlder.append("         },");
    jsonOlder.append("         \"name\":\"Priorities and Policies for CSA\",");
    jsonOlder.append("         \"acronym\":\"F1 (before F4 - Philip)\",");
    jsonOlder.append("         \"programType\":1,");
    jsonOlder.append("         \"active\":true,");
    jsonOlder.append("         \"createdBy\":{");
    jsonOlder.append("            \"cgiarUser\":false");
    jsonOlder.append("         },");
    jsonOlder.append("         \"activeSince\":\"Nov 30, 2016 4:08:18 AM\",");
    jsonOlder.append("         \"modifiedBy\":{");
    jsonOlder.append("            \"cgiarUser\":false");
    jsonOlder.append("         },");
    jsonOlder.append("         \"modificationJustification\":\"\",");
    jsonOlder.append("         \"color\":\"#4B91D7\"");
    jsonOlder.append("      },");
    jsonOlder.append("      \"institution\":{");
    jsonOlder.append("         \"id\":66,");
    jsonOlder.append("         \"institutionType\":{");
    jsonOlder.append("            \"id\":3,");
    jsonOlder.append("            \"name\":\"CGIAR Center\",");
    jsonOlder.append("            \"acronym\":\"CG\"");
    jsonOlder.append("         },");
    jsonOlder.append("         \"name\":\"International Livestock Research Institute\",");
    jsonOlder.append("         \"locElement\":{");
    jsonOlder.append("            \"id\":113,");
    jsonOlder.append("            \"isoAlpha2\":\"KE\",");
    jsonOlder.append("            \"isoNumeric\":404,");
    jsonOlder.append("            \"locElementType\":{");
    jsonOlder.append("               \"active\":false,");
    jsonOlder.append("               \"scope\":false");
    jsonOlder.append("            },");
    jsonOlder.append("            \"name\":\"Kenya\",");
    jsonOlder.append("            \"locElement\":{");
    jsonOlder.append("               \"active\":false");
    jsonOlder.append("            },");
    jsonOlder.append("            \"isSiteIntegration\":true,");
    jsonOlder.append("            \"active\":true,");
    jsonOlder.append("            \"activeSince\":\"Jun 24, 2016 5:17:09 AM\",");
    jsonOlder.append("            \"modifiedBy\":{");
    jsonOlder.append("               \"cgiarUser\":false");
    jsonOlder.append("            },");
    jsonOlder.append("            \"modificationJustification\":\"\"");
    jsonOlder.append("         },");
    jsonOlder.append("         \"acronym\":\"ILRI\",");
    jsonOlder.append("         \"city\":\"Nairobi\",");
    jsonOlder.append("         \"websiteLink\":\"https://www.ilri.org/\",");
    jsonOlder.append("         \"added\":\"Jun 20, 2016 7:50:12 AM\"");
    jsonOlder.append("      },");
    jsonOlder.append("      \"name\":\"Flagship 1\",");
    jsonOlder.append("      \"acronym\":\"F1\",");
    jsonOlder.append("      \"crp\":{");
    jsonOlder.append("         \"id\":1,");
    jsonOlder.append("         \"name\":\"CCAFS\",");
    jsonOlder.append("         \"acronym\":\"ccafs\",");
    jsonOlder.append("         \"active\":true,");
    jsonOlder.append("         \"marlo\":true,");
    jsonOlder.append("         \"activeSince\":\"Jun 7, 2016 2:08:48 PM\",");
    jsonOlder.append("         \"modifiedBy\":{");
    jsonOlder.append("            \"cgiarUser\":false");
    jsonOlder.append("         },");
    jsonOlder.append("         \"modificationJustification\":\"\"");
    jsonOlder.append("      },");
    jsonOlder.append("      \"active\":true");
    jsonOlder.append("   },");
    jsonOlder.append("   \"liaisonUser\":{");
    jsonOlder.append("      \"id\":9,");
    jsonOlder.append("      \"liaisonInstitution\":{");
    jsonOlder.append("         \"id\":5,");
    jsonOlder.append("         \"crpProgram\":{");
    jsonOlder.append("            \"id\":87,");
    jsonOlder.append("            \"crp\":{");
    jsonOlder.append("               \"active\":false,");
    jsonOlder.append("               \"marlo\":false");
    jsonOlder.append("            },");
    jsonOlder.append("            \"name\":\"Priorities and Policies for CSA\",");
    jsonOlder.append("            \"acronym\":\"F1 (before F4 - Philip)\",");
    jsonOlder.append("            \"programType\":1,");
    jsonOlder.append("            \"active\":true,");
    jsonOlder.append("            \"createdBy\":{");
    jsonOlder.append("               \"cgiarUser\":false");
    jsonOlder.append("            },");
    jsonOlder.append("            \"activeSince\":\"Nov 30, 2016 4:08:18 AM\",");
    jsonOlder.append("            \"modifiedBy\":{");
    jsonOlder.append("               \"cgiarUser\":false");
    jsonOlder.append("            },");
    jsonOlder.append("            \"modificationJustification\":\"\",");
    jsonOlder.append("            \"color\":\"#4B91D7\"");
    jsonOlder.append("         },");
    jsonOlder.append("         \"institution\":{");
    jsonOlder.append("            \"id\":66,");
    jsonOlder.append("            \"institutionType\":{");
    jsonOlder.append("            },");
    jsonOlder.append("            \"name\":\"International Livestock Research Institute\",");
    jsonOlder.append("            \"locElement\":{");
    jsonOlder.append("               \"active\":false");
    jsonOlder.append("            },");
    jsonOlder.append("            \"acronym\":\"ILRI\",");
    jsonOlder.append("            \"city\":\"Nairobi\",");
    jsonOlder.append("            \"websiteLink\":\"https://www.ilri.org/\",");
    jsonOlder.append("            \"added\":\"Jun 20, 2016 7:50:12 AM\"");
    jsonOlder.append("         },");
    jsonOlder.append("         \"name\":\"Flagship 1\",");
    jsonOlder.append("         \"acronym\":\"F1\",");
    jsonOlder.append("         \"crp\":{");
    jsonOlder.append("            \"id\":1,");
    jsonOlder.append("            \"name\":\"CCAFS\",");
    jsonOlder.append("            \"acronym\":\"ccafs\",");
    jsonOlder.append("            \"active\":true,");
    jsonOlder.append("            \"marlo\":true,");
    jsonOlder.append("            \"activeSince\":\"Jun 7, 2016 2:08:48 PM\",");
    jsonOlder.append("            \"modifiedBy\":{");
    jsonOlder.append("               \"cgiarUser\":false");
    jsonOlder.append("            },");
    jsonOlder.append("            \"modificationJustification\":\"\"");
    jsonOlder.append("         },");
    jsonOlder.append("         \"active\":true");
    jsonOlder.append("      },");
    jsonOlder.append("      \"user\":{");
    jsonOlder.append("         \"id\":7,");
    jsonOlder.append("         \"firstName\":\"Philip\",");
    jsonOlder.append("         \"lastName\":\"Thornton\",");
    jsonOlder.append("         \"username\":\"pthornton\",");
    jsonOlder.append("         \"email\":\"p.thornton@cgiar.org\",");
    jsonOlder.append("         \"password\":\"202cb962ac59075b964b07152d234b70\",");
    jsonOlder.append("         \"cgiarUser\":false");
    jsonOlder.append("      },");
    jsonOlder.append("      \"crp\":{");
    jsonOlder.append("         \"id\":1,");
    jsonOlder.append("         \"name\":\"CCAFS\",");
    jsonOlder.append("         \"acronym\":\"ccafs\",");
    jsonOlder.append("         \"active\":true,");
    jsonOlder.append("         \"marlo\":true,");
    jsonOlder.append("         \"activeSince\":\"Jun 7, 2016 2:08:48 PM\",");
    jsonOlder.append("         \"modifiedBy\":{");
    jsonOlder.append("            \"cgiarUser\":false");
    jsonOlder.append("         },");
    jsonOlder.append("         \"modificationJustification\":\"\"");
    jsonOlder.append("      },");
    jsonOlder.append("      \"active\":true");
    jsonOlder.append("   },");
    jsonOlder.append("   \"locationGlobal\":false,");
    jsonOlder.append("   \"modificationJustification\":\"\",");
    jsonOlder.append("   \"modifiedBy\":{");
    jsonOlder.append("      \"id\":844,");
    jsonOlder.append("      \"firstName\":\"Christian\",");
    jsonOlder.append("      \"lastName\":\"Garcia\",");
    jsonOlder.append("      \"username\":\"cdgarcia\",");
    jsonOlder.append("      \"email\":\"c.d.garcia@cgiar.org\",");
    jsonOlder.append("      \"password\":\"202cb962ac59075b964b07152d234b70\",");
    jsonOlder.append("      \"cgiarUser\":false");
    jsonOlder.append("   },");
    jsonOlder.append("   \"noRegional\":false,");
    jsonOlder.append("   \"presetDate\":\"Jan 16, 2017 11:35:41 AM\",");
    jsonOlder.append("   \"projectEditLeader\":true,");
    jsonOlder.append("   \"administrative\":false,");
    jsonOlder.append("   \"scale\":0,");
    jsonOlder.append("   \"startDate\":\"Mar 1, 2014 12:00:00 AM\",");
    jsonOlder.append("   \"status\":2,");
    jsonOlder.append(
      "   \"summary\":\"Through its regional scenario process and the set-up of national science-policy exchange platforms, CCAFS-WA has engaged with regional and national institutions in charge of planning agricultural development and food security. The CCAFS national science-policy exchange platforms will form the backbone for a top-down and bottom-up mainstreaming of climate change into national development frameworks by 1/ catalyzing multi-scale, participatory identification of priorities and knowledge gaps using appropriate tools and approaches to define priority investments, and 2/ learning from participatory action research in selected districts of three pilot countries, while constituent communities benefit from technical and political support from the national platforms. This systemic framework for integrated climate impact assessments and adaptation planning will produce site-specific contextual insights and scalable evidences to guide policy design and decision-making processes. This will rapidly increase the adaptive capacity of people and institutions across scales, allowing for intelligently targeted investments in agriculture and food security sectors.\\r\\n\\r\\na\",");
    jsonOlder.append(
      "   \"title\":\"(ICRISAT WA) Capacitating science-policy exchange platforms to mainstream climate change into national agricultural and food security policy plans a\",");
    jsonOlder.append("   \"type\":\"CCAFS_COFUNDED\"");
    jsonOlder.append("}");

    Gson g = new Gson();
    Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();
    Map<String, Object> firstMap = g.fromJson(jsonNew.toString(), mapType);
    Map<String, Object> secondMap = g.fromJson(jsonOlder.toString(), mapType);
    MapDifference<String, Object> comparable = Maps.difference(firstMap, secondMap);
    Map<String, ValueDifference<Object>> diferences = comparable.entriesDiffering();
    Map<String, Object> diferencesLeft = comparable.entriesOnlyOnLeft();
    diferences.forEach((k, v) -> System.out.println("Key: " + k));
    diferencesLeft.forEach((k, v) -> System.out.println("Key: " + k));

    Class<?> c = (new Project()).getClass();
    Field[] fields = c.getDeclaredFields();

    for (Field field : fields) {


      try {
        ParameterizedType integerListType = (ParameterizedType) field.getGenericType();
        Class<?> integerListClass = (Class<?>) integerListType.getActualTypeArguments()[0];
        if (integerListClass.equals(ProjectClusterActivity.class) && field.getType().getSimpleName().equals("List")) {
          System.out.println(field.getName());
        }

      } catch (Exception e) {

      }
    }


  }
}
