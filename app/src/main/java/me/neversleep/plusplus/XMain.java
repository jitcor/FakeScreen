package me.neversleep.plusplus;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XMain implements IXposedHookLoadPackage, IXposedHookZygoteInit {
     public static final String TAG = "neversleep";

     public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
          XUtils.xLog("neversleep", "package:" + loadPackageParam.packageName);
          XUtils.xLog("neversleep", "process:" + loadPackageParam.processName);
          if ("android".equals(loadPackageParam.packageName)) {
               XUtils.xLog("neversleep", "start hook system_server...");
               hookAndroid(loadPackageParam);
               XUtils.xLog("neversleep", "end hook system_server...");
          }
          if (BuildConfig.APPLICATION_ID.equals(loadPackageParam.packageName)) {
               hookSelf(loadPackageParam);
          }
     }

     public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
          if (startupParam.startsSystemServer) {
               XUtils.xLog("neversleep", "initZygote:" + startupParam.modulePath);
               XUtils.xLog("neversleep", "start hook system_server...");
               HookImpl.main(null);
               XUtils.xLog("neversleep", "end hook system_server...");
          }
     }

     private void hookSelf(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
          XposedHelpers.findAndHookMethod("me.neversleep.plusplus.MainActivity", loadPackageParam.classLoader, "getActiveVersion", new XC_MethodHook() {
               protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                    super.afterHookedMethod(methodHookParam);
                    methodHookParam.setResult(BuildConfig.VERSION_CODE);
               }
          });
     }

     private void hookAndroid(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
          XposedBridge.hookAllMethods(XposedHelpers.findClass("com.android.server.am.ActivityManagerService", loadPackageParam.classLoader), "systemReady", new XC_MethodHook() { // from class: me.neversleep.plusplus.XMain.2
               protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                    try {
                         XUtils.xLog("neversleep", "Preparing system");
                         XUtils.xLog("neversleep", " Preparing system");
                         getContext(methodHookParam.thisObject);
                    } catch (Throwable th) {
                         XUtils.xLog("neversleep", Log.getStackTraceString(th));
                    }
               }

               protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                    try {
                         XUtils.xLog("neversleep", "System ready");
                         getContext(methodHookParam.thisObject);
                         HookImpl.main(methodHookParam.thisObject.getClass().getClassLoader());
                    } catch (Throwable th) {
                         Log.e("neversleep", Log.getStackTraceString(th));
                         XposedBridge.log(th);
                    }
               }

               private Context getContext(Object obj) throws Throwable {
                    Context context = null;
                    for (Class<?> cls = obj.getClass(); cls != null && context == null; cls = cls.getSuperclass()) {
                         Field[] declaredFields = cls.getDeclaredFields();
                         int length = declaredFields.length;
                         int i = 0;
                         while (true) {
                              if (i < length) {
                                   Field field = declaredFields[i];
                                   if (field.getType().equals(Context.class)) {
                                        field.setAccessible(true);
                                        context = (Context) field.get(obj);
                                        XUtils.xLog("neversleep", "Context found in " + cls + " as " + field.getName());
                                        break;
                                   }
                                   i++;
                              }
                         }
                    }
                    if (context != null) {
                         return context;
                    }
                    throw new Throwable("Context not found");
               }
          });
     }
}
