#include <stdio.h>
#include <stdlib.h>
#include <limits.h>


#define MAXV 10      // maximum number of vertices
#define MAXE 100     // maximum number of edges (for priority queue capacity)
#define INF  INT_MAX


/* -------------------- Graph Structures (Adjacency List) -------------------- */


typedef struct Edge {
   int to;
   int weight;
   struct Edge *next;
} Edge;


Edge *adj[MAXV];   // adjacency list
int V;             // number of vertices


/* -------------------- Priority Queue: Min-Heap for (vertex, dist) --------- */


typedef struct {
   int vertex;
   int dist;
} PQNode;


typedef struct {
   PQNode heap[MAXE];
   int size;
} MinHeap;


void initHeap(MinHeap *h) {
   h->size = 0;
}


void swap(PQNode *a, PQNode *b) {
   PQNode temp = *a;
   *a = *b;
   *b = temp;
}


void heapifyUp(MinHeap *h, int idx) {
   while (idx > 0) {
       int parent = (idx - 1) / 2;
       if (h->heap[parent].dist <= h->heap[idx].dist)
           break;
       swap(&h->heap[parent], &h->heap[idx]);
       idx = parent;
   }
}


void heapifyDown(MinHeap *h, int idx) {
   while (1) {
       int left = 2 * idx + 1;
       int right = 2 * idx + 2;
       int smallest = idx;


       if (left < h->size && h->heap[left].dist < h->heap[smallest].dist)
           smallest = left;
       if (right < h->size && h->heap[right].dist < h->heap[smallest].dist)
           smallest = right;
       if (smallest == idx) break;
       swap(&h->heap[smallest], &h->heap[idx]);
       idx = smallest;
   }
}


int isEmpty(MinHeap *h) {
   return h->size == 0;
}


void push(MinHeap *h, int vertex, int dist) {
   if (h->size >= MAXE) {
       printf("Priority queue overflow!\n");
       return;
   }
   PQNode node;
   node.vertex = vertex;
   node.dist = dist;
   h->heap[h->size] = node;
   heapifyUp(h, h->size);
   h->size++;
}


PQNode pop(MinHeap *h) {
   PQNode top = h->heap[0];
   h->size--;
   h->heap[0] = h->heap[h->size];
   heapifyDown(h, 0);
   return top;
}


/* -------------------- Graph Helper Functions ------------------------------- */


// Add edge u -> v with given weight (validates indices and weight)
void addEdgeDirected(int u, int v, int weight) {
   if (u < 0 || u >= V || v < 0 || v >= V) {
       printf("Invalid edge (%d -> %d). Skipping.\n", u, v);
       return;
   }
   if (weight < 0) {
       printf("Negative weight not allowed in Dijkstra (%d -> %d, w=%d). Skipping.\n", u, v, weight);
       return;
   }


   Edge *e = (Edge *)malloc(sizeof(Edge));
   if (!e) {
       printf("Memory allocation failed for edge (%d -> %d).\n", u, v);
       return;
   }
   e->to = v;
   e->weight = weight;
   e->next = adj[u];
   adj[u] = e;
}


// For undirected graph: add u->v and v->u
void addEdgeUndirected(int u, int v, int weight) {
   addEdgeDirected(u, v, weight);
   addEdgeDirected(v, u, weight);
}


void printGraph(char branchNames[][30]) {
   printf("\nAdjacency List of Library Branch Graph:\n");
   for (int i = 0; i < V; i++) {
       printf("  %d (%s):", i, branchNames[i]);
       Edge *cur = adj[i];
       while (cur != NULL) {
           printf(" -> %d(%s, w=%d)", cur->to, branchNames[cur->to], cur->weight);
           cur = cur->next;
       }
       printf("\n");
   }
}


/* -------------------- Dijkstra's Algorithm with Traces -------------------- */


