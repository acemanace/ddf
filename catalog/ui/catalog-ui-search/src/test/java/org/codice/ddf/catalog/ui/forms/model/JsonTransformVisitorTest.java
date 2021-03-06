/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.catalog.ui.forms.model;

import static java.lang.String.format;
import static junit.framework.TestCase.fail;
import static org.codice.ddf.catalog.ui.forms.model.FilterNodeAssertionSupport.assertLeafNode;
import static org.codice.ddf.catalog.ui.forms.model.FilterNodeAssertionSupport.assertTemplatedNode;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.codice.ddf.catalog.ui.forms.SearchFormsLoaderTest;
import org.codice.ddf.catalog.ui.forms.filter.FilterReader;
import org.codice.ddf.catalog.ui.forms.filter.VisitableXmlElement;
import org.codice.ddf.catalog.ui.forms.filter.VisitableXmlElementImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JsonTransformVisitorTest {
  private static final URL FILTER_RESOURCES_DIR =
      SearchFormsLoaderTest.class.getResource("/forms/filter2");

  private static final String DEPTH_PROP = "depth";

  private static final String DEPTH_VAL = "100";

  private JsonTransformVisitor visitor;

  @Before
  public void setup() {
    visitor = new JsonTransformVisitor();
  }

  @Test
  public void testVisitPropertyIsEqualTo() throws Exception {
    getRootFilterNode("comparison-binary-ops", "PropertyIsEqualTo.xml").accept(visitor);
    assertLeafNode(visitor.getResult(), "=", DEPTH_PROP, DEPTH_VAL);
  }

  @Test
  public void testVisitPropertyIsNotEqualTo() throws Exception {
    getRootFilterNode("comparison-binary-ops", "PropertyIsNotEqualTo.xml").accept(visitor);
    assertLeafNode(visitor.getResult(), "!=", DEPTH_PROP, DEPTH_VAL);
  }

  @Test
  public void testVisitPropertyIsLessThan() throws Exception {
    getRootFilterNode("comparison-binary-ops", "PropertyIsLessThan.xml").accept(visitor);
    assertLeafNode(visitor.getResult(), "<", DEPTH_PROP, DEPTH_VAL);
  }

  @Test
  public void testVisitPropertyIsLessThanOrEqualTo() throws Exception {
    getRootFilterNode("comparison-binary-ops", "PropertyIsLessThanOrEqualTo.xml").accept(visitor);
    assertLeafNode(visitor.getResult(), "<=", DEPTH_PROP, DEPTH_VAL);
  }

  @Test
  public void testVisitPropertyIsGreaterThan() throws Exception {
    getRootFilterNode("comparison-binary-ops", "PropertyIsGreaterThan.xml").accept(visitor);
    assertLeafNode(visitor.getResult(), ">", DEPTH_PROP, DEPTH_VAL);
  }

  @Test
  public void testVisitPropertyIsGreaterThanOrEqualTo() throws Exception {
    getRootFilterNode("comparison-binary-ops", "PropertyIsGreaterThanOrEqualTo.xml")
        .accept(visitor);
    assertLeafNode(visitor.getResult(), ">=", DEPTH_PROP, DEPTH_VAL);
  }

  @Test
  public void testVisitIntersectsWithFunction() throws Exception {
    getRootFilterNode("function-ops", "Intersects.xml").accept(visitor);
    assertTemplatedNode(visitor.getResult(), "INTERSECTS", "location", null, "id");
  }

  private static VisitableXmlElement getRootFilterNode(String... resourceRoute) throws Exception {
    File dir = new File(FILTER_RESOURCES_DIR.toURI());
    if (!dir.exists()) {
      fail(
          format(
              "Invalid setup parameter '%s', the directory does not exist",
              FILTER_RESOURCES_DIR.toString()));
    }

    Path route = Arrays.stream(resourceRoute).map(Paths::get).reduce(Path::resolve).orElse(null);
    if (route == null) {
      fail("Could not reduce resource route to a single path");
    }

    File xmlFile = dir.toPath().resolve(route).toFile();
    if (!xmlFile.exists()) {
      fail("File was not found " + xmlFile.getAbsolutePath());
    }

    return new VisitableXmlElementImpl(
        new FilterReader().unmarshalFilter(new FileInputStream(xmlFile)));
  }
}
