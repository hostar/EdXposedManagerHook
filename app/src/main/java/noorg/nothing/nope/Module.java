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

    public static native boolean isBlackWhiteListEnabled();

    public static native boolean isNoModuleLogEnabled();

    public static native boolean isResourcesHookEnabled();

//    public static native boolean isDeoptBootImageEnabled();

    public static native boolean isSELinuxEnforced();

    public static native String getInstallerPackageName();

//    public static native String getXposedPropPath();

    public static native String getLibSandHookName();

    public static native String getConfigPath(String suffix);

    public static native String getPrefsPath(String suffix);

    public static native String getCachePath(String suffix);

    public static native String getBaseConfigPath();

    public static native String getDataPathPrefix();

    public static native String getModulesList();

    private static final String CONSTANTS_CLASS = "noorg.nothing.nope.no.Constants";
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable
    {
        if (lpparam.packageName.equals("noorg.nothing.nope.no"))
        {
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
                        XC_MethodReplacement.returnConstant(getBaseConfigPath() + "/")
                );

                findAndHookMethod(CONSTANTS_CLASS, lpparam.classLoader, "isSELinuxEnforced",
                        XC_MethodReplacement.returnConstant(isSELinuxEnforced())
                );
            }
            catch (NoSuchMethodError e)
            {
                XposedBridge.log("Unable to hook new methods: (" + e.toString() + ")");
            }
        }
    }
}