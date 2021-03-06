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
package org.apache.sqoop.submission.counter;

import org.apache.sqoop.classification.InterfaceAudience;
import org.apache.sqoop.classification.InterfaceStability;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
@InterfaceAudience.Private
@InterfaceStability.Unstable
public class Counters implements Iterable<CounterGroup> {
  Map<String, CounterGroup> groups;

  public Counters() {
    this.groups = new HashMap<String, CounterGroup>();
  }

  public Counters addCounterGroup(CounterGroup group) {
    groups.put(group.getName(), group);
    return this;
  }

  public CounterGroup getCounterGroup(String name) {
    return groups.get(name);
  }

  @Override
  public Iterator<CounterGroup> iterator() {
    return groups.values().iterator();
  }

  public boolean isEmpty() {
    return groups.isEmpty();
  }
}
