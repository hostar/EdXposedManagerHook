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
import java.util.Scanner; // Import the Scanner class to read text files

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Module implements IXposedHookLoadPackage {

    private static final String CONSTANTS_CLASS = "noorg.nothing.nope.no.Constants";
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable
    {
        if (lpparam.packageName.equals("noorg.nothing.nope.no"))
        {
            try {
                Runtime rt = Runtime.getRuntime();
                Process process = rt.exec("su");
            } catch (Exception e) {
                XposedBridge.log(e.toString());
            }

            String folderName = "";
            try {
                File myObj = new File("/data/adb/edxp/misc_path");
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    folderName = myReader.nextLine();
                    XposedBridge.log("folderName " + folderName);
                    break;
                    //System.out.println(data);
                }
                myReader.close();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);

                XposedBridge.log(e.toString());
                //XposedBridge.log(sw.toString());
            }

            String configPath = "/data/misc/" + folderName;
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