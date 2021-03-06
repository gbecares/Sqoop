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
package org.apache.sqoop.error.code;

import org.apache.sqoop.common.ErrorCode;

public enum AuditLoggerError implements ErrorCode {

  /** An unknown error has occurred. */
  AUDIT_0000("An unknown error has occurred"),

  /** The system was unable to find or load the audit logger provider. */
  AUDIT_0001("The system was unable to find or load audit logger class"),

  /** The audit logger name is not given. */
  AUDIT_0002("The logger name for FileAuditLogger is not specified"),

  ;

  private final String message;

  private AuditLoggerError(String message) {
    this.message = message;
  }

  @Override
  public String getCode() {
    return name();
  }

  @Override
  public String getMessage() {
    return message;
  }
}
