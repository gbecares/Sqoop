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
package org.apache.sqoop.audit;

import org.apache.log4j.Logger;
import org.apache.sqoop.common.MapContext;
import org.apache.sqoop.common.SqoopException;
import org.apache.sqoop.core.Reconfigurable;
import org.apache.sqoop.core.SqoopConfiguration;
import org.apache.sqoop.core.SqoopConfiguration.CoreConfigurationListener;
import org.apache.sqoop.error.code.AuditLoggerError;
import org.apache.sqoop.utils.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuditLoggerManager implements Reconfigurable {

  /**
   * Logger object for this class
   */
  private static final Logger LOG = Logger.getLogger(AuditLoggerManager.class);

  /**
   * All audit loggers
   */
  private List<AuditLogger> loggers;

  /**
   * Private instance to singleton of this class
   */
  private static AuditLoggerManager instance;

  /**
   * Create default object
   */
  static {
    instance = new AuditLoggerManager();
  }

  /**
   * Return current instance
   *
   * @return Current instance
   */
  public static AuditLoggerManager getInstance() {
    return instance;
  }

  private AuditLoggerManager() {
  }

  public synchronized void initialize() {
    LOG.info("Begin audit logger manager initialization");
    if (loggers == null) {
      loggers = new ArrayList<AuditLogger>();
    }
    initializeLoggers();

    SqoopConfiguration.getInstance().getProvider()
        .registerListener(new CoreConfigurationListener(this));

    LOG.info("Audit logger manager initialized: OK");
  }

  private void initializeLoggers() {
    loggers.clear();

    MapContext context = SqoopConfiguration.getInstance().getContext();

    Map<String, String> auditLoggerProps = context.getNestedProperties(
        AuditLoggerConstants.PREFIX_AUDITLOGGER_CONFIG);

    // Initialize audit loggers
    for (Map.Entry<String, String> entry : auditLoggerProps.entrySet()) {
      String key = entry.getKey();
      if (key.endsWith(AuditLoggerConstants.SUFFIX_AUDITLOGGER_CLASS)) {
        String loggerName = key.substring(0, key.indexOf("."));
        String loggerClassName = entry.getValue();

        if (loggerClassName == null || loggerClassName.trim().length() == 0) {
          throw new SqoopException(AuditLoggerError.AUDIT_0001,
              "Logger name: " + loggerName);
        }

        Class<?> loggerClass =
            ClassUtils.loadClass(loggerClassName);

        if (loggerClass == null) {
          throw new SqoopException(AuditLoggerError.AUDIT_0001,
              "Logger Class: " + loggerClassName);
        }

        AuditLogger newLogger;
        try {
          newLogger = (AuditLogger) loggerClass.newInstance();
        } catch (Exception ex) {
          throw new SqoopException(AuditLoggerError.AUDIT_0001,
              "Logger Class: " + loggerClassName, ex);
        }

        newLogger.setLoggerName(loggerName);
        newLogger.initialize();
        loggers.add(newLogger);
        LOG.info("Audit Logger has been initialized: " + loggerName);
      }
    }
  }

  public synchronized void destroy() {
    loggers = null;
    LOG.trace("Begin audit logger manager destroy");
  }

  public synchronized void logAuditEvent(String username,
      String ip, String operation, String objectType, String objectId) {
    for (AuditLogger logger : loggers) {
      logger.logAuditEvent(username, ip, operation, objectType, objectId);
    }
  }

  @Override
  public synchronized void configurationChanged() {
    LOG.info("Begin audit logger manager reconfiguring");
    initializeLoggers();
    LOG.info("Audit logger manager reconfigured");
  }

}
