#include <cesmi_adapter.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

void buffer_print(char *item, int item_width) {

    for (int i = 0; i<item_width; i++) {
        printf("%#04x ", item[i]);
    }
    printf(", size: %d\n", item_width);
}

int main(int argc, char const *argv[])
{
    void *  the_source          = calloc(state_size, sizeof(char));
    int     the_source_width    = state_size;
    char *  the_target          = alloca(state_size * sizeof(char));
    int     the_target_width    = state_size;
    char    the_has_next        = 0;

    gve_cesmi_context *context = create_context(0);


    gve_next_initial(context, &the_target, &the_target_width, &the_has_next);

    gve_is_accepting(context, the_target, the_target_width);

    buffer_print(the_target, the_target_width);
    for (int i = 0; i<10; i++) {
        memcpy(the_source, the_target, the_target_width);
        gve_next_target(context, the_source, the_source_width, &the_target, &the_target_width, &the_has_next);
        buffer_print(the_target, the_target_width);
    }

    free_context(context);

    printf("-----------------\n");
    context = create_context(1);
    the_source = calloc(state_size, sizeof (char ));
    ((char*)the_source)[0] = 1;
    ((char*)the_source)[1] = 1;

    do {
        gve_next_target(context, the_source, the_source_width, &the_target, &the_target_width, &the_has_next);
        buffer_print(the_target, the_target_width);
    } while (the_has_next);


    free_context(context);

    return 0;
}
