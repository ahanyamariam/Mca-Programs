#include <stdio.h>
#include <limits.h>


#define MAXV 20
#define INF 1000000000   // A large value representing "no connection"


// Utility function to print the distance matrix
void printMatrix(int V, int dist[MAXV][MAXV]) {
   int i, j;
   printf("\nCurrent Distance Matrix (INF = no direct path):\n");
   for (i = 0; i < V; i++) {
       for (j = 0; j < V; j++) {
           if (dist[i][j] >= INF)
               printf(" INF ");
           else
               printf("%4d ", dist[i][j]);
       }
       printf("\n");
   }
}


// Utility function to print a reconstructed path using "next" matrix
void printPath(int u, int v, int next[MAXV][MAXV]) {
   if (next[u][v] == -1) {
       printf("No path exists between these sections.\n");
       return;
   }


   printf("Shortest path: Section %d", u + 1);
   while (u != v) {
       u = next[u][v];
       printf(" -> Section %d", u + 1);
   }
   printf("\n");
}


int main() {
   int V, E;
   int directed;
   int dist[MAXV][MAXV];
   int next[MAXV][MAXV];
   int i, j, k;


   printf("=== Library Management System - Floyd Warshall (All-Pairs Shortest Path) ===\n");


   // -------------------- Read number of vertices --------------------
   printf("Enter number of library sections/branches (vertices, max %d): ", MAXV);
   scanf("%d", &V);


   if (V <= 0 || V > MAXV) {
       printf("Error: Number of vertices must be between 1 and %d.\n", MAXV);
       return 0;
   }


   // -------------------- Directed or undirected --------------------
   printf("Is the network directed? (1 = directed, 0 = undirected): ");
   scanf("%d", &directed);
   if (directed != 0 && directed != 1) {
       printf("Error: Invalid choice. Please enter 1 (directed) or 0 (undirected).\n");
       return 0;
   }


   // -------------------- Initialize distance and next matrices --------------------
   for (i = 0; i < V; i++) {
       for (j = 0; j < V; j++) {
           if (i == j)
               dist[i][j] = 0;
           else
               dist[i][j] = INF;
           next[i][j] = -1;  // -1 means no path yet
       }
   }


   // -------------------- Read edges --------------------
   printf("Enter number of edges (connections between sections): ");
   scanf("%d", &E);


   if (E < 0) {
       printf("Error: Number of edges cannot be negative.\n");
       return 0;
   }


   printf("\nEnter each edge as: <source> <destination> <cost>\n");
   printf("Vertices are numbered from 1 to %d.\n", V);
   printf("Example: 1 3 5  means an edge from Section 1 to Section 3 with cost 5.\n\n");


   int count = 0;
   while (count < E) {
       int u, v, w;
       printf("Edge %d: ", count + 1);
       if (scanf("%d %d %d", &u, &v, &w) != 3) {
           printf("Invalid input format. Please enter three integers.\n");
           // Clear input buffer (simple approach)
           while (getchar() != '\n');
           continue;
       }


       // Validate vertices
       if (u < 1 || u > V || v < 1 || v > V) {
           printf("Error: Vertex indices must be between 1 and %d.\n", V);
           continue; // ask for this edge again
       }


       // Validate cost
       if (w < 0) {
           printf("Error: Negative edge weights are not allowed in this library system.\n");
           continue;
       }


       // Convert to 0-based index
       u--;
       v--;


       // If multiple edges are given between same pair, keep the smallest cost
       if (w < dist[u][v]) {
           dist[u][v] = w;
           next[u][v] = v;
           if (!directed) {
               dist[v][u] = w;
               next[v][u] = u;
           }
       }


       count++;
   }


   printf("\nInitial distance matrix (before Floyd-Warshall):\n");
   printMatrix(V, dist);


   // -------------------- Floyd-Warshall Algorithm with simple optimizations --------------------
   /*
       Dynamic Programming Idea:
       dist[i][j] = minimum cost from i to j using only intermediate vertices {1..k}.


       Optimization methods used:
       1. Skip update if dist[i][k] or dist[k][j] is INF (no need to add).
       2. Only update if newDistance < current dist[i][j].
       3. If in a full iteration of k no updates are made, break early (no further improvement).
   */


   for (k = 0; k < V; k++) {
       int updated = 0; // To check if this iteration made any changes


       printf("\n--- Considering Section %d as an intermediate node (k = %d) ---\n",
              k + 1, k + 1);


       for (i = 0; i < V; i++) {
           for (j = 0; j < V; j++) {
               // Optimization 1: skip if either side is INF
               if (dist[i][k] >= INF || dist[k][j] >= INF)
                   continue;


               int newDist = dist[i][k] + dist[k][j];


               // Optimization 2: only update if strictly better
               if (newDist < dist[i][j]) {
                   dist[i][j] = newDist;
                   next[i][j] = next[i][k]; // path reconstruction
                   updated = 1;
               }
           }
       }


       // Print intermediate trace after this k
       printMatrix(V, dist);


       // Optimization 3: early stopping if no change
       if (!updated) {
           printf("\nNo updates in this iteration. Algorithm can stop early.\n");
           break;
       }
   }


   // -------------------- Check for negative cycles (theoretically) --------------------
   // (We disallow negative edges, so this should not occur, but this is a safety check.)
   for (i = 0; i < V; i++) {
       if (dist[i][i] < 0) {
           printf("\nWarning: Negative cycle detected. Shortest paths not well-defined.\n");
           break;
       }
   }


   // -------------------- Final all-pairs shortest path distances --------------------
   printf("\n=== Final All-Pairs Shortest Path Distance Matrix ===\n");
   printMatrix(V, dist);


   // -------------------- Allow user to query paths --------------------
   while (1) {
       int u, v, choice;
       printf("\nDo you want to query the shortest path between two sections? (1 = Yes, 0 = No): ");
       scanf("%d", &choice);
       if (choice == 0) break;


       printf("Enter source section (1..%d): ", V);
       scanf("%d", &u);
       printf("Enter destination section (1..%d): ", V);
       scanf("%d", &v);


       if (u < 1 || u > V || v < 1 || v > V) {
           printf("Invalid section numbers.\n");
           continue;
       }


       u--;
       v--;


       if (dist[u][v] >= INF) {
           printf("No path exists from Section %d to Section %d.\n", u + 1, v + 1);
       } else {
           printf("Shortest distance from Section %d to Section %d = %d\n",
                  u + 1, v + 1, dist[u][v]);
           printPath(u, v, next);
       }
   }


   printf("\nProgram finished. Use the matrices and logs as screenshots for your lab report.\n");


   return 0;
}




