/*
 * Copyright (C) 2016 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sqoop.schema.type;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

public class TestSet {

  @Test
  public void testSetWithSameListType() {
    Set a1 = new Set("A", new Text("T"));
    Set a2 = new Set("A", new Text("T"));
    assertTrue(a1.equals(a2));
    assertEquals(a1.toString(), a2.toString());
  }

  @Test
  public void testSetWithDifferentName() {
    Set a1 = new Set("A", new Text("T"));
    Set a2 = new Set("B", new Text("T"));
    assertFalse(a1.equals(a2));
    assertNotEquals(a1.toString(), a2.toString());
  }

  @Test
  public void testSetWithDifferentListType() {
    Set a1 = new Set("A", new Text("T"));
    Set a2 = new Set("A", new Binary("B"));
    assertFalse(a1.equals(a2));
    assertNotEquals(a1.toString(), a2.toString());
  }
}
