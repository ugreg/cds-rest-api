import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestJunit {
    String message = "Mr.Universe";

    @Test
    public void testPrintMessage() {
        System.out.println("Inside testPrintMessage()");
        assertEquals(message, "Mr.Universe");
    }
}
