package com.devonfw.cobigen.jsonplugin.matcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minidev.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.to.MatcherTo;
import com.devonfw.cobigen.api.to.VariableAssignmentTo;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class JsonMatcher implements MatcherInterpreter {

  /** Assigning logger to XmlClassMatcher */
  private static final Logger LOG = LoggerFactory.getLogger(JsonMatcher.class);

  /** Currently supported matcher types */
  private enum MatcherType {
    /** Should match if input is a JSON */
    JSONPATH
  }

  /** Available variable types for the matcher */
  private enum VariableType {
    /** Constant variable assignment */
    CONSTANT,
    /** Should match if input is a JSON */
    JSONPATH
  }

  @Override
  public boolean matches(MatcherTo matcher) {

    if (matcher.getType().toUpperCase().equals(MatcherType.JSONPATH.toString())) {
      if (matcher.getTarget() instanceof JsonObject) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Map<String, String> resolveVariables(MatcherTo matcher, List<VariableAssignmentTo> variableAssignments)
      throws InvalidConfigurationException {

    Map<String, String> resolvedVariables = new HashMap<String, String>();

    if (matcher.getTarget() instanceof JsonObject) {
      JsonObject jsonObject = (JsonObject) matcher.getTarget();
      String jsonString = jsonObject.toString();

      JsonPath nodeJsonPath = JsonPath.compile(matcher.getValue());

      Map<String, Object> nodeJsonPathResults = nodeJsonPath.read(jsonString);

      for (Entry<String, Object> nodeJsonPathResult : nodeJsonPathResults.entrySet()) {
        for (VariableAssignmentTo variableAssignmentTo : variableAssignments) {

          if (variableAssignmentTo.getType().toUpperCase().equals(VariableType.JSONPATH.toString())) {

            LOG.debug("Found json result {}", nodeJsonPathResult);

            String jsonElementString = "";
            String jsonPathExpression = variableAssignmentTo.getValue();

            if (nodeJsonPathResult.getValue() instanceof JSONArray) {
              JSONArray jsonArray = (JSONArray) nodeJsonPathResult.getValue();
              jsonElementString = jsonArray.toJSONString();

            } else if (nodeJsonPathResult.getValue() instanceof JsonObject) {
              JsonObject jsonElement = (JsonObject) nodeJsonPathResult.getValue();
              jsonElementString = jsonElement.toString();
            } else {
              // throw new InvalidConfigurationException(variableAssignmentTo.getValue(),
              // "Is not a json element: " + jsonPathResult, null);
            }

            String matchedString = matchJsonPath(jsonElementString, jsonPathExpression);
            if (matchedString != null) {
              resolvedVariables.put(variableAssignmentTo.getVarName(), matchedString);
            }

            // if (variableAssignmentTo.getValue().equals(jsonPathResult.getKey())) {
            // JsonObject jsonElementObject = (JsonObject) jsonPathResult.getValue();
            // resolvedVariables.put(variableAssignmentTo.getVarName(), String.valueOf(jsonElementObject.toString()));
            // }
          } else if (variableAssignmentTo.getType().toUpperCase().equals(VariableType.CONSTANT.toString())) {

            if (variableAssignmentTo.getValue().equals(nodeJsonPathResult.getKey())) {
              resolvedVariables.put(variableAssignmentTo.getVarName(), String.valueOf(nodeJsonPathResult.getValue()));
            }
          } // else throw exception
        }
      }

    }
    return resolvedVariables;
  }

  private String matchJsonPath(String jsonString, String jsonPathExpression) {

    if (jsonString.isBlank()) {
      return null;
    }

    try {

      Object readResult = JsonPath.read(jsonString, jsonPathExpression);
      if (readResult instanceof Map) {

        Map<String, Object> readMap = (Map<String, Object>) readResult;
        String matchedString = null;
        if (readMap.entrySet().size() > 1) {
          throw new InvalidConfigurationException(jsonPathExpression,
              "JSONPATH expression returns multiple values for variable assignment" + readMap.entrySet(), null);
        } else if (readMap.entrySet().size() == 1) {

          Set<Entry<String, Object>> entrySet = readMap.entrySet();
          Entry<String, Object> element = entrySet.iterator().next();
          matchedString = String.valueOf(element.getValue());
          return matchedString;
        }
      } else {
        if (!String.valueOf(readResult).isBlank()) {
          return String.valueOf(readResult);
        }
      }
    } catch (PathNotFoundException e) {
      return null;
    } catch (Exception e) {
      throw new CobiGenRuntimeException("An error occured during JSON Path matching for json string " + jsonString
          + " and expression " + jsonPathExpression, e);
    }
    return null;
  }

}
