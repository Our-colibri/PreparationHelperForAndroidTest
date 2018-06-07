package jp.kemomimi.testsample;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.kemomimi.testhelper.SettingsHelper;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void testDateSetting() {
        SettingsHelper helper = new SettingsHelper(InstrumentationRegistry.getInstrumentation());
        helper.setSettingAppDateMoveMonth(1);
        helper.setSettingAppDateSet(new Date(System.currentTimeMillis() - (3L * 1000L*60L*60L*24L*365)));
        helper.setSettingAppDateAuto(true);

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("jp.kemomimi.testsample", appContext.getPackageName());
    }

    @Test
    public void testAirplane(){
        SettingsHelper helper = new SettingsHelper(InstrumentationRegistry.getInstrumentation());
        helper.setSettingAirplaneMode(true);
        helper.setSettingAirplaneMode(false);

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("jp.kemomimi.testsample", appContext.getPackageName());
    }

    @Test
    public void testWifi(){
        SettingsHelper helper = new SettingsHelper(InstrumentationRegistry.getInstrumentation());
        helper.setSettingWifiEnable(false);
        helper.setSettingWifiEnable(true);

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("jp.kemomimi.testsample", appContext.getPackageName());
    }

    @Test
    public void testClock(){
        SettingsHelper helper = new SettingsHelper(InstrumentationRegistry.getInstrumentation());
        SimpleDateFormat df = new SimpleDateFormat("kk:mm",Locale.JAPAN);
        try {
            helper.setSettingClock(df.parse("23:59"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("jp.kemomimi.testsample", appContext.getPackageName());
    }
}
