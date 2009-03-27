/* $Id: jni.c,v 1.1 2009-03-27 13:51:36 ball Exp $ */

#include <jni.h>

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void *reserved) {
    return;
}
/*
 * $Log: not supported by cvs2svn $
 */
