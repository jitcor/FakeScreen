package me.neversleep.plusplus;

import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class HookImpl {
     public static final String TAG = "neversleep";

     public static void main(final ClassLoader classLoader) {
          try {
               final XSharedPreferences xSharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, "x_conf");
               xSharedPreferences.makeWorldReadable();
               xSharedPreferences.reload();
               XposedBridge.hookAllMethods(XposedHelpers.findClass("com.android.server.policy.PhoneWindowManager", classLoader), "powerPress", new XC_MethodHook() { // from class: me.neversleep.plusplus.HookImpl.1
                    int mode = 0;

                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                         super.beforeHookedMethod(methodHookParam);
                         try {
                              XUtils.xLog("neversleep", "beforeHookedMethod: start");
                              xSharedPreferences.reload();
                              int i = 0;
                              if (!xSharedPreferences.getBoolean("power", false)) {
                                   Log.e("neversleep", "beforeHookedMethod: power is false");
                                   return;
                              }
                              XUtils.xLog("neversleep", "beforeHookedMethod: power is true");
                              Class<?> cls = Class.forName("android.view.SurfaceControl", false, classLoader);
                              IBinder iBinder = getDisplayBinder(classLoader);
                              if (iBinder != null) {
                                   XposedHelpers.callStaticMethod(cls, "setDisplayPowerMode", iBinder, this.mode);
                                   if (this.mode == 0) {
                                        i = 2;
                                   }
                                   this.mode = i;
                              }
                              methodHookParam.setResult(null);
                              XUtils.xLog("neversleep", "replace success");
                         } catch (Throwable th) {
                              XUtils.xLog("neversleep", "beforeHookedMethod: error:", th);
                         }
                    }
               });
               XUtils.xLog("neversleep", "main: Hook success");
               //ref:https://cs.android.com/android/platform/superproject/+/android-5.1.0_r1:frameworks/base/services/core/java/com/android/server/power/PowerManagerService.java;drc=01c06dfb076b71cb72c4bff9175bec9d59d2efde;l=1539
               XposedBridge.hookAllMethods(XposedHelpers.findClass("com.android.server.power.PowerManagerService", classLoader), "getScreenOffTimeoutLocked", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                         super.afterHookedMethod(param);
                         try {
                              XUtils.xLog("neversleep", "getScreenOffTimeoutLocked: start");
                              xSharedPreferences.reload();
                              if (!xSharedPreferences.getBoolean("power", false)) {
                                   Log.e("neversleep", "getScreenOffTimeoutLocked: power is false");
                                   return;
                              }

                              XUtils.xLog("neversleep", "getScreenOffTimeoutLocked: power is true");
                              if (param.method instanceof Method) {
                                   String type = ((Method) param.method).getGenericReturnType().toString();
                                   switch (type) {
                                        case "int":
                                             param.setResult(24 * 24 * 60 * 60 * 1000);
                                             XUtils.xLog("neversleep", "getScreenOffTimeoutLocked success:" + Integer.MAX_VALUE);//2147483647
                                             break;
                                        case "long":
                                             param.setResult(5 * 365 * 24 * 60 * 60 * 1000L);
                                             XUtils.xLog("neversleep", "getScreenOffTimeoutLocked success:" + (5 * 365 * 24 * 60 * 60 * 1000L));//2147483647
                                             break;
                                        default:
                                             XUtils.xLog("neversleep", "getScreenOffTimeoutLocked: error:  not support type: " + type);
                                   }
                              } else {
                                   XUtils.xLog("neversleep", "getScreenOffTimeoutLocked: error:  param.method not instanceof Method ");
                              }
                         } catch (Throwable t) {
                              XUtils.xLog("neversleep", "getScreenOffTimeoutLocked: error:", t);
                         }
                    }
               });
               XUtils.xLog("neversleep", "main: Hook getScreenOffTimeoutLocked success");
          } catch (Throwable th) {
               th.printStackTrace();
               XUtils.xLog("neversleep", "main: error:" + th.getMessage(), th);
          }
     }

     static IBinder getDisplayBinder(ClassLoader classLoader){
          try {
               Class<?> clsSurfaceControl = XposedHelpers.findClass("android.view.SurfaceControl", classLoader);
               if(Build.VERSION.SDK_INT<Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                    return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ?
                            (IBinder) XposedHelpers.callStaticMethod(clsSurfaceControl, "getBuiltInDisplay", 0) :
                            (IBinder) XposedHelpers.callStaticMethod(clsSurfaceControl, "getInternalDisplayToken");
               }else {
                    Class<?> clsDisplayControl = XposedHelpers.findClass("com.android.server.display.DisplayControl", classLoader);
                    long[] ids= (long[]) XposedHelpers.callStaticMethod(clsDisplayControl,"getPhysicalDisplayIds");
                    if(ids==null||ids.length==0){
                         return null;
                    }
                    return (IBinder) XposedHelpers.callStaticMethod(clsDisplayControl,"getPhysicalDisplayToken",ids[0]);
               }

          }catch (Throwable t){
               XUtils.xLog(TAG,"getDisplayBinder",t);
          }
          return null;
     }

}
