package com.devonfw.cobigen.jsonplugin;

import com.devonfw.cobigen.api.annotation.ReaderPriority;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.extension.Priority;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.jsonplugin.inputreader.JSONInputReader;
import com.devonfw.cobigen.jsonplugin.matcher.JsonMatcher;

/**
 * {@link TriggerInterpreter} implementation of a Xml Interpreter
 */
@ReaderPriority(Priority.LOW)
public class JsonTriggerInterpreter implements TriggerInterpreter {

  /**
   * {@link TriggerInterpreter} type to be registered
   */
  public String type;

  /**
   * creates a new {@link JsonTriggerInterpreter}
   *
   * @param type to be registered
   */
  public JsonTriggerInterpreter(String type) {

    super();
    this.type = type;
  }

  @Override
  public String getType() {

    return this.type;
  }

  @Override
  public InputReader getInputReader() {

    return new JSONInputReader();
  }

  @Override
  public MatcherInterpreter getMatcher() {

    return new JsonMatcher();
  }

}
