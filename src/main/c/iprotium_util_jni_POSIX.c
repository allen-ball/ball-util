/* $Id: iprotium_util_jni_POSIX.c,v 1.2 2008-11-10 01:01:58 ball Exp $ */

#include "iprotium_util_jni_POSIX.h"

#include <unistd.h>

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void *reserved) {
    return;
}

JNIEXPORT jboolean JNICALL
Java_iprotium_util_jni_POSIX_link(JNIEnv *env,
                                  jclass class, jstring s, jstring t) {
    const char *source = (*env)->GetStringUTFChars(env, s, 0);
    const char *target = (*env)->GetStringUTFChars(env, t, 0);
    int status = link(source, target);

    (*env)->ReleaseStringUTFChars(env, s, source);
    (*env)->ReleaseStringUTFChars(env, t, target);

    return (jboolean) (status == 0);
}

JNIEXPORT jboolean JNICALL
Java_iprotium_util_jni_POSIX_symlink(JNIEnv *env,
                                     jclass class, jstring s, jstring t) {
    const char *source = (*env)->GetStringUTFChars(env, s, 0);
    const char *target = (*env)->GetStringUTFChars(env, t, 0);
    int status = symlink(source, target);

    (*env)->ReleaseStringUTFChars(env, s, source);
    (*env)->ReleaseStringUTFChars(env, t, target);

    return (jboolean) (status == 0);
}
/*
 * $Log: not supported by cvs2svn $
 */
