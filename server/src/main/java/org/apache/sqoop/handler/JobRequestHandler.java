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
package org.apache.sqoop.handler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.sqoop.audit.AuditLoggerManager;
import org.apache.sqoop.common.Direction;
import org.apache.sqoop.common.SqoopException;
import org.apache.sqoop.connector.ConnectorManager;
import org.apache.sqoop.connector.spi.SqoopConnector;
import org.apache.sqoop.driver.Driver;
import org.apache.sqoop.driver.JobManager;
import org.apache.sqoop.json.JSONUtils;
import org.apache.sqoop.json.JobBean;
import org.apache.sqoop.json.JobsBean;
import org.apache.sqoop.json.JsonBean;
import org.apache.sqoop.json.SubmissionBean;
import org.apache.sqoop.json.ValidationResultBean;
import org.apache.sqoop.model.*;
import org.apache.sqoop.repository.Repository;
import org.apache.sqoop.repository.RepositoryManager;
import org.apache.sqoop.request.HttpEventContext;
import org.apache.sqoop.security.authorization.AuthorizationEngine;
import org.apache.sqoop.security.AuthorizationManager;
import org.apache.sqoop.server.RequestContext;
import org.apache.sqoop.server.RequestHandler;
import org.apache.sqoop.server.common.ServerError;
import org.apache.sqoop.submission.SubmissionStatus;
import org.apache.sqoop.validation.ConfigValidationResult;
import org.apache.sqoop.validation.Status;
import org.json.simple.JSONObject;

public class JobRequestHandler implements RequestHandler {
  private static final long serialVersionUID = 1L;

  /** enum for representing the actions supported on the job resource*/
  enum JobAction {
    ENABLE("enable"),
    DISABLE("disable"),
    START("start"),
    STOP("stop"),
    ;
    JobAction(String name) {
      this.name = name;
    }

    String name;

    public static JobAction fromString(String name) {
      if (name != null) {
        for (JobAction action : JobAction.values()) {
          if (name.equalsIgnoreCase(action.name)) {
            return action;
          }
        }
      }
      return null;
    }
  }

  private static final Logger LOG = Logger.getLogger(JobRequestHandler.class);

  static final String JOBS_PATH = "jobs";
  static final String JOB_PATH = "job";
  static final String STATUS = "status";

  public JobRequestHandler() {
    LOG.info("JobRequestHandler initialized");
  }

  @Override
  public JsonBean handleEvent(RequestContext ctx) {
    LOG.info("Got job request");
    switch (ctx.getMethod()) {
    case GET:
      if (STATUS.equals(ctx.getLastURLElement())) {
        return getJobStatus(ctx);
      }
      return getJobs(ctx);
    case POST:
      return createUpdateJob(ctx, true);
    case PUT:
      JobAction action = JobAction.fromString(ctx.getLastURLElement());
      if (action != null) {
        switch (action) {
          case ENABLE:
            return enableJob(ctx, true);
          case DISABLE:
            return enableJob(ctx, false);
          case START:
            return startJob(ctx);
          case STOP:
            return stopJob(ctx);
        }
      }
      return createUpdateJob(ctx, false);
    case DELETE:
      return deleteJob(ctx);
    }

    return null;
  }

  /**
   * Delete job from repository.
   *
   * @param ctx
   *          Context object
   * @return Empty bean
   */
  private JsonBean deleteJob(RequestContext ctx) {

    Repository repository = RepositoryManager.getInstance().getRepository();

    String jobIdentifier = ctx.getLastURLElement();
    MJob job = HandlerUtils.getJobFromIdentifier(jobIdentifier);
    String jobName = job.getName();

    // Authorization check
    AuthorizationEngine.deleteJob(ctx.getUserName(), jobName);

    AuditLoggerManager.getInstance().logAuditEvent(ctx.getUserName(),
        ctx.getRequest().getRemoteAddr(), "delete", "job", jobIdentifier);
    repository.deleteJob(jobName);
    MResource resource = new MResource(jobName, MResource.TYPE.JOB);
    AuthorizationManager.getInstance().getAuthorizationHandler().removeResource(resource);
    return JsonBean.EMPTY_BEAN;
  }

