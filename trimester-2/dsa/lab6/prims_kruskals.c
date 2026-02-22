#include <stdio.h>
#include <stdlib.h>
#include <string.h>


#define MAXV 20
#define INF  999999


/* -------------------- Graph: Adjacency Matrix -------------------- */


int graph[MAXV][MAXV];
char branchName[MAXV][50];    // Library branch names


/* -------------------- Kruskal Structures (Edge List + DSU) ------- */


typedef struct {
   int src, dest, weight;
} Edge;


Edge edges[MAXV * MAXV];
Edge mstEdgesKruskal[MAXV];   // store MST edges from Kruskal


int parentDSU[MAXV];
int rankDSU[MAXV];


void makeSet(int n) {
   for (int i = 0; i < n; i++) {
       parentDSU[i] = i;
       rankDSU[i] = 0;
   }
}


int findSet(int v) {
   if (v == parentDSU[v]) return v;
   parentDSU[v] = findSet(parentDSU[v]); // path compression
   return parentDSU[v];
}


void unionSet(int a, int b) {
   a = findSet(a);
   b = findSet(b);
   if (a != b) {
       if (rankDSU[a] < rankDSU[b]) {
           int t = a; a = b; b = t;
       }
       parentDSU[b] = a;
       if (rankDSU[a] == rankDSU[b]) rankDSU[a]++;
   }
}


/* Sort edges for Kruskal (simple bubble sort for clarity) */
void sortEdges(Edge edges[], int m) {
   for (int i = 0; i < m - 1; i++) {
       for (int j = 0; j < m - i - 1; j++) {
           if (edges[j].weight > edges[j + 1].weight) {
               Edge temp = edges[j];
               edges[j] = edges[j + 1];
               edges[j + 1] = temp;
           }
       }
   }
}


/* -------------------- Prim's Algorithm ---------------------------- */


void primMST(int n, int start) {
   int key[MAXV];        // minimum weight edge to a vertex
   int parent[MAXV];     // MST parent of each vertex
   int inMST[MAXV];      // 1 if vertex included in MST


   for (int i = 0; i < n; i++) {
       key[i] = INF;
       parent[i] = -1;
       inMST[i] = 0;
   }


   key[start] = 0;


   printf("\n--- Prim's Algorithm (Library Network MST) ---\n");
   for (int count = 0; count < n - 1; count++) {
       // pick minimum key vertex not yet included in MST
       int u = -1;
       int minKey = INF;
       for (int v = 0; v < n; v++) {
           if (!inMST[v] && key[v] < minKey) {
               minKey = key[v];
               u = v;
           }
       }


       if (u == -1) {
           printf("Graph is not connected. MST not possible.\n");
           return;
       }


       inMST[u] = 1;


       printf("Step %d: Picked branch '%s' (vertex %d) with key = %d\n",
              count + 1, branchName[u], u, key[u]);


       // update key values of adjacent vertices
       for (int v = 0; v < n; v++) {
           int w = graph[u][v];
           if (w != 0 && w != INF && !inMST[v] && w < key[v]) {
               key[v] = w;
               parent[v] = u;
               printf("   Updated: parent[%s] = %s, key = %d\n",
                      branchName[v], branchName[u], w);
           }
       }
   }


   // print MST
   int totalWeight = 0;
   printf("\nPrim's MST Edges (Library Connections):\n");
   printf("Edge\t\tWeight\n");
   for (int v = 0; v < n; v++) {
       if (parent[v] != -1) {
           printf("%s -- %s\t%d\n",
                  branchName[parent[v]],
                  branchName[v],
                  graph[parent[v]][v]);
           totalWeight += graph[parent[v]][v];
       }
   }
   printf("Total Cost of Library Network (Prim): %d\n", totalWeight);
}


/* -------------------- Kruskal's Algorithm ------------------------- */


