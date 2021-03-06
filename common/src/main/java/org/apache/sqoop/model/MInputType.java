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
package org.apache.sqoop.model;

import org.apache.sqoop.classification.InterfaceAudience;
import org.apache.sqoop.classification.InterfaceStability;

/**
 * Represents the various input types supported by the system.
 */
@InterfaceAudience.Private
@InterfaceStability.Unstable
public enum MInputType {

  /** Unknown input type */
  OTHER,

  /** String input type */
  STRING,

  /** Map input type */
  MAP,

  /** Integer input type */
  INTEGER,

  /** Boolean input type */
  BOOLEAN,

  /** String based input that can contain only predefined values **/
  ENUM,

  /** Long input type */
  LONG,

  /** List input type */
  LIST,

  /** DateTime input type */
  DATETIME

  ;
}
