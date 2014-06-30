/* $Id$ */

#include "ball_util_jni_POSIX.h"

#include <dirent.h>
#include <limits.h>
#include <string.h>
#include <unistd.h>

JNIEXPORT jboolean JNICALL
Java_ball_util_jni_POSIX_link(JNIEnv *env, jclass class,
                              jstring from, jstring to) {
    const char *path1 = (*env)->GetStringUTFChars(env, from, 0);
    const char *path2 = (*env)->GetStringUTFChars(env, to, 0);
    int status = link(path1, path2);

    (*env)->ReleaseStringUTFChars(env, from, path1);
    (*env)->ReleaseStringUTFChars(env, to, path2);

    return (jboolean) (status == 0);
}

JNIEXPORT jboolean JNICALL
Java_ball_util_jni_POSIX_symlink(JNIEnv *env, jclass class,
                                 jstring from, jstring to) {
    const char *path1 = (*env)->GetStringUTFChars(env, from, 0);
    const char *path2 = (*env)->GetStringUTFChars(env, to, 0);
    int status = symlink(path1, path2);

    (*env)->ReleaseStringUTFChars(env, from, path1);
    (*env)->ReleaseStringUTFChars(env, to, path2);

    return (jboolean) (status == 0);
}

JNIEXPORT jstring JNICALL
Java_ball_util_jni_POSIX_readlink(JNIEnv *env, jclass class, jstring from) {
    const char *path = (*env)->GetStringUTFChars(env, from, 0);
    char buf[PATH_MAX];
    ssize_t len = readlink(path, buf, (sizeof buf) - 1);

    if (len > 0) {
        buf[len] = '\0';
    }

    (*env)->ReleaseStringUTFChars(env, from, path);

    return (len > 0) ? (*env)->NewStringUTF(env, buf) : NULL;
}

JNIEXPORT jlong JNICALL
Java_ball_util_jni_POSIX_opendir(JNIEnv *env, jclass class, jstring from) {
    const char *path = (*env)->GetStringUTFChars(env, from, 0);
    DIR *dirp = opendir(path);

    (*env)->ReleaseStringUTFChars(env, from, path);

    jlong peer = 0;

    if (! (*env)->ExceptionCheck(env)) {
        memcpy(&peer, &dirp, sizeof(DIR *));
    }

    return peer;
}
