/* $Id$ */

#include "iprotium_util_jni_EditLine.h"
#ifndef USE_EDITLINE
#define USE_EDITLINE 0
#endif
#if USE_EDITLINE
#include <histedit.h>
#else
#include <readline/readline.h>
#include <readline/history.h>
#endif
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

static const char POINTER_FIELD_NAME[] = "pointer";
static const char LONG_SIGNATURE[] = "J";

struct rl_t {
#if USE_EDITLINE
    char prompt[256];
    char rprompt[256];
    History *hist;
#else
    char *rl_readline_name;
#endif
};

#if USE_EDITLINE
static const char *prompt(EditLine *el) {
    struct rl_t *clientdata = NULL;

    el_get(el, EL_CLIENTDATA, &clientdata);

    return clientdata->prompt;
}

static const char *rprompt(EditLine *el) {
    struct rl_t *clientdata = NULL;

    el_get(el, EL_CLIENTDATA, &clientdata);

    return clientdata->rprompt;
}

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
#else
static jfieldID getPointerFieldID(JNIEnv *env, jobject object) {
    jclass class = (*env)->GetObjectClass(env, object);

    return (*env)->GetFieldID(env, class, POINTER_FIELD_NAME, LONG_SIGNATURE);
}

static struct rl_t *getPointer(JNIEnv *env, jobject object) {
    jfieldID fieldID = getPointerFieldID(env, object);
    jlong peer = (*env)->GetLongField(env, object, fieldID);
    struct rl_t *rl = NULL;

    memcpy(&rl, &peer, sizeof(struct rl_t *));

    return rl;
}

static void setPointer(JNIEnv *env, jobject object, struct rl_t *rl) {
    jfieldID fieldID = getPointerFieldID(env, object);
    jlong peer = 0;

    memcpy(&peer, &rl, sizeof(struct rl_t *));
    (*env)->SetLongField(env, object, fieldID, peer);
}
#endif

JNIEXPORT void JNICALL
Java_iprotium_util_jni_EditLine_init(JNIEnv *env, jobject this,
                                     jstring string) {
    const char *prog = (*env)->GetStringUTFChars(env, string, 0);
#if USE_EDITLINE
    FILE *fin = fdopen(0, "r");
    FILE *fout = fdopen(1, "w");
    FILE *ferr = fdopen(2, "w");
    EditLine *el = el_init(prog, fin, fout, ferr);

    struct rl_t *clientdata = (struct rl_t *) malloc(sizeof(struct rl_t));

    snprintf(clientdata->prompt, sizeof clientdata->prompt, "%s> ", prog);
    snprintf(clientdata->rprompt, sizeof clientdata->rprompt, "");
    clientdata->hist = history_init();

    HistEvent ev;

    history(clientdata->hist, &ev, H_SETSIZE, 128);
    history(clientdata->hist, &ev, H_SETUNIQUE, 1);

    el_set(el, EL_CLIENTDATA, (void *) clientdata);
    el_set(el, EL_EDITOR, "emacs");
    el_set(el, EL_HIST, history, clientdata->hist);
    el_set(el, EL_PROMPT, prompt);
    el_set(el, EL_RPROMPT, rprompt);
    el_source(el, NULL);

    setPointer(env, this, el);
#else
    struct rl_t *rl = (struct rl_t *) malloc(sizeof(struct rl_t));

    rl->rl_readline_name = strdup(prog);

    setPointer(env, this, rl);
#endif
    (*env)->ReleaseStringUTFChars(env, string, prog);
}

JNIEXPORT jstring JNICALL
Java_iprotium_util_jni_EditLine_readline(JNIEnv *env, jobject this,
                                         jstring string) {
    jstring line = NULL;
    const char *prompt =
        (string != NULL) ? (*env)->GetStringUTFChars(env, string, 0) : NULL;
#if USE_EDITLINE
    EditLine *el = getPointer(env, this);
    struct rl_t *clientdata = NULL;

    el_get(el, EL_CLIENTDATA, &clientdata);

    if (prompt != NULL) {
        snprintf(clientdata->prompt, sizeof clientdata->prompt, "%s", prompt);
    }

    snprintf(clientdata->rprompt, sizeof clientdata->rprompt, "");

    int count = 0;
    const char *result = el_gets(el, &count);

    if (result != NULL) {
        if (result[count - 1] == '\n') {
            count -= 1;
        }

        if (result[count - 1] == '\r') {
            count -= 1;
        }

        char buffer[count + 1];

        memset(buffer, 0, sizeof buffer);
        memcpy(buffer, result, count);

        line = (*env)->NewStringUTF(env, buffer);
    }
#else
    struct rl_t *rl = getPointer(env, this);

    rl_readline_name = rl->rl_readline_name;

    char *result = readline(prompt);

    if (result != NULL) {
        line = (*env)->NewStringUTF(env, result);
        free(result);
    }
#endif
    if (prompt != NULL) {
        (*env)->ReleaseStringUTFChars(env, string, prompt);
    }

    return line;
}

