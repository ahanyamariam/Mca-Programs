
#include <stdio.h>
#include <stdlib.h>
#include <string.h>


#define TITLE_LEN 64


typedef struct {
    int isbn;
    char title[TITLE_LEN];
} Book;


/* ---- tiny input helpers ---- */
static void read_line(char *buf, size_t n) {
    if (!fgets(buf, (int)n, stdin)) { buf[0] = 0; return; }
    size_t m = strlen(buf);
    if (m && buf[m-1] == '\n') buf[m-1] = 0;
}


static int read_int(const char *prompt) {
    char buf[64];
    int x;
    for (;;) {
        printf("%s", prompt);
        read_line(buf, sizeof(buf));
        if (sscanf(buf, "%d", &x) == 1) return x;
        puts("Enter a valid integer.");
    }
}


static void read_title(char *out, size_t n) {
    printf("Enter Title: ");
    read_line(out, n);
    if (out[0] == 0) strncpy(out, "(untitled)", n);
}


static void print_book(const Book *b) {
    printf("[ISBN:%d] \"%s\"\n", b->isbn, b->title);
}


/* ================== BST ================== */
typedef struct BSTNode {
    int key;
    Book val;
    struct BSTNode *l, *r;
} BSTNode;


static BSTNode *bst_new(int k, Book v){ BSTNode *n=malloc(sizeof(*n)); n->key=k; n->val=v; n->l=n->r=NULL; return n; }


static BSTNode *bst_insert(BSTNode *t, int k, Book v){
    if(!t) return bst_new(k,v);
    if(k < t->key) t->l = bst_insert(t->l,k,v);
    else if(k > t->key) t->r = bst_insert(t->r,k,v);
    else t->val = v; // update
    return t;
}
static BSTNode *bst_min(BSTNode *t){ while(t && t->l) t=t->l; return t; }
static BSTNode *bst_delete(BSTNode *t, int k){
    if(!t) return NULL;
    if(k < t->key) t->l = bst_delete(t->l,k);
    else if(k > t->key) t->r = bst_delete(t->r,k);
    else {
        if(!t->l){ BSTNode *r=t->r; free(t); return r; }
        if(!t->r){ BSTNode *l=t->l; free(t); return l; }
        BSTNode *s = bst_min(t->r);
        t->key = s->key; t->val = s->val;
        t->r = bst_delete(t->r, s->key);
    }
    return t;
}
static BSTNode *bst_search(BSTNode *t, int k){
    while(t){ if(k<t->key) t=t->l; else if(k>t->key) t=t->r; else return t; }
    return NULL;
}
static void bst_in(BSTNode *t){ if(!t) return; bst_in(t->l); print_book(&t->val); bst_in(t->r); }
static void bst_pre(BSTNode *t){ if(!t) return; print_book(&t->val); bst_pre(t->l); bst_pre(t->r); }
static void bst_post(BSTNode *t){ if(!t) return; bst_post(t->l); bst_post(t->r); print_book(&t->val); }
static void bst_free(BSTNode *t){ if(!t) return; bst_free(t->l); bst_free(t->r); free(t); }


/* ================== AVL ================== */
typedef struct AVLNode {
    int key, h;
    Book val;
    struct AVLNode *l, *r;
} AVLNode;


static int H(AVLNode *n){ return n ? n->h : 0; }
static int B(AVLNode *n){ return n ? H(n->l) - H(n->r) : 0; }
static void upd(AVLNode *n){ if(n){ int hl=H(n->l), hr=H(n->r); n->h = (hl>hr?hl:hr)+1; } }
static AVLNode *avl_new(int k, Book v){ AVLNode *n=malloc(sizeof(*n)); n->key=k; n->val=v; n->l=n->r=NULL; n->h=1; return n; }
static AVLNode *rotR(AVLNode *y){ AVLNode *x=y->l, *t=x->r; x->r=y; y->l=t; upd(y); upd(x); return x; }
static AVLNode *rotL(AVLNode *x){ AVLNode *y=x->r, *t=y->l; y->l=x; x->r=t; upd(x); upd(y); return y; }


static AVLNode *avl_insert(AVLNode *t, int k, Book v){
    if(!t) return avl_new(k,v);
    if(k < t->key) t->l = avl_insert(t->l,k,v);
    else if(k > t->key) t->r = avl_insert(t->r,k,v);
    else { t->val = v; return t; }
    upd(t);
    int b=B(t);
    if(b>1 && k<t->l->key) return rotR(t);                 // LL
    if(b>1 && k>t->l->key){ t->l=rotL(t->l); return rotR(t);} // LR
    if(b<-1 && k>t->r->key) return rotL(t);                // RR
    if(b<-1 && k<t->r->key){ t->r=rotR(t->r); return rotL(t);} // RL
    return t;
}


static AVLNode *avl_min(AVLNode *t){ while(t && t->l) t=t->l; return t; }


