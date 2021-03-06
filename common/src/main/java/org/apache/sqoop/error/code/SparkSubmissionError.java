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

/**
 *
 */
public enum SparkSubmissionError implements ErrorCode {

    SPARK_0001("Unknown error"),

    SPARK_0002("Failure on submission engine initialization"),

    SPARK_0003("Can't get RunningJob instance"),

    SPARK_0004("Unknown spark job status"),

    SPARK_0005("Failure on submission engine destroy"),

    ;

    private final String message;

    private SparkSubmissionError(String message) {
        this.message = message;
    }

    public String getCode() {
        return name();
    }

    public String getMessage() {
        return message;
    }
}