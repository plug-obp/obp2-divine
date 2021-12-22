//
// Created by Ciprian TEODOROV on 22/12/2021.
//

#include <jni.h>
#include <string.h>
#include <obp2_cesmi.h>


JNIEXPORT jlong JNICALL Java_obp2_cesmi_LibCESMI_createContext (
        JNIEnv *env, jobject self, jstring libPath, jboolean asBuchi) {
    const char *nativeString = (*env)->GetStringUTFChars(env, libPath, NULL);

    printf("Loading CESMI from libPath = %s\n", nativeString);

    void * context = obp2_cesmi_create_context(nativeString, (char)asBuchi);

    (*env)->ReleaseStringUTFChars(env, libPath, nativeString);
    return (jlong)context;
}

JNIEXPORT void JNICALL Java_obp2_cesmi_LibCESMI_freeContext (
        JNIEnv *env, jobject self, jlong context) {
    obp2_cesmi_free_context((obp2_cesmi_context_t*)context);
}

JNIEXPORT jint JNICALL Java_obp2_cesmi_LibCESMI_configurationWidth (
        JNIEnv *env, jobject self, jlong context) {
    return *(int *) ( (obp2_cesmi_context_t*) context )->m_library->m_state_size;
}


JNIEXPORT jint JNICALL Java_obp2_cesmi_LibCESMI_initial (
        JNIEnv *env, jobject self, jlong context, jint inHandle, jbyteArray outTarget) {
    cesmi_node n = {.handle =0, .memory = NULL};
    int hasNext = obp2_cesmi_initial((obp2_cesmi_context_t *)context, inHandle, &n);

    char *target = (*env)->GetPrimitiveArrayCritical(env, outTarget, 0);
    memcpy(target, n.memory, n.handle);
    (*env)->ReleasePrimitiveArrayCritical(env, outTarget, target, 0);

    obp2_free_node(&n);

    return hasNext;
}

JNIEXPORT jint JNICALL Java_obp2_cesmi_LibCESMI_successor (
        JNIEnv *env, jobject self, jlong context, jint inHandle, jbyteArray inSource, jbyteArray outTarget) {
    char *source = (*env)->GetPrimitiveArrayCritical(env, inSource, 0);

    cesmi_node s = {.handle = 0, .memory = source};
    cesmi_node n = {.handle =0, .memory = NULL};
    int hasNext = obp2_cesmi_successor((obp2_cesmi_context_t *)context, inHandle, s, &n);

    (*env)->ReleasePrimitiveArrayCritical(env, inSource, source, 0);

    //write the result
    char *target = (*env)->GetPrimitiveArrayCritical(env, outTarget, 0);
    memcpy(target, n.memory, n.handle);
    (*env)->ReleasePrimitiveArrayCritical(env, outTarget, target, 0);

    obp2_free_node(&n);
    return hasNext;
}

JNIEXPORT jlong JNICALL Java_obp2_cesmi_LibCESMI_flags (
        JNIEnv *env, jobject self, jlong context, jbyteArray node) {

    char *source = (*env)->GetPrimitiveArrayCritical(env, node, 0);

    cesmi_node s = {.handle = 0, .memory = source};
    uint64_t flags = obp2_cesmi_flags((obp2_cesmi_context_t *)context, s);

    (*env)->ReleasePrimitiveArrayCritical(env,  node, source, 0);
    return (jlong) flags;
}