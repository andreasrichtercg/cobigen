package com.devonfw.cobigen.jsonplugin.inputreader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.extension.InputReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class JSONInputReader implements InputReader {

  /** Valid file extension */
  public static final String VALID_EXTENSION = "json";

  @Override
  public boolean isValidInput(Object input) {

    if (input instanceof JsonObject) {
      return true;
    }
    return false;
  }

  @Override
  public Map<String, Object> createModel(Object input) {

    HashMap<String, Object> model = new HashMap<String, Object>();

    if (input instanceof JsonObject) {
      JsonObject jsonDoc = (JsonObject) input;
      model.put("json", fillModel(jsonDoc, model));
    }
    return model;
  }

  private Object fillModel(JsonElement jsonElement, Object parentModel) {

    if (jsonElement.isJsonObject()) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      return fillModel(jsonObject, parentModel);
    } else if (jsonElement.isJsonArray()) {
      JsonArray jsonArray = jsonElement.getAsJsonArray();
      return fillModel(jsonArray, parentModel);
    } else if (jsonElement.isJsonPrimitive()) {
      JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
      return fillModel(jsonPrimitive);
    }
    return null;
  }

  private Map<String, Object> fillModel(JsonObject jsonObject, Object parentModel) {

    HashMap<String, Object> model = new HashMap<String, Object>();
    model.put("_parent", parentModel);

    Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
    for (Entry<String, JsonElement> entry : entrySet) {
      model.put(entry.getKey(), fillModel(entry.getValue(), model));
    }
    return model;
  }

  private String fillModel(JsonPrimitive jsonPrimitve) {

    return jsonPrimitve.getAsString();

  }

  private List<Object> fillModel(JsonArray jsonArray, Object parentModel) {

    ArrayList<Object> elementList = new ArrayList<Object>();
    Iterator<JsonElement> iterator = jsonArray.iterator();
    while (iterator.hasNext()) {
      JsonElement element = iterator.next();
      elementList.add(fillModel(element, elementList));
    }

    return elementList;
  }

  @Override
  public List<Object> getInputObjects(Object input, Charset inputCharset) {

    List<Object> inputObjects = new ArrayList<Object>();
    if (input instanceof JsonObject) {

      JsonObject jsonObject = (JsonObject) input;
      Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
      for (Entry<String, JsonElement> entry : entrySet) {
        inputObjects.add(entry.getValue());
      }
    }

    return inputObjects;

  }

  @Override
  public Map<String, Object> getTemplateMethods(Object input) {

    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset) {

    return getInputObjects(input, inputCharset);
  }

  @Override
  public Object read(Path path, Charset inputCharset, Object... additionalArguments) throws InputReaderException {

    try (InputStream in = Files.newInputStream(path);
        InputStreamReader inSR = new InputStreamReader(in, inputCharset);
        JsonReader reader = new JsonReader(inSR);) {

      JsonParser parser = new JsonParser();
      JsonElement jsonBase = parser.parse(reader);
      JsonObject jsonObject = jsonBase.getAsJsonObject();
      return jsonObject;
    } catch (JsonIOException e) {
      throw new InputReaderException("Not JSON file", e);
    } catch (JsonSyntaxException e) {
      throw new InputReaderException("JSON syntax error. ", e);
    } catch (FileNotFoundException e) {
      throw new InputReaderException("File not found", e);
    } catch (IOException e) {
      throw new InputReaderException("Could not read " + path.getFileName(), e);
    }

  }

  @Override
  public boolean isMostLikelyReadable(Path path) {

    String fileExtension = FilenameUtils.getExtension(path.toString()).toLowerCase();
    return VALID_EXTENSION.equals(fileExtension) || Files.isDirectory(path);
  }

}
