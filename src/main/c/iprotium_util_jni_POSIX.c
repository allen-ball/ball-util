/* $Id: iprotium_util_jni_POSIX.c,v 1.4 2009-03-27 13:51:36 ball Exp $ */

#include "iprotium_util_jni_POSIX.h"

#include <unistd.h>

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
