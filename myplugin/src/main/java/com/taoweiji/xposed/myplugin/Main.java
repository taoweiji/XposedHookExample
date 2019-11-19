package com.taoweiji.xposed.myplugin;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Main implements IXposedHookLoadPackage {
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.taoweiji.xposed.example"))
            return;
        XposedBridge.log("Loaded app:" + lpparam.packageName);
        findAndHookMethod("com.taoweiji.xposed.example.MainActivity", lpparam.classLoader, "getInfo", String.class, String.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("开始劫持了~");
                XposedBridge.log("参数1 = " + param.args[0]);
                XposedBridge.log("参数2 = " + param.args[1]);
                param.args[0] = "arg1参数被修改";
                param.args[1] = "arg2参数被修改";
            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("劫持结束了~");
                XposedBridge.log("参数1 = " + param.args[0]);
                XposedBridge.log("参数2 = " + param.args[1]);
                Object object = param.getResult();
                XposedBridge.log("实际返回值 = " + object);
                param.setResult("返回值被修改," + object.toString());
            }
        });
    }
}