  /**
   * Update or create job in repository.
   *
   * @param ctx
   *          Context object
   * @return Validation bean object
   */
  private JsonBean createUpdateJob(RequestContext ctx, boolean create) {

    Repository repository = RepositoryManager.getInstance().getRepository();

    JobBean bean = new JobBean();

    try {
      JSONObject json = JSONUtils.parse(ctx.getRequest().getReader());
      bean.restore(json);
    } catch (IOException e) {
      throw new SqoopException(ServerError.SERVER_0003, "Can't read request content", e);
    }

    String username = ctx.getUserName();

    // Get job object
    List<MJob> jobs = bean.getJobs();

    if (jobs.size() != 1) {
      throw new SqoopException(ServerError.SERVER_0003, "Expected one job but got " + jobs.size());
    }

    // Job object
    MJob postedJob = jobs.get(0);

    // Authorization check
    if (create) {
      AuthorizationEngine.createJob(ctx.getUserName(), postedJob.getFromLinkName(), postedJob.getToLinkName());
    } else {
      AuthorizationEngine.updateJob(ctx.getUserName(), postedJob.getFromLinkName(), postedJob.getToLinkName(),
              postedJob.getName());
    }

    // Verify that user is not trying to spoof us
    MFromConfig fromConfig = ConnectorManager.getInstance()
        .getConnectorConfigurable(postedJob.getFromConnectorName()).getFromConfig();
    MToConfig toConfig = ConnectorManager.getInstance()
        .getConnectorConfigurable(postedJob.getToConnectorName()).getToConfig();
    MDriverConfig driverConfig = Driver.getInstance().getDriver().getDriverConfig();

    if (!fromConfig.equals(postedJob.getFromJobConfig())
        || !driverConfig.equals(postedJob.getDriverConfig())
        || !toConfig.equals(postedJob.getToJobConfig())) {
      throw new SqoopException(ServerError.SERVER_0003, "Detected incorrect config structure");
    }

    // if update get the job id from the request URI
    if (!create) {
      String jobIdentifier = ctx.getLastURLElement();
      MJob existingJob = HandlerUtils.getJobFromIdentifier(jobIdentifier);
      if (postedJob.getPersistenceId() == MPersistableEntity.PERSISTANCE_ID_DEFAULT) {
        postedJob.setPersistenceId(existingJob.getPersistenceId());
      }
    }

    // Corresponding connectors for this
    SqoopConnector fromConnector = ConnectorManager.getInstance().getSqoopConnector(
        postedJob.getFromConnectorName());
    SqoopConnector toConnector = ConnectorManager.getInstance().getSqoopConnector(
        postedJob.getToConnectorName());

    if (!fromConnector.getSupportedDirections().contains(Direction.FROM)) {
      throw new SqoopException(ServerError.SERVER_0004, "Connector "
          + fromConnector.getClass().getCanonicalName() + " does not support FROM direction.");
    }

    if (!toConnector.getSupportedDirections().contains(Direction.TO)) {
      throw new SqoopException(ServerError.SERVER_0004, "Connector "
          + toConnector.getClass().getCanonicalName() + " does not support TO direction.");
    }

    // Validate user supplied data
    ConfigValidationResult fromConfigValidator = ConfigUtils.validateConfigs(
        postedJob.getFromJobConfig().getConfigs(),
        fromConnector.getJobConfigurationClass(Direction.FROM));
    ConfigValidationResult toConfigValidator = ConfigUtils.validateConfigs(
        postedJob.getToJobConfig().getConfigs(),
        toConnector.getJobConfigurationClass(Direction.TO));
    ConfigValidationResult driverConfigValidator = ConfigUtils.validateConfigs(postedJob
        .getDriverConfig().getConfigs(), Driver.getInstance().getDriverJobConfigurationClass());
    Status finalStatus = Status.getWorstStatus(fromConfigValidator.getStatus(),
        toConfigValidator.getStatus(), driverConfigValidator.getStatus());
    // Return back validations in all cases
    ValidationResultBean validationResultBean = new ValidationResultBean(fromConfigValidator, toConfigValidator, driverConfigValidator);

    // If we're good enough let's perform the action
    if (finalStatus.canProceed()) {
      if (create) {
        AuditLoggerManager.getInstance().logAuditEvent(ctx.getUserName(),
            ctx.getRequest().getRemoteAddr(), "create", "job",
            String.valueOf(postedJob.getPersistenceId()));

        postedJob.setCreationUser(username);
        postedJob.setLastUpdateUser(username);
        repository.createJob(postedJob);
        validationResultBean.setId(postedJob.getPersistenceId());
      } else {
        AuditLoggerManager.getInstance().logAuditEvent(ctx.getUserName(),
            ctx.getRequest().getRemoteAddr(), "update", "job",
            String.valueOf(postedJob.getPersistenceId()));

        postedJob.setLastUpdateUser(username);
        repository.updateJob(postedJob);
      }
    }
    return validationResultBean;
  }

