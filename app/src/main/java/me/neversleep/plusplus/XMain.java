package me.neversleep.plusplus;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XMain implements IXposedHookLoadPackage {

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


     private void hookSelf(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
          XposedHelpers.findAndHookMethod("me.neversleep.plusplus.MainActivity", loadPackageParam.classLoader, "getActiveVersion", new XC_MethodHook() {
               protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                    super.afterHookedMethod(methodHookParam);
                    methodHookParam.setResult(BuildConfig.VERSION_CODE);
               }
          });
     }

     private void hookAndroid(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
          final XSharedPreferences xSharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, "x_conf");
          xSharedPreferences.makeWorldReadable();
          xSharedPreferences.reload();
          try {
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
          } catch (Throwable error) {
               Log.e("neversleep", "systemReady error:", error);
          }
          Class<?> powerManagerServiceClass = XposedHelpers.findClass("com.android.server.power.PowerManagerService", loadPackageParam.classLoader);

          //部分设备没效果：Redmi note5 android 9
          try {
//          Class<?> powerGroupClass = XposedHelpers.findClass("com.android.server.power.PowerGroup", loadPackageParam.classLoader);

               XposedBridge.hookAllMethods(powerManagerServiceClass, "isBeingKeptAwakeLocked", new XC_MethodHook() {
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                         xSharedPreferences.reload();
                         Log.e("neversleep", "get_disable_sleep: disable_sleep is " + xSharedPreferences.getBoolean("disable_sleep", false));

                         if (!xSharedPreferences.getBoolean("disable_sleep", false)) {
                              Log.e("neversleep", "afterHookedMethod: disable_sleep is false");
                              return;
                         }
                         param.setResult(true);
                         Log.e("neversleep", "afterHookedMethod: disable_sleep is true");

                    }
               });
          } catch (Throwable error) {
               Log.e("neversleep", "isBeingKeptAwakeLocked error:", error);
          }
          if (false) {
               //完全没效果
               try {
                    XposedHelpers.findAndHookMethod(powerManagerServiceClass, "goToSleep", long.class, int.class, int.class, new XC_MethodHook() {
                         @Override
                         protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                              super.beforeHookedMethod(param);
                              xSharedPreferences.reload();
                              Log.e("neversleep", "[goToSleep]get_disable_sleep: disable_sleep is " + xSharedPreferences.getBoolean("disable_sleep", false));

                              if (!xSharedPreferences.getBoolean("disable_sleep", false)) {
                                   Log.e("neversleep", "[goToSleep]afterHookedMethod: disable_sleep is false");
                                   return;
                              }
                              param.setResult(null);
                              Log.e("neversleep", "[goToSleep]afterHookedMethod: disable_sleep is true");
                         }
                    });
               } catch (Throwable error) {
                    Log.e("neversleep", "goToSleep error:", error);
               }
          }
          if (false) {
               //会导致黑屏
               try {
                    XposedBridge.hookAllMethods(powerManagerServiceClass, "getScreenOffTimeoutLocked", new XC_MethodHook() {
                         @Override
                         protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                              super.afterHookedMethod(param);
                              xSharedPreferences.reload();
                              Log.e("neversleep", "[getScreenOffTimeoutLocked]get_disable_sleep: disable_sleep is " + xSharedPreferences.getBoolean("disable_sleep", false));

                              if (!xSharedPreferences.getBoolean("disable_sleep", false)) {
                                   Log.e("neversleep", "[getScreenOffTimeoutLocked]afterHookedMethod: disable_sleep is false");
                                   return;
                              }
                              param.setResult(Long.MAX_VALUE);
                              Log.e("neversleep", "[getScreenOffTimeoutLocked]afterHookedMethod: disable_sleep is true");
                         }
                    });
               } catch (Throwable error) {
                    Log.e("neversleep", "getScreenOffTimeoutLocked error:", error);
               }
          }
          //目前测试没什么问题
          try {
               XposedHelpers.findAndHookMethod(
                       "com.android.server.power.PowerManagerService",
                       loadPackageParam.classLoader,
                       "updateUserActivitySummaryLocked",
                       long.class,
                       int.class,
                       new XC_MethodHook() {

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                 super.afterHookedMethod(param);
                                 xSharedPreferences.reload();
                                 Log.e("neversleep", "[updateUserActivitySummaryLocked]get_disable_sleep: disable_sleep is " + xSharedPreferences.getBoolean("disable_sleep", false));

                                 if (!xSharedPreferences.getBoolean("disable_sleep", false)) {
                                      Log.e("neversleep", "[updateUserActivitySummaryLocked]afterHookedMethod: disable_sleep is false");
                                      return;
                                 }
                                 // 修改结果，确保屏幕不会进入 SCREEN_OFF 状态
                                 Object powerManagerService = param.thisObject;
                                 int mUserActivitySummary = (int) XposedHelpers.getObjectField(powerManagerService, "mUserActivitySummary");

                                 // 如果当前状态是屏幕暗或亮，则不修改
                                 if ((mUserActivitySummary & PowerMangerService.USER_ACTIVITY_SCREEN_BRIGHT) != 0 ||
                                         (mUserActivitySummary & PowerMangerService.USER_ACTIVITY_SCREEN_DIM) != 0) {
                                      return;
                                 }

                                 // 修改结果为 SCREEN_DIM，确保屏幕不会进入 SCREEN_OFF 状态
                                 mUserActivitySummary = PowerMangerService.USER_ACTIVITY_SCREEN_DIM;
                                 XposedHelpers.setObjectField(powerManagerService, "mUserActivitySummary", mUserActivitySummary);

                                 XUtils.xLog("neversleep", "Modified mUserActivitySummary to prevent SCREEN_OFF");
                            }
                       }
               );
          } catch (Throwable error) {
               Log.e("neversleep", "updateUserActivitySummaryLocked error:", error);
          }

          Log.e("neversleep", "hookAndroid finish");

     }
}
