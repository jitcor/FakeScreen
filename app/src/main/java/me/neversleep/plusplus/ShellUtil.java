package me.neversleep.plusplus;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * ShellUtil
 * <ul>
 * <strong>Check root</strong>
 * <li>{@link ShellUtil#permission()}</li>
 * </ul>
 * <ul>
 * <strong>Execte command</strong>
 * <li>{@link ShellUtil#execCommand(String, boolean)}</li>
 * <li>{@link ShellUtil#execCommand(String, boolean, boolean)}</li>
 * <li>{@link ShellUtil#execCommand(List, boolean)}</li>
 * <li>{@link ShellUtil#execCommand(List, boolean, boolean)}</li>
 * <li>{@link ShellUtil#execCommand(String[], boolean)}</li>
 * <li>{@link ShellUtil#execCommand(String[], boolean, boolean)}</li>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-16
 */
public class ShellUtil {
     public static final String TAG = "ShellUtil";
     public static final String COMMAND_SU = "su";
     public static final String COMMAND_SH = "sh";
     public static final String COMMAND_EXIT = "exit\n";
     public static final String COMMAND_LINE_END = "\n";

     public static final OnExecResultInfoListener mOnExecResultInfoListener = null;

     /**
      * check whether has root permission
      *
      * @return
      */
     public static boolean permission() {
          return execCommand("echo root", true, false).result == 0;
     }

     /**
      * execute shell command, default return result msg
      *
      * @param command command
      * @param isRoot  whether need to run with root
      * @return
      * @see ShellUtil#execCommand(String[], boolean, boolean)
      */
     public static CommandResult execCommand(String command, boolean isRoot) {
          return execCommand(new String[]{command}, isRoot, true);
     }

     /**
      * execute shell commands, default return result msg
      *
      * @param commands command list
      * @param isRoot   whether need to run with root
      * @return
      * @see ShellUtil#execCommand(String[], boolean, boolean)
      */
     public static CommandResult execCommand(List<String> commands, boolean isRoot) {
          return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, true);
     }

     /**
      * execute shell commands, default return result msg
      *
      * @param commands command array
      * @param isRoot   whether need to run with root
      * @return
      * @see ShellUtil#execCommand(String[], boolean, boolean)
      */
     public static CommandResult execCommand(String[] commands, boolean isRoot) {
          return execCommand(commands, isRoot, true);
     }

     /**
      * execute shell command
      *
      * @param command         command
      * @param isRoot          whether need to run with root
      * @param isNeedResultMsg whether need result msg
      * @return
      * @see ShellUtil#execCommand(String[], boolean, boolean)
      */
     public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
          return execCommand(new String[]{command}, isRoot, isNeedResultMsg);
     }

     public static void execCommand(String command, boolean isRoot, OnExecResultInfoListener eListener) {

          execCommand(new String[]{command}, isRoot, eListener);

     }

     /**
      * execute shell commands
      *
      * @param commands        command list
      * @param isRoot          whether need to run with root
      * @param isNeedResultMsg whether need result msg
      * @return
      * @see ShellUtil#execCommand(String[], boolean, boolean)
      */
     public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
          return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, isNeedResultMsg);
     }

     public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
          int result = -1;
          if (commands == null || commands.length == 0) {
               return new CommandResult(result, null, null);
          }

          Process process = null;
          BufferedReader successResult = null;
          BufferedReader errorResult = null;
          StringBuilder successMsg = null;
          StringBuilder errorMsg = null;

          DataOutputStream os = null;
          try {
               process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
               os = new DataOutputStream(process.getOutputStream());
               for (String command : commands) {
                    if (command == null) {
                         continue;
                    }

                    // donnot use os.writeBytes(commmand), avoid chinese charset error
                    os.write(command.getBytes());
                    os.writeBytes(COMMAND_LINE_END);
                    os.flush();
               }
               os.writeBytes(COMMAND_EXIT);
               os.flush();

               result = process.waitFor();
               // get command result
               if (isNeedResultMsg) {
                    successMsg = new StringBuilder();
                    errorMsg = new StringBuilder();
                    successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String s;
                    while ((s = successResult.readLine()) != null) {
                         successMsg.append(s + "\n");
                    }
                    while ((s = errorResult.readLine()) != null) {
                         errorMsg.append(s + "\n");
                    }
               }
          } catch (IOException e) {
               e.printStackTrace();
          } catch (Exception e) {
               e.printStackTrace();
          } finally {
               try {
                    if (os != null) {
                         os.close();
                    }
                    if (successResult != null) {
                         successResult.close();
                    }
                    if (errorResult != null) {
                         errorResult.close();
                    }
               } catch (IOException e) {
                    e.printStackTrace();
               }

               if (process != null) {
                    process.destroy();
               }
          }
          return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
                  : errorMsg.toString());
     }

     /**
      * execute shell commands
      *
      * @param commands                 command array
      * @param isRoot                   whether need to run with root
      * @param onExecResultinfolistener 监听器
      * @return <ul>
      * <li>if isNeedResultMsg is false, {@link CommandResult#successMsg} is null and
      * {@link CommandResult#errorMsg} is null.</li>
      * <li>if {@link CommandResult#result} is -1, there maybe some excepiton.</li>
      * </ul>
      */
     public static void execCommand(String[] commands, boolean isRoot, OnExecResultInfoListener onExecResultinfolistener) {
          if (commands == null || commands.length == 0) {
               return;
          }

          Process process = null;
          BufferedReader successResult = null;
          BufferedReader errorResult = null;
          StringBuilder successMsg = null;
          StringBuilder errorMsg = null;

          DataOutputStream os = null;

          try {
               process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
               os = new DataOutputStream(process.getOutputStream());
               for (String command : commands) {
                    if (command == null) {
                         continue;
                    }

                    // donnot use os.writeBytes(commmand), avoid chinese charset error
                    os.write(command.getBytes());
                    os.writeBytes(COMMAND_LINE_END);
                    os.flush();
               }

               os.writeBytes(COMMAND_EXIT);

               os.flush();

               successMsg = new StringBuilder();
               errorMsg = new StringBuilder();
               while (true) {
                    if (onExecResultinfolistener != null && onExecResultinfolistener instanceof OnExecResultInfoCallback) {
                         if (((OnExecResultInfoCallback) onExecResultinfolistener).isStop()) break;
                    }
                    successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    if (!(null != successResult | null != errorResult)) continue;

                    String s;

                    while ((s = successResult.readLine()) != null) {

                         successMsg.append(s + "\n");

                         if (null == onExecResultinfolistener) continue;

                         onExecResultinfolistener.OnResult(s);

                    }

                    while ((s = errorResult.readLine()) != null) {

                         errorMsg.append(s + "\n");

                         if (null == onExecResultinfolistener) continue;

                         onExecResultinfolistener.OnResult(s);

                    }

                    Thread.sleep(500);

               }

          } catch (IOException e) {
               e.printStackTrace();
          } catch (Exception e) {
               e.printStackTrace();
          } finally {
               try {
                    if (os != null) {
                         os.close();
                    }
                    if (successResult != null) {
                         successResult.close();
                    }
                    if (errorResult != null) {
                         errorResult.close();
                    }
               } catch (IOException e) {
                    e.printStackTrace();
               }

               if (process != null) {
                    process.destroy();
               }
          }

     }

     /**
      * 重启设备
      *
      * @param isSoft 软重启
      * @return 是否重启成功
      */
     public static boolean reboot(boolean isSoft) {
          if (isSoft) {
               CommandResult commandResult = execCommand("setprop ctl.restart surfaceflinger; setprop ctl.restart zygote", true);
               return commandResult.result == 0;
          } else {
               CommandResult commandResult = execCommand("/system/bin/reboot", true);
               if (commandResult.result != 0)
                    commandResult = execCommand("/system/xbin/reboot", true);
               return commandResult.result == 0;
          }
     }

     //设置返回信息监听

     public interface OnExecResultInfoListener {
          void OnResult(String result);
     }

     /**
      * result of command
      * <ul>
      * <li>{@link CommandResult#result} means result of command, 0 means normal, else means error, same to excute in
      * linux shell</li>
      * <li>{@link CommandResult#successMsg} means success message of command result</li>
      * <li>{@link CommandResult#errorMsg} means error message of command result</li>
      * </ul>
      *
      * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-16
      */
     public static class CommandResult {

          /**
           * result of command
           **/
          public int result;
          /**
           * success message of command result
           **/
          public String successMsg;
          /**
           * error message of command result
           **/
          public String errorMsg;

          public CommandResult(int result) {
               this.result = result;
          }

          public CommandResult(int result, String successMsg, String errorMsg) {
               this.result = result;
               this.successMsg = successMsg;
               this.errorMsg = errorMsg;
          }
     }

     public abstract static class OnExecResultInfoCallback implements OnExecResultInfoListener {
          protected abstract boolean isStop();
     }
}

