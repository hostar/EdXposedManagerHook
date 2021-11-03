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

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Module implements IXposedHookLoadPackage {

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable
    {
        if (lpparam.packageName.equals("noorg.nothing.nope.no"))
        {
            XposedBridge.log("Loaded " + lpparam.packageName + ", hooking some things");

            try
            {
                findAndHookMethod(CONSTANTS_CLASS, classLoader, "getActiveXposedVersion",
                        XC_MethodReplacement.returnConstant(XposedBridge.getXposedVersion())
                );

                findAndHookMethod(CONSTANTS_CLASS, classLoader, "getInstalledXposedVersion",
                        XC_MethodReplacement.returnConstant(BuildConfig.VERSION_NAME + "_" + BuildConfig.VERSION_CODE + " (" + variant + ")")
                );

                findAndHookMethod(CONSTANTS_CLASS, classLoader, "getBaseDir",
                        XC_MethodReplacement.returnConstant(ConfigManager.getBaseConfigPath() + "/")
                );

                findAndHookMethod(CONSTANTS_CLASS, classLoader, "isSELinuxEnforced",
                        XC_MethodReplacement.returnConstant(ConfigManager.isSELinuxEnforced())
                );
            }
            catch (NoSuchMethodError e)
            {
                XposedBridge.log("Unable to hook new methods: (" + e.toString() + ")");
            }
        }
    }
}