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
package org.apache.sqoop.server;

import org.apache.sqoop.handler.VersionRequestHandler;
import org.apache.sqoop.json.JsonBean;

/**
 * Exposes the supported versions available in the server.
 *
 */
@SuppressWarnings("serial")
public class VersionServlet extends SqoopProtocolServlet {
  private static final long serialVersionUID = 1L;

  private RequestHandler versionRequestHandler;

  public VersionServlet() {
    versionRequestHandler = new VersionRequestHandler();
  }

  @Override
  protected JsonBean handleGetRequest(RequestContext ctx) throws Exception {
    return versionRequestHandler.handleEvent(ctx);
  }
}
