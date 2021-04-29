package com.devonfw.cobigen.jsonplugin.unittest.matcher;

import static com.devonfw.cobigen.test.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.CobiGenFactory;

/**
 * Test suite for integrating JsonPath typed matchers and variable assignments
 */
public class JsonPathGenerationTest {

  /** JUnit rule to savely create and cleanup temporary test folders */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  /**
   * Testing basic JsonPath Access
   *
   * @throws Exception test fails
   */
  @Test
  public void testJsonPathAccess() throws Exception {

    Path cobigenConfigFolder = new File("src/test/resources/testdata/integrationtest/basic/").toPath();
    Path input = cobigenConfigFolder.resolve("basic.json");

    CobiGen cobigen = CobiGenFactory.create(cobigenConfigFolder.toUri());
    Object compliantInput = cobigen.read(input, Charset.forName("UTF-8"));
    List<TemplateTo> matchingTemplates = cobigen.getMatchingTemplates(compliantInput);
    assertThat(matchingTemplates).isNotNull().hasSize(1);

    File targetFolder = this.tmpFolder.newFolder("testJsonPathAccess");
    GenerationReportTo report = cobigen.generate(compliantInput, matchingTemplates.get(0), targetFolder.toPath());

    assertThat(report).isSuccessful();
    System.out.println(targetFolder.toPath().resolve("DocJsonPath.txt"));
    assertThat(targetFolder.toPath().resolve("DocJsonPath.txt")).hasContent("test");
  }

}
