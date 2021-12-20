#include <jni.h>
#include <cesmi_adapter.h>
#include <stdlib.h>
#include <string.h>

JNIEXPORT jint JNICALL Java_obp2_divine_CesmiBindingJNI_configurationWidth (
        JNIEnv *env, jobject self) {
    return state_size;
}

JNIEXPORT jlong JNICALL Java_obp2_divine_CesmiBindingJNI_createContext (
        JNIEnv *env, jobject self, jboolean hasLTL) {

    void * context = create_context(hasLTL ? 1 : 0);
    jlong handle = (jlong) context;
    return handle;
}

JNIEXPORT void JNICALL Java_obp2_divine_CesmiBindingJNI_freeContext (
        JNIEnv *env, jobject self, jlong handle) {
    free_context((void *) handle);
}


void buffer_print(char *item, int item_width) {

    for (int i = 0; i<item_width; i++) {
        printf("%#04x ", item[i]);
    }
    printf(", size: %d\n", item_width);
}

JNIEXPORT jboolean JNICALL Java_obp2_divine_CesmiBindingJNI_initial (
        JNIEnv *env, jobject self, jlong handle, jbyteArray target, jint target_width) {
    gve_cesmi_context *context = (void *)handle;

    jbyte *the_target = (*env)->GetPrimitiveArrayCritical(env, target, 0);
    ((instance_dump_t *)context->setup.instance)->m_target = the_target;

    char has_next = 0;
    gve_next_initial(context, NULL, &target_width, &has_next);

    (*env)->ReleasePrimitiveArrayCritical(env, target, the_target, 0);

    return has_next;
}

JNIEXPORT jboolean JNICALL Java_obp2_divine_CesmiBindingJNI_next (
        JNIEnv *env, jobject self, jlong handle, jbyteArray source, jint source_width, jbyteArray target, jint target_width) {
    gve_cesmi_context *context = (void *)handle;

    char *the_source = (*env)->GetPrimitiveArrayCritical(env, source, 0);
    char *the_target = (*env)->GetPrimitiveArrayCritical(env, target, 0);
    ((instance_dump_t *)context->setup.instance)->m_target = the_target;

    char has_next = 0;
    gve_next_target(context, the_source, source_width, NULL, &target_width, &has_next);

    (*env)->ReleasePrimitiveArrayCritical(env, target, the_target, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, source, the_source, 0);

    return has_next;
}

JNIEXPORT jboolean JNICALL Java_obp2_divine_CesmiBindingJNI_isAccepting (
        JNIEnv *env, jobject self, jlong handle, jbyteArray source, jint source_width) {
    gve_cesmi_context *context = (void *) handle;

    char *the_source = (*env)->GetPrimitiveArrayCritical(env, source, 0);

    uint64_t  is_accepting = gve_is_accepting(context, the_source, source_width);

    (*env)->ReleasePrimitiveArrayCritical(env, source, the_source, 0);

    return (jboolean)(is_accepting > 0);
}