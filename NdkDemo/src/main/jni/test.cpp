#include <jni.h>
#include "android/log.h"

int *array;

extern "C" {

JNIEXPORT void JNICALL
Java_com_johnnyyin_ndkdemo_AllocTest_test(JNIEnv *env, jclass type) {
    __android_log_write(ANDROID_LOG_ERROR, "SS", "TEST");
    array = new int[1024 * 1024 * 100];
}

}