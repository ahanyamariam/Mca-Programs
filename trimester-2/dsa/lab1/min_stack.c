
#include <stdio.h>


#define MAX_RECORDS 30


int dailyRecords[MAX_RECORDS], minRecords[MAX_RECORDS];
int recordTop = -1, minTop = -1;


void addRecord(int books) {
    if (recordTop + 1 == MAX_RECORDS) {
        printf("Error: Maximum records reached!\n");
        return;
    }
    recordTop++;
    dailyRecords[recordTop] = books;


    if (minTop == -1 || books <= minRecords[minTop]) {
        minTop++;
        minRecords[minTop] = books;
    }


    printf("Record Added: %d books\n", books);
}


void removeRecord() {
    if (recordTop == -1) {
        printf("No records to remove.\n");
        return;
    }


    int removed = dailyRecords[recordTop];


    if (removed == minRecords[minTop]) {
        minTop--;
    }
    recordTop--;


    printf("Record Removed: %d books\n", removed);
    if (recordTop != -1) {
        printf("Latest Record: %d books\n", dailyRecords[recordTop]);
        printf("Lowest Books Issued: %d\n", minRecords[minTop]);
    } else {
        printf("All records cleared.\n");
    }
}


void viewLatest() {
    if (recordTop == -1) {
        printf("No records available.\n");
        return;
    }
    printf("Most Recent Books Issued: %d\n", dailyRecords[recordTop]);
}


void viewLowest() {
    if (minTop == -1) {
        printf("No records available.\n");
        return;
    }
    printf("Lowest Books Issued in a Day: %d\n", minRecords[minTop]);
}


int main() {
    int choice, books;


    printf("=== Library Books Tracker ===\n");
    printf("1. Add today's issued books\n");
    printf("2. View most recent record\n");
    printf("3. View lowest books issued in a day\n");
    printf("4. Remove last record\n");
    printf("5. Exit\n");


    while (1) {
        printf("\nSelect an option: ");
        scanf("%d", &choice);


        switch (choice) {
            case 1:
                printf("Enter number of books issued: ");
                scanf("%d", &books);
                if (books < 0) {
                    printf("Invalid number! Must be >= 0.\n");
                } else {
                    addRecord(books);
                }
                break;


            case 2:
                viewLatest();
                break;


            case 3:
                viewLowest();
                break;


            case 4:
                removeRecord();
                break;


            case 5:
                printf("Exiting...\n");
                return 0;


            default:
                printf("Invalid option. Try again.\n");
        }
    }
}













