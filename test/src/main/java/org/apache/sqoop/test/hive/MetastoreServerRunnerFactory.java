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
package org.apache.sqoop.test.hive;

import java.util.Properties;

/**
 * Create HiveServer2 runner.
 */
public class MetastoreServerRunnerFactory {

  public static final String RUNNER_CLASS_PROPERTY = "sqoop.hive.metastore.runner.class";

  public static final String HOSTNAME_PROPERTY = "sqoop.hive.metastore.server.hostname";

  public static final String PORT_PROPERTY = "sqoop.hive.metastore.server.port";

  public static final String DEFAULT_HOSTNAME = "127.0.0.1";

  public static final String DEFAULT_PORT = "0";

  public static MetastoreServerRunner getRunner(
      Properties properties, Class<? extends MetastoreServerRunner> defaultRunner)
      throws Exception {
    Class<?> klass;

    String hostname = properties.getProperty(HOSTNAME_PROPERTY, DEFAULT_HOSTNAME);
    int port = Integer.parseInt(
        properties.getProperty(PORT_PROPERTY, DEFAULT_PORT));

    String className = properties.getProperty(RUNNER_CLASS_PROPERTY);
    if(className == null) {
      klass = defaultRunner;
    } else {
      klass = Class.forName(className);
    }

    return (MetastoreServerRunner)klass.getConstructor(String.class, int.class)
        .newInstance(hostname, port);
  }
}
