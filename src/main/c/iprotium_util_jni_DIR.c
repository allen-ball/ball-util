/* $Id$ */

#include "ball_util_jni_DIR.h"

#include <dirent.h>
#include <string.h>

static const char POINTER_FIELD_NAME[] = "pointer";
static const char LONG_SIGNATURE[] = "J";

static jfieldID getPointerFieldID(JNIEnv *env, jobject object) {
    jclass class = (*env)->GetObjectClass(env, object);

    return (*env)->GetFieldID(env, class, POINTER_FIELD_NAME, LONG_SIGNATURE);
}

static DIR *getPointer(JNIEnv *env, jobject object) {
    jfieldID fieldID = getPointerFieldID(env, object);
    jlong peer = (*env)->GetLongField(env, object, fieldID);
    DIR *dirp = NULL;

    memcpy(&dirp, &peer, sizeof(DIR *));

    return dirp;
}

static void setPointer(JNIEnv *env, jobject object, DIR *dirp) {
    jfieldID fieldID = getPointerFieldID(env, object);
    jlong peer = 0;

    memcpy(&peer, &dirp, sizeof(DIR *));
    (*env)->SetLongField(env, object, fieldID, peer);
}

JNIEXPORT void JNICALL
Java_ball_util_jni_DIR_closedir(JNIEnv *env, jobject this) {
    DIR *dirp = getPointer(env, this);

    setPointer(env, this, NULL);

    if (dirp != NULL) {
        closedir(dirp);
    }
}

JNIEXPORT jstring JNICALL
Java_ball_util_jni_DIR_readdir(JNIEnv *env, jobject this) {
    DIR *dirp = getPointer(env, this);
    struct dirent *dp = readdir(dirp);

    return (dp != NULL) ? (*env)->NewStringUTF(env, dp->d_name) : NULL;
}

JNIEXPORT void JNICALL
Java_ball_util_jni_DIR_rewinddir(JNIEnv *env, jobject this) {
    DIR *dirp = getPointer(env, this);

    rewinddir(dirp);
}