void dijkstra(int src, int dist[], int parent[], char branchNames[][30]) {
   int visited[MAXV] = {0};
   MinHeap pq;
   initHeap(&pq);


   // Initialize distances
   for (int i = 0; i < V; i++) {
       dist[i] = INF;
       parent[i] = -1;
   }
   dist[src] = 0;
   push(&pq, src, 0);


   printf("\n--- Dijkstra's Algorithm Trace (Source: %d - %s) ---\n", src, branchNames[src]);


   while (!isEmpty(&pq)) {
       PQNode node = pop(&pq);
       int u = node.vertex;
       int d = node.dist;


       if (visited[u]) {
           // This entry is outdated; skip
           continue;
       }


       visited[u] = 1;
       printf("Extract vertex %d (%s) with current shortest distance = %d\n", u, branchNames[u], d);


       Edge *cur = adj[u];
       while (cur != NULL) {
           int v = cur->to;
           int w = cur->weight;


           if (!visited[v] && dist[u] != INF && dist[u] + w < dist[v]) {
               int oldDist = dist[v];
               dist[v] = dist[u] + w;
               parent[v] = u;
               push(&pq, v, dist[v]);


               printf("  Relax edge (%d-%s -> %d-%s), weight=%d: dist[%d] updated from ",
                      u, branchNames[u], v, branchNames[v], w, v);
               if (oldDist == INF) printf("INF");
               else printf("%d", oldDist);
               printf(" to %d\n", dist[v]);
           }
           cur = cur->next;
       }
   }
}


/* -------------------- Utility: Print Shortest Paths ------------------------ */


void printPath(int v, int parent[], char branchNames[][30]) {
   if (v == -1) return;
   if (parent[v] != -1) {
       printPath(parent[v], parent, branchNames);
       printf(" -> ");
   }
   printf("%s", branchNames[v]);
}


/* -------------------- main: User-Defined Graph ---------------------------- */


int main() {
   int E;  // number of edges (roads)
   char branchNames[MAXV][30];


   // Initialize adjacency list
   for (int i = 0; i < MAXV; i++) {
       adj[i] = NULL;
   }


   printf("=== Library Management System - Shortest Path Using Dijkstra ===\n");


   // 1. Input number of library branches
   printf("Enter number of library branches (vertices) [1-%d]: ", MAXV);
   scanf("%d", &V);


   if (V <= 0 || V > MAXV) {
       printf("Invalid number of vertices. Exiting.\n");
       return 0;
   }


   // 2. Input branch names
   printf("\nEnter name for each branch (single word, no spaces):\n");
   for (int i = 0; i < V; i++) {
       printf("  Branch %d name: ", i);
       scanf("%29s", branchNames[i]);  // read as a single word
   }


   // 3. Input number of roads (edges)
   printf("\nEnter number of roads (edges) between branches (max %d): ", MAXE);
   scanf("%d", &E);


   if (E < 0 || E > MAXE) {
       printf("Invalid number of edges. Exiting.\n");
       return 0;
   }


   printf("\nEnter each road as: <from> <to> <travel_time>\n");
   printf("(Vertices are indexed 0 to %d)\n", V - 1);
   printf("Assuming UNDIR ECTED roads (u <-> v) with non-negative weights.\n\n");


   for (int i = 0; i < E; i++) {
       int u, v, w;
       printf("Road %d: ", i + 1);
       scanf("%d %d %d", &u, &v, &w);
       addEdgeUndirected(u, v, w);
   }


   // 4. Print the graph structure (for screenshots)
   printGraph(branchNames);


   // 5. Input source vertex (starting branch)
   int src;
   printf("\nEnter source branch index for Dijkstra (0 to %d): ", V - 1);
   scanf("%d", &src);


   if (src < 0 || src >= V) {
       printf("Invalid source vertex. Exiting.\n");
       return 0;
   }


   int dist[MAXV];
   int parent[MAXV];


   // 6. Run Dijkstra
   dijkstra(src, dist, parent, branchNames);


   // 7. Print final shortest paths
   printf("\n--- Shortest Travel Time from %s to All Branches ---\n", branchNames[src]);
   for (int i = 0; i < V; i++) {
       printf("Branch %d (%s): ", i, branchNames[i]);
       if (dist[i] == INF) {
           printf("No path\n");
       } else {
           printf("Distance = %d, Path = ", dist[i]);
           printPath(i, parent, branchNames);
           printf("\n");
       }
   }


   return 0;
}



