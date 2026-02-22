
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>


#define MAX_BUFFER 1000   // for initial input


/* --------------------- Utility Functions --------------------- */


// Remove trailing newline from fgets
void trimNewline(char *s) {
   size_t len = strlen(s);
   if (len > 0 && s[len - 1] == '\n') {
       s[len - 1] = '\0';
   }
}


// Check if string is empty or only spaces
int isValidString(const char *s) {
   if (s == NULL || strlen(s) == 0) return 0;
   for (size_t i = 0; i < strlen(s); i++) {
       if (!isspace((unsigned char)s[i])) {
           return 1;
       }
   }
   return 0;
}


/* --------------------- DP Table Allocation --------------------- */


int **allocateDP(int m, int n) {
   int **dp = (int **)malloc((m + 1) * sizeof(int *));
   if (dp == NULL) {
       printf("Memory allocation failed for DP rows.\n");
       exit(1);
   }
   for (int i = 0; i <= m; i++) {
       dp[i] = (int *)malloc((n + 1) * sizeof(int));
       if (dp[i] == NULL) {
           printf("Memory allocation failed for DP columns.\n");
           exit(1);
       }
   }
   return dp;
}


void freeDP(int **dp, int m) {
   for (int i = 0; i <= m; i++) {
       free(dp[i]);
   }
   free(dp);
}


/* --------------------- LCS Core Functions --------------------- */


// Fill DP table for LCS length
void computeLCS(const char *X, const char *Y, int m, int n, int **dp) {
   for (int i = 0; i <= m; i++) {
       for (int j = 0; j <= n; j++) {
           if (i == 0 || j == 0) {
               dp[i][j] = 0;
           } else if (X[i - 1] == Y[j - 1]) {
               dp[i][j] = dp[i - 1][j - 1] + 1;
           } else {
               dp[i][j] = (dp[i - 1][j] > dp[i][j - 1]) ? dp[i - 1][j] : dp[i][j - 1];
           }
       }
   }
}


// Print the DP table (intermediate trace)
void printDPTable(int **dp, int m, int n, const char *X, const char *Y) {
   int i, j;


   printf("\n--- LCS DP Table (rows = X, columns = Y) ---\n\n");


   // Header row (Y characters)
   printf("    ");  // space for row labels
   printf("   ");   // for initial 0
   for (j = 0; j < n; j++) {
       printf("  %c", Y[j]);
   }
   printf("\n");


   // Top row of zeros
   printf("    ");
   for (j = 0; j <= n; j++) {
       printf("%3d", dp[0][j]);
   }
   printf("\n");


   // Remaining rows
   for (i = 1; i <= m; i++) {
       printf("  %c ", X[i - 1]); // row label
       for (j = 0; j <= n; j++) {
           printf("%3d", dp[i][j]);
       }
       printf("\n");
   }
   printf("\n");
}


// Reconstruct one valid LCS string from DP table
char *reconstructLCS(const char *X, const char *Y, int m, int n, int **dp) {
   int index = dp[m][n];                // length of LCS
   char *lcs = (char *)malloc((index + 1) * sizeof(char));
   if (lcs == NULL) {
       printf("Memory allocation failed for LCS string.\n");
       exit(1);
   }
   lcs[index] = '\0';                   // null-terminate


   int i = m, j = n;
   while (i > 0 && j > 0) {
       if (X[i - 1] == Y[j - 1]) {
           lcs[index - 1] = X[i - 1];   // or Y[j - 1]
           i--;
           j--;
           index--;
       } else if (dp[i - 1][j] >= dp[i][j - 1]) {
           i--;
       } else {
           j--;
       }
   }
   return lcs;
}


/* --------------------- Main Program --------------------- */


int main() {
   char bufferX[MAX_BUFFER];
   char bufferY[MAX_BUFFER];
   char *X, *Y;
   int m, n;


   printf("Library Management System - Longest Common Subsequence (LCS)\n");
   printf("Example use: compare two book codes, shelf IDs, or category names.\n\n");


   // Read first string
   printf("Enter first string X (e.g., Book Code / Shelf ID / Section Name):\n");
   if (fgets(bufferX, MAX_BUFFER, stdin) == NULL) {
       printf("Error reading input for X.\n");
       return 1;
   }
   trimNewline(bufferX);


   // Read second string
   printf("Enter second string Y (e.g., Book Code / Shelf ID / Section Name):\n");
   if (fgets(bufferY, MAX_BUFFER, stdin) == NULL) {
       printf("Error reading input for Y.\n");
       return 1;
   }
   trimNewline(bufferY);


   // Validations to reject empty or invalid strings
   if (!isValidString(bufferX) || !isValidString(bufferY)) {
       printf("\nError: Empty or invalid strings are not allowed.\n");
       printf("Please enter non-empty strings containing at least one non-space character.\n");
       return 1;
   }


   // Dynamic memory allocation for strings X and Y
   m = (int)strlen(bufferX);
   n = (int)strlen(bufferY);


   X = (char *)malloc((m + 1) * sizeof(char));
   Y = (char *)malloc((n + 1) * sizeof(char));


   if (X == NULL || Y == NULL) {
       printf("Memory allocation failed for input strings.\n");
       return 1;
   }


   strcpy(X, bufferX);
   strcpy(Y, bufferY);


   // Allocate DP table dynamically
   int **dp = allocateDP(m, n);


   // Compute LCS DP table
   computeLCS(X, Y, m, n, dp);


   // Display DP table (intermediate trace)
   printDPTable(dp, m, n, X, Y);


   // LCS length
   int lcsLength = dp[m][n];
   printf("Length of Longest Common Subsequence (LCS): %d\n", lcsLength);


   // Display any one valid common subsequence
   if (lcsLength > 0) {
       char *lcs = reconstructLCS(X, Y, m, n, dp);
       printf("One valid Longest Common Subsequence: \"%s\"\n", lcs);
       free(lcs);
   } else {
       printf("There is no common subsequence between the given strings.\n");
   }


   // Complexity & Discussion
   printf("\n--- Complexity & Discussion ---\n");
   printf("Let m = length of X and n = length of Y.\n");
   printf("Time Complexity:  O(m * n), because each cell of the DP table is filled once.\n");
   printf("Space Complexity: O(m * n) for storing the DP table.\n");
   printf("In a Library Management System, LCS can be used to:\n");
   printf(" - Match similar book titles or catalogue entries.\n");
   printf(" - Suggest closest matches when a user types a slightly wrong book code.\n");
   printf(" - Compare shelf or section labels to find common patterns.\n");


   // Free dynamically allocated memory
   freeDP(dp, m);
   free(X);
   free(Y);


   return 0;
}