void kruskalMST(int n) {
   int m = 0;


   // build edge list from adjacency matrix
   for (int i = 0; i < n; i++) {
       for (int j = i + 1; j < n; j++) {
           if (graph[i][j] != 0 && graph[i][j] != INF) {
               edges[m].src = i;
               edges[m].dest = j;
               edges[m].weight = graph[i][j];
               m++;
           }
       }
   }


   printf("\n--- Kruskal's Algorithm (Library Network MST) ---\n");
   printf("Initial Edge List (u, v, w):\n");
   for (int i = 0; i < m; i++) {
       printf("(%s, %s, %d)\n",
              branchName[edges[i].src],
              branchName[edges[i].dest],
              edges[i].weight);
   }


   sortEdges(edges, m);


   printf("\nSorted Edge List by Weight:\n");
   for (int i = 0; i < m; i++) {
       printf("(%s, %s, %d)\n",
              branchName[edges[i].src],
              branchName[edges[i].dest],
              edges[i].weight);
   }


   makeSet(n);


   int mstWeight = 0;
   int eCount = 0;


   printf("\nSelecting edges for MST (Kruskal):\n");
   for (int i = 0; i < m; i++) {
       int u = edges[i].src;
       int v = edges[i].dest;
       int w = edges[i].weight;


       int setU = findSet(u);
       int setV = findSet(v);


       printf("Considering edge (%s, %s, %d): ",
              branchName[u], branchName[v], w);


       if (setU != setV) {
           printf("ACCEPTED\n");
           mstEdgesKruskal[eCount++] = edges[i];
           mstWeight += w;
           unionSet(setU, setV);
       } else {
           printf("REJECTED (cycle)\n");
       }


       if (eCount == n - 1) break;
   }


   if (eCount != n - 1) {
       printf("Graph is not connected. MST not possible.\n");
       return;
   }


   printf("\nKruskal's MST Edges (Library Connections):\n");
   printf("Edge\t\tWeight\n");
   for (int i = 0; i < eCount; i++) {
       printf("%s -- %s\t%d\n",
              branchName[mstEdgesKruskal[i].src],
              branchName[mstEdgesKruskal[i].dest],
              mstEdgesKruskal[i].weight);
   }
   printf("Total Cost of Library Network (Kruskal): %d\n", mstWeight);
}


/* -------------------- Dijkstra's Algorithm ------------------------ */


void dijkstra(int n, int src) {
   int dist[MAXV];
   int visited[MAXV];


   for (int i = 0; i < n; i++) {
       dist[i] = INF;
       visited[i] = 0;
   }
   dist[src] = 0;


   printf("\n--- Dijkstra's Algorithm (Shortest Paths from %s) ---\n",
          branchName[src]);


   for (int count = 0; count < n - 1; count++) {
       int u = -1;
       int minDist = INF;
       for (int v = 0; v < n; v++) {
           if (!visited[v] && dist[v] < minDist) {
               minDist = dist[v];
               u = v;
           }
       }


       if (u == -1) break; // remaining vertices unreachable
       visited[u] = 1;


       printf("Step %d: Visiting '%s' (vertex %d), dist = %d\n",
              count + 1, branchName[u], u, dist[u]);


       for (int v = 0; v < n; v++) {
           int w = graph[u][v];
           if (w != 0 && w != INF && !visited[v]) {
               if (dist[u] + w < dist[v]) {
                   printf("   Relax edge %s -> %s: old dist = %d, new dist = %d\n",
                          branchName[u], branchName[v],
                          dist[v], dist[u] + w);
                   dist[v] = dist[u] + w;
               }
           }
       }
   }


   printf("\nFinal Shortest Distances from '%s':\n", branchName[src]);
   for (int i = 0; i < n; i++) {
       if (dist[i] == INF)
           printf("To %s: INF (unreachable)\n", branchName[i]);
       else
           printf("To %s: %d\n", branchName[i], dist[i]);
   }
}


/* -------------------- Input & Main ------------------------ */


int main() {
   int n;


   printf("Enter number of library branches (vertices, max %d): ", MAXV);
   scanf("%d", &n);


   if (n <= 0 || n > MAXV) {
       printf("Invalid number of vertices.\n");
       return 0;
   }


   printf("Enter name of each library branch:\n");
   for (int i = 0; i < n; i++) {
       printf("Branch %d name: ", i);
       scanf("%s", branchName[i]);
   }


   printf("\nEnter adjacency matrix of edge weights.\n");
   printf("Weight = cost/time between branches.\n");
   printf("Enter 0 if there is NO direct connection between branches.\n\n");


   for (int i = 0; i < n; i++) {
       for (int j = 0; j < n; j++) {
           printf("Weight between %s and %s: ",
                  branchName[i], branchName[j]);
           scanf("%d", &graph[i][j]);
           if (i == j) {
               graph[i][j] = 0; // no self-loop
           } else if (graph[i][j] == 0) {
               graph[i][j] = INF; // treat 0 as no edge
           }
       }
   }


   printf("\nGraph successfully created for Library Network.\n");


   int primStart;
   printf("\nEnter start vertex index for Prim's MST (0 to %d): ", n - 1);
   scanf("%d", &primStart);
   if (primStart < 0 || primStart >= n) {
       printf("Invalid start vertex. Defaulting to 0.\n");
       primStart = 0;
   }
   primMST(n, primStart);


   kruskalMST(n);


   int src;
   printf("\nEnter source vertex index for Dijkstra (0 to %d): ", n - 1);
   scanf("%d", &src);
   if (src < 0 || src >= n) {
       printf("Invalid source vertex. Defaulting to 0.\n");
       src = 0;
   }
   dijkstra(n, src);


   return 0;
}





