//
// Created by Ciprian TEODOROV on 22/12/2021.
//

#ifndef C_OBP2_CESMI_H
#define C_OBP2_CESMI_H

#include "obp2_cesmi_loader.h"

struct obp2_cesmi_context_s {
    cesmi_library_t* m_library;
    cesmi_setup      m_setup;
};
typedef struct obp2_cesmi_context_s obp2_cesmi_context_t;

obp2_cesmi_context_t* obp2_cesmi_create_context(const char *in_cesmi_path, char has_buchi);
void obp2_cesmi_free_context(obp2_cesmi_context_t *context);
int obp2_cesmi_initial(obp2_cesmi_context_t *context, int in_handle, cesmi_node *out_target);
int obp2_cesmi_successor(obp2_cesmi_context_t *context, int in_handle, cesmi_node in_source, cesmi_node *out_target);
uint64_t obp2_cesmi_flags(obp2_cesmi_context_t *context, cesmi_node in_node);

void obp2_free_node(cesmi_node *node);

#endif //C_OBP2_CESMI_H
