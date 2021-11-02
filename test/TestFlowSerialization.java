import com.bradyrussell.flow.lib.Constants;
import com.bradyrussell.flow.lib.adapter.UISCoinLangFlowAdapter;
import com.bradyrussell.flow.lib.graph.*;
import com.bradyrussell.flow.lib.repository.FlowDependency;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TestFlowSerialization {
    @Test
    void TestSerialization(){
        //Arrange
        Gson gson = Constants.gson.get();
        Flow f = new Flow();
        f.setMeta("Name", "example_flow");

        NodeDefinition nodeDefinition = new NodeDefinition();
        nodeDefinition.setId("Print");
        nodeDefinition.setInputs(List.of(new VariableDefinition("Message", "string")));
        f.addNodeDefinition(nodeDefinition);
        StructDefinition s = new StructDefinition();
        s.setId("myStruct");
        s.setColor("0xffffff");
        VariableDefinition var1 = new VariableDefinition();
        var1.setId("var1");
        var1.setType("int64");
        VariableDefinition var2 = new VariableDefinition();
        var2.setId("var2");
        var2.setType("int32");
        s.setVariables(List.of(var1, var2));
        f.addStruct(s);
        Node node = new Node(f);
        node.setId("Print",0);
        node.setX(1);
        node.setY(2);
        node.setValue("Message", "Hello world!");
        node.setValue("Log", "true");
        f.addNode(node);
        Node node2 = new Node(f);
        node2.setId("Print",1);
        node2.setX(2);
        node2.setY(1);
        node2.setValue("Message", "Hello world 2!");
        node2.setValue("Log", "false");
        f.addNode(node2);
        Connection c = new Connection();
        c.setBetween(List.of("Flow.Begin", "Print#1.FlowIn"));
        f.addConnection(c);
        Connection c2 = new Connection();
        c2.setBetween(List.of("Print#1.FlowOut", "Print#0.FlowIn"));
        f.addConnection(c2);
        Connection c3 = new Connection();
        c3.setBetween(List.of("Print#0.FlowOut", "Flow.End"));
        f.addConnection(c3);
        System.out.println(f.getNextNodeIndex("Print"));
        String pin0 = f.getConnectedPinId("Flow", "Begin");
        System.out.println(pin0);
        String pin1 = f.getConnectedPinId(pin0.replace("In", "Out"));
        System.out.println(pin1);
        String pin2 = f.getConnectedPinId(pin1.replace("In", "Out"));
        System.out.println(pin2);
        f.addDependency(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot@rest://https://uiscoin.com"));

        //Act
        String json = gson.toJson(f);
        System.out.println(json);
        Flow df = gson.fromJson(json, Flow.class);
        String json2 = gson.toJson(df);
        System.out.println(json2);
        try {
            Files.writeString(Path.of("example_flow.fl"), json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Assert
        Assertions.assertEquals(json, json2);
    }

    @Test
    public void TestFlowVisitor() throws IOException {
        Gson gson = Constants.gson.get();
        String flowJson = Files.readString(Path.of("example_flow.fl"));
        Flow flow = gson.fromJson(flowJson, Flow.class);
        flow.load();
        UISCoinLangFlowAdapter uisCoinLangFlowAdapter = new UISCoinLangFlowAdapter();
        System.out.println(uisCoinLangFlowAdapter.visitFlow(flow));
    }
}
