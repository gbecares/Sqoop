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
package org.apache.sqoop.json;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.sqoop.classification.InterfaceAudience;
import org.apache.sqoop.classification.InterfaceStability;
import org.apache.sqoop.model.MConnector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Json representation of the connectors object
 *
 */
@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ConnectorsBean extends ConnectorBean {

  // to represent the config and inputs with values
  private static final String CONNECTORS = "connectors";

  // for "extract"
  public ConnectorsBean(List<MConnector> connectors, Map<String, ResourceBundle> bundles) {
    super(connectors, bundles);
  }

  // for "restore"
  public ConnectorsBean() {
  }

  @SuppressWarnings("unchecked")
  @Override
  public JSONObject extract(boolean skipSensitive) {
    JSONArray connectorArray = extractConnectors(skipSensitive);
    JSONObject connectors = new JSONObject();
    connectors.put(CONNECTORS, connectorArray);
    return connectors;
  }

  @Override
  public void restore(JSONObject jsonObject) {
    JSONArray array = JSONUtils.getJSONArray(jsonObject, CONNECTORS);
    super.restoreConnectors(array);
  }
}
