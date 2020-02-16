import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        DynamicsDaoUnitTest.class,
        DynamicsDaoIntegrationTest.class
})

public class MicrosoftDataAccessObjectTestSuite { }

