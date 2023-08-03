/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.contract.verifier.builder;

import org.springframework.cloud.contract.spec.internal.DslProperty;
import org.springframework.cloud.contract.spec.internal.MatchingStrategy;
import org.springframework.cloud.contract.spec.internal.OptionalProperty;
import org.springframework.cloud.contract.spec.internal.QueryParameter;

interface QueryParamsResolver {

	/**
	 * Converts the query parameter value into String
	 */
	default String resolveParamValue(Object value) {
		if (value instanceof QueryParameter parameter) {
			return resolveParamValue(parameter.getServerValue());
		}
		else if (value instanceof OptionalProperty property) {
			return resolveParamValue(property.optionalPattern());
		}
		else if (value instanceof MatchingStrategy strategy) {
			return resolveParamValue(strategy.getServerValue());
		}
		else if (value instanceof DslProperty property) {
			return resolveParamValue(property.getServerValue());
		}
		return value == null ? "null" : value.toString();
	}

}
