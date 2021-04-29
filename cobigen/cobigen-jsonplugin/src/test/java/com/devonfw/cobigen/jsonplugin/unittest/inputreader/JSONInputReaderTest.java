package com.devonfw.cobigen.jsonplugin.unittest.inputreader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.jsonplugin.inputreader.JSONInputReader;

/**
 * Unit tests for {@link JSONInputReader}
 */
public class JSONInputReaderTest {

  /** UTF-8 Charset */
  private static final Charset UTF_8 = Charset.forName("UTF-8");

  /** Root path to all resources used in this test case */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/inputreader/";

  /**
   * Tests the correct retrieval of input objects. Here: generically return all elements of the input document as a new
   * document.
   *
   * @throws Exception test fails
   */
  @Test
  public void testGetInputObjects() throws Exception {

    JSONInputReader JSONInputReader = new JSONInputReader();
    File jsonFile = new File(testFileRootPath + "basic.json");
    Object jsonFileObject = JSONInputReader.read(jsonFile.toPath(), UTF_8);

    List<Object> inputObjects = JSONInputReader.getInputObjects(jsonFileObject, UTF_8);
    assertNotNull(inputObjects);

  }
  //
  // /**
  // * Test method for {@link JSONInputReader#isValidInput(java.lang.Object)} in case of a valid input.
  // *
  // * @throws ParserConfigurationException test fails
  // * @throws IOException test fails
  // * @throws SAXException test fails
  // */
  // @Test
  // public void testIsValidInput_isValid() throws ParserConfigurationException, SAXException, IOException {
  //
  // JSONInputReader jsonInputReader = new JSONInputReader();
  // File xmlFile = new File(testFileRootPath + "/testLibrary.xml");
  // DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
  // DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
  // Document validInput = dBuilder.parse(xmlFile);
  // assertThat(jsonInputReader.isValidInput(validInput)).isTrue();
  // }

  /**
   * Test method for {@link JSONInputReader#isValidInput(java.lang.Object)} in case of an invalid input.
   */
  @Test
  public void testIsValidInput_isNotValid() {

    JSONInputReader JSONInputReader = new JSONInputReader();
    Object invalidInput = new Object();
    assertThat(JSONInputReader.isValidInput(invalidInput)).isFalse();
  }

}
