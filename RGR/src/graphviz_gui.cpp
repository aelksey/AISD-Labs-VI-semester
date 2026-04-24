#include "Graph.h"
#include <fstream>

using namespace std;

using GraphType = Graph<Vertex<string, int>, Edge<Vertex<string, int>, int, int>>;

void generate_dot_file(GraphType &g, const string &filename) {
    ofstream out(filename);
    
    if (g.Directed()) {
        out << "digraph G {" << endl;
    } else {
        out << "graph G {" << endl;
    }
    
    out << "    node [shape=box, style=filled, fillcolor=lightblue];" << endl;
    out << "    edge [color=gray];" << endl;
    
    for (int i = 0; i < g.V(); i++) {
        Vertex<string, int> *v = g.getVertex(i);
        out << "    " << v->getName() << " [label=\"" << v->getName() << "\"];" << endl;
    }
    
    for (int i = 0; i < g.V(); i++) {
        for (int j = 0; j < g.V(); j++) {
            if (g.hasEdge(i, j)) {
                string v1 = g.getVertex(i)->getName();
                string v2 = g.getVertex(j)->getName();
                int weight = g.read_weight_edge(v1, v2);
                
                if (g.Directed()) {
                    out << "    " << v1 << " -> " << v2;
                    if (weight > 0) {
                        out << " [label=\"" << weight << "\"]";
                    }
                    out << ";" << endl;
                } else {
                    if (i < j) {
                        out << "    " << v1 << " -- " << v2;
                        if (weight > 0) {
                            out << " [label=\"" << weight << "\"]";
                        }
                        out << ";" << endl;
                    }
                }
            }
        }
    }
    
    out << "}" << endl;
    out.close();
}

int main() {
    setlocale(0, "Rus");
    
    cout << "========================================" << endl;
    cout << "    ВИЗУАЛИЗАЦИЯ ГРАФА (Graphviz)" << endl;
    cout << "========================================" << endl << endl;
    
    cout << "Создание тестового графа..." << endl;
    GraphType g(5, 0, 0);
    
    g.InsertE(g.getVertex(0), g.getVertex(1));
    g.write_weight_edge("0", "1", 10);
    g.InsertE(g.getVertex(1), g.getVertex(2));
    g.write_weight_edge("1", "2", 20);
    g.InsertE(g.getVertex(2), g.getVertex(3));
    g.write_weight_edge("2", "3", 30);
    g.InsertE(g.getVertex(3), g.getVertex(0));
    g.write_weight_edge("3", "0", 40);
    g.InsertE(g.getVertex(0), g.getVertex(2));
    g.write_weight_edge("0", "2", 50);
    
    cout << "Граф:" << endl;
    g.print_graph();
    
    string dot_file = "graph.dot";
    string png_file = "graph.png";
    
    generate_dot_file(g, dot_file);
    cout << endl << "DOT-файл создан: " << dot_file << endl;
    
    string cmd = "dot -Tpng " + dot_file + " -o " + png_file;
    int result = system(cmd.c_str());
    
    if (result == 0) {
        cout << "Изображение создано: " << png_file << endl;
        cout << "Для просмотра откройте файл в любом просмотрщике изображений" << endl;
    } else {
        cout << "Ошибка создания изображения. Убедитесь, что graphviz установлен:" << endl;
        cout << "  sudo apt-get install graphviz" << endl;
    }
    
    cout << endl << "========================================" << endl;
    
    return 0;
}