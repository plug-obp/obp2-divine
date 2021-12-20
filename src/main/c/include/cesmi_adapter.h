#ifndef CESMI_ADAPTER_H
#define CESMI_ADAPTER_H
#include "usr-cesmi.h"

struct instance_dump_s {
    void *m_target;
    void *m_dump;
    char m_is_dump;
};
typedef struct instance_dump_s  instance_dump_t;


cesmi_node make_node(const struct cesmi_setup *setup, int size ) ; 

cesmi_node clone_node( const struct cesmi_setup *setup, cesmi_node orig ); 

typedef struct  {
    cesmi_setup setup;
    int handle;
} gve_cesmi_context;

void printNode(void* node);

int gve_next_initial(void *context, char **out_target, int *io_target_size, char *out_has_next);
int gve_next_target(void *context, char *in_source, int in_source_size, char **out_target, int *io_target_size, char *out_has_next);
uint64_t gve_is_accepting(void *context, char *in_source, int in_source_size);
extern int state_size;

void *create_context(char has_ltl);
void free_context(void *context);

#endif