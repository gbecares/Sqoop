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
package org.apache.sqoop.connector.matcher;

import java.io.Serializable;

import org.apache.sqoop.classification.InterfaceAudience;
import org.apache.sqoop.classification.InterfaceStability;
import org.apache.sqoop.schema.Schema;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class MatcherFactory implements Serializable {
  public static Matcher getMatcher(Schema fromSchema, Schema toSchema) {
    if (toSchema.isEmpty() || fromSchema.isEmpty()) {
      return new LocationMatcher(fromSchema, toSchema);
    } else {
      return new NameMatcher(fromSchema, toSchema);
    }
  }
}
