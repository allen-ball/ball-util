/* $Id: iprotium_util_jni_EditLine.c,v 1.6 2010-12-22 17:48:07 ball Exp $ */

#include "iprotium_util_jni_EditLine.h"

#include <histedit.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

struct clientdata_t {
    JNIEnv *env;
    jobject object;
    char prompt[256];
    char rprompt[256];
    History *hist;
};

static const char *prompt(EditLine *el) {
    struct clientdata_t *clientdata = NULL;

    el_get(el, EL_CLIENTDATA, &clientdata);

    JNIEnv *env = clientdata->env;
    jobject this = clientdata->object;

    return clientdata->prompt;
}

static const char *rprompt(EditLine *el) {
    struct clientdata_t *clientdata = NULL;

    el_get(el, EL_CLIENTDATA, &clientdata);

    JNIEnv *env = clientdata->env;
    jobject this = clientdata->object;

    return clientdata->rprompt;
}

static const char POINTER_FIELD_NAME[] = "pointer";
static const char LONG_SIGNATURE[] = "J";

static jfieldID getPointerFieldID(JNIEnv *env, jobject object) {
    jclass class = (*env)->GetObjectClass(env, object);

    return (*env)->GetFieldID(env, class, POINTER_FIELD_NAME, LONG_SIGNATURE);
}

static EditLine *getPointer(JNIEnv *env, jobject object) {
    jfieldID fieldID = getPointerFieldID(env, object);
    jlong peer = (*env)->GetLongField(env, object, fieldID);
    EditLine *el = NULL;

    memcpy(&el, &peer, sizeof(EditLine *));

    return el;
}

static void setPointer(JNIEnv *env, jobject object, EditLine *el) {
    jfieldID fieldID = getPointerFieldID(env, object);
    jlong peer = 0;

    memcpy(&peer, &el, sizeof(EditLine *));
    (*env)->SetLongField(env, object, fieldID, peer);
}

static const char EMACS[] = "emacs";

JNIEXPORT void JNICALL
Java_iprotium_util_jni_EditLine_init(JNIEnv *env, jobject this,
                                     jstring string) {
    const char *prog = (*env)->GetStringUTFChars(env, string, 0);
    FILE *fin = fdopen(0, "r");
    FILE *fout = fdopen(1, "w");
    FILE *ferr = fdopen(2, "w");
    EditLine *el = el_init(prog, fin, fout, ferr);

    (*env)->ReleaseStringUTFChars(env, string, prog);

    struct clientdata_t *clientdata =
        (struct clientdata_t *) malloc(sizeof(struct clientdata_t));

    clientdata->env = env;
    clientdata->object = this;
    snprintf(clientdata->prompt, sizeof clientdata->prompt, "%s> ", prog);
    snprintf(clientdata->rprompt, sizeof clientdata->rprompt, "");
    clientdata->hist = history_init();

    HistEvent ev;

    history(clientdata->hist, &ev, H_SETSIZE, 128);
    history(clientdata->hist, &ev, H_SETUNIQUE, 1);

    el_set(el, EL_CLIENTDATA, (void *) clientdata);
    el_set(el, EL_EDITOR, EMACS);
    el_set(el, EL_HIST, history, clientdata->hist);
    el_set(el, EL_PROMPT, prompt);
    el_set(el, EL_RPROMPT, rprompt);
    el_source(el, NULL);

    setPointer(env, this, el);
}

JNIEXPORT void JNICALL
Java_iprotium_util_jni_EditLine_reset(JNIEnv *env, jobject this) {
    EditLine *el = getPointer(env, this);

    el_reset(el);
}

