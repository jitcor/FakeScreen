package me.neversleep.plusplus;

public class PowerMangerService {
 public static final String TAG = PowerMangerService.class.getSimpleName();

 // Dirty bit: mWakeLocks changed
 public static final int DIRTY_WAKE_LOCKS = 1 << 0;
 // Dirty bit: mWakefulness changed
 public static final int DIRTY_WAKEFULNESS = 1 << 1;
 // Dirty bit: user activity was poked or may have timed out
 public static final int DIRTY_USER_ACTIVITY = 1 << 2;
 // Dirty bit: actual display power state was updated asynchronously
 public static final int DIRTY_ACTUAL_DISPLAY_POWER_STATE_UPDATED = 1 << 3;
 // Dirty bit: mBootCompleted changed
 public static final int DIRTY_BOOT_COMPLETED = 1 << 4;
 // Dirty bit: settings changed
 public static final int DIRTY_SETTINGS = 1 << 5;
 // Dirty bit: mIsPowered changed
 public static final int DIRTY_IS_POWERED = 1 << 6;
 // Dirty bit: mStayOn changed
 public static final int DIRTY_STAY_ON = 1 << 7;
 // Dirty bit: battery state changed
 public static final int DIRTY_BATTERY_STATE = 1 << 8;
 // Dirty bit: proximity state changed
 public static final int DIRTY_PROXIMITY_POSITIVE = 1 << 9;
 // Dirty bit: dock state changed
 public static final int DIRTY_DOCK_STATE = 1 << 10;

 // Wakefulness: The device is asleep and can only be awoken by a call to wakeUp().
 // The screen should be off or in the process of being turned off by the display controller.
 // The device typically passes through the dozing state first.
 public static final int WAKEFULNESS_ASLEEP = 0;
 // Wakefulness: The device is fully awake.  It can be put to sleep by a call to goToSleep().
 // When the user activity timeout expires, the device may start dreaming or go to sleep.
 public static final int WAKEFULNESS_AWAKE = 1;
 // Wakefulness: The device is dreaming.  It can be awoken by a call to wakeUp(),
 // which ends the dream.  The device goes to sleep when goToSleep() is called, when
 // the dream ends or when unplugged.
 // User activity may brighten the screen but does not end the dream.
 public static final int WAKEFULNESS_DREAMING = 2;
 // Wakefulness: The device is dozing.  It is almost asleep but is allowing a special
 // low-power "doze" dream to run which keeps the display on but lets the application
 // processor be suspended.  It can be awoken by a call to wakeUp() which ends the dream.
 // The device fully goes to sleep if the dream cannot be started or ends on its own.
 public static final int WAKEFULNESS_DOZING = 3;

 // Summarizes the state of all active wakelocks.
 public static final int WAKE_LOCK_CPU = 1 << 0;
 public static final int WAKE_LOCK_SCREEN_BRIGHT = 1 << 1;
 public static final int WAKE_LOCK_SCREEN_DIM = 1 << 2;
 public static final int WAKE_LOCK_BUTTON_BRIGHT = 1 << 3;
 public static final int WAKE_LOCK_PROXIMITY_SCREEN_OFF = 1 << 4;
 public static final int WAKE_LOCK_STAY_AWAKE = 1 << 5; // only set if already awake
 public static final int WAKE_LOCK_DOZE = 1 << 6;

 // Summarizes the user activity state.
 public static final int USER_ACTIVITY_SCREEN_BRIGHT = 1 << 0;
 public static final int USER_ACTIVITY_SCREEN_DIM = 1 << 1;
 public static final int USER_ACTIVITY_SCREEN_DREAM = 1 << 2;

 // Default timeout in milliseconds.  This is only used until the settings
 // provider populates the actual default value (R.integer.def_screen_off_timeout).
 public static final int DEFAULT_SCREEN_OFF_TIMEOUT = 15 * 1000;
 public static final int DEFAULT_SLEEP_TIMEOUT = -1;

 // Power hints defined in hardware/libhardware/include/hardware/power.h.
 public static final int POWER_HINT_INTERACTION = 2;
 public static final int POWER_HINT_LOW_POWER = 5;

}
