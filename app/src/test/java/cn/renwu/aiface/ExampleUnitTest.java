package cn.renwu.aiface;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws InterruptedException {
        assertEquals(4, 2 + 2);
        URI uri = URI.create("ws://192.168.2.178:8887");
        JWebSClient client = new JWebSClient(uri);
        client.connectBlocking();
        assertEquals(true, client.isOpen());
    }
}