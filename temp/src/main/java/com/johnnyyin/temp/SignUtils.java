package com.johnnyyin.temp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.DisplayMetrics;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SignUtils {

    public static byte[] getApkSignature(String apkPath) {
        String PATH_PackageParser = "android.content.pm.PackageParser";
        try {
            Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
            Class<?>[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            Object pkgParser;
            if (Build.VERSION.SDK_INT > 19) {
                pkgParser = pkgParserCls.newInstance();
            } else {
                Constructor constructor = pkgParserCls.getConstructor(typeArgs);
                pkgParser = constructor.newInstance(valueArgs);
            }

            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            // PackageParser.Package mPkgInfo = packageParser.parsePackage(new
            // File(apkPath), apkPath,
            // metrics, 0);
            Object pkgParserPkg;
            if (Build.VERSION.SDK_INT > 19) {
                typeArgs = new Class[2];
                typeArgs[0] = File.class;
                typeArgs[1] = int.class;

                Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
                        "parsePackage", typeArgs);
                pkgParser_parsePackageMtd.setAccessible(true);

                valueArgs = new Object[2];
                valueArgs[0] = new File(apkPath);
                valueArgs[1] = PackageManager.GET_SIGNATURES;
                pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
                        valueArgs);
            } else {
                typeArgs = new Class[4];
                typeArgs[0] = File.class;
                typeArgs[1] = String.class;
                typeArgs[2] = DisplayMetrics.class;
                typeArgs[3] = int.class;

                Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
                        "parsePackage", typeArgs);
                pkgParser_parsePackageMtd.setAccessible(true);

                valueArgs = new Object[4];
                valueArgs[0] = new File(apkPath);
                valueArgs[1] = apkPath;
                valueArgs[2] = metrics;
                valueArgs[3] = PackageManager.GET_SIGNATURES;
                pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
                        valueArgs);
            }

            typeArgs = new Class[2];
            typeArgs[0] = pkgParserPkg.getClass();
            typeArgs[1] = int.class;
            Method pkgParser_collectCertificatesMtd = pkgParserCls.getDeclaredMethod("collectCertificates", typeArgs);
            valueArgs = new Object[2];
            valueArgs[0] = pkgParserPkg;
            valueArgs[1] = PackageManager.GET_SIGNATURES;
            pkgParser_collectCertificatesMtd.invoke(pkgParser, valueArgs);
            Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField(
                    "mSignatures");
            Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
            return info[0].toByteArray();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

}
