package jp.kemomimi.testhelper;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.widget.Switch;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingsHelper extends HelperBase{

    /**
     * Construct a SettingsHelper object.
     * can call {@link #show}.
     *
     * @param instrumentation Usually your {@link Instrumentation} object.
     */
    public SettingsHelper(Instrumentation instrumentation){
        super(instrumentation);
    }


    private void setDay(int day){
        UiDevice mDevice;
        Instrumentation instrumentation = mInstrumentation;
        mDevice = UiDevice.getInstance(instrumentation);

        mDevice.wait(Until.hasObject(By.res("android:id/datePicker")),10000);

        UiObject2 date_picker_day_picker = mDevice.findObject(By.res("android:id/date_picker_day_picker"));

        UiObject2 month_view = date_picker_day_picker.findObject(By.res("android:id/month_view"));

        UiObject2 today = month_view.findObject(By.text(String.valueOf(day)));

        if(today != null){
            today.click();
            mDevice.wait(Until.hasObject(By.checked(true)),10000);
        }
    }

    private void moveMonth(int moveMonth){
        if(moveMonth == 0) return;

        UiDevice mDevice;
        Instrumentation instrumentation = mInstrumentation;
        mDevice = UiDevice.getInstance(instrumentation);

        Context settingContext = null;

        mDevice.wait(Until.hasObject(By.res("android:id/datePicker")),10000);
        UiObject2 date_picker_day_picker = mDevice.findObject(By.res("android:id/date_picker_day_picker"));
        UiObject2 month_view = date_picker_day_picker.findObject(By.res("android:id/month_view"));
        UiObject2 today = month_view.findObject(By.checked(true));

        String clickResName = "android:id/prev";
        if(moveMonth > 0){
            clickResName = "android:id/next";
        }
        moveMonth = Math.abs(moveMonth);
        for(int i = 0;moveMonth > i;i++) {
            date_picker_day_picker.findObject(By.res(clickResName)).click();
        }

        mDevice.wait(Until.gone(By.checked(true)),10000);

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
        }

        String date_time_set_date = settingContext.getString(
                settingContext.getResources().getIdentifier("date_time_set_date","string",settingContext.getPackageName()));
        String okay = settingContext.getString(
                settingContext.getResources().getIdentifier("okay","string",settingContext.getPackageName()));


        UiObject2 setTimeObj = mDevice.findObject(By.textContains(date_time_set_date));
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
        exitSettings();
    }

    private void selectYear(int year){
        Direction direction = Direction.DOWN;
        UiDevice mDevice;
        Instrumentation instrumentation = mInstrumentation;
        mDevice = UiDevice.getInstance(instrumentation);

        Context context = instrumentation.getTargetContext();

        mDevice.wait(Until.hasObject(By.res("android:id/datePicker")),10000);
        UiObject2 date_picker_header_year = mDevice.findObject(By.res("android:id/date_picker_header_year"));

        date_picker_header_year.click();
        mDevice.wait(Until.hasObject(By.res("android:id/date_picker_year_picker")),10000);
        UiObject2 date_picker_year_picker = mDevice.findObject(By.res("android:id/date_picker_year_picker"));

        UiObject2 nowyear = date_picker_year_picker.findObject(By.clazz("android.widget.TextView"));
        if(nowyear != null){
            int nowInt = Integer.parseInt(nowyear.getText());
            direction = (year > nowInt)?Direction.DOWN:Direction.UP;
        }

        UiObject2 obj = scrollAndFind(date_picker_year_picker, By.text(String.valueOf(year)), direction);

        if(obj != null) {
            obj.click();
        }
    }

    /**
     * Automatically set the "date auto" option of the android standard setting application.
     *
     * @param enable
     *
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setSettingAppDateAuto(boolean enable){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) return;
        UiDevice mDevice;
        startSettingAppDateSetting();
        mDevice = UiDevice.getInstance(mInstrumentation);
        setDateAuto(enable);
        exitSettings();
    }

    private void setDateAuto(boolean enable){
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
        }

        String date_time_auto = settingContext.getString(
                settingContext.getResources().getIdentifier("date_time_auto","string",settingContext.getPackageName()));

        UiObject2 autoTimeObj = mDevice.findObject(By.textContains(date_time_auto/*"日付と時刻の自動設定"*/));
        UiObject2 switchObj = findByParent(autoTimeObj, By.clazz("android.widget.Switch"));

        if(!enable && switchObj != null && switchObj.getText().equals(new Switch(context).getTextOn())){
            switchObj.click();
        }
        if(enable && switchObj != null && switchObj.getText().equals(new Switch(context).getTextOff())){
            switchObj.click();
        }
    }

    /**
     * Automatically set the "Airplane" option of the android standard setting application.
     *
     * @param enable
     *
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setSettingAirplaneMode(boolean enable){
        startSettingAppAirplaneModeSetting();
        UiDevice mDevice;
        mDevice = UiDevice.getInstance(mInstrumentation);
        setAirplane(enable);
        exitSettings();
    }
    private void startSettingAppDateSetting(){
        UiDevice mDevice;
        mDevice = UiDevice.getInstance(mInstrumentation);

        Context context = mInstrumentation.getTargetContext();
        Intent settingIntest = new Intent(android.provider.Settings.ACTION_DATE_SETTINGS);
        settingIntest.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingIntest);
        mDevice.wait(Until.hasObject(By.pkg("com.android.settings").depth(0)),10000);
    }

    private void startSettingAppAirplaneModeSetting(){
        UiDevice mDevice;
        mDevice = UiDevice.getInstance(mInstrumentation);

        Context context = mInstrumentation.getTargetContext();
        Intent settingIntest = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        settingIntest.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingIntest);
        mDevice.wait(Until.hasObject(By.pkg("com.android.settings").depth(0)),10000);
    }

    private void setAirplane(boolean enable){
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
        }

        String wifi_in_airplane_mode = settingContext.getString(
                settingContext.getResources().getIdentifier("wifi_in_airplane_mode","string",settingContext.getPackageName()));

        UiObject2 autoTimeObj = mDevice.findObject(By.textContains(wifi_in_airplane_mode));
        UiObject2 switchObj = findByParent(autoTimeObj, By.clazz("android.widget.Switch"));

        if(!enable && switchObj != null && switchObj.getText().equals(new Switch(context).getTextOn())){
            switchObj.click();
        }
        if(enable && switchObj != null && switchObj.getText().equals(new Switch(context).getTextOff())){
            switchObj.click();
        }
    }


    private void startSettingWifiSetting(){
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
     * @param enable
     *
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setSettingWifiEnable(boolean enable){
        startSettingAppAirplaneModeSetting();
        UiDevice mDevice;
        mDevice = UiDevice.getInstance(mInstrumentation);
        setWifiEnable(enable);
        exitSettings();
    }

    private void setWifiEnable(boolean enable){
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
        }

        String wifi_settings_title = settingContext.getString(
                settingContext.getResources().getIdentifier("wifi_settings_title","string",settingContext.getPackageName()));

        UiObject2 autoTimeObj = mDevice.findObject(By.textContains(wifi_settings_title));

        UiObject2 switchObj = findByParent(autoTimeObj, By.clazz("android.widget.Switch"));

        if(!enable && switchObj != null && switchObj.getText().equals(new Switch(context).getTextOn())){
            switchObj.click();
        }
        if(enable && switchObj != null && switchObj.getText().equals(new Switch(context).getTextOff())){
            switchObj.click();
        }
    }

    private void startSettingWirelessSetting(){
        UiDevice mDevice;
        mDevice = UiDevice.getInstance(mInstrumentation);

        Context context = mInstrumentation.getTargetContext();
        Intent settingIntest = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        settingIntest.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingIntest);
        mDevice.wait(Until.hasObject(By.pkg("com.android.settings").depth(0)),10000);
    }


    private void exitSettings(){
        UiDevice mDevice;
        mDevice = UiDevice.getInstance(mInstrumentation);

        do{
            mDevice.pressBack();
        }while (mDevice.wait(Until.hasObject(By.pkg("com.android.settings").depth(0)),5000));

    }
}
