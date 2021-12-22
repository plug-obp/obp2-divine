//
// Created by Ciprian TEODOROV on 22/12/2021.
//

#ifndef C_OBP2_CESMI_LOADER_H
#define C_OBP2_CESMI_LOADER_H

#include "usr-cesmi.h"

typedef void *      library_handle_t;
typedef void        (*fn_setup_t)           ( cesmi_setup * );
typedef int         (*fn_initial_t)         ( const cesmi_setup *, int, cesmi_node * );
typedef int         (*fn_successor_t)       ( const cesmi_setup *, int, cesmi_node, cesmi_node * );
typedef uint64_t    (*fn_flags_t)       ( const cesmi_setup *, cesmi_node );
typedef char *      (*fn_show_node_t)       ( const cesmi_setup *, cesmi_node );
typedef char *      (*fn_show_transition_t) ( const cesmi_setup *, cesmi_node, int );

struct cesmi_library_s {
    library_handle_t        m_library_handle;
    fn_setup_t              m_setup;
    fn_initial_t            m_initial;
    fn_successor_t          m_successor;
    fn_flags_t              m_flags;
    fn_show_node_t          m_show_node;
    fn_show_transition_t    m_show_transition;

    void*                   m_state_size;
};
typedef struct cesmi_library_s cesmi_library_t;

cesmi_library_t* load_cesmi_library(const char * in_cesmi_path);
void free_cesmi_library(cesmi_library_t *library);

#endif //C_OBP2_CESMI_LOADER_H
