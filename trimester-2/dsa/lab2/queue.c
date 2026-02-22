
#include <stdio.h>
#include <stdlib.h>
#include <string.h> // for strcpy


#define MAX 50


// ----------- BORROW REQUEST QUEUE (Linked List) -----------
struct borrowNode {
    int memberId;                 // Member placing the borrow request
    int bookId;                   // Book requested
    struct borrowNode* next;
};


struct borrowNode *bFront = NULL, *bRear = NULL;


int isBorrowQueueEmpty() {
    return bFront == NULL;
}


void enqueue_borrow(int memberId, int bookId) {
    struct borrowNode* temp = (struct borrowNode*)malloc(sizeof(struct borrowNode));
    temp->memberId = memberId;
    temp->bookId = bookId;
    temp->next = NULL;
    if (bRear == NULL) {
        bFront = bRear = temp;
    } else {
        bRear->next = temp;
        bRear = temp;
    }
    printf("Borrow request added: Member %d -> Book %d.\nTime Complexity: O(1)\nSpace Complexity: O(n)\n", memberId, bookId);
}


void dequeue_borrow() {
    if (isBorrowQueueEmpty()) {
        printf("Queue Underflow — No borrow requests pending.\n");
        return;
    }
    struct borrowNode* temp = bFront;
    printf("Borrow request processed: Member %d issued Book %d.\nTime Complexity: O(1)\nSpace Complexity: O(n)\n", temp->memberId, temp->bookId);
    bFront = bFront->next;
    if (bFront == NULL)
        bRear = NULL;
    free(temp);
}


void display_borrow() {
    if (isBorrowQueueEmpty()) {
        printf("No borrow requests in queue.\n");
        return;
    }
    printf("Borrow Request Queue: ");
    struct borrowNode* temp = bFront;
    while (temp != NULL) {
        printf("[M%d:B%d] -> ", temp->memberId, temp->bookId);
        temp = temp->next;
    }
    printf("NULL\nTime Complexity: O(n)\nSpace Complexity: O(n)\n");
}




// ----------- OVERDUE NOTIFICATION CIRCULAR QUEUE (Linked List) -----------
struct overdueNode {
    int memberId;                 // Member to notify
    int bookId;                   // Overdue book
    struct overdueNode* next;
};


struct overdueNode* oRear = NULL;


int isOverdueQueueEmpty() {
    return oRear == NULL;
}


void enqueue_overdue(int memberId, int bookId) {
    struct overdueNode* temp = (struct overdueNode*)malloc(sizeof(struct overdueNode));
    temp->memberId = memberId;
    temp->bookId = bookId;
    if (oRear == NULL) {
        oRear = temp;
        oRear->next = oRear;
    } else {
        temp->next = oRear->next;
        oRear->next = temp;
        oRear = temp;
    }
    printf("Added to overdue notification queue: Member %d (Book %d).\nTime Complexity: O(1)\nSpace Complexity: O(n)\n", memberId, bookId);
}


void dequeue_overdue() {
    if (isOverdueQueueEmpty()) {
        printf("Circular Queue Underflow — No overdue notifications pending.\n");
        return;
    }
    struct overdueNode* temp = oRear->next;
    printf("Overdue notice sent to Member %d for Book %d.\nTime Complexity: O(1)\nSpace Complexity: O(n)\n", temp->memberId, temp->bookId);
    if (oRear == temp) {
        oRear = NULL;
    } else {
        oRear->next = temp->next;
    }
    free(temp);
}


void display_overdue() {
    if (isOverdueQueueEmpty()) {
        printf("Overdue notification circular queue is empty.\n");
        return;
    }
    struct overdueNode* temp = oRear->next;
    printf("Overdue Notification Circular Queue: ");
    do {
        printf("[M%d:B%d] -> ", temp->memberId, temp->bookId);
        temp = temp->next;
    } while (temp != oRear->next);
    printf("(back to start)\nTime Complexity: O(n)\nSpace Complexity: O(n)\n");
}




// ----------- PRIORITY ACQUISITION/REQUEST QUEUE (Linked List) -----------
struct priorityNode {
    char requester[30];           // Who is requesting (e.g., Faculty/Student name)
    char title[30];               // Book title (short)
    int priority;                 // 1 = highest priority
    struct priorityNode* next;
};


struct priorityNode* pFront = NULL;


int isPriorityEmpty() {
    return pFront == NULL;
}


void enqueue_priority(char requester[], char title[], int priority) {
    struct priorityNode* temp = (struct priorityNode*)malloc(sizeof(struct priorityNode));
    strcpy(temp->requester, requester);
    strcpy(temp->title, title);
    temp->priority = priority;
    temp->next = NULL;


    if (pFront == NULL || priority < pFront->priority) {
        temp->next = pFront;
        pFront = temp;
    } else {
        struct priorityNode* current = pFront;
        while (current->next != NULL && current->next->priority <= priority)
            current = current->next;
        temp->next = current->next;
        current->next = temp;
    }
    printf("Acquisition request added: \"%s\" by %s with priority %d.\nTime Complexity: O(n)\nSpace Complexity: O(n)\n",
           title, requester, priority);
}


void dequeue_priority() {
    if (isPriorityEmpty()) {
        printf("Priority queue empty — no acquisition requests.\n");
        return;
    }
    struct priorityNode* temp = pFront;
    printf("Processed acquisition request: \"%s\" by %s (priority %d).\nTime Complexity: O(1)\nSpace Complexity: O(n)\n",
           temp->title, temp->requester, temp->priority);
    pFront = pFront->next;
    free(temp);
}


void display_priority() {
    if (isPriorityEmpty()) {
        printf("Priority acquisition queue empty.\n");
        return;
    }
    struct priorityNode* temp = pFront;
    printf("Priority Acquisition Queue (High Priority First):\n");
    while (temp != NULL) {
        printf("Title: \"%s\" | Requester: %s | Priority: %d\n", temp->title, temp->requester, temp->priority);
        temp = temp->next;
    }
    printf("Time Complexity: O(n)\nSpace Complexity: O(n)\n");
}




// ----------- MAIN MENU -----------
int main() {
    int ch, ich;
    int memberId, bookId, priority;
    char requester[30], title[30];


    while (1) {
        printf("\n=== Library Management Queue System ===\n");
        printf("1. Borrow Requests (Normal Queue)\n");
        printf("2. Overdue Notifications (Circular Queue)\n");
        printf("3. Acquisition Requests (Priority Queue)\n");
        printf("4. Exit\n");
        printf("Enter choice: ");
        if (scanf("%d", &ch) != 1) return 0;


        if (ch == 1) {
            do {
                printf("\n-- Borrow Request Operations --\n");
                printf("1. Enqueue (New borrow request)\n2. Dequeue (Issue book)\n3. Display\n4. Exit menu\nEnter: ");
                if (scanf("%d", &ich) != 1) return 0;
                if (ich == 1) {
                    printf("Enter Member ID: ");
                    scanf("%d", &memberId);
                    printf("Enter Book ID: ");
                    scanf("%d", &bookId);
                    enqueue_borrow(memberId, bookId);
                } else if (ich == 2) {
                    dequeue_borrow();
                } else if (ich == 3) {
                    display_borrow();
                }
            } while (ich != 4);
        }


        else if (ch == 2) {
            do {
                printf("\n-- Overdue Notification Operations --\n");
                printf("1. Enqueue (Add overdue notice)\n2. Dequeue (Send one notice)\n3. Display\n4. Exit menu\nEnter: ");
                if (scanf("%d", &ich) != 1) return 0;
                if (ich == 1) {
                    printf("Enter Member ID: ");
                    scanf("%d", &memberId);
                    printf("Enter Overdue Book ID: ");
                    scanf("%d", &bookId);
                    enqueue_overdue(memberId, bookId);
                } else if (ich == 2) {
                    dequeue_overdue();
                } else if (ich == 3) {
                    display_overdue();
                }
            } while (ich != 4);
        }


        else if (ch == 3) {
            do {
                printf("\n-- Acquisition Request Operations --\n");
                printf("1. Enqueue (New book acquisition request)\n2. Dequeue (Process highest priority)\n3. Display\n4. Exit menu\nEnter: ");
                if (scanf("%d", &ich) != 1) return 0;
                if (ich == 1) {
                    printf("Enter Requester Name (e.g., Faculty/Student): ");
                    scanf("%s", requester); // note: no spaces; keep as in your original
                    printf("Enter Short Book Title (no spaces): ");
                    scanf("%s", title);     // matches original %s behavior
                    printf("Enter Priority (1 = High, higher number = lower priority): ");
                    scanf("%d", &priority);
                    enqueue_priority(requester, title, priority);
                } else if (ich == 2) {
                    dequeue_priority();
                } else if (ich == 3) {
                    display_priority();
                }
            } while (ich != 4);
        }


        else if (ch == 4) {
            printf("Exiting Library Management Queue System.\n");
            break;
        } else {
            printf("Invalid choice.\n");
        }
    }
    return 0;
}


	
