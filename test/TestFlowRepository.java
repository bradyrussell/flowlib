import com.bradyrussell.flow.lib.Constants;
import com.bradyrussell.flow.lib.graph.Flow;
import com.bradyrussell.flow.lib.repository.FlowArtifact;
import com.bradyrussell.flow.lib.repository.FlowDependency;
import com.bradyrussell.flow.lib.repository.FlowArtifactBase;
import com.bradyrussell.flow.lib.repository.rest.RestFlowArtifact;
import com.bradyrussell.flow.lib.repository.rest.RestFlowRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TestFlowRepository {
    @Test
    void TestVersionComparison() {
        Assertions.assertEquals(FlowArtifact.compareVersion("1.2.3.4", "1.2.3.5-a"), -1);
        Assertions.assertEquals(FlowArtifact.compareVersion("1.2.3.4", "1.2.3.4-tag"), 0);
        Assertions.assertEquals(FlowArtifact.compareVersion("1.2.3.4-othertag", "1.2.3"), 1);
    }

    @Test
    void TestFlowDependencyString() {
        Gson gson = Constants.gson.get();
        //com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot@rest://https://uiscoin.com
        //com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot+@rest://https://uiscoin.com
        //com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot-@rest://https://uiscoin.com
        //com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot|2.0.0-snapshot@rest://https://uiscoin.com

        Assertions.assertEquals("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot@rest://https://uiscoin.com", FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot@rest://https://uiscoin.com").toDependencyString());
        Assertions.assertEquals("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot+@rest://https://uiscoin.com", FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot+@rest://https://uiscoin.com").toDependencyString());
        Assertions.assertEquals("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot-@rest://https://uiscoin.com", FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot-@rest://https://uiscoin.com").toDependencyString());
        Assertions.assertEquals("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot|2.0.0-snapshot@rest://https://uiscoin.com", FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot|2.0.0-snapshot@rest://https://uiscoin.com").toDependencyString());

        System.out.println(gson.toJson(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot@rest://https://uiscoin.com")));
        System.out.println(gson.toJson(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot+@rest://https://uiscoin.com")));
        System.out.println(gson.toJson(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot-@rest://https://uiscoin.com")));
        System.out.println(gson.toJson(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot|2.0.0-snapshot@rest://https://uiscoin.com")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> System.out.println(gson.toJson(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot|0.1.0-snapshot@rest://https://uiscoin.com"))));
    }

    @Test
    void TestFlowDependencyMatch() {
        Assertions.assertTrue(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot@rest://https://uiscoin.com").isMatchingArtifact("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot@rest://https://uiscoin.com"));
        Assertions.assertTrue(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot+@rest://https://uiscoin.com").isMatchingArtifact("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot@rest://https://uiscoin.com"));
        Assertions.assertTrue(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot-@rest://https://uiscoin.com").isMatchingArtifact("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot@rest://https://uiscoin.com"));
        Assertions.assertTrue(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot|2.0.0-snapshot@rest://https://uiscoin.com").isMatchingArtifact("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot@rest://https://uiscoin.com"));

        Assertions.assertFalse(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.1-snapshot@rest://https://uiscoin.com").isMatchingArtifact("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot@rest://https://uiscoin.com"));
        Assertions.assertFalse(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.1-snapshot+@rest://https://uiscoin.com").isMatchingArtifact("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot@rest://https://uiscoin.com"));
        Assertions.assertFalse(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot-@rest://https://uiscoin.com").isMatchingArtifact("com.bradyrussell.flow.MyExampleFlow:1.0.1-snapshot@rest://https://uiscoin.com"));
        Assertions.assertFalse(FlowDependency.fromDependencyString("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot|2.0.0-snapshot@rest://https://uiscoin.com").isMatchingArtifact("com.bradyrussell.flow.MyExampleFlow:3.0.0-snapshot@rest://https://uiscoin.com"));
    }

    @Test @Disabled
    void TestGetFlow() throws ExecutionException, InterruptedException {
        Gson gson = Constants.gson.get();
        RestFlowRepository repository = new RestFlowRepository("https://uiscoin.com");

        CompletableFuture<Flow> flowCompletableFuture = repository.getFlowByFullyQualifiedId("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot");
        Flow flow = flowCompletableFuture.get();

        System.out.println(gson.toJson(flow));

        CompletableFuture<FlowArtifact> artifactCompletableFuture = repository.getArtifactByFullyQualifiedId("com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot");
        FlowArtifact artifact = artifactCompletableFuture.get();

        System.out.println(gson.toJson(artifact));

        Flow flow1 = artifact.getFlow().get();

        System.out.println(gson.toJson(flow1));
    }
}