JNIEXPORT void JNICALL
Java_iprotium_util_jni_EditLine_add_1history(JNIEnv *env, jobject this,
                                             jstring string) {
    if (string != NULL) {
        const char *line = (*env)->GetStringUTFChars(env, string, 0);
#if USE_EDITLINE
        EditLine *el = getPointer(env, this);

        struct rl_t *clientdata = NULL;

        el_get(el, EL_CLIENTDATA, &clientdata);

        if (clientdata != NULL && clientdata->hist != NULL) {
            HistEvent ev;

            history(clientdata->hist, &ev, H_ENTER, line);
        }
#else
        struct rl_t *rl = getPointer(env, this);

        rl_readline_name = rl->rl_readline_name;
        add_history(strdup(line));
#endif
        (*env)->ReleaseStringUTFChars(env, string, line);
    }
}

JNIEXPORT void JNICALL
Java_iprotium_util_jni_EditLine_reset(JNIEnv *env, jobject this) {
#if USE_EDITLINE
    EditLine *el = getPointer(env, this);

    el_reset(el);
#else
    struct rl_t *rl = getPointer(env, this);

    rl_readline_name = rl->rl_readline_name;
#endif
}

JNIEXPORT jstring JNICALL
Java_iprotium_util_jni_EditLine_gets(JNIEnv *env, jobject this) {
    jstring line = NULL;
#if USE_EDITLINE
    EditLine *el = getPointer(env, this);
    int count = 0;
    const char *result = el_gets(el, &count);

    if (result != NULL) {
        char buffer[count + 1];

        memset(buffer, 0, sizeof buffer);
        memcpy(buffer, result, count);

        line = (*env)->NewStringUTF(env, buffer);
    }
#else
    struct rl_t *rl = getPointer(env, this);

    rl_readline_name = rl->rl_readline_name;

    char *result = readline(NULL);

    if (result != NULL) {
        line = (*env)->NewStringUTF(env, result);
        free(result);
    }
#endif
    return line;
}

JNIEXPORT jint JNICALL
Java_iprotium_util_jni_EditLine_getc(JNIEnv *env, jobject this) {
    char character = -1;
#if USE_EDITLINE
    EditLine *el = getPointer(env, this);
    int count = el_getc(el, &character);

    if (! (count > 0)) {
        character = -1;
    }
#else
    struct rl_t *rl = getPointer(env, this);

    rl_readline_name = rl->rl_readline_name;
    character = rl_read_key();
#endif
    return (jint) character;
}

JNIEXPORT void JNICALL
Java_iprotium_util_jni_EditLine_push(JNIEnv *env,
                                     jobject this, jstring string) {
    const char *str = (*env)->GetStringUTFChars(env, string, 0);
#if USE_EDITLINE
    EditLine *el = getPointer(env, this);

    el_push(el, str);
#else
    struct rl_t *rl = getPointer(env, this);

    rl_readline_name = rl->rl_readline_name;

    size_t i = 0;

    for (i = strlen(str) - 1; i >= 0; i -= 1) {
        rl_stuff_char(str[i]);
    }
#endif
    (*env)->ReleaseStringUTFChars(env, string, str);
}

JNIEXPORT jint JNICALL
Java_iprotium_util_jni_EditLine_parse(JNIEnv *env,
                                      jobject this, jobjectArray array) {
    int result = -1;
#if USE_EDITLINE
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
#endif
    return result;
}

JNIEXPORT jint JNICALL
Java_iprotium_util_jni_EditLine_source(JNIEnv *env,
                                       jobject this, jstring string) {
    int result = -1;
#if USE_EDITLINE
    EditLine *el = getPointer(env, this);
    const char *path = (*env)->GetStringUTFChars(env, string, 0);

    result = el_source(el, path);
    (*env)->ReleaseStringUTFChars(env, string, path);
#endif
    return result;
}

JNIEXPORT void JNICALL
Java_iprotium_util_jni_EditLine_end(JNIEnv *env, jobject this) {
#if USE_EDITLINE
    EditLine *el = getPointer(env, this);

    setPointer(env, this, NULL);

    if (el != NULL) {
        struct rl_t *clientdata = NULL;

        el_get(el, EL_CLIENTDATA, &clientdata);

        if (clientdata != NULL) {
            if (clientdata->hist != NULL) {
                history_end(clientdata->hist);
                clientdata->hist = NULL;
            }

            memset(clientdata, 0, sizeof(*clientdata));
            free(clientdata);
        }

        el_end(el);
    }
#else
    rl_readline_name = NULL;

    struct rl_t *rl = getPointer(env, this);

    setPointer(env, this, NULL);

    if (rl != NULL) {
        if (rl->rl_readline_name != NULL) {
            free(rl->rl_readline_name);
        }

        memset(rl, 0, sizeof(*rl));
        free(rl);
    }
#endif
}
