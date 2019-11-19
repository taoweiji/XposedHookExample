# Android 逆向工程：基于Xposed Hook实现动态逆向分析



Xposed是一个非常神奇的框架，对于普通用户，Xposed框架可以发挥Android系统更高的使用效率，可以随便折腾，美化优化系统。但是用于开发者而言，Xposed可以用于 `逆向工程`，动态逆向分析APP，截取内容。也可以用于Mock定位、修改系统参数等。

##### 选择合适的手机/模拟器

由于 [Xposed](https://xposed.appkg.com/3000.html) 对系统的支持有限，需要选择 Android 4.0 ~ Android 8.1的手机，并且必须获得Root权限。我们也可以选择使用 Android 模拟器。

##### 安装 Xposed installer

可以 [官方论坛](https://forum.xda-developers.com/showthread.php?t=3034811) 下载最新的 Xposed installer "XposedInstaller_3.1.5.apk" ，如果是模拟器，把下载好的apk拖进模拟器即可。

##### Xposed 框架安装

打开 Xposed installer，在首页就可以看到安装框架的按钮，点击安装即可。

> 附加：小米4C直接安装会提示失败，需要下载 [Syslock](https://pan.baidu.com/s/1A3t_ZaD9uP_qxZqH_F062w ) 提取码: 4sa5 ，解锁System分区才可以正常安装。



### 编写测试的目标APP

这里创建了一个测试的目标项目，包名为“com.taoweiji.xposed.example”，Activity只有一个按钮。

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            val text = getInfo("test", System.currentTimeMillis().toString())
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }
    private fun getInfo(arg1: String, arg2: String): String {
        return "$arg1,$arg2"
    }
}

```

### 编写插件APP

创建另外一个项目myplugin，修改build.gradle，增加依赖，项目的依赖尽可能简单。

```groovy
apply plugin: 'com.android.application'
android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "com.taoweiji.xposed.myplugin"
        minSdkVersion 14
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
    }
}
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    compileOnly 'de.robv.android.xposed:api:82'
    compileOnly 'de.robv.android.xposed:api:82:sources'
}
```

修改 AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.taoweiji.xposed.myplugin">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <meta-data
            android:name="xposedmodule"
            android:value="true" />
        <meta-data
            android:name="xposedminversion"
            android:value="82" />
        <meta-data
            android:name="xposeddescription"
            android:value="myxpose" />
    </application>
</manifest>
```

创建Main.java

```kotlin
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

```

把插件项目安装到手机上，记得Android Studio不要开启 Instant Run，否则会导致插件无法正常功能。

https://api.xposed.info/using.html
