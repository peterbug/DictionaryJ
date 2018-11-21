package mvpdemo.hd.net.ruler;

import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testMath() {
        double[] data = {3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9};
        for (double d : data) {
//            assertEquals(Math.ceil(d),);
//            assertEquals(Math.floor(d)));
//            assertEquals(Math.round(d)));
//
//            d = -d;
//            assertEquals(Math.ceil(d)));
//            assertEquals(Math.floor(d)));
//            assertEquals(Math.round(d)));
        }
        assertEquals(Math.round(-3.5), -3);
    }
}