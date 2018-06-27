package jp.kemomimi.testhelper;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SettingsHelper extends HelperBase{

    /**
     * Construct a SettingsHelper object.
     *
     * @param instrumentation Usually your {@link Instrumentation} object.
     */
    public SettingsHelper(Instrumentation instrumentation){
        super(instrumentation);
    }


    private void setDay(int day) throws UnsupportedDeviceException{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            setDay_M_or_later(day);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            setDay_JBMR2_or_later(day);
        }
    }
    private void setDay_M_or_later(int day) throws UnsupportedDeviceException{
        UiDevice mDevice;
        Instrumentation instrumentation = mInstrumentation;
        mDevice = UiDevice.getInstance(instrumentation);

        mDevice.wait(Until.hasObject(By.res("android:id/datePicker")),10000);

        UiObject2 date_picker_day_picker = mDevice.findObject(By.res("android:id/date_picker_day_picker"));
        if(date_picker_day_picker == null) throw new UnsupportedDeviceException();

        UiObject2 month_view = date_picker_day_picker.findObject(By.res("android:id/month_view"));
        if(month_view == null) throw new UnsupportedDeviceException();

        UiObject2 today = month_view.findObject(By.text(String.valueOf(day)));

        if(today != null){
            today.click();
            mDevice.wait(Until.hasObject(By.checked(true)),10000);
        }
    }

    private void setDay_JBMR2_or_later(int day) throws UnsupportedDeviceException{
        UiDevice mDevice;
        Instrumentation instrumentation = mInstrumentation;
        mDevice = UiDevice.getInstance(instrumentation);

        mDevice.wait(Until.hasObject(By.res("android:id/datePicker")),10000);

        UiObject2 animator = mDevice.findObject(By.res("android:id/animator"));
        if(animator == null) throw new UnsupportedDeviceException();

        UiObject2 today = animator.findObject(By.descStartsWith(String.format("%02d",day)));

        if(today != null){
            today.click();
            mDevice.wait(Until.hasObject(By.selected(true)),10000);
        }
    }

    private void moveMonth(int moveMonth) throws UnsupportedDeviceException{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            moveMonth_M_or_later(moveMonth);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            moveMonth_JBMR2_or_later(moveMonth);
        }
    }
    private void moveMonth_M_or_later(int moveMonth) throws UnsupportedDeviceException{
        if(moveMonth == 0) return;

        UiDevice mDevice;
        Instrumentation instrumentation = mInstrumentation;
        mDevice = UiDevice.getInstance(instrumentation);

        mDevice.wait(Until.hasObject(By.res("android:id/datePicker")),10000);
        UiObject2 date_picker_day_picker = mDevice.findObject(By.res("android:id/date_picker_day_picker"));
        if(date_picker_day_picker == null) throw new UnsupportedDeviceException();

        UiObject2 month_view = date_picker_day_picker.findObject(By.res("android:id/month_view"));

        String clickResName = "android:id/prev";
        if (moveMonth > 0) {
            clickResName = "android:id/next";
        }
        moveMonth = Math.abs(moveMonth);
        for (int i = 0; moveMonth > i; i++) {
            date_picker_day_picker.findObject(By.res(clickResName)).click();
        }


        mDevice.wait(Until.gone(By.checked(true)),10000);

    }

    private void moveMonth_JBMR2_or_later(int moveMonth) throws UnsupportedDeviceException {
        if (moveMonth == 0) return;

        final String DATE_FORMAT = "dd MMMM yyyy";
        UiDevice mDevice;
        Instrumentation instrumentation = mInstrumentation;
        mDevice = UiDevice.getInstance(instrumentation);
        Context context = instrumentation.getTargetContext();

        mDevice.wait(Until.hasObject(By.res("android:id/datePicker")), 10000);
        UiObject2 date_picker_year = mDevice.findObject(By.res("android:id/date_picker_year"));
        UiObject2 date_picker_month = mDevice.findObject(By.res("android:id/date_picker_month"));
        UiObject2 date_picker_day = mDevice.findObject(By.res("android:id/date_picker_day"));
        if (date_picker_year == null || date_picker_month == null | date_picker_day == null){
            throw new UnsupportedDeviceException();
        }
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        int year = Integer.valueOf(date_picker_year.getText());
        int month = Integer.valueOf(new SimpleDateFormat("MM").format(new Date()));
        int day = Integer.valueOf(date_picker_day.getText());
        cal.set(year, (month-1) + moveMonth, day);

        String monthAndDayText = DateFormat.format(DATE_FORMAT, cal.getTimeInMillis()).toString();

        UiObject2 animator = mDevice.findObject(By.res("android:id/animator"));
        if(animator != null) {
            UiObject2 listview = animator.findObject(By.clazz("android.widget.ListView"));
            Direction d = moveMonth > 0?Direction.DOWN:Direction.UP;
            moveMonth = Math.abs(moveMonth);
            while (!mDevice.wait(Until.hasObject(By.desc(monthAndDayText)), 10)) {
                listview.scroll(d, 90.0f);
            }
        }

//        mDevice.wait(Until.gone(By.checked(true)),10000);

    }

    /**
     * Automatically set the date of the android standard setting application.
     *
     * @param month  Specify the month you want to set.
     *
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setSettingAppDateMoveMonth(int month) {
        long settime = System.currentTimeMillis() + (month * 1000L * 60L * 60L * 24L * 30L);
        Date setDate = new Date(settime);

        setSettingAppDateSet(setDate);
    }
    /**
     * Automatically set the date of the android standard setting application.
     *
     * @param date  Specify the month you want to set.
     *
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setSettingAppDateSet(Date date) {
        try {
            setSettingAppDateSetInternal(date);
        } catch (UnsupportedDeviceException e) {
            notifyUnsupportdevice();
        }
    }
    private void setSettingAppDateSetInternal(Date date) throws UnsupportedDeviceException{
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) return;
        startSettingAppDateSetting();
        setDateAuto(false);

        UiDevice mDevice;
        Instrumentation instrumentation = mInstrumentation;
        mDevice = UiDevice.getInstance(instrumentation);

        Context settingContext = null;
        try {
            settingContext = mInstrumentation.getContext().createPackageContext(
                    "com.android.settings",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        int resId = settingContext.getResources().getIdentifier("date_time_set_date","string",settingContext.getPackageName());
        if(resId == 0) throw new UnsupportedDeviceException();
        String date_time_set_date = settingContext.getString(resId);

        resId = settingContext.getResources().getIdentifier("okay","string",settingContext.getPackageName());
        if(resId == 0) throw new UnsupportedDeviceException();
        String okay = settingContext.getString(resId);

        if(date_time_set_date == null || okay == null) throw new UnsupportedDeviceException();

        UiObject2 setTimeObj = mDevice.findObject(By.textContains(date_time_set_date));
        if(setTimeObj == null) throw new UnsupportedDeviceException();
        mDevice.wait(Until.hasObject(By.textContains(date_time_set_date).enabled(true)),10000);
        setTimeObj.click();



        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();

        String[] setdateStr = df.format(date).split("-");
        String[] nowdateStr = df.format(now).split("-");
        int moveMonthcount = Integer.parseInt(setdateStr[1]) - Integer.parseInt(nowdateStr[1]);
        selectYear(Integer.parseInt(setdateStr[0]));

        moveMonth(moveMonthcount);
        setDay(Integer.parseInt(setdateStr[2]));

        mDevice.findObject(By.textContains(okay)).click();
        mDevice.wait(Until.gone(By.res("android:id/datePicker")),10000);
        exitSettings();
    }

    private void selectYear(int year) throws UnsupportedDeviceException{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            selectYear_M_or_later(year);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            selectYear_JBMR2_or_later(year);
        }
    }

    private void selectYear_M_or_later(int year) throws UnsupportedDeviceException{
        Direction direction = Direction.DOWN;
        UiDevice mDevice;
        Instrumentation instrumentation = mInstrumentation;
        mDevice = UiDevice.getInstance(instrumentation);

        Context context = instrumentation.getTargetContext();

        mDevice.wait(Until.hasObject(By.res("android:id/datePicker")),10000);
        UiObject2 date_picker_header_year = mDevice.findObject(By.res("android:id/date_picker_header_year"));
        if(date_picker_header_year == null) throw new UnsupportedDeviceException();

        date_picker_header_year.click();
        mDevice.wait(Until.hasObject(By.res("android:id/date_picker_year_picker")),10000);
        UiObject2 date_picker_year_picker = mDevice.findObject(By.res("android:id/date_picker_year_picker"));
        if(date_picker_year_picker == null) throw new UnsupportedDeviceException();

        UiObject2 nowyear = date_picker_year_picker.findObject(By.clazz("android.widget.TextView"));
        if(nowyear == null) throw new UnsupportedDeviceException();

        int nowInt = Integer.parseInt(nowyear.getText());
        direction = (year > nowInt)?Direction.DOWN:Direction.UP;

        UiObject2 obj = scrollAndFind(date_picker_year_picker, By.text(String.valueOf(year)), direction);

        if(obj == null) throw new UnsupportedDeviceException();

        obj.click();

    }

    private void selectYear_JBMR2_or_later(int year) throws UnsupportedDeviceException{
        Direction direction = Direction.DOWN;
        UiDevice mDevice;
        Instrumentation instrumentation = mInstrumentation;
        mDevice = UiDevice.getInstance(instrumentation);

        Context context = instrumentation.getTargetContext();

        mDevice.wait(Until.hasObject(By.res("android:id/datePicker")),10000);
        UiObject2 date_picker_year = mDevice.findObject(By.res("android:id/date_picker_year"));
        if(date_picker_year == null) throw new UnsupportedDeviceException();

        date_picker_year.click();
        mDevice.wait(Until.hasObject(By.res("android:id/animator")
                .hasChild(By.clazz("android.widget.ListView").hasChild(By.text(date_picker_year.getText())))),10000);
        UiObject2 date_picker_year_picker = mDevice.findObject(By.res("android:id/animator"));
        if(date_picker_year_picker == null) throw new UnsupportedDeviceException();

        UiObject2 nowyear = date_picker_year_picker.findObject(By.clazz("android.widget.TextView"));
        if(nowyear == null) throw new UnsupportedDeviceException();

        int nowInt = Integer.parseInt(nowyear.getText());
        direction = (year > nowInt)?Direction.DOWN:Direction.UP;


        UiObject2 obj = scrollAndFind(date_picker_year_picker, By.text(String.valueOf(year)), direction);

        if(obj == null) throw new UnsupportedDeviceException();

        obj.click();
    }

    /**
     * Automatically set the "date auto" option of the android standard setting application.
     *
     * @param enable ON/OFF
     *
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setSettingAppDateAuto(boolean enable){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) return;
        try {
            UiDevice mDevice;
            startSettingAppDateSetting();
            mDevice = UiDevice.getInstance(mInstrumentation);
            setDateAuto(enable);
            mDevice.wait(Until.gone(By.res("android:id/datePicker")), 10000);
            exitSettings();
        } catch (UnsupportedDeviceException e) {
            notifyUnsupportdevice();
        }
    }

    private void setDateAuto(boolean enable) throws UnsupportedDeviceException{
        UiDevice mDevice;

        mDevice = UiDevice.getInstance(mInstrumentation);
        Context context = mInstrumentation.getTargetContext();
        Context settingContext = null;
        try {
            settingContext = mInstrumentation.getContext().createPackageContext(
                    "com.android.settings",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new UnsupportedDeviceException();
        }

        int resId = settingContext.getResources().getIdentifier("date_time_auto","string",settingContext.getPackageName());
        if(resId == 0) throw new UnsupportedDeviceException();

        String date_time_auto = settingContext.getString(resId);

        UiObject2 autoTimeObj = mDevice.findObject(By.textContains(date_time_auto/*"日付と時刻の自動設定"*/));
        if(autoTimeObj == null) throw new UnsupportedDeviceException();

        UiObject2 switchObj = findByParent(autoTimeObj, By.clazz("android.widget.Switch"));
        if(switchObj == null) {
            UiObject2 checkboxObj = findByParent(autoTimeObj, By.clazz("android.widget.CheckBox"));
            if(checkboxObj == null) throw new UnsupportedDeviceException();

            if (!enable && checkboxObj.isChecked()) {
                checkboxObj.click();
            }
            if (enable && !checkboxObj.isChecked()) {
                checkboxObj.click();
            }
        } else {

            if (!enable && switchObj.isChecked()) {
                switchObj.click();
            }
            if (enable && !switchObj.isChecked()) {
                switchObj.click();
            }
        }
    }

    /**
     * Automatically set the "Airplane" option of the android standard setting application.
     *
     * @param enable ON/OFF
     *
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setSettingAirplaneMode(boolean enable){
        try {
            startSettingAppAirplaneModeSetting();
            UiDevice mDevice;
            mDevice = UiDevice.getInstance(mInstrumentation);
            setAirplane(enable);
            exitSettings();
        } catch (UnsupportedDeviceException e) {
            notifyUnsupportdevice();
        }
    }
    private void startSettingAppDateSetting() throws UnsupportedDeviceException{
        UiDevice mDevice;
        mDevice = UiDevice.getInstance(mInstrumentation);

        Context context = mInstrumentation.getTargetContext();
        Intent settingIntest = new Intent(android.provider.Settings.ACTION_DATE_SETTINGS);
        settingIntest.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingIntest);
        mDevice.wait(Until.hasObject(By.pkg("com.android.settings").depth(0)),10000);
    }

    private void startSettingAppAirplaneModeSetting() throws UnsupportedDeviceException{
        UiDevice mDevice;
        mDevice = UiDevice.getInstance(mInstrumentation);

        Context context = mInstrumentation.getTargetContext();
        Intent settingIntest = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        settingIntest.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingIntest);
        mDevice.wait(Until.hasObject(By.pkg("com.android.settings").depth(0)),10000);
    }

    private void setAirplane(boolean enable) throws UnsupportedDeviceException{
        UiDevice mDevice;

        mDevice = UiDevice.getInstance(mInstrumentation);
        Context context = mInstrumentation.getTargetContext();
        Context settingContext = null;
        try {
            settingContext = mInstrumentation.getContext().createPackageContext(
                    "com.android.settings",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new UnsupportedDeviceException();
        }

        int resId = settingContext.getResources().getIdentifier("airplane_mode","string",settingContext.getPackageName());
        if(resId == 0) throw new UnsupportedDeviceException();

        String airplane_mode = settingContext.getString(resId);

        UiObject2 autoTimeObj = mDevice.findObject(By.textContains(airplane_mode));
        if(autoTimeObj == null) throw new UnsupportedDeviceException();

        UiObject2 switchObj = findByParent(autoTimeObj, By.clazz("android.widget.Switch"));

        if(switchObj == null) {
            UiObject2 checkboxObj = findByParent(autoTimeObj, By.clazz("android.widget.CheckBox"));
            if(checkboxObj == null) throw new UnsupportedDeviceException();
            if (!enable && checkboxObj.isChecked()) {
                checkboxObj.click();
            }
            if (enable && !checkboxObj.isChecked()) {
                checkboxObj.click();
            }
        } else {
            if (!enable && switchObj.isChecked()) {
                switchObj.click();
            }
            if (enable && !switchObj.isChecked()) {
                switchObj.click();
            }
        }

        //For confirmation dialog box for model
        mDevice.wait(Until.gone(By.clazz("android.widget.FrameLayout").depth(0)),5000);
    }


    private void startSettingWifiSetting() throws UnsupportedDeviceException{
        UiDevice mDevice;
        mDevice = UiDevice.getInstance(mInstrumentation);
        mDevice.pressHome();
        Context context = mInstrumentation.getTargetContext();
        Intent settingIntest = new Intent(Settings.ACTION_WIFI_SETTINGS);
        settingIntest.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingIntest);
        mDevice.wait(Until.hasObject(By.pkg("com.android.settings").depth(0)),10000);
    }

    /**
     * Automatically set the "Wi-Fi ON/OFF" option of the android standard setting application.
     *
     * @param enable ON/OFF
     *
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setSettingWifiEnable(boolean enable){
        try {
            startSettingWifiSetting();
            UiDevice mDevice;
            mDevice = UiDevice.getInstance(mInstrumentation);
            setWifiEnable(enable);
            exitSettings();
        } catch (UnsupportedDeviceException e) {
            notifyUnsupportdevice();
        }
    }

    private void setWifiEnable(boolean enable) throws UnsupportedDeviceException{
        UiDevice mDevice;

        mDevice = UiDevice.getInstance(mInstrumentation);
        Context context = mInstrumentation.getTargetContext();
        Context settingContext = null;
        try {
            settingContext = mInstrumentation.getContext().createPackageContext(
                    "com.android.settings",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new UnsupportedDeviceException();
        }

        int resId = settingContext.getResources().getIdentifier("wifi_settings_title","string",settingContext.getPackageName());
        if(resId == 0) throw new UnsupportedDeviceException();
        String wifi_settings_title = settingContext.getString(resId);

        UiObject2 autoTimeObj = mDevice.findObject(By.textContains(wifi_settings_title));
        if(autoTimeObj == null) throw new UnsupportedDeviceException();

        UiObject2 switchObj = findByParent(autoTimeObj, By.clazz("android.widget.Switch"));

        if(switchObj == null) {
            UiObject2 checkboxObj = findByParent(autoTimeObj, By.clazz("android.widget.CheckBox"));
            if(checkboxObj == null) throw new UnsupportedDeviceException();
            if (!enable && checkboxObj.isChecked()) {
                checkboxObj.click();
            }
            if (enable && !checkboxObj.isChecked()) {
                checkboxObj.click();
            }
        } else {
            if (!enable && switchObj != null && switchObj.isChecked()) {
                switchObj.click();
            }
            if (enable && switchObj != null && !switchObj.isChecked()) {
                switchObj.click();
            }
        }
    }

    private void startSettingWirelessSetting() throws UnsupportedDeviceException{
        UiDevice mDevice;
        mDevice = UiDevice.getInstance(mInstrumentation);

        Context context = mInstrumentation.getTargetContext();
        Intent settingIntest = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        settingIntest.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingIntest);
        mDevice.wait(Until.hasObject(By.pkg("com.android.settings").depth(0)),10000);
    }


    private void exitSettings() throws UnsupportedDeviceException{
        UiDevice mDevice;
        mDevice = UiDevice.getInstance(mInstrumentation);
        mDevice.waitForIdle(1000);

        do{
            mDevice.pressBack();
        }while (mDevice.wait(Until.hasObject(By.pkg("com.android.settings").depth(0)),5000));

    }

    /**
     * Automatically set the Clock of the android standard setting application.
     *
     * @param date date
     *
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setSettingClock(Date date){
        try {
            setSettingClockInternal(date);
        } catch (UnsupportedDeviceException e) {
            notifyUnsupportdevice();
        }
    }
    public void setSettingClockInternal(Date date) throws UnsupportedDeviceException{
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) return;
        startSettingAppDateSetting();
        setDateAuto(false);

        UiDevice mDevice;
        Instrumentation instrumentation = mInstrumentation;
        mDevice = UiDevice.getInstance(instrumentation);

        Context settingContext = null;
        try {
            settingContext = mInstrumentation.getContext().createPackageContext(
                    "com.android.settings",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new UnsupportedDeviceException();
        }

        int resId = settingContext.getResources().getIdentifier("date_time_set_time","string",settingContext.getPackageName());
        if(resId == 0) throw new UnsupportedDeviceException();
        String date_time_set_time = settingContext.getString(resId);

        resId = settingContext.getResources().getIdentifier("okay","string",settingContext.getPackageName());
        if(resId == 0) throw new UnsupportedDeviceException();
        String okay = settingContext.getString(resId);

        if(date_time_set_time == null || okay == null) throw new UnsupportedDeviceException();

        UiObject2 setTimeObj = mDevice.findObject(By.textContains(date_time_set_time));
        if(setTimeObj == null) throw new UnsupportedDeviceException();
        mDevice.wait(Until.hasObject(By.textContains(date_time_set_time).enabled(true)),10000);
        setTimeObj.click();

        setClock(date);

        UiObject2 okayObj = mDevice.findObject(By.textContains(okay));
        if(okayObj == null) throw new UnsupportedDeviceException();
        okayObj.click();

        mDevice.wait(Until.gone(By.res("android:id/timePicker")),10000);
        exitSettings();
    }

    private void setClock(Date date) throws UnsupportedDeviceException{
        UiDevice mDevice = UiDevice.getInstance(mInstrumentation);
        Context context = mInstrumentation.getTargetContext();
        Context settingContext = null;
        try {
            settingContext = mInstrumentation.getContext().createPackageContext(
                    "com.android.settings",
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new UnsupportedDeviceException();
        }
        SimpleDateFormat df = new SimpleDateFormat("kk-mm-ss", Locale.getDefault());
        String[] datestr = df.format(date).split("-");
        int hour = Integer.parseInt(datestr[0]);
        if (hour == 24) hour = 0;
        int min = Integer.parseInt(datestr[1]);

        if(mDevice.wait(Until.hasObject(By.res("android:id/timePicker")),10000)){
            UiObject2 am_label = mDevice.findObject(By.res("android:id/am_label"));
            UiObject2 pm_label = mDevice.findObject(By.res("android:id/pm_label"));

            if(am_label != null && pm_label != null){
                if( hour > 12){
                    pm_label.click();
                    hour = hour - 12;
                } else {
                    am_label.click();
                }
            }
            UiObject2 hourobj = mDevice.findObject(By.desc(String.valueOf(hour)));
            if(hourobj == null) throw new UnsupportedDeviceException();
            hourobj.click();

            int startmin = min/5*5;
            String start = String.valueOf(startmin);
            int endmin = min >= 55?0:(min/5*5)+5;
            String end = String.valueOf(endmin);


            UiObject2 startObj = mDevice.findObject(By.desc(start));
            UiObject2 endObj = mDevice.findObject(By.desc(end));
            if(startObj == null || endObj == null) throw new UnsupportedDeviceException();

            Point startPoint = startObj.getVisibleCenter();
            Point endPoint = endObj.getVisibleCenter();
            int y = (startPoint.y - endPoint.y)/10;
            int x = (startPoint.x - endPoint.x)/10;
            Point dest = new Point(startPoint);

            do{
                mDevice.click(dest.x,dest.y);
                dest.set(dest.x - x, dest.y - y);
            } while (!mDevice.wait(Until.hasObject(By.res("android:id/minutes").text(String.valueOf(min))),10));
        }
    }

    /**
     * set the stay_on_while_plugged_in of the android developer options.
     *
     * @param enable ON/OFF
     *
     */
    public void stay_on_while_plugged_in(boolean enable) {
        try {
            runShellCommand("settings put global stay_on_while_plugged_in " + (enable?"3":"0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
