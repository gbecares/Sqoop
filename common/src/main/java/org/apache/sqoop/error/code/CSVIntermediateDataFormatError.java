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

import org.apache.sqoop.classification.InterfaceAudience;
import org.apache.sqoop.classification.InterfaceStability;
import org.apache.sqoop.common.ErrorCode;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public enum CSVIntermediateDataFormatError implements ErrorCode {
  /** An unknown error has occurred. */
  CSV_INTERMEDIATE_DATA_FORMAT_0000("An unknown error has occurred."),

  /** An encoding is missing in the Java native libraries. */
  CSV_INTERMEDIATE_DATA_FORMAT_0001("Native character set error."),

  /** Error while escaping a row. */
  CSV_INTERMEDIATE_DATA_FORMAT_0002("An error has occurred while escaping a row."),

  /** Error while escaping a row. */
  CSV_INTERMEDIATE_DATA_FORMAT_0003("An error has occurred while unescaping a row."),

  /**
   * For arrays and maps we use JSON representation and incorrect representation
   * results in parse exception
   */
  CSV_INTERMEDIATE_DATA_FORMAT_0004("JSON parse internal error."),

  /** Unsupported bit values */
  CSV_INTERMEDIATE_DATA_FORMAT_0005("Unsupported bit value."),

  ;

  private final String message;

  private CSVIntermediateDataFormatError(String message) {
    this.message = message;
  }

  public String getCode() {
    return name();
  }

  public String getMessage() {
    return message;
  }
}
