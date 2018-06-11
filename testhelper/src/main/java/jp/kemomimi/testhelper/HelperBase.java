package jp.kemomimi.testhelper;

import android.app.Instrumentation;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiObject2;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class HelperBase {
    protected Instrumentation mInstrumentation;

    public HelperBase(Instrumentation instrumentation){
        mInstrumentation = instrumentation;
    }
    protected UiObject2 findByParent(UiObject2 obj, BySelector selector){
        UiObject2 ret = null;
        do{
            obj = obj.getParent();
            ret = obj.findObject(selector);
            if(ret != null) {
                return ret;
            }
        } while(obj != null);
        return ret;
    }

    protected UiObject2 scrollAndFind(UiObject2 container, BySelector selector, Direction direction) {
        do {
            UiObject2 widget = container.findObject(selector);
            if (widget != null) {
                return widget;
            }
        } while (container.scroll(direction, 1f));
        return container.findObject(selector);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected String runShellCommand(String command) throws Exception {
        ParcelFileDescriptor pfd;
        FileDescriptor fd = null;
        InputStream is = null;
        String outputString = null;
        BufferedReader br = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                pfd = mInstrumentation.getUiAutomation().executeShellCommand(command);

                fd = pfd.getFileDescriptor();
                is = new BufferedInputStream(new FileInputStream(fd));
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                outputString = br.readLine();
                is.close();
            } catch (IOException ioe) {
                ioe.getStackTrace();
            } finally {
                if (is != null) is.close();
                if (br != null) br.close();
            }

            return outputString;
        } else {
            try {
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                is = process.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                outputString = br.readLine();
            }finally {
                if (is != null) is.close();
                if (br != null) br.close();
            }

            return outputString;
        }
    }
}
