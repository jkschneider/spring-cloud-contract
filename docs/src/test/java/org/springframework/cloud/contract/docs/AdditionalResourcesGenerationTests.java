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

package org.springframework.cloud.contract.docs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.contract.spec.Contract;
import org.springframework.cloud.contract.verifier.converter.YamlContractConverter;

class AdditionalResourcesGenerationTests {

	// @formatter:off
	private static final String CONTRACT = """
            description: Some description
            name: some name
            priority: 8
            ignored: true
            inProgress: true
            request:
              method: PUT
              url: /foo
              queryParameters:
                a: b
                b: c
              headers:
                foo: bar
                fooReq: baz
              cookies:
                foo: bar
                fooReq: baz
              body:
                foo: bar
              matchers:
                body:
                  - path: $.foo
                    type: by_regex
                    value: bar
                headers:
                  - key: foo
                    regex: bar
            response:
              status: 200
              fixedDelayMilliseconds: 1000
              headers:
                foo2: bar
                foo3: foo33
                fooRes: baz
              body:
                foo2: bar
                foo3: baz
                nullValue: null
              matchers:
                body:
                  - path: $.foo2
                    type: by_regex
                    value: bar
                  - path: $.foo3
                    type: by_command
                    value: executeMe($it)
                  - path: $.nullValue
                    type: by_null
                    value: null
                headers:
                  - key: foo2
                    regex: bar
                  - key: foo3
                    command: andMeToo($it)
                cookies:
                  - key: foo2
                    regex: bar
                  - key: foo3
                    predefined:
            """;
	// @formatter:on

	@Test
	void should_convert_yaml_to_contract() throws IOException {
		File ymlFile = new File("target/contract.yml");
		Files.write(ymlFile.toPath(), CONTRACT.getBytes());

		Collection<Contract> contracts = new YamlContractConverter().convertFrom(ymlFile);

		BDDAssertions.then(contracts).isNotEmpty();
	}

}
