/* $Id: iprotium_util_jni_POSIX.c,v 1.3 2009-03-24 05:57:41 ball Exp $ */

#include "iprotium_util_jni_POSIX.h"

#include <unistd.h>

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void *reserved) {
    return;
}

JNIEXPORT jboolean JNICALL
Java_iprotium_util_jni_POSIX_link(JNIEnv *env, jclass class,
                                  jstring from, jstring to) {
    const char *path1 = (*env)->GetStringUTFChars(env, from, 0);
    const char *path2 = (*env)->GetStringUTFChars(env, to, 0);
    int status = link(path1, path2);

    (*env)->ReleaseStringUTFChars(env, from, path1);
    (*env)->ReleaseStringUTFChars(env, to, path2);

    return (jboolean) (status == 0);
}

JNIEXPORT jboolean JNICALL
Java_iprotium_util_jni_POSIX_symlink(JNIEnv *env, jclass class,
                                     jstring from, jstring to) {
    const char *path1 = (*env)->GetStringUTFChars(env, from, 0);
    const char *path2 = (*env)->GetStringUTFChars(env, to, 0);
    int status = symlink(path1, path2);

    (*env)->ReleaseStringUTFChars(env, from, path1);
    (*env)->ReleaseStringUTFChars(env, to, path2);

    return (jboolean) (status == 0);
}
/*
 * $Log: not supported by cvs2svn $
 */
