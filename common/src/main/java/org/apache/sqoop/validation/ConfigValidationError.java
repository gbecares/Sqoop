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
package org.apache.sqoop.validation;

import org.apache.sqoop.classification.InterfaceAudience;
import org.apache.sqoop.classification.InterfaceStability;
import org.apache.sqoop.common.ErrorCode;

/**
 *
 */
@InterfaceAudience.Public
@InterfaceStability.Unstable
public enum ConfigValidationError implements ErrorCode {

  VALIDATION_0000("Unknown error"),

  VALIDATION_0001("Missing class declaration."),

  VALIDATION_0002("Usage of missing field"),

  VALIDATION_0003("Invalid representation of config and input field"),

  VALIDATION_0004("Can't find validator class"),

  ;

  private final String message;

  private ConfigValidationError(String message) {
    this.message = message;
  }

  public String getCode() {
    return name();
  }

  public String getMessage() {
    return message;
  }
}
