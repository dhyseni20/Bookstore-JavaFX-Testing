import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;


public class Driver {
    public static void main(String[] args) {

        Class<?>[] testClasses = {TestUser.class, TestAuthentication.class, TestAuthorization.class, TestAuthor.class, TestBook.class, TestOrder.class};

        Launcher launcher = LauncherFactory.create();

        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);

        for (Class<?> testClass : testClasses) {
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(
                            org.junit.platform.engine.discovery.DiscoverySelectors.selectClass(testClass.getName())
                    )
                    .build();


            launcher.execute(request);

            System.out.println("Test class: " + testClass.getName());
            System.out.println("Total tests run: " + listener.getSummary().getTestsFoundCount());
            System.out.println("Total failures: " + listener.getSummary().getTestsFailedCount());
            listener.getSummary().getFailures().forEach(failure -> System.out.println(failure.toString()));

            listener = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(listener);
        }
    }
}
