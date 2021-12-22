//
// Created by Ciprian TEODOROV on 22/12/2021.
//
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <obp2_cesmi.h>

cesmi_node obp2_make_node(const struct cesmi_setup *setup, int size) {
    cesmi_node n = {
            .handle = size,
            .memory = malloc(size)
    };
    return n;
}

void obp2_free_node(cesmi_node *node) {
    if (node == NULL) return;
    if (node->memory == NULL) return;
    free(node->memory);
}

int obp2_add_property( struct cesmi_setup *setup, char *id, char *desc, int type ) {
    return 0;
}

void obp2_add_flag( struct cesmi_setup *setup, char *name, int id, int type ) {
    return;
}

cesmi_node obp2_clone_node(const struct cesmi_setup *setup, cesmi_node orig) {
    cesmi_node n;
    n.memory = (char *) calloc(orig.handle, sizeof(char));
    n.handle = orig.handle;

    memcpy(n.memory, orig.memory, orig.handle);
    return n;
}

obp2_cesmi_context_t* obp2_cesmi_create_context(const char *in_cesmi_path, char as_buchi) {
    obp2_cesmi_context_t *context = malloc(sizeof(obp2_cesmi_context_t));

    *context = (obp2_cesmi_context_t) {
            .m_library = load_cesmi_library(in_cesmi_path),
            .m_setup = {
                    .loader = 0,
                    .make_node = &obp2_make_node,
                    .clone_node = &obp2_clone_node,
                    .add_property = &obp2_add_property,
                    .instance = NULL,
                    .property_count = 0,
                    .property = as_buchi == 0 ? 1 : 2,
                    .instance_initialised = 0,
                    .add_flag = &obp2_add_flag,
            }
    };

    context->m_library->m_setup(&context->m_setup);

    return context;
}

void obp2_cesmi_free_context(obp2_cesmi_context_t *context) {
    if (context == NULL) return;
    if (context->m_library != NULL) {
        free_cesmi_library(context->m_library);
    }
    free(context);
}

/*
 * To obtain the first initial state the system calls the cesmi library initial function with handle=1
 * the initial returns 0 if no more states are available, otherwise it returns a number different from 0
 * */
int obp2_cesmi_initial(obp2_cesmi_context_t *context, int in_handle, cesmi_node *out_target) {
    return context->m_library->m_initial(&context->m_setup, in_handle, out_target);
}

/*
 * To obtain the first successor the system calls the cesmi library initial function with handle=1
 * the initial returns 0 if no more states are available, otherwise it returns a number different from 0
 * */

int obp2_cesmi_successor(obp2_cesmi_context_t *context, int in_handle, cesmi_node in_source, cesmi_node *out_target) {
    return context->m_library->m_successor(&context->m_setup, in_handle, in_source, out_target);
}

uint64_t obp2_cesmi_flags(obp2_cesmi_context_t *context, cesmi_node in_node) {
    return context->m_library->m_flags(&context->m_setup, in_node);
}