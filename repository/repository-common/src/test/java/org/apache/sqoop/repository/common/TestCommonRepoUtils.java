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
package org.apache.sqoop.repository.common;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TestCommonRepoUtils {

  @Test
  public void testQuoting() throws Exception {
    String schemaName = "SCHEMA";
    String tableName = "TABLE";
    String columnName = "COLUMN";
    assertEquals("\"SCHEMA\"", CommonRepoUtils.escapeSchemaName(schemaName));
    assertEquals("\"TABLE\"", CommonRepoUtils.escapeTableName(tableName));
    assertEquals("\"COLUMN\"", CommonRepoUtils.escapeColumnName(columnName));
    assertEquals("\"SCHEMA\".\"TABLE\"", CommonRepoUtils.getTableName(schemaName, tableName));
    assertEquals("\"TABLE\"", CommonRepoUtils.getTableName(null, tableName));
    assertEquals("\"TABLE\".\"COLUMN\"", CommonRepoUtils.getColumnName(tableName, columnName));
    assertEquals("\"COLUMN\"", CommonRepoUtils.escapeColumnName(columnName));
  }
}
