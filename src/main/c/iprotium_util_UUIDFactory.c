/* $Id$ */

#include "iprotium_util_UUIDFactory.h"

#include <uuid/uuid.h>

JNIEXPORT void JNICALL
Java_iprotium_util_UUIDFactory_uuid_1generate(JNIEnv *env, jclass class,
                                              jbyteArray bytes) {
    uuid_t out;

    uuid_generate(out);
    (*env)->SetByteArrayRegion(env, bytes, 0, sizeof out, (jbyte *) out);
}

JNIEXPORT void JNICALL
Java_iprotium_util_UUIDFactory_uuid_1generate_1random(JNIEnv *env,
                                                      jclass class,
                                                      jbyteArray bytes) {
    uuid_t out;

    uuid_generate_random(out);
    (*env)->SetByteArrayRegion(env, bytes, 0, sizeof out, (jbyte *) out);
}

JNIEXPORT void JNICALL
Java_iprotium_util_UUIDFactory_uuid_1generate_1time(JNIEnv *env, jclass class,
                                                    jbyteArray bytes) {
    uuid_t out;

    uuid_generate_time(out);
    (*env)->SetByteArrayRegion(env, bytes, 0, sizeof out, (jbyte *) out);
}
