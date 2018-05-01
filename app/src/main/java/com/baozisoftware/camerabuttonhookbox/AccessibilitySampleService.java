package com.baozisoftware.camerabuttonhookbox;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class AccessibilitySampleService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        try {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_FOCUS) {
                    return doWork(false);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_CAMERA) {
                    return doWork(true);
                }
            }
        } catch (Exception e) {

        }
        return super.onKeyEvent(event);
    }

    private void press(int x, int y, int delay) {
        Path path = new Path();
        path.moveTo(x, y);

        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, 0, delay)).build();
        dispatchGesture(gestureDescription, null, null);
    }


    private void click(int x, int y) {
        press(x, y, ViewConfiguration.getTapTimeout());
    }


    private boolean clickByViewId(String viewId) {
        int x, y;
        List<AccessibilityNodeInfo> list = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(viewId);
        if (list != null && !list.isEmpty()) {
            Rect rect = new Rect();
            list.get(0).getBoundsInScreen(rect);
            x = rect.centerX();
            y = rect.centerY();
            click(x, y);
            return true;
        }
        return false;
    }

    private boolean clickByViewPath(int[] path) {
        int x, y;
        AccessibilityNodeInfo view = getRootInActiveWindow();
        if (path != null && path.length > 0) {
            for (int i : path) {
                view = view.getChild(i);
                if (view == null)
                    return false;
            }
        }
        Rect rect = new Rect();
        view.getBoundsInScreen(rect);
        x = rect.centerX();
        y = rect.centerY();
        click(x, y);
        return true;
    }

    private Boolean doWork(boolean camera) {
        switch (getRootInActiveWindow().getPackageName().toString()) {
            case "com.tencent.mm":
                return doWechat(camera);
            case "com.google.android.apps.photos.scanner":
                return doGooglePhotosScanner(camera);
            case "com.tencent.tim":
                return doTim(camera);
        }
        return false;
    }

    private Boolean doWechat(boolean camera) {
        return !camera ? clickByViewId("com.tencent.mm:id/uy") : clickByViewId("com.tencent.mm:id/cb1");
    }

    private boolean doGooglePhotosScanner(boolean camera) {
        return camera && clickByViewId("com.google.android.apps.photos.scanner:id/photos_scanner_home_start_capture_button_view");
    }

    private boolean doTim(boolean camera) {
        return !camera ? clickByViewPath(null) : clickByViewPath(new int[]{4});
    }

   /* private void debug(AccessibilityNodeInfo view, Stack path) {
        print(view, path);
        if (view.getChildCount() > 0) {
            for (int x = 0; x < view.getChildCount(); x++) {
                if (path == null)
                    path = new Stack();
                path.push(x);
                debug(view.getChild(x), path);
                path.pop();
            }
        }
    }

    private void print(AccessibilityNodeInfo view, Stack path) {
        Rect rect = new Rect();
        view.getBoundsInScreen(rect);
        int x = rect.centerX(),
                y = rect.centerY();
        Log.d("info", view + "\t" + "(" + x + "," + y + ")" + "\t" + path);
    }*/
}