//
// Created by Ciprian TEODOROV on 22/12/2021.
//


#if     defined(POSIX)
#include <dlfcn.h>
#elif   defined(_WIN32)
#include <external/dlfcn-win32/dlfcn.h>
#elif   defined(__APPLE__)
#include <dlfcn.h>
#endif

#ifndef OBP2_CESMI_LOADER_H
#define OBP2_CESMI_LOADER_H

#include <stdlib.h>
#include <stdio.h>
#include <obp2_cesmi_loader.h>


cesmi_library_t* load_cesmi_library(const char * in_cesmi_path) {
    library_handle_t handle = dlopen(in_cesmi_path, RTLD_LAZY);
    if (handle == NULL) {
        fprintf(stderr, "Error loading the cesmi library from %s [%s]\n", in_cesmi_path, dlerror());
        return NULL;
    }

    cesmi_library_t *library    = malloc(sizeof(cesmi_library_t));
    library->m_library_handle   = handle;
    library->m_setup            = dlsym(handle, "setup");
    library->m_initial          = dlsym(handle, "get_initial");
    library->m_successor        = dlsym(handle, "get_successor");
    library->m_flags            = dlsym(handle, "get_flags");
    library->m_show_node        = dlsym(handle, "show_node");
    library->m_show_transition  = dlsym(handle, "show_transition");

    library->m_state_size       = dlsym(handle, "state_size");

    if (library->m_initial == NULL) {
        fprintf(stderr, "Error finding the get_initial function in %s\n", in_cesmi_path);
        free(library);
        return NULL;
    }

    if (library->m_successor == NULL) {
        fprintf(stderr, "Error finding the get_successor function in %s\n", in_cesmi_path);
        free(library);
        return NULL;
    }
    return library;
}

void free_cesmi_library(cesmi_library_t *library) {
    if (library == NULL || library->m_library_handle == NULL) {
        return;
    }
    dlclose(library->m_library_handle);
    library->m_library_handle = NULL;
    free(library);
}

#endif