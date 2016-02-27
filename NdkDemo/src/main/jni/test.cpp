#include <jni.h>
#include "android/log.h"
#include "Object.h"

#define LOG_TAG "SS"

#define ALOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

int *array;

extern "C" {

JNIEXPORT void JNICALL
Java_com_johnnyyin_ndkdemo_AllocTest_test(JNIEnv *env, jclass type) {
    ALOGD("TEST");
    array = new int[1024 * 1024 * 100];
}

JNIEXPORT void JNICALL
Java_com_johnnyyin_ndkdemo_AllocTest_monifyClassLoader(JNIEnv *env, jclass type, jobject a,
                                                       jobject b) {

    ALOGD("monifyClassLoader start");
    ClassObject* clazza = (ClassObject*) a;
    ClassObject* clazzb = (ClassObject*) b;
    clazza->classLoader = clazzb->classLoader;
    ALOGD("monifyClassLoader end");
}

}