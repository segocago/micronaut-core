/*
 * Copyright 2017-2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.http.filter;

import io.micronaut.core.util.PathMatcher;

public enum FilterPatternStyle {
  /**
   * Ant-style pattern matching.
   * @see io.micronaut.core.util.AntPathMatcher
   */
  ANT,
  /**
   * Regex-style pattern matching.
   * @see io.micronaut.core.util.RegexPathMatcher
   */
  REGEX;

  public PathMatcher getPathMatcher() {
    return this.equals(FilterPatternStyle.REGEX) ? PathMatcher.REGEX : PathMatcher.ANT;
  }

  public static FilterPatternStyle defaultStyle() {
    return ANT;
  }
}
