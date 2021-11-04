package noorg.nothing.nope;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.StringReader;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Scanner; // Import the Scanner class to read text files

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Module implements IXposedHookLoadPackage {

    private static final String CONSTANTS_CLASS = "noorg.nothing.nope.no.Constants";
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable
    {
        if (lpparam.packageName.equals("noorg.nothing.nope.no"))
        {
            String folderName = "";
            try {
                Runtime rt = Runtime.getRuntime();
                Process process = rt.exec("su -c cat /data/adb/edxp/misc_path");

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                int read;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                }
                reader.close();

                // Waits for the command to finish.
                process.waitFor();

                folderName = output.toString().trim();
            } catch (Exception e) {
                XposedBridge.log(e.toString());
            }

            String configPath = "/data/misc/" + folderName + "/0";
            XposedBridge.log("configPath " + configPath);

            XposedBridge.log("Loaded " + lpparam.packageName + ", hooking some things");

            try
            {
                findAndHookMethod(CONSTANTS_CLASS, lpparam.classLoader, "getActiveXposedVersion",
                        XC_MethodReplacement.returnConstant(XposedBridge.getXposedVersion())
                );

                String variant = "SandHook"; // YAHFA

                findAndHookMethod(CONSTANTS_CLASS, lpparam.classLoader, "getInstalledXposedVersion",
                        XC_MethodReplacement.returnConstant(BuildConfig.VERSION_NAME + "_" + BuildConfig.VERSION_CODE + " (" + variant + ")")
                );

                findAndHookMethod(CONSTANTS_CLASS, lpparam.classLoader, "getBaseDir",
                        XC_MethodReplacement.returnConstant(configPath)
                );

                findAndHookMethod(CONSTANTS_CLASS, lpparam.classLoader, "isSELinuxEnforced",
                        XC_MethodReplacement.returnConstant(true)
                );
            }
            catch (NoSuchMethodError e)
            {
                XposedBridge.log("Unable to hook new methods: (" + e.toString() + ")");
            }
        }
    }
}