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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.assertj.core.api.BDDAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.springframework.cloud.contract.verifier.config.ContractVerifierConfigProperties;
import org.springframework.cloud.contract.verifier.config.TestFramework;
import org.springframework.cloud.contract.verifier.file.ContractMetadata;
import org.springframework.util.FileSystemUtils;

import static org.springframework.cloud.contract.verifier.util.ContractVerifierDslConverter.convertAsCollection;

public class GeneratedTestClassTests {

	// @formatter:off
	String contract = """
            org.springframework.cloud.contract.spec.Contract.make {
             name "foo"
             request {
              method 'PUT'
              url 'url'
              headers {
                header('foo', 'bar')
              }
              body (
                ["foo1":"bar1"]
              )
             }
             response {
              status OK()
              headers {
                header('foo2', 'bar2')
              }
              body (
                ["foo3":"bar3"]
              )
             }
            }\
            """;
	// @formatter:on

	// @formatter:off
	String expectedTest = """
package test;

import BazBar;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.Rule;
import org.junit.Ignore;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.response.ResponseOptions;

import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;
import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.*;
import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;

@SuppressWarnings("rawtypes")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FooBarTest extends BazBar {

	@Test
	@Ignore
	public void validate_foo() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("foo", "bar")
					.body("{\\"foo1\\":\\"bar1\\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("url");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("foo2")).isEqualTo("bar2");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['foo3']").isEqualTo("bar3");
	}

}
""";
	// @formatter:on

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	File file;

	File tmp;

	@Before
	public void setup() throws IOException, URISyntaxException {
		this.file = this.tmpFolder.newFile();
		Files.write(this.file.toPath(), this.contract.getBytes());
		this.tmp = this.tmpFolder.newFolder();
		File classpath = new File(GeneratedTestClassTests.class.getResource("/classpath/").toURI());
		FileSystemUtils.copyRecursively(classpath, this.tmp);
	}

	@Test
	public void should_work_for_junit4_mockmvc_json_non_binary() {
		// given
		JavaTestGenerator generator = new JavaTestGenerator();
		ContractVerifierConfigProperties configProperties = new ContractVerifierConfigProperties();
		configProperties.setTestFramework(TestFramework.JUNIT);
		Collection<ContractMetadata> contracts = Collections.singletonList(
				new ContractMetadata(this.file.toPath(), true, 1, 2, convertAsCollection(new File("/"), this.file)));
		String includedDirectoryRelativePath = "some/path";
		String convertedClassName = "fooBar";
		String packageName = "test";
		Path classPath = new File("/tmp").toPath();
		configProperties.setBaseClassForTests("BazBar");

		// when
		String builtClass = generator.buildClass(configProperties, contracts, includedDirectoryRelativePath,
				new SingleTestGenerator.GeneratedClassData(convertedClassName, packageName, classPath));

		// then
		BDDAssertions.then(builtClass).isEqualTo(this.expectedTest);
	}

}