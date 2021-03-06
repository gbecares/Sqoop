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
package org.apache.sqoop.test.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

import org.apache.log4j.Logger;
import org.apache.sqoop.client.SubmissionCallback;
import org.apache.sqoop.common.test.asserts.ProviderAsserts;
import org.apache.sqoop.common.test.db.DatabaseProvider;
import org.apache.sqoop.common.test.db.DatabaseProviderFactory;
import org.apache.sqoop.common.test.db.TableName;
import org.apache.sqoop.connector.hdfs.configuration.ToFormat;
import org.apache.sqoop.model.MConfigList;
import org.apache.sqoop.model.MJob;
import org.apache.sqoop.model.MLink;
import org.apache.sqoop.model.MPersistableEntity;
import org.apache.sqoop.model.MSubmission;
import org.apache.sqoop.submission.SubmissionStatus;
import org.apache.sqoop.test.data.Cities;
import org.apache.sqoop.test.data.ShortStories;
import org.apache.sqoop.test.data.UbuntuReleases;
import org.apache.sqoop.test.utils.SqoopUtils;
import org.apache.sqoop.validation.Status;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * Base test case suitable for connector testing.
 *
 * In addition to Jetty based test case it will also create and initialize
 * the database provider prior every test execution.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings({"MS_PKGPROTECT", "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD"})
abstract public class ConnectorTestCase extends JettyTestCase {

  private static final Logger LOG = Logger.getLogger(ConnectorTestCase.class);

  protected static DatabaseProvider provider;

  /**
   * Default submission callbacks that are printing various status about the submission.
   */
  protected static final SubmissionCallback DEFAULT_SUBMISSION_CALLBACKS = new SubmissionCallback() {
    @Override
    public void submitted(MSubmission submission) {
      LOG.info("Submission submitted: " + submission);
    }

    @Override
    public void updated(MSubmission submission) {
      LOG.info("Submission updated: " + submission);
    }

    @Override
    public void finished(MSubmission submission) {
      LOG.info("Submission finished: " + submission);
    }
  };

  @BeforeSuite(alwaysRun = true)
  public void startProvider() throws Exception {
    provider = DatabaseProviderFactory.getProvider(System.getProperties());
    LOG.info("Starting database provider: " + provider.getClass().getName());
    provider.start();
  }

  @AfterSuite(alwaysRun = true)
  public void stopProvider() {
    LOG.info("Stopping database provider: " + provider.getClass().getName());
    provider.stop();
  }

  public TableName getTableName() {
    return new TableName(getClass().getSimpleName());
  }

  protected void createTable(String primaryKey, String ...columns) {
    provider.createTable(getTableName(), primaryKey, columns);
  }

  protected void dropTable() {
    provider.dropTable(getTableName());
  }

  protected void insertRow(Object ...values) {
    provider.insertRow(getTableName(), values);
  }

  protected void insertRow(Boolean escapeValues, Object ...values) {
    provider.insertRow(getTableName(), escapeValues, values);
  }

  protected long rowCount() {
    return provider.rowCount(getTableName());
  }

  protected void dumpTable() {
    provider.dumpTable(getTableName());
  }

  /**
   * Fill link config based on currently active provider.
   *
   * @param link MLink object to fill
   */
  protected void fillRdbmsLinkConfig(MLink link) {
    MConfigList configs = link.getConnectorLinkConfig();
    configs.getStringInput("linkConfig.jdbcDriver").setValue(provider.getJdbcDriver());
    configs.getStringInput("linkConfig.connectionString").setValue(provider.getConnectionUrl());
    configs.getStringInput("linkConfig.username").setValue(provider.getConnectionUsername());
    configs.getStringInput("linkConfig.password").setValue(provider.getConnectionPassword());
  }

  protected void fillRdbmsFromConfig(MJob job, String partitionColumn) {
    MConfigList fromConfig = job.getFromJobConfig();
    fromConfig.getStringInput("fromJobConfig.tableName").setValue(getTableName().getTableName());
    fromConfig.getStringInput("fromJobConfig.partitionColumn").setValue(partitionColumn);
  }

  protected void fillRdbmsToConfig(MJob job) {
    MConfigList toConfig = job.getToJobConfig();
    toConfig.getStringInput("toJobConfig.tableName").setValue(getTableName().getTableName());
  }

  protected void fillHdfsLink(MLink link) {
    MConfigList configs = link.getConnectorLinkConfig();
    configs.getStringInput("linkConfig.confDir").setValue(getCluster().getConfigurationPath());
  }

