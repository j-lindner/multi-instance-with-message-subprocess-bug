package org.camunda.community.messagecorrelator;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest;
import io.camunda.zeebe.process.test.inspections.InspectionUtility;
import io.camunda.zeebe.process.test.inspections.model.InspectedProcessInstance;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import test.MyObject;

@ZeebeProcessTest
public class ZeebeServiceTest {

  private ZeebeTestEngine engine;
  private ZeebeClient client;

  @Test
  public void test() throws Exception {

    String myId = "test";

    DeploymentEvent deploymentEvent =
        client.newDeployCommand().addResourceFromClasspath("process.bpmn").send().join();
    BpmnAssert.assertThat(deploymentEvent);

    List<MyObject> myObjects = new ArrayList<>();
    myObjects.add(new MyObject().setMyId("id1"));
    myObjects.add(new MyObject().setMyId("id2"));
    myObjects.add(new MyObject().setMyId("id3"));

    client
        .newCreateInstanceCommand()
        .bpmnProcessId("example_process")
        .latestVersion()
        .variables(Map.of("myObjects", myObjects))
        .send()
        .join();

    engine.waitForIdleState(Duration.ofSeconds(5));
    InspectedProcessInstance processInstance =
        InspectionUtility.findProcessInstances().findLastProcessInstance().get();
    BpmnAssert.assertThat(processInstance).isStarted();
    BpmnAssert.assertThat(processInstance).isActive();
    BpmnAssert.assertThat(processInstance).isWaitingAtElements("MyUserTask");
  }

  @Test
  public void testWithWorkaround() throws Exception {

    String myId = "test";

    DeploymentEvent deploymentEvent =
        client
            .newDeployCommand()
            .addResourceFromClasspath("processWithWorkaround.bpmn")
            .send()
            .join();
    BpmnAssert.assertThat(deploymentEvent);

    List<MyObject> myObjects = new ArrayList<>();
    myObjects.add(new MyObject().setMyId("id1"));
    myObjects.add(new MyObject().setMyId("id2"));
    myObjects.add(new MyObject().setMyId("id3"));

    client
        .newCreateInstanceCommand()
        .bpmnProcessId("example_process")
        .latestVersion()
        .variables(Map.of("myObjects", myObjects))
        .send()
        .join();

    engine.waitForIdleState(Duration.ofSeconds(5));
    InspectedProcessInstance processInstance =
        InspectionUtility.findProcessInstances().findLastProcessInstance().get();
    BpmnAssert.assertThat(processInstance).isStarted();
    BpmnAssert.assertThat(processInstance).isActive();
    BpmnAssert.assertThat(processInstance).isWaitingAtElements("MyUserTask");
  }
}