  private JsonBean getJobs(RequestContext ctx) {
    String connectorIdentifier = ctx.getLastURLElement();
    JobBean jobBean;
    Locale locale = ctx.getAcceptLanguageHeader();
    Repository repository = RepositoryManager.getInstance().getRepository();
    // jobs by connector
    if (ctx.getParameterValue(CONNECTOR_NAME_QUERY_PARAM) != null) {
      connectorIdentifier = ctx.getParameterValue(CONNECTOR_NAME_QUERY_PARAM);
      AuditLoggerManager.getInstance().logAuditEvent(ctx.getUserName(),
          ctx.getRequest().getRemoteAddr(), "get", "jobsByConnector", connectorIdentifier);
      MConnector mConnector = HandlerUtils.getConnectorFromConnectorName(connectorIdentifier);
      List<MJob> jobList = repository.findJobsForConnector(mConnector.getPersistenceId());

      // Authorization check
      jobList = AuthorizationEngine.filterResource(ctx.getUserName(), MResource.TYPE.JOB, jobList);

      jobBean = createJobsBean(jobList, locale);
    } else
    // all jobs in the system
    if (ctx.getPath().contains(JOBS_PATH)
        || (ctx.getPath().contains(JOB_PATH) && connectorIdentifier.equals("all"))) {
      AuditLoggerManager.getInstance().logAuditEvent(ctx.getUserName(),
          ctx.getRequest().getRemoteAddr(), "get", "jobs", "all");
      List<MJob> jobList = repository.findJobs();

      // Authorization check
      jobList = AuthorizationEngine.filterResource(ctx.getUserName(), MResource.TYPE.JOB, jobList);

      jobBean = createJobsBean(jobList, locale);
    }
    // job by Id
    else {
      AuditLoggerManager.getInstance().logAuditEvent(ctx.getUserName(),
          ctx.getRequest().getRemoteAddr(), "get", "job", connectorIdentifier);

      MJob job = HandlerUtils.getJobFromIdentifier(connectorIdentifier);
      String jobName = job.getName();

      // Authorization check
      AuthorizationEngine.readJob(ctx.getUserName(), jobName);

      jobBean = createJobBean(Arrays.asList(job), locale);
    }
    return jobBean;
  }

  private JobBean createJobBean(List<MJob> jobs, Locale locale) {
    JobBean jobBean = new JobBean(jobs);
    addConnectorConfigBundle(jobBean, locale);
    return jobBean;
  }

  private JobsBean createJobsBean(List<MJob> jobs, Locale locale) {
    JobsBean jobsBean = new JobsBean(jobs);
    addConnectorConfigBundle(jobsBean, locale);
    return jobsBean;
  }

