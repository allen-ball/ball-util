/* $Id: iprotium_util_jni_Readline.c,v 1.3 2010-12-26 18:30:05 ball Exp $ */

#include "iprotium_util_jni_Readline.h"

#include <readline/readline.h>
#include <stdlib.h>
#include <string.h>

struct readline_t {
    JNIEnv *env;
    jobject object;
    char *rl_readline_name;
};

static const char POINTER_FIELD_NAME[] = "pointer";
static const char LONG_SIGNATURE[] = "J";

static jfieldID getPointerFieldID(JNIEnv *env, jobject object) {
    jclass class = (*env)->GetObjectClass(env, object);

    return (*env)->GetFieldID(env, class, POINTER_FIELD_NAME, LONG_SIGNATURE);
}

static struct readline_t *getPointer(JNIEnv *env, jobject object) {
    jfieldID fieldID = getPointerFieldID(env, object);
    jlong peer = (*env)->GetLongField(env, object, fieldID);
    struct readline_t *rl = NULL;

    memcpy(&rl, &peer, sizeof(char *));

    return rl;
}

static void setPointer(JNIEnv *env, jobject object, struct readline_t *rl) {
    jfieldID fieldID = getPointerFieldID(env, object);
    jlong peer = 0;

    memcpy(&peer, &rl, sizeof(char *));
    (*env)->SetLongField(env, object, fieldID, peer);
}

JNIEXPORT void JNICALL
Java_iprotium_util_jni_Readline_init(JNIEnv *env, jobject this,
                                     jstring string) {
    const char *prog = (*env)->GetStringUTFChars(env, string, 0);
    struct readline_t *rl =
        (struct readline_t *) malloc(sizeof(struct readline_t));

    rl->env = env;
    rl->object = this;
    rl->rl_readline_name = strdup(prog);

    setPointer(env, this, rl);
    (*env)->ReleaseStringUTFChars(env, string, prog);
}

JNIEXPORT jstring JNICALL
Java_iprotium_util_jni_Readline_readline(JNIEnv *env, jobject this,
                                         jstring string) {
    jstring line = NULL;
    const char *prompt =
        (string != NULL) ? (*env)->GetStringUTFChars(env, string, 0) : NULL;
    struct readline_t *rl = getPointer(env, this);

    rl_readline_name = rl->rl_readline_name;

    char *result = readline(prompt);

    if (result != NULL) {
        line = (*env)->NewStringUTF(env, result);
        free(result);
    }

    if (prompt != NULL) {
        (*env)->ReleaseStringUTFChars(env, string, prompt);
    }

    return line;
}

JNIEXPORT void JNICALL
Java_iprotium_util_jni_Readline_add_1history(JNIEnv *env, jobject this,
                                             jstring string) {
    if (string != NULL) {
        const char *line = (*env)->GetStringUTFChars(env, string, 0);
        struct readline_t *rl = getPointer(env, this);

        rl_readline_name = rl->rl_readline_name;
        add_history(strdup(line));
        (*env)->ReleaseStringUTFChars(env, string, line);
    }
}

JNIEXPORT void JNICALL
Java_iprotium_util_jni_Readline_end(JNIEnv *env, jobject this) {
    rl_readline_name = NULL;

    struct readline_t *rl = getPointer(env, this);

    setPointer(env, this, NULL);

    if (rl != NULL) {
        if (rl->rl_readline_name != NULL) {
            free(rl->rl_readline_name);
        }

        memset(rl, 0, sizeof(*rl));
        free(rl);
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
