#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>


#define MAX 20


int n; // number of vertices
char vertex[MAX][50]; // store vertex names (library tasks)
int adj[MAX][MAX]; // adjacency matrix


// Queue for BFS
int queue[MAX], front = -1, rear = -1;


// Function declarations
void createGraph();
void displayGraph();
void BFS(int start);
void DFS(int v, int visited[]);
void BFT();
void DFT();
bool isDAG();
void topologicalSort();


void enqueue(int v) {
    if (rear == MAX - 1) return;
    if (front == -1) front = 0;
    queue[++rear] = v;
}


int dequeue() {
    if (front == -1 || front > rear) return -1;
    return queue[front++];
}


// ---------- MAIN ----------
int main() {
    int choice;


    while (1) {
        printf("\n=============================\n");
        printf(" LIBRARY MANAGEMENT - GRAPH MENU\n");
        printf("=============================\n");
        printf("1. Create a graph\n");
        printf("2. Check if DAG (Acyclic)\n");
        printf("3. Traversal (BFT & DFT)\n");
        printf("4. Topological Sort\n");
        printf("5. Exit\n");
        printf("Enter your choice: ");
        scanf("%d", &choice);


        switch (choice) {
        case 1:
            createGraph();
            break;
        case 2:
            if (isDAG())
                printf("✅ The graph is Acyclic (DAG)\n");
            else
                printf("❌ The graph has a Cycle (Not a DAG)\n");
            break;
        case 3:
            BFT();
            DFT();
            break;
        case 4:
            topologicalSort();
            break;
        case 5:
            exit(0);
        default:
            printf("Invalid choice!\n");
        }
    }
    return 0;
}


// ---------- GRAPH CREATION ----------
void createGraph() {
    int i, j, edges, src, dest;


    printf("Enter number of vertices (library tasks): ");
    scanf("%d", &n);
    getchar();


    for (i = 0; i < n; i++) {
        printf("Enter name of task %d: ", i);
        gets(vertex[i]);
    }


    for (i = 0; i < n; i++)
        for (j = 0; j < n; j++)
            adj[i][j] = 0;


    printf("Enter number of directed edges: ");
    scanf("%d", &edges);


    for (i = 0; i < edges; i++) {
        printf("Enter edge (src dest) as indices (0 to %d): ", n - 1);
        scanf("%d%d", &src, &dest);
        adj[src][dest] = 1;
    }


    printf("Graph created successfully!\n");
    displayGraph();
}


// ---------- DISPLAY GRAPH ----------
void displayGraph() {
    int i, j;
    printf("\nAdjacency Matrix:\n   ");
    for (i = 0; i < n; i++)
        printf("%d ", i);
    printf("\n");


    for (i = 0; i < n; i++) {
        printf("%d: ", i);
        for (j = 0; j < n; j++)
            printf("%d ", adj[i][j]);
        printf("\n");
    }
}


// ---------- BFS TRAVERSAL ----------
void BFS(int start) {
    int visited[MAX] = {0};
    front = rear = -1;
    enqueue(start);
    visited[start] = 1;


    while (front <= rear) {
        int v = dequeue();
        printf("%s -> ", vertex[v]);
        for (int i = 0; i < n; i++) {
            if (adj[v][i] && !visited[i]) {
                enqueue(i);
                visited[i] = 1;
            }
        }
    }
}


// ---------- DFS TRAVERSAL ----------
void DFS(int v, int visited[]) {
    visited[v] = 1;
    printf("%s -> ", vertex[v]);
    for (int i = 0; i < n; i++) {
        if (adj[v][i] && !visited[i])
            DFS(i, visited);
    }
}


// ---------- BFT & DFT ----------
void BFT() {
    int visited[MAX] = {0};
    printf("\nBREADTH-FIRST TRAVERSAL:\n");
    for (int i = 0; i < n; i++) {
        if (!visited[i])
            BFS(i);
    }
    printf("NULL\n");
}


void DFT() {
    int visited[MAX] = {0};
    printf("\nDEPTH-FIRST TRAVERSAL:\n");
    for (int i = 0; i < n; i++) {
        if (!visited[i])
            DFS(i, visited);
    }
    printf("NULL\n");
}


// ---------- CHECK DAG (Cycle Detection using Kahn’s Algorithm) ----------
bool isDAG() {
    int indeg[MAX] = {0}, count = 0;
    int i, j;


    for (i = 0; i < n; i++)
        for (j = 0; j < n; j++)
            if (adj[i][j])
                indeg[j]++;


    int tempQueue[MAX], frontQ = 0, rearQ = 0;


    for (i = 0; i < n; i++)
        if (indeg[i] == 0)
            tempQueue[rearQ++] = i;


    while (frontQ < rearQ) {
        int v = tempQueue[frontQ++];
        count++;
        for (i = 0; i < n; i++) {
            if (adj[v][i] && --indeg[i] == 0)
                tempQueue[rearQ++] = i;
        }
    }


    return (count == n);
}


// ---------- TOPOLOGICAL SORT ----------
void topologicalSort() {
    int indeg[MAX] = {0};
    int i, j;


    for (i = 0; i < n; i++)
        for (j = 0; j < n; j++)
            if (adj[i][j])
                indeg[j]++;


    int stack[MAX], top = -1;


    for (i = 0; i < n; i++)
        if (indeg[i] == 0)
            stack[++top] = i;


    printf("\nTopological Order:\n");
    int count = 0;


    while (top != -1) {
        int v = stack[top--];
        printf("%s -> ", vertex[v]);
        count++;
        for (i = 0; i < n; i++) {
            if (adj[v][i]) {
                indeg[i]--;
                if (indeg[i] == 0)
                    stack[++top] = i;
            }
        }
    }


    if (count != n)
        printf("\n Graph has a cycle! Topological sort not possible.\n");
    else
        printf("NULL\n");
}