  private void addConnectorConfigBundle(JobBean bean, Locale locale) {
    // Add associated resources into the bean
    for (MJob job : bean.getJobs()) {
      String fromConnectorName = job.getFromConnectorName();
      String toConnectorName = job.getToConnectorName();

      // replace it only if it does not already exist
      if (!bean.hasConnectorConfigBundle(fromConnectorName)) {
        bean.addConnectorConfigBundle(fromConnectorName, ConnectorManager.getInstance()
            .getResourceBundle(fromConnectorName, locale));
      }
      if (!bean.hasConnectorConfigBundle(toConnectorName)) {
        bean.addConnectorConfigBundle(toConnectorName, ConnectorManager.getInstance()
            .getResourceBundle(toConnectorName, locale));
      }
    }
  }

  private JsonBean enableJob(RequestContext ctx, boolean enabled) {
    Repository repository = RepositoryManager.getInstance().getRepository();
    String[] elements = ctx.getUrlElements();
    String jobIdentifier = elements[elements.length - 2];
    MJob job = HandlerUtils.getJobFromIdentifier(jobIdentifier);
    String jobName = job.getName();

    // Authorization check
    AuthorizationEngine.enableDisableJob(ctx.getUserName(), jobName);

    repository.enableJob(jobName, enabled);
    return JsonBean.EMPTY_BEAN;
  }

  private JsonBean startJob(RequestContext ctx) {
    String[] elements = ctx.getUrlElements();
    String jobIdentifier = elements[elements.length - 2];
    MJob job = HandlerUtils.getJobFromIdentifier(jobIdentifier);
    String jobName = job.getName();

    // Authorization check
    AuthorizationEngine.startJob(ctx.getUserName(), jobName);

    AuditLoggerManager.getInstance().logAuditEvent(ctx.getUserName(),
        ctx.getRequest().getRemoteAddr(), "submit", "job", jobName);
    // TODO(SQOOP-1638): This should be outsourced somewhere more suitable than here
    // Current approach is to point JobManager to use /v1/job/notification/$JOB_ID/status
    // and depend on the behavior of status that for running jobs will go to the cluster
    // and fetch the latest state. We don't have notification first class
    if (JobManager.getInstance().getNotificationBaseUrl() == null) {
      String url = ctx.getRequest().getRequestURL().toString();
      JobManager.getInstance().setNotificationBaseUrl(
          url.split("v1")[0] + "/v1/job/notification/");
    }

    MSubmission submission = JobManager.getInstance()
        .start(jobName, prepareRequestEventContext(ctx));
    return new SubmissionBean(submission);
  }

  private JsonBean stopJob(RequestContext ctx) {
    String[] elements = ctx.getUrlElements();
    String jobIdentifier = elements[elements.length - 2];
    MJob job = HandlerUtils.getJobFromIdentifier(jobIdentifier);
    String jobName = job.getName();

    // Authorization check
    AuthorizationEngine.stopJob(ctx.getUserName(), jobName);

    AuditLoggerManager.getInstance().logAuditEvent(ctx.getUserName(),
        ctx.getRequest().getRemoteAddr(), "stop", "job", jobName);
    MSubmission submission = JobManager.getInstance().stop(jobName, prepareRequestEventContext(ctx));
    return new SubmissionBean(submission);
  }

  private JsonBean getJobStatus(RequestContext ctx) {
    String[] elements = ctx.getUrlElements();
    String jobIdentifier = elements[elements.length - 2];
    MJob job = HandlerUtils.getJobFromIdentifier(jobIdentifier);
    String jobName = job.getName();

    // Authorization check
    AuthorizationEngine.statusJob(ctx.getUserName(), jobName);

    AuditLoggerManager.getInstance().logAuditEvent(ctx.getUserName(),
        ctx.getRequest().getRemoteAddr(), "status", "job", jobName);
    MSubmission submission = JobManager.getInstance().status(jobName);
    if (submission == null) {
      submission = new MSubmission(job.getPersistenceId(), new Date(), SubmissionStatus.NEVER_EXECUTED);
    }

    return new SubmissionBean(submission);
  }

  private HttpEventContext prepareRequestEventContext(RequestContext ctx) {
    HttpEventContext httpEventContext = new HttpEventContext();
    httpEventContext.setUsername(ctx.getUserName());
    return httpEventContext;
  }

}