JNIEXPORT jstring JNICALL
Java_iprotium_util_jni_EditLine_gets(JNIEnv *env, jobject this) {
    jstring string = NULL;
    EditLine *el = getPointer(env, this);
    int count = 0;
    const char *result = el_gets(el, &count);

    if (result != NULL) {
        char buffer[count + 1];

        memset(buffer, 0, sizeof buffer);
        memcpy(buffer, result, count);

        struct clientdata_t *clientdata = NULL;

        el_get(el, EL_CLIENTDATA, &clientdata);

        if (clientdata != NULL && clientdata->hist != NULL) {
            HistEvent ev;

            history(clientdata->hist, &ev, H_ENTER, buffer);
        }

        string = (*env)->NewStringUTF(env, buffer);
    }

    return string;
}

JNIEXPORT jint JNICALL
Java_iprotium_util_jni_EditLine_getc(JNIEnv *env, jobject this) {
    char character = -1;
    EditLine *el = getPointer(env, this);
    int count = el_getc(el, &character);

    if (! (count > 0)) {
        character = -1;
    }

    return (jint) character;
}

JNIEXPORT void JNICALL
Java_iprotium_util_jni_EditLine_push(JNIEnv *env,
                                     jobject this, jstring string) {
    EditLine *el = getPointer(env, this);
    const char *str = (*env)->GetStringUTFChars(env, string, 0);

    el_push(el, str);
    (*env)->ReleaseStringUTFChars(env, string, str);
}

JNIEXPORT jint JNICALL
Java_iprotium_util_jni_EditLine_parse(JNIEnv *env,
                                      jobject this, jobjectArray array) {
    int result = -1;
    EditLine *el = getPointer(env, this);
    int argc = (*env)->GetArrayLength(env, array);
    const char *argv[argc];
    jsize i = 0;

    for (i = 0; i < argc; i += 1) {
        jstring string =
            (jstring) (*env)->GetObjectArrayElement(env, array, i);

        argv[i] = (*env)->GetStringUTFChars(env, string, 0);
    }

    result = el_parse(el, argc, argv);

    for (i = 0; i < argc; i += 1) {
        jstring string =
            (jstring) (*env)->GetObjectArrayElement(env, array, i);

        (*env)->ReleaseStringUTFChars(env, string, argv[i]);
    }

    return result;
}

JNIEXPORT jint JNICALL
Java_iprotium_util_jni_EditLine_source(JNIEnv *env,
                                       jobject this, jstring string) {
    int result = -1;
    EditLine *el = getPointer(env, this);
    const char *path = (*env)->GetStringUTFChars(env, string, 0);

    result = el_source(el, path);
    (*env)->ReleaseStringUTFChars(env, string, path);

    return result;
}

JNIEXPORT void JNICALL
Java_iprotium_util_jni_EditLine_end(JNIEnv *env, jobject this) {
    EditLine *el = getPointer(env, this);

    setPointer(env, this, NULL);

    if (el != NULL) {
        struct clientdata_t *clientdata = NULL;

        el_get(el, EL_CLIENTDATA, &clientdata);

        if (clientdata != NULL) {
            if (clientdata->hist != NULL) {
                history_end(clientdata->hist);
                clientdata->hist = NULL;
            }

            free(clientdata);
        }

        el_end(el);
    }
}

JNIEXPORT jobjectArray JNICALL
Java_iprotium_util_jni_EditLine_tokenize(JNIEnv *env, jclass class,
                                         jstring string) {
    jobjectArray array = NULL;
    Tokenizer *tok = tok_init(NULL);
    const char *str = (*env)->GetStringUTFChars(env, string, 0);
    int argc = 0;
    const char **argv;

    if (tok_str(tok, str, &argc, &argv) == 0) {
        jclass type = (*env)->FindClass(env, "java/lang/String");

        array = (*env)->NewObjectArray(env, argc, type, 0);

        jsize i = 0;

        for (i = 0; i < argc; i += 1) {
            jstring element = (*env)->NewStringUTF(env, argv[i]);

            (*env)->SetObjectArrayElement(env, array, i, element);
        }
    }

    tok_end(tok);
    (*env)->ReleaseStringUTFChars(env, string, str);

    return array;
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2010/12/21 16:19:32  ball
 * Added history support.
 *
 */