  /**
   * Fill TO config with specific storage and output type.
   *
   * @param job MJob object to fill
   * @param output Output type that should be set
   */
  protected void fillHdfsToConfig(MJob job, ToFormat output) {
    MConfigList toConfig = job.getToJobConfig();
    toConfig.getEnumInput("toJobConfig.outputFormat").setValue(output);
    toConfig.getStringInput("toJobConfig.outputDirectory").setValue(getMapreduceDirectory());
  }

  /**
   * Fill FROM config
   *
   * @param job MJob object to fill
   */
  protected void fillHdfsFromConfig(MJob job) {
    MConfigList fromConfig = job.getFromJobConfig();
    fromConfig.getStringInput("fromJobConfig.inputDirectory").setValue(getMapreduceDirectory());
  }

  /**
   * Fill Driver config
   * @param job
   */
  protected void fillDriverConfig(MJob job) {
    job.getDriverConfig().getStringInput("throttlingConfig.numExtractors").setValue("3");
  }


  /**
   * Create table cities.
   */
  protected void createTableCities() {
    new Cities(provider, getTableName()).createTables();
  }

  /**
   * Create table cities and load few rows.
   */
  protected void createAndLoadTableCities() {
    new Cities(provider, getTableName()).createTables().loadBasicData();
  }

  /**
   * Create table for ubuntu releases.
   */
  protected void createTableUbuntuReleases() {
    new UbuntuReleases(provider, getTableName()).createTables();
  }

  /**
   * Create table for ubuntu releases.
   */
  protected void createAndLoadTableUbuntuReleases() {
    new UbuntuReleases(provider, getTableName()).createTables().loadBasicData();
  }

  /**
   * Create table for short stories.
   */
  protected void createTableShortStories() {
    new ShortStories(provider, getTableName()).createTables();
  }

  /**
   * Create table for short stories.
   */
  protected void createAndLoadTableShortStories() {
    new ShortStories(provider, getTableName()).createTables().loadBasicData();
  }

  /**
   * Assert row in testing table.
   *
   * @param conditions Conditions in config that are expected by the database provider
   * @param values Values that are expected in the table (with corresponding types)
   */
  protected void assertRow(Object[] conditions, Object ...values) {
    ProviderAsserts.assertRow(provider, getTableName(), conditions, values);
  }

  /**
   * Assert row in testing table.
   *
   * @param conditions Conditions in config that are expected by the database provider
   * @param escapeValues Flag whether the values should be escaped based on their type when using in the generated queries or not
   * @param values Values that are expected in the table (with corresponding types)
   */
  protected void assertRow(Object []conditions, Boolean escapeValues, Object ...values) {
    ProviderAsserts.assertRow(provider, getTableName(), escapeValues, conditions, values);
  }

  /**
   * Assert row in table "cities".
   *
   * @param values Values that are expected
   */
  protected void assertRowInCities(Object... values) {
    assertRow(new Object[]{"id", values[0]}, values);
  }

  /**
   * Create link.
   *
   * With asserts to make sure that it was created correctly.
   *
   * @param link
   */
  protected void saveLink(MLink link) {
    SqoopUtils.fillObjectName(link);
    assertEquals(getClient().saveLink(link), Status.OK);
    assertNotSame(link.getPersistenceId(), MPersistableEntity.PERSISTANCE_ID_DEFAULT);
  }

 /**
   * Create job.
   *
   * With asserts to make sure that it was created correctly.
   *
   * @param job
   */
 protected void saveJob(MJob job) {
   SqoopUtils.fillObjectName(job);
   assertEquals(getClient().saveJob(job), Status.OK);
   assertNotSame(job.getPersistenceId(), MPersistableEntity.PERSISTANCE_ID_DEFAULT);
  }

  /**
   * Run job with given jid.
   *
   * @param jid Job id
   * @throws Exception
   */
  protected void executeJob(String jobName) throws Exception {
    MSubmission finalSubmission = getClient().startJob(jobName, DEFAULT_SUBMISSION_CALLBACKS, 100);

    if(finalSubmission.getStatus().isFailure()) {
      LOG.error("Submission has failed: " + finalSubmission.getError().getErrorSummary());
      LOG.error("Corresponding error details: " + finalSubmission.getError().getErrorDetails());
    }
    assertEquals(finalSubmission.getStatus(), SubmissionStatus.SUCCEEDED,
            "Submission finished with error: " + finalSubmission.getError().getErrorSummary());
  }

  /**
   * Run given job.
   *
   * @param job Job object
   * @throws Exception
   */
  protected void executeJob(MJob job) throws Exception {
    executeJob(job.getName());
  }
}
