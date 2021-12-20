#include <stdlib.h>
#include "cesmi_adapter.h"
#include <string.h>
#include <assert.h>

cesmi_node make_node(const struct cesmi_setup *setup, int size) {
    cesmi_node n;
    if (((instance_dump_t *) setup->instance)->m_is_dump) {
        n.memory = ((instance_dump_t *) setup->instance)->m_dump;
    } else {
        n.memory = ((instance_dump_t *) setup->instance)->m_target;
    }
    n.handle = (uint64_t) size;
    return n;
}

void free_node(cesmi_node *node) {
}

cesmi_node clone_node(const struct cesmi_setup *setup, cesmi_node orig) {
    cesmi_node n;
    n.memory = (char *) calloc(orig.handle, sizeof(char));
    n.handle = orig.handle;

    memcpy(n.memory, orig.memory, orig.handle);
    return n;
}

int gve_next_initial(void *context, char **out_target, int *io_target_size, char *out_has_next) {
    gve_cesmi_context *ctx = (gve_cesmi_context *) context;
    cesmi_setup setup = ctx->setup;

    cesmi_node current = {.handle = 0, .memory = 0};
    get_initial(&setup, 1, &current);

    if (out_target != NULL) {
        *out_target = current.memory;
    }
    *io_target_size = (int) current.handle;
    *out_has_next = 0;
    return 0;
}

int gve_next_target(void *context, char *in_source, int in_source_size, char **out_target, int *io_target_size,
                    char *out_has_next) {
    gve_cesmi_context *ctx = (gve_cesmi_context *) context;
    cesmi_setup setup = ctx->setup;

    cesmi_node source = {.handle = (uint64_t) in_source_size, .memory = in_source};
    cesmi_node target = {.handle = 0, .memory = 0};
    ctx->handle = get_successor(&setup, ctx->handle, source, &target);
    if (!ctx->handle) {
        assert(target.handle == 0 && target.memory == 0);
        ctx->handle = 1;
        if (out_target != NULL) {
            *out_target = 0;
        }
        *io_target_size = 0;
        *out_has_next = 0;
        return 0;
    }
    if (out_target != NULL) {
        *out_target = target.memory;
    }
    *io_target_size = (int) target.handle;

    cesmi_node dump = {.handle = 0, .memory = 0};
    ((instance_dump_t *) setup.instance)->m_is_dump = 1;
    if (!get_successor(&setup, ctx->handle, source, &dump)) {
        ctx->handle = 1;
        *out_has_next = 0;
        ((instance_dump_t *) setup.instance)->m_is_dump = 0;
        return 0;
    }
    free_node(&dump);
    ((instance_dump_t *) setup.instance)->m_is_dump = 0;
    *out_has_next = 1;
    return 0;
}

uint64_t gve_is_accepting(void *context, char *in_source, int in_source_size) {
    gve_cesmi_context *ctx = (gve_cesmi_context *) context;
    cesmi_setup setup = ctx->setup;
    cesmi_node source = {.handle = (uint64_t) in_source_size, .memory = in_source};

    return get_flags(&setup, source);
}

void *create_context(char has_ltl) {

    instance_dump_t *instance = malloc(sizeof(instance_dump_t));
    *instance = (instance_dump_t) {
            .m_target = malloc(state_size * sizeof(char)),
            .m_dump   = malloc(state_size * sizeof(char)),
            .m_is_dump = 0};

    gve_cesmi_context *context = malloc(sizeof(gve_cesmi_context));
    *context = (gve_cesmi_context) {
            .setup = {
                    .loader = 0,
                    .make_node = &make_node,
                    .clone_node = &clone_node,
                    .add_property = 0,
                    .instance = instance,
                    .property_count = 0,
                    .property = has_ltl == 0 ? 1 : 2,
                    .instance_initialised = 0,
                    .add_flag = 0,
            },
            .handle = 1};

    return context;
}

void free_context(void *context) {
    gve_cesmi_context *ctx = (gve_cesmi_context *) context;
    instance_dump_t *instance = (instance_dump_t *) (ctx->setup.instance);
    free(instance->m_dump);
    free(instance);
    free(ctx);
}

