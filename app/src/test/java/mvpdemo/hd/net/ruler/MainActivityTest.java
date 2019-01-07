package mvpdemo.hd.net.ruler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import mvpdemo.hd.net.ruler.UI.MainActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainActivityTest {

    @Before
    public void setUp() {
//        Activity activity = Robolectric.setupActivity(MainActivity.class);
    }

//    @After
//    public void tearDown() throws Exception {
//    }

    @Test
    public void onCreate() {
        MainActivity sampleActivity = Robolectric.setupActivity(MainActivity.class);
        assertNotNull(sampleActivity);
        assertEquals(sampleActivity.getTitle(), sampleActivity.getResources().getString(R.string.app_name));
    }
}