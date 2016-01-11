/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sqoop.connector.jdbc;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.sqoop.common.MutableContext;
import org.apache.sqoop.common.MutableMapContext;
import org.apache.sqoop.connector.jdbc.configuration.LinkConfiguration;
import org.apache.sqoop.connector.jdbc.configuration.FromJobConfiguration;
import org.apache.sqoop.job.etl.Partition;
import org.apache.sqoop.job.etl.Partitioner;
import org.apache.sqoop.job.etl.PartitionerContext;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestPartitioner {

  private static final int START = -5;
  private static final int NUMBER_OF_ROWS = 11;

  @Test
  public void testIntegerEvenPartition() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNNAME,
        "ICOL");
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNTYPE,
        String.valueOf(Types.INTEGER));
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MINVALUE,
        String.valueOf(START));
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MAXVALUE,
        String.valueOf(START + NUMBER_OF_ROWS - 1));

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 5, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);

    verifyResult(partitions, new String[] {
        "-5 <= ICOL AND ICOL < -3",
        "-3 <= ICOL AND ICOL < -1",
        "-1 <= ICOL AND ICOL < 1",
        "1 <= ICOL AND ICOL < 3",
        "3 <= ICOL AND ICOL <= 5"
    });
  }

  @Test
  public void testIntegerUnevenPartition() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNNAME,
        "ICOL");
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNTYPE,
        String.valueOf(Types.INTEGER));
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MINVALUE,
        String.valueOf(START));
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MAXVALUE,
        String.valueOf(START + NUMBER_OF_ROWS - 1));

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 3, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);

    verifyResult(partitions, new String[] {
        "-5 <= ICOL AND ICOL < -1",
        "-1 <= ICOL AND ICOL < 2",
        "2 <= ICOL AND ICOL <= 5"
    });
  }

  @Test
  public void testIntegerOverPartition() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNNAME,
        "ICOL");
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNTYPE,
        String.valueOf(Types.INTEGER));
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MINVALUE,
        String.valueOf(START));
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MAXVALUE,
        String.valueOf(START + NUMBER_OF_ROWS - 1));

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 13, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);

    verifyResult(partitions, new String[] {
        "-5 <= ICOL AND ICOL < -4",
        "-4 <= ICOL AND ICOL < -3",
        "-3 <= ICOL AND ICOL < -2",
        "-2 <= ICOL AND ICOL < -1",
        "-1 <= ICOL AND ICOL < 0",
        "0 <= ICOL AND ICOL < 1",
        "1 <= ICOL AND ICOL < 2",
        "2 <= ICOL AND ICOL < 3",
        "3 <= ICOL AND ICOL < 4",
        "4 <= ICOL AND ICOL <= 5"
    });
  }

  @Test
  public void testFloatingPointEvenPartition() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNNAME,
        "DCOL");
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNTYPE,
        String.valueOf(Types.DOUBLE));
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MINVALUE,
        String.valueOf((double)START));
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MAXVALUE,
        String.valueOf((double)(START + NUMBER_OF_ROWS - 1)));

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 5, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);

    verifyResult(partitions, new String[] {
        "-5.0 <= DCOL AND DCOL < -3.0",
        "-3.0 <= DCOL AND DCOL < -1.0",
        "-1.0 <= DCOL AND DCOL < 1.0",
        "1.0 <= DCOL AND DCOL < 3.0",
        "3.0 <= DCOL AND DCOL <= 5.0"
    });
  }

  @Test
  public void testFloatingPointUnevenPartition() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNNAME,
        "DCOL");
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNTYPE,
        String.valueOf(Types.DOUBLE));
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MINVALUE,
        String.valueOf((double)START));
    context.setString(
        GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MAXVALUE,
        String.valueOf((double)(START + NUMBER_OF_ROWS - 1)));

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 3, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);

    verifyResult(partitions, new String[] {
        "-5.0 <= DCOL AND DCOL < -1.6666666666666665",
        "-1.6666666666666665 <= DCOL AND DCOL < 1.666666666666667",
        "1.666666666666667 <= DCOL AND DCOL <= 5.0"
    });
  }

  @Test
  public void testNumericEvenPartition() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNNAME, "ICOL");
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNTYPE, String.valueOf(Types.NUMERIC));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MINVALUE, String.valueOf(START));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MAXVALUE, String.valueOf(START + NUMBER_OF_ROWS - 1));

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 5, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);

    verifyResult(partitions, new String[] {
        "-5 <= ICOL AND ICOL < -3",
        "-3 <= ICOL AND ICOL < -1",
        "-1 <= ICOL AND ICOL < 1",
        "1 <= ICOL AND ICOL < 3",
        "3 <= ICOL AND ICOL <= 5"
    });
  }

  @Test
  public void testNumericUnevenPartition() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNNAME, "DCOL");
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNTYPE, String.valueOf(Types.NUMERIC));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MINVALUE, String.valueOf(new BigDecimal(START)));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MAXVALUE, String.valueOf(new BigDecimal(START + NUMBER_OF_ROWS - 1)));

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 3, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);

    verifyResult(partitions, new String[]{
      "-5 <= DCOL AND DCOL < -2",
      "-2 <= DCOL AND DCOL < 1",
      "1 <= DCOL AND DCOL <= 5"
    });
  }

  // We may round the quotient when calculating splitsize, this tests ensure we catch those values in the final partition
  @Test
  public void testNumericInaccurateSplit() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNNAME, "DCOL");
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNTYPE, String.valueOf(Types.NUMERIC));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MINVALUE, String.valueOf(new BigDecimal(1)));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MAXVALUE, String.valueOf(new BigDecimal(13)));

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 11, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);

    verifyResult(partitions, new String[] {
      "1 <= DCOL AND DCOL < 2",
      "2 <= DCOL AND DCOL < 3",
      "3 <= DCOL AND DCOL < 4",
      "4 <= DCOL AND DCOL < 5",
      "5 <= DCOL AND DCOL < 6",
      "6 <= DCOL AND DCOL < 7",
      "7 <= DCOL AND DCOL < 8",
      "8 <= DCOL AND DCOL < 9",
      "9 <= DCOL AND DCOL < 10",
      "10 <= DCOL AND DCOL < 11",
      "11 <= DCOL AND DCOL <= 13"
    });
  }

  @Test
  public void testNumericSinglePartition() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNNAME, "DCOL");
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNTYPE, String.valueOf(Types.NUMERIC));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MINVALUE, String.valueOf(new BigDecimal(START)));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MAXVALUE, String.valueOf(new BigDecimal(START)));

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 3, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);

    verifyResult(partitions, new String[]{
      "DCOL = -5",
    });
  }

  @Test
  public void testDatePartition() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_COLUMNNAME, "DCOL");
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_COLUMNTYPE, String.valueOf(Types.DATE));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MINVALUE,
      String.valueOf(Date.valueOf("2004-10-20").getTime()));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MAXVALUE,
      String.valueOf(Date.valueOf("2013-10-17").getTime()));


    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 3, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);


    verifyResult(partitions, new String[]{
        "'2004-10-20' <= DCOL AND DCOL < '2007-10-19'",
        "'2007-10-19' <= DCOL AND DCOL < '2010-10-18'",
        "'2010-10-18' <= DCOL AND DCOL <= '2013-10-17'",
    });

  }

  @Test
  public void testTimePartition() throws Exception {
    DateFormat timeDateFormat = new SimpleDateFormat("HH:mm:ss");
    timeDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    Long startTime = timeDateFormat.parse("01:01:01").getTime();
    Long endTime = timeDateFormat.parse("10:40:50").getTime();

    MutableContext context = new MutableMapContext();
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_COLUMNNAME, "TCOL");
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_COLUMNTYPE, String.valueOf(Types.TIME));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MINVALUE,
        String.valueOf(startTime));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MAXVALUE,
        String.valueOf(endTime));


    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 3, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);

    verifyResult(partitions, new String[]{
        "'01:01:01' <= TCOL AND TCOL < '04:14:17'",
        "'04:14:17' <= TCOL AND TCOL < '07:27:33'",
        "'07:27:33' <= TCOL AND TCOL <= '10:40:50'",
    });
  }

  @Test
  public void testTimestampPartition() throws Exception {
    DateFormat timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    timestampDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    Long startTime = timestampDateFormat.parse("2013-01-01 01:01:01.123").getTime();
    Long endTime = timestampDateFormat.parse("2013-12-31 10:40:50.654").getTime();

    MutableContext context = new MutableMapContext();
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_COLUMNNAME, "TSCOL");
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_COLUMNTYPE, String.valueOf(Types.TIMESTAMP));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MINVALUE,
        String.valueOf(startTime));
    context.setString(GenericJdbcConnectorConstants.CONNECTOR_JDBC_PARTITION_MAXVALUE,
        String.valueOf(endTime));

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 3, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);
    verifyResult(partitions, new String[]{
        "'2013-01-01 01:01:01.123' <= TSCOL AND TSCOL < '2013-05-02 12:14:17.634'",
        "'2013-05-02 12:14:17.634' <= TSCOL AND TSCOL < '2013-08-31 23:27:34.144'",
        "'2013-08-31 23:27:34.144' <= TSCOL AND TSCOL <= '2013-12-31 10:40:50.654'",
    });
  }

  @Test
  public void testBooleanPartition() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_COLUMNNAME, "BCOL");
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_COLUMNTYPE, String.valueOf(Types.BOOLEAN));
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_MINVALUE, "0");
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_MAXVALUE, "1");

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 3, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);
    verifyResult(partitions, new String[]{
      "BCOL = TRUE",
      "BCOL = FALSE",
    });
  }

  @Test
  public void testVarcharPartition() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_COLUMNNAME, "VCCOL");
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_COLUMNTYPE, String.valueOf(Types.VARCHAR));
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_MINVALUE, "A");
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_MAXVALUE, "Z");

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 25, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);

    verifyResult(partitions, new String[] {
        "'A' <= VCCOL AND VCCOL < 'B'",
        "'B' <= VCCOL AND VCCOL < 'C'",
        "'C' <= VCCOL AND VCCOL < 'D'",
        "'D' <= VCCOL AND VCCOL < 'E'",
        "'E' <= VCCOL AND VCCOL < 'F'",
        "'F' <= VCCOL AND VCCOL < 'G'",
        "'G' <= VCCOL AND VCCOL < 'H'",
        "'H' <= VCCOL AND VCCOL < 'I'",
        "'I' <= VCCOL AND VCCOL < 'J'",
        "'J' <= VCCOL AND VCCOL < 'K'",
        "'K' <= VCCOL AND VCCOL < 'L'",
        "'L' <= VCCOL AND VCCOL < 'M'",
        "'M' <= VCCOL AND VCCOL < 'N'",
        "'N' <= VCCOL AND VCCOL < 'O'",
        "'O' <= VCCOL AND VCCOL < 'P'",
        "'P' <= VCCOL AND VCCOL < 'Q'",
        "'Q' <= VCCOL AND VCCOL < 'R'",
        "'R' <= VCCOL AND VCCOL < 'S'",
        "'S' <= VCCOL AND VCCOL < 'T'",
        "'T' <= VCCOL AND VCCOL < 'U'",
        "'U' <= VCCOL AND VCCOL < 'V'",
        "'V' <= VCCOL AND VCCOL < 'W'",
        "'W' <= VCCOL AND VCCOL < 'X'",
        "'X' <= VCCOL AND VCCOL < 'Y'",
        "'Y' <= VCCOL AND VCCOL <= 'Z'",
    });
  }

  @Test
  public void testVarcharPartition2() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(GenericJdbcConnectorConstants
      .CONNECTOR_JDBC_PARTITION_COLUMNNAME, "VCCOL");
    context.setString(GenericJdbcConnectorConstants
      .CONNECTOR_JDBC_PARTITION_COLUMNTYPE, String.valueOf(Types.VARCHAR));
    context.setString(GenericJdbcConnectorConstants
      .CONNECTOR_JDBC_PARTITION_MINVALUE, "Breezy Badger");
    context.setString(GenericJdbcConnectorConstants
      .CONNECTOR_JDBC_PARTITION_MAXVALUE, "Warty Warthog");

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();
    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 5, null, "test_user");
    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);
    assertEquals(partitions.size(), 5);
    // First partition needs to contain entire upper bound
    assertTrue(partitions.get(0).toString().contains("Breezy Badger"));
    // Last partition needs to contain entire lower bound
    assertTrue(partitions.get(4).toString().contains("Warty Warthog"));
  }

  @Test
  public void testVarcharPartitionWithCommonPrefix() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_COLUMNNAME, "VCCOL");
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_COLUMNTYPE, String.valueOf(Types.VARCHAR));
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_MINVALUE, "AAA");
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_MAXVALUE, "AAF");

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 5, null, "test_user");

    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);

    verifyResult(partitions, new String[] {
        "'AAA' <= VCCOL AND VCCOL < 'AAB'",
        "'AAB' <= VCCOL AND VCCOL < 'AAC'",
        "'AAC' <= VCCOL AND VCCOL < 'AAD'",
        "'AAD' <= VCCOL AND VCCOL < 'AAE'",
        "'AAE' <= VCCOL AND VCCOL <= 'AAF'",
    });

  }

  @Test
  public void testPartitionWithNullValues() throws Exception {
    MutableContext context = new MutableMapContext();
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_COLUMNNAME, "VCCOL");
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_COLUMNTYPE, String.valueOf(Types.VARCHAR));
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_MINVALUE, "AAA");
    context.setString(GenericJdbcConnectorConstants
        .CONNECTOR_JDBC_PARTITION_MAXVALUE, "AAE");

    LinkConfiguration linkConfig = new LinkConfiguration();
    FromJobConfiguration jobConfig = new FromJobConfiguration();
    jobConfig.fromJobConfig.allowNullValueInPartitionColumn = true;

    Partitioner partitioner = new GenericJdbcPartitioner();
    PartitionerContext partitionerContext = new PartitionerContext(context, 5, null, "test_user");

    List<Partition> partitions = partitioner.getPartitions(partitionerContext, linkConfig, jobConfig);

    verifyResult(partitions, new String[] {
        "VCCOL IS NULL",
        "'AAA' <= VCCOL AND VCCOL < 'AAB'",
        "'AAB' <= VCCOL AND VCCOL < 'AAC'",
        "'AAC' <= VCCOL AND VCCOL < 'AAD'",
        "'AAD' <= VCCOL AND VCCOL <= 'AAE'",
    });

  }

  private void verifyResult(List<Partition> partitions,
      String[] expected) {
    assertEquals(expected.length, partitions.size());

    Iterator<Partition> iterator = partitions.iterator();
    for (int i = 0; i < expected.length; i++) {
      GenericJdbcPartition partition = ((GenericJdbcPartition) iterator.next());
      assertEquals(partition.toString(), expected[i]);
    }
  }
}