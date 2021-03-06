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
package org.apache.sqoop.common;

import org.apache.sqoop.classification.InterfaceAudience;
import org.apache.sqoop.classification.InterfaceStability;

import java.util.Map;

/**
 * Mutable addition to immutable context.
 */
@InterfaceAudience.Public
@InterfaceStability.Unstable
public interface MutableContext extends ImmutableContext {

  /**
   * Set string value for given key.
   *
   * @param key Key
   * @param value New value
   */
  public void setString(String key, String value);

  /**
   * Set long value for given key.
   *
   * @param key Key
   * @param value New value
   */
  public void setLong(String key, long value);

  /**
   * Set integer value for given key.
   *
   * @param key Key
   * @param value New value
   */
  public void setInteger(String key, int value);

  /**
   * Set boolean value for given key.
   *
   * @param key Key
   * @param value New value
   */
  public void setBoolean(String key, boolean value);

  /**
   * Add all properties from given map to this context instance.
   */
  public void setAll(Map<String, String> map);
}
