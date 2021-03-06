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
package org.apache.sqoop.server.v1;

import org.apache.sqoop.handler.DriverRequestHandler;
import org.apache.sqoop.json.JsonBean;
import org.apache.sqoop.server.RequestContext;
import org.apache.sqoop.server.RequestHandler;
import org.apache.sqoop.server.SqoopProtocolServlet;

/**
 * Displays driver registered in sqoop
 *
 * GET /v1/driver/
 *  Return details about the registered driver and its configs
 */
@SuppressWarnings("serial")
public class DriverServlet extends SqoopProtocolServlet {
  private static final long serialVersionUID = 1L;

  private RequestHandler driverRequestHandler;

  public DriverServlet() {
    driverRequestHandler = new DriverRequestHandler();
  }

  @Override
  protected JsonBean handleGetRequest(RequestContext ctx) throws Exception {
    return driverRequestHandler.handleEvent(ctx);
  }
}