static AVLNode *avl_delete(AVLNode *t, int k){
    if(!t) return NULL;
    if(k < t->key) t->l = avl_delete(t->l,k);
    else if(k > t->key) t->r = avl_delete(t->r,k);
    else {
        if(!t->l || !t->r){
            AVLNode *tmp = t->l ? t->l : t->r;
            if(!tmp){ free(t); return NULL; }
            *t = *tmp; free(tmp);
        } else {
            AVLNode *s = avl_min(t->r);
            t->key = s->key; t->val = s->val;
            t->r = avl_delete(t->r, s->key);
        }
    }
    upd(t);
    int b=B(t);
    if(b>1 && B(t->l)>=0) return rotR(t);
    if(b>1 && B(t->l)<0){ t->l=rotL(t->l); return rotR(t);}
    if(b<-1 && B(t->r)<=0) return rotL(t);
    if(b<-1 && B(t->r)>0){ t->r=rotR(t->r); return rotL(t);}
    return t;
}


static AVLNode *avl_search(AVLNode *t, int k){
    while(t){ if(k<t->key) t=t->l; else if(k>t->key) t=t->r; else return t; }
    return NULL;
}
static void avl_in(AVLNode *t){ if(!t) return; avl_in(t->l); print_book(&t->val); avl_in(t->r); }
static void avl_pre(AVLNode *t){ if(!t) return; print_book(&t->val); avl_pre(t->l); avl_pre(t->r); }
static void avl_post(AVLNode *t){ if(!t) return; avl_post(t->l); avl_post(t->r); print_book(&t->val); }
static void avl_free(AVLNode *t){ if(!t) return; avl_free(t->l); avl_free(t->r); free(t); }


/* ================== Menus ================== */
static void bst_menu(BSTNode **root){
    for(;;){
        printf("\n--- BST ---\n"
               "1. Insert\n2. Delete\n3. Search\n4. Display\n5. Back\nChoose: ");
        char ch[8]; read_line(ch, sizeof(ch));
        if(!strcmp(ch,"1")){
            Book b; b.isbn = read_int("Enter ISBN: "); read_title(b.title, TITLE_LEN);
            *root = bst_insert(*root, b.isbn, b);
            puts("Inserted.");
        } else if(!strcmp(ch,"2")){
            int k = read_int("Enter ISBN to delete: ");
            *root = bst_delete(*root, k);
            puts("Done.");
        } else if(!strcmp(ch,"3")){
            int k = read_int("Enter ISBN to search: ");
            BSTNode *n = bst_search(*root, k);
            if(n){ printf("Found: "); print_book(&n->val); } else puts("Not found.");
        } else if(!strcmp(ch,"4")){
            puts("Inorder:");  bst_in(*root);
            puts("Preorder:"); bst_pre(*root);
            puts("Postorder:");bst_post(*root);
        } else if(!strcmp(ch,"5")){
            break;
        } else puts("Invalid.");
    }
}


static void avl_menu(AVLNode **root){
    for(;;){
        printf("\n--- AVL ---\n"
               "1. Insert\n2. Delete\n3. Search\n4. Display\n5. Back\nChoose: ");
        char ch[8]; read_line(ch, sizeof(ch));
        if(!strcmp(ch,"1")){
            Book b; b.isbn = read_int("Enter ISBN: "); read_title(b.title, TITLE_LEN);
            *root = avl_insert(*root, b.isbn, b);
            puts("Inserted.");
        } else if(!strcmp(ch,"2")){
            int k = read_int("Enter ISBN to delete: ");
            *root = avl_delete(*root, k);
            puts("Done.");
        } else if(!strcmp(ch,"3")){
            int k = read_int("Enter ISBN to search: ");
            AVLNode *n = avl_search(*root, k);
            if(n){ printf("Found: "); print_book(&n->val); } else puts("Not found.");
        } else if(!strcmp(ch,"4")){
            puts("Inorder:");  avl_in(*root);
            puts("Preorder:"); avl_pre(*root);
            puts("Postorder:");avl_post(*root);
        } else if(!strcmp(ch,"5")){
            break;
        } else puts("Invalid.");
    }
}


/* ================== Main ================== */
int main(void){
    BSTNode *bst = NULL;
    AVLNode *avl = NULL;


    for(;;){
        printf("\n=== Library (BST & AVL) ===\n"
               "1. Work with BST\n"
               "2. Work with AVL\n"
               "3. Exit\n"
               "Choose: ");
        char ch[8]; read_line(ch, sizeof(ch));
        if(!strcmp(ch,"1")) bst_menu(&bst);
        else if(!strcmp(ch,"2")) avl_menu(&avl);
        else if(!strcmp(ch,"3")) break;
        else puts("Invalid.");
    }


    bst_free(bst);
    avl_free(avl);
    puts("Goodbye!");
    return 0;
}





