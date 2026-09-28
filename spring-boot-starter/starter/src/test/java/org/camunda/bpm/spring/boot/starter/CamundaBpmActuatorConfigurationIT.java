/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.spring.boot.starter;

import static org.assertj.core.api.Assertions.assertThat;

import org.camunda.bpm.spring.boot.starter.test.nonpa.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import com.jayway.jsonpath.JsonPath;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class CamundaBpmActuatorConfigurationIT extends AbstractCamundaAutoConfigurationIT{

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  public void jobExecutorHealthIndicatorTest() {
    final String body = getHealthBody();
    assertThat(readHealth(body, "jobExecutor.status")).as("wrong body %s", body).isEqualTo("UP");
  }

  @Test
  public void processEngineHealthIndicatorTest() {
    final String body = getHealthBody();
    assertThat(readHealth(body, "processEngine.status")).as("wrong body %s", body).isEqualTo("UP");
    assertThat(readHealth(body, "processEngine.details.name")).as("wrong body %s", body).isEqualTo("testEngine");
  }

  /**
   * Reads a value out of the health response by path, so the assertions do not depend on the order
   * in which the health details are serialized.
   */
  private String readHealth(String body, String path) {
    return JsonPath.read(body, "$.components." + path);
  }

  private String getHealthBody() {
    ResponseEntity<String> entity = testRestTemplate.getForEntity("/actuator/health", String.class);
    final String body = entity.getBody();
    return body;
  }
}
