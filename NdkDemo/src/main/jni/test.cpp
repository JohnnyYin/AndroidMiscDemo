#include <jni.h>
#include <assert.h>
#include <malloc.h>
#include <string.h>
#include <stdlib.h>
#include "android/log.h"

// 日志tag
#define LOG_TAG "TR"

// debug开关
#define DEBUG true

// 应用的正式签名
#define SIGN_HEX "95D866E6B6EC18A80A041D89E65A4CA3"

#define ALOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

int *array;

extern "C" {

JNIEXPORT void JNICALL
Java_com_johnnyyin_ndkdemo_AllocTest_testAllocMemory(JNIEnv *env, jclass type) {
    if (DEBUG)
        ALOGD("TEST");

    // 测试分配大内存
    array = new int[1024 * 1024 * 100];
}

void byteToHexStr(const unsigned char *source, char *dest, int sourceLen) {
    short i;
    unsigned char highByte, lowByte;

    for (i = 0; i < sourceLen; i++) {
        highByte = source[i] >> 4;
        lowByte = source[i] & 0x0f;

        highByte += 0x30;

        if (highByte > 0x39)
            dest[i * 2] = highByte + 0x07;
        else
            dest[i * 2] = highByte;

        lowByte += 0x30;
        if (lowByte > 0x39)
            dest[i * 2 + 1] = lowByte + 0x07;
        else
            dest[i * 2 + 1] = lowByte;
    }
    return;
}

/**
 * 初始化, 校验签名
 */
void checkSign(JNIEnv *env) {
    jclass clsApplication = env->FindClass("com/johnnyyin/ndkdemo/DemoApplication");
    if (clsApplication != NULL) {
        // "()Lcom/johnnyyin/ndkdemo/DemoApplication;"
        jmethodID midGetInstance = env->GetStaticMethodID(clsApplication, "getInstance",
                                                          "()Lcom/johnnyyin/ndkdemo/DemoApplication;");
        jobject application = env->CallStaticObjectMethod(clsApplication, midGetInstance);

        jmethodID midGetPackageManager = env->GetMethodID(clsApplication, "getPackageManager",
                                                          "()Landroid/content/pm/PackageManager;");
        jmethodID midGetPackageName = env->GetMethodID(clsApplication, "getPackageName",
                                                       "()Ljava/lang/String;");
        jmethodID midGetPackageInfo = env->GetMethodID(
                env->FindClass("android/content/pm/PackageManager"), "getPackageInfo",
                "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");

        jobject packageManager = env->CallObjectMethod(application, midGetPackageManager);
        jstring packageName = (jstring) env->CallObjectMethod(application, midGetPackageName);

        jobject packageInfo = env->CallObjectMethod(packageManager, midGetPackageInfo, packageName,
                                                    0x00000040);

        jfieldID fidSignatures = env->GetFieldID(env->FindClass("android/content/pm/PackageInfo"),
                                                 "signatures",
                                                 "[Landroid/content/pm/Signature;");
        jobjectArray signatures = (jobjectArray) env->GetObjectField(packageInfo, fidSignatures);
        int length = env->GetArrayLength(signatures);
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                jobject signature = env->GetObjectArrayElement(signatures, i);

                // localSignature.toByteArray()
                jmethodID midToByteArray = env->GetMethodID(env->GetObjectClass(signature),
                                                            "toByteArray", "()[B");
                jobject obj_sign_byte_array = env->CallObjectMethod(signature,
                                                                    midToByteArray);

                //      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
                jclass clsMessageDigest = env->FindClass("java/security/MessageDigest");
                jmethodID midMessageDigestGetInstance = env->GetStaticMethodID(clsMessageDigest,
                                                                               "getInstance",
                                                                               "(Ljava/lang/String;)Ljava/security/MessageDigest;");
                jobject objMd5 = env->CallStaticObjectMethod(clsMessageDigest,
                                                             midMessageDigestGetInstance,
                                                             env->NewStringUTF("md5"));
                //      localMessageDigest.update(localSignature.toByteArray());
                //tem_class = (*env)->GetObjectClass(env, obj_md5);
                jmethodID midUpdate = env->GetMethodID(clsMessageDigest, "update",
                                                       "([B)V");
                env->CallVoidMethod(objMd5, midUpdate, obj_sign_byte_array);
                // localMessageDigest.digest()
                jmethodID midDigest = env->GetMethodID(clsMessageDigest, "digest", "()[B");
                // 这个是md5以后的byte数组，现在只要将它转换成16进制字符串，就可以和之前的比较了
                jbyteArray objArraySign = (jbyteArray) env->CallObjectMethod(objMd5,
                                                                             midDigest);
                //      // 这个就是签名的md5值
                //      String str2 = toHex(localMessageDigest.digest());

                jsize int_array_length = env->GetArrayLength(objArraySign);
                jbyte *byte_array_elements = env->GetByteArrayElements(objArraySign,
                                                                       JNI_FALSE);
                char *char_result = (char *) malloc(int_array_length * 2 + 1);
                // 将byte数组转换成16进制字符串
                byteToHexStr((const unsigned char *) byte_array_elements, char_result,
                             int_array_length);
                *(char_result + int_array_length * 2) = '\0';// 在末尾补\0

                if (DEBUG) {
                    jstring string_result = env->NewStringUTF(char_result);
                    // release
                    env->ReleaseByteArrayElements(objArraySign, byte_array_elements,
                                                  JNI_ABORT);
                    ALOGD(env->GetStringUTFChars(string_result, false));
                }

                free(char_result);

                int cmpResult = strcmp(char_result, SIGN_HEX);
                if (cmpResult == 0) {
                    if (DEBUG)
                        ALOGD("ok");
                } else {
                    exit(0);
                }
            }
        }
    }
}

void init(JNIEnv *env) {
    ::checkSign(env);
}

/**
 * 成功则返回JNI版本号，失败返回-1.
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);

    // 执行初始化操作
    ::init(env);

    /* 注册成功，返回JNI版本号 */
    return JNI_VERSION_1_6;
}
}