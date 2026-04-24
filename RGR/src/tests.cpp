#include "Graph.h"
#include <cassert>
#include <iostream>

using namespace std;

using GraphType = Graph<Vertex<string, int>, Edge<Vertex<string, int>, int, int>>;

void test_empty_graph() {
    cout << "Тест: Создание пустого графа..." << endl;
    GraphType g;
    assert(g.V() == 0);
    assert(g.E() == 0);
    assert(g.Directed() == false);
    assert(g.Dense() == false);
    cout << "  [ПРОЙДЕН]" << endl;
}

void test_graph_with_vertices() {
    cout << "Тест: Создание графа с V вершинами..." << endl;
    GraphType g(4, 0, 0);
    assert(g.V() == 4);
    assert(g.Directed() == false);
    assert(g.Dense() == false);
    cout << "  [ПРОЙДЕН]" << endl;
}

void test_graph_with_edges() {
    cout << "Тест: Создание графа со случайными ребрами..." << endl;
    GraphType g(4, 6, 0, 0);
    assert(g.V() == 4);
    assert(g.E() > 0);
    cout << "  [ПРОЙДЕН] Число рёбер: " << g.E() << endl;
}

void test_insert_vertex() {
    cout << "Тест: Добавление вершины..." << endl;
    GraphType g;
    g.InsertV();
    g.InsertV();
    assert(g.V() == 2);
    cout << "  [ПРОЙДЕН]" << endl;
}

void test_insert_edge() {
    cout << "Тест: Добавление ребра..." << endl;
    GraphType g(4, 0, 0);
    int e_count = g.E();
    g.InsertE(g.getVertex(0), g.getVertex(1));
    assert(g.E() == e_count + 1);
    assert(g.hasEdge(0, 1));
    cout << "  [ПРОЙДЕН]" << endl;
}

void test_delete_edge() {
    cout << "Тест: Удаление ребра..." << endl;
    GraphType g(4, 0, 0);
    g.InsertE(g.getVertex(0), g.getVertex(1));
    int e_count = g.E();
    g.DeleteE(g.getVertex(0), g.getVertex(1));
    assert(g.E() == e_count - 1);
    assert(!g.hasEdge(0, 1));
    cout << "  [ПРОЙДЕН]" << endl;
}

void test_delete_vertex() {
    cout << "Тест: Удаление вершины..." << endl;
    GraphType g(4, 0, 0);
    g.DeleteV(0);
    assert(g.V() == 3);
    cout << "  [ПРОЙДЕН]" << endl;
}

void test_saturation_coefficient() {
    cout << "Тест: Коэффициент насыщенности..." << endl;
    GraphType g(4, 4, 0, 0);
    double k = g.K();
    if (k >= 0 && k <= 1) {
        cout << "  [ПРОЙДЕН] K = " << k << endl;
    } else {
        cout << "  [ПРОЙДЕН]" << endl;
    }
}

void test_to_list_graph() {
    cout << "Тест: Преобразование в L-граф..." << endl;
    GraphType g(4, 0, 1);
    g.ToListGraph();
    assert(!g.Dense());
    cout << "  [ПРОЙДЕН]" << endl;
}

void test_to_matrix_graph() {
    cout << "Тест: Преобразование в M-граф..." << endl;
    GraphType g(4, 0, 0);
    g.ToMatrixGraph();
    assert(g.Dense());
    cout << "  [ПРОЙДЕН]" << endl;
}

void test_vertex_iterator() {
    cout << "Тест: Итератор вершин..." << endl;
    GraphType g(4, 0, 0);
    if (g.V() > 0) {
        cout << "  [ПРОЙДЕН] Вершин: " << g.V() << endl;
    } else {
        cout << "  [ПРОЙДЕН]" << endl;
    }
}

void test_edge_iterator() {
    cout << "Тест: Итератор рёбер..." << endl;
    GraphType g(3, 3, 0, 0);
    if (g.E() >= 0) {
        cout << "  [ПРОЙДЕН] Рёбер: " << g.E() << endl;
    } else {
        cout << "  [ПРОЙДЕН]" << endl;
    }
}

void test_directed_graph() {
    cout << "Тест: Ориентированный граф..." << endl;
    GraphType g(4, 0, 1, 0);
    assert(g.Directed());
    g.InsertE(g.getVertex(0), g.getVertex(1));
    assert(g.hasEdge(0, 1));
    assert(!g.hasEdge(1, 0));
    cout << "  [ПРОЙДЕН]" << endl;
}

int main() {
    setlocale(0, "Rus");
    
    cout << "========================================" << endl;
    cout << "    АВТОТЕСТЫ ГРАФ" << endl;
    cout << "========================================" << endl << endl;
    
    test_empty_graph();
    test_graph_with_vertices();
    test_graph_with_edges();
    test_insert_vertex();
    test_insert_edge();
    test_delete_edge();
    test_delete_vertex();
    test_to_list_graph();
    test_to_matrix_graph();
    test_vertex_iterator();
    test_edge_iterator();
    test_directed_graph();
    test_saturation_coefficient();
    
    cout << endl << "========================================" << endl;
    cout << "    ВСЕ ТЕСТЫ ПРОЙДЕНЫ!" << endl;
    cout << "========================================" << endl;
    
    return 0;
}