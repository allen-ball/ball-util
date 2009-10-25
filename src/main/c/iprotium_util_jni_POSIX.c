/* $Id: iprotium_util_jni_POSIX.c,v 1.6 2009-10-25 14:57:29 ball Exp $ */

#include "iprotium_util_jni_POSIX.h"

#include <dirent.h>
#include <limits.h>
#include <string.h>
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

JNIEXPORT jstring JNICALL
Java_iprotium_util_jni_POSIX_readlink(JNIEnv *env, jclass class,
                                      jstring from) {
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
Java_iprotium_util_jni_POSIX_opendir(JNIEnv *env, jclass class, jstring from) {
    const char *path = (*env)->GetStringUTFChars(env, from, 0);
    DIR *dirp = opendir(path);

    (*env)->ReleaseStringUTFChars(env, from, path);

    jlong peer = 0;

    if (! (*env)->ExceptionCheck(env)) {
        memcpy(&peer, &dirp, sizeof(DIR *));
    }

    return peer;
}

JNIEXPORT void JNICALL
Java_iprotium_util_jni_POSIX_closedir(JNIEnv *env, jclass class, jlong peer) {
    DIR *dirp = (DIR *) NULL;

    memcpy(&dirp, &peer, sizeof (void *));
    closedir(dirp);

    return;
}

JNIEXPORT jstring JNICALL
Java_iprotium_util_jni_POSIX_readdir(JNIEnv *env, jclass class, jlong peer) {
    DIR *dirp = (DIR *) NULL;

    memcpy(&dirp, &peer, sizeof (void *));

    struct dirent *dp = readdir(dirp);

    return (dp != NULL) ? (*env)->NewStringUTF(env, dp->d_name) : NULL;
}

JNIEXPORT void JNICALL
Java_iprotium_util_jni_POSIX_rewinddir(JNIEnv *env, jclass class, jlong peer) {
    DIR *dirp = (DIR *) NULL;

    memcpy(&dirp, &peer, sizeof (void *));
    rewinddir(dirp);

    return;
}
/*
 * $Log: not supported by cvs2svn $
 */
