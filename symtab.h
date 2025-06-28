#ifndef SYMTAB_H
#define SYMTAB_H

#define MAX_SYMBOLS 100
#define TYPE_INT 1
#define TYPE_BOOL 2
#define TYPE_CHAR 3
#define TYPE_STRING 4
#define TYPE_VOID 5

typedef struct {
    char* name;
    int type;
    int scope;
} Symbol;

void init_symbol_table();
int add_symbol(const char* name, int type, int scope);
int lookup_symbol(const char* name);

#endif
