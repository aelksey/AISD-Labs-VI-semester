#include <iostream>
#include <string>
#include <vector>
#include <list>
#include <iomanip>
#include <sstream>
#include <stack>
#include <climits>

#ifdef _WIN32
#include <conio.h>
#endif

using namespace std;

int sz=0;

string i_to_s(int x){
    return std::to_string(x);
}
//АТД «Дескриптор вершины графа»
template <class NameT, class DataT> class Vertex {
    NameT name;     //Имя вершины
    DataT data;     //Данные вершины
public:
	 bool nameSet, dataSet;
    //Конструкторы
    Vertex() :
        nameSet(false),
        dataSet(false) {}
    Vertex(NameT name, DataT data) :
        name(name),
        data(data),
        nameSet(true),
        dataSet(true) {}
    //Установка, получение имени и данных
    void setName(NameT name) { 
		this->name = name; 
		nameSet = true; 
	}
    void setData(DataT data) { 
		this->data = data; 
		dataSet = true; 
	}
    NameT getName() { 
		return name; 
	}
    DataT getData() { 
		return data; 
	}
    bool isNameSet() { 
		return nameSet; 
	}
    bool isDataSet() { 
		return dataSet; 
	}
};
//АТД «Дескриптор ребра графа»
template <class VertexT, class WeightT, class DataT> class Edge {
    VertexT *v1, *v2;   //Вершины, которые соединяет ребро
    WeightT weight;     //Вес
    DataT data;         //Данные ребра 
	bool weightSet, dataSet;
public:
    //Конструкторы
    Edge(VertexT *v1, VertexT *v2) :
        v1(v1),
        v2(v2),
        weightSet(false),
        weight(0),
        dataSet(false) {}
    Edge(VertexT *v1, VertexT *v2, WeightT weight) :
        v1(v1),
        v2(v2),
        weight(weight),
        weightSet(true),
        dataSet(false) {}
    Edge(VertexT *v1, VertexT *v2, WeightT weight, DataT data) :
        v1(v1),
        v2(v2),
        weight(weight),
        data(data),
        weightSet(true),
        dataSet(true) {}
    //Установка и получение веса и данных
    void setWeight(WeightT weight) { 
		this->weight = weight; 
		weightSet = true; 
	}
    void setData(DataT data) { 
		this->data = data; 
		dataSet = true; 
	}
    WeightT getWeight() { 
		return weight; 
	}
    DataT getData() { 
		return data; 
	}
	VertexT* getVertex1(){
		return v1;
	};
	VertexT* getVertex2(){
		return v2;
	};
    bool isWeightSet() { 
		return weightSet; 
	}
    bool isDataSet() { 
		return dataSet; 
	}
};
//форма представления
template <class EdgeT> class GraphForm {
public:
    //Вставка и удаление вершин и рёбер
    virtual bool InsertV(int index) = 0;
    virtual bool DeleteV(int index) = 0;
    virtual bool InsertE(int v1, int v2, EdgeT *t) = 0;
    virtual bool DeleteE(int v1, int v2) = 0;
    //Удалить входящие и исходящие из вершины рёбра
    virtual int DeleteEsFromVertex(int index, bool directed) = 0;
    //Проверка и получение
    virtual bool hasEdge(int v1, int v2) = 0;
    virtual EdgeT* getEdge(int v1, int v2) = 0;
};
//форма представления матричная
template <class EdgeT> class GraphMatrixForm : public GraphForm<EdgeT> {
    friend class EdgeIterator;	
    bool directed;
public:
    vector<vector<EdgeT*>> matrix;		//Матрица смежности		
	vector<vector<EdgeT*>> matrix_tmp1;  //temp Матрица смежности
	vector<vector<EdgeT*>> matrix_tmp2;  //temp Матрица смежности

    GraphMatrixForm(bool directed) : directed(directed) {}
    //Вставка и удаление вершин и рёбер
    bool InsertV(int index) {
        int size = matrix.size(); //Число вершин
        //Неверный номер вершины
        if(index < 0 || index > size) return false;
        //Создаём новую пустую строку
        vector<EdgeT*> newLine;
        newLine.assign(size, NULL);
        //Вставляем новую строку:
        matrix.insert(matrix.begin() + index, newLine);
        ++size;
        //Вставляем новый столбец:
        for(int i = 0; i < size; ++i)
            matrix[i].insert(matrix[i].begin() + index, (EdgeT*)NULL);
        return true;
    }
    bool DeleteV(int index) {
        int size = matrix.size(); //Число вершин
        //Неверный номер вершины
        if(index < 0 || index >= size) return false;
        //Удаляем строку:
        matrix.erase(matrix.begin() + index);
        --size;
        //Удаляем столбец:
        for(int i = 0; i < size; i++)
            matrix[i].erase(matrix[i].begin() + index);
        return true;
    }
    bool InsertE(int v1, int v2, EdgeT *t) {
        int size = matrix.size(); //Число вершин
        //Неверный номер вершины
        if(v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) return false;
        //Петля или ребро уже есть
        if(v1 == v2 || matrix[v1][v2] != NULL) return false;
        //Вставляем ребро
        matrix[v1][v2] = t;
        return true;
    }
    bool DeleteE(int v1, int v2) {
        int size = matrix.size(); //Число вершин
        //Неверный номер вершины
        if(v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) return false;
        //Ребра нет
        if(v1 == v2 || matrix[v1][v2] == NULL) return false;
        matrix[v1][v2] = NULL;
        return true;
    }
    //Удалить входящие и исходящие из вершины рёбра
    int DeleteEsFromVertex(int index, bool directed) {
        int size = matrix.size(); //Число вершин
        int deleted = 0;
        //Неверный номер вершины
        if(index < 0 || index >= size) return 0;
        //Удаляем связанные с вершиной рёбра
        for(int i = 0; i < size; i++) {
            if(matrix[i][index] != NULL) {
                delete matrix[i][index];
                matrix[i][index] = NULL;
                ++deleted;
                //Стираем симметричное ребро
                if(directed == false)
                    matrix[index][i] = NULL;
            }
            if(matrix[index][i] != NULL) {
                delete matrix[index][i];
                matrix[index][i] = NULL;
                ++deleted;
            }
        }
        return deleted;
    }
    //Проверка и получение
    bool hasEdge(int v1, int v2) {
        int size = matrix.size(); //Число вершин
        //Неверный номер вершины
        if(v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) 
			return false;
        if(v1 == v2) //Петля
			return false;
        if(matrix[v1][v2] != NULL) 
			return true;
        return false;
    }
    EdgeT* getEdge(int v1, int v2) {
        int size = matrix.size(); //Число вершин
        //Неверный номер вершины
        if(v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) 
			throw 1;
        if(v1 == v2 || matrix[v1][v2] == NULL)//Петля 
			throw 1;
        return matrix[v1][v2];
    }
    //Итератор рёбер
    class EdgeIterator {
        bool end;
        GraphMatrixForm *f;
    public:
		int curI, curJ;
        EdgeIterator(GraphMatrixForm *f) {
            this->f = f;
            begin();
        }
        bool begin() {
            for(int i = 0; i < f->matrix.size(); ++i)
                for(int j = 0; j < f->matrix.size(); ++j)
                    if(f->matrix[i][j]) {
                        curI = i;
                        curJ = j;
                        end = false;
                        return true;
                    }
					end = true;
					return false;
        }
        bool onEnd() { 
			return end; 
		}
		bool toend(){
			for(int i=f->matrix.size()-1; i>=0; --i)
				for(int j=f->matrix.size()-1; j>=0; --j)
					if (f->matrix[i][j]){
						curI=i;
						curJ=j;
						end=false;
						return true;
					}
					end=true;
					return false;
		}
        bool next() {
            if(end)
                return false;
            ++curJ;
            while(curI < f->matrix.size()) {
                while(curJ < f->matrix.size()) {
                    if(f->matrix[curI][curJ])
                        return true;
                    ++curJ;
                }
                ++curI;
                curJ = (f->directed ? 0 : curI + 1);
            }
            end = true;
            return false;
        }
        EdgeT* getEdge() {
            if(end)
                throw 1;
			if (!(f->matrix[curI][curJ]))
				throw 1;
            return f->matrix[curI][curJ];
        }
    };

    //Итератор исходящих рёбер
    class OutputEdgeIterator {
        int curI, curJ;
        bool end;
        GraphMatrixForm *f;
    public:
        OutputEdgeIterator(GraphMatrixForm *f, int vIndex) {
            this->f = f;
            curI = vIndex;
            begin();
        }
        bool begin() {
            for(curJ = 0; curJ < f->matrix.size(); ++curJ)
                if(f->matrix[curI][curJ]) {
                    end = false;
                    return true;
                }
            end = true;
            return false;
        }
		bool toend(){
			for(curJ=f->matrix.size()-1; curJ>=0; --curJ)
				if (f->matrix[curI][curJ]){
					end=false;
					return true;
				}
				end=true;
				return false;
		}
        bool onEnd() { 
			return end; 
		}
        bool next() {
            if(end)
                return false;
            ++curJ;
            while(curJ < f->matrix.size()) {
                if(f->matrix[curI][curJ])
                    return true;
                ++curJ;
            }
            end = true;
            return false;
        }
        EdgeT* getEdge() {
            if(end)
                throw 1;
            return f->matrix[curI][curJ];
        }
    };
};
//форма представления список
template <class EdgeT> class GraphListForm : public GraphForm<EdgeT> {    
	//Элемент списка
    class Node {
    public:
        EdgeT *edge; //Само ребро
        int v2;      //Вторая вершина, которую ребро соединяет
    };    
    bool directed;
	vector<list<Node>> edgeList; //Списки смежности
public:	
    GraphListForm(bool directed) : directed(directed) {}
    //Вставка и удаление вершин и рёбер
    bool InsertV(int index) {
        int size = edgeList.size(); //Число вершин
        if(index < 0 || index > size) //Неверный номер вершины
			return false;
        //Создаём новый список смежности
        list<Node> newList;
        //Вставляем
        edgeList.insert(edgeList.begin() + index, newList);
        ++size;
        //Обновляем дескрипторы
        for(int i = 0; i < size; ++i)
            for(typename list<Node>::iterator j = edgeList[i].begin(); j != edgeList[i].end(); ++j)
                if((*j).v2 >= index)//если текущая вершина имеет больший номер, чем вставляемая,
                    ++((*j).v2);//то увеличиваем этот номер
        return true;
    }
    bool DeleteV(int index) {
        int size = edgeList.size(); //Число вершин
        if(index < 0 || index >= size)  //Неверный номер вершины
			return false;
        //Удаляем из списков записи о рёбрах
        for(int i = 0; i < size; ++i)
            for(typename list<Node>::iterator j = edgeList[i].begin(); j != edgeList[i].end(); ++j)
                if((*j).v2 == index) {
                    edgeList[i].erase(j);
                    break;
                }
        //Удаляем список смежности
        edgeList.erase(edgeList.begin() + index);
        --size;
        //Обновляем дескрипторы
        for(int i = 0; i < size; ++i)
            for(typename list<Node>::iterator j = edgeList[i].begin(); j != edgeList[i].end(); ++j)
                if((*j).v2 > index)//если текущая вершина имеет больший номер, чем удаляемая,
                    --((*j).v2);//то уменьшить этот номер
        return true;
    }
    bool InsertE(int v1, int v2, EdgeT *t) {
        int size = edgeList.size(); //Число вершин
        if(v1 < 0 || v2 < 0 || v1 >= size || v2 >= size)//Неверный номер вершины
			return false;
        if(v1 == v2 || hasEdge(v1, v2))  //Петля или ребро уже есть
			return false;
        //Вставляем ребро
        Node node;
        node.edge = t;
        node.v2 = v2;
        edgeList[v1].push_front(node);
        return true;
    }
    bool DeleteE(int v1, int v2) {
        int size = edgeList.size(); //Число вершин
        //Неверный номер вершины
        if(v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) 
			return false;
        //Ребра нет
        if(v1 == v2 || hasEdge(v1, v2) == false) 
			return false;
        //Удаляем ребро
        for(typename list<Node>::iterator j = edgeList[v1].begin(); j != edgeList[v1].end(); ++j)
            if((*j).v2 == v2) {
                edgeList[v1].erase(j);
                break;
            }
        return true;
    }
    //Удалить входящие и исходящие из вершины рёбра
    int DeleteEsFromVertex(int index, bool directed) {
        int size = edgeList.size(); //Число вершин
        int deleted = 0;
        //Неверный номер вершины
        if(index < 0 || index >= size) 
			return 0;
        //Удаляем связанные с вершиной рёбра
        for(int i = 0; i < size; ++i)
            for(typename list<Node>::iterator j = edgeList[i].begin(); j != edgeList[i].end(); ++j)
                if((*j).v2 == index) {
                    delete (*j).edge;
                    edgeList[i].erase(j);
                    ++deleted;
                    //Стираем симметричное ребро
                    if(directed == false)
                        for(typename list<Node>::iterator k = edgeList[index].begin(); k != edgeList[index].end(); ++k)
                            if((*k).v2 == i) {
                                edgeList[index].erase(k);
                                break;
							}
							break;
				}
	    if (directed==true){
			for(typename list<Node>::iterator z = edgeList[index].begin(); z != edgeList[index].end(); ++z){  
				delete (*z).edge; 
				deleted++;
			}
		}	
        return deleted;
    }
    //Проверка и получение
    bool hasEdge(int v1, int v2) {
        int size = edgeList.size(); //Число вершин
        //Неверный номер вершины
        if(v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) 
			return false;
        //Петля
        if(v1 == v2) 
			return false;
        for(typename list<Node>::iterator j = edgeList[v1].begin(); j != edgeList[v1].end(); ++j)
            if((*j).v2 == v2)
                return true;
        return false;
    }
    EdgeT* getEdge(int v1, int v2) {
        int size = edgeList.size(); //Число вершин
        //Неверный номер вершины
        if(v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) 
			throw 1;
        //Петля
        if(v1 == v2) 
			throw 1;
        for(typename list<Node>::iterator j = edgeList[v1].begin(); j != edgeList[v1].end(); ++j)
            if((*j).v2 == v2)
                return (*j).edge;
        throw 1;
    }
    //Итератор рёбер
    class EdgeIterator {
        int curI;
        typename list<typename GraphListForm<EdgeT>::Node>::iterator curJ;
        bool end;
        GraphListForm *f;
    public:
        EdgeIterator(GraphListForm *f) {
            this->f=f;                 
		}
		//Установить итератор рёбер на начальное ребро
        bool begin() {
            for(curI = 0; curI < f->edgeList.size(); ++curI)
                for(curJ = f->edgeList[curI].begin(); curJ != f->edgeList[curI].end(); ++curJ)
                    if((*curJ).edge) {
                        end = false;
                        return true;                    
					}
            end = true;
            return false;        
		}
        bool onEnd() { 
			return end; 
		}
		//Установить итератор рёбер на конечное ребро
		bool toend(){
			 typename list<typename GraphListForm<EdgeT>::Node>::iterator prev_curJ;
			 for(curI = 0; curI < f->edgeList.size(); ++curI) {
                for(curJ = f->edgeList[curI].begin(); curJ != f->edgeList[curI].end(); ++curJ) {
					if ((*curJ).edge)
						prev_curJ=curJ;			 
					if (!(*prev_curJ).edge){			
						end=true;
						return false;
					}
					else{
						end=false;				 
						curJ=prev_curJ;
						return true;
					} 
				}
			 }
		};
		//Переход к следующей позиции
        bool next() {
            if(end)
                return false;
            ++curJ;
            while(curI < f->edgeList.size()) {
                while(curJ != f->edgeList[curI].end()) {
                    if((*curJ).edge && (f->directed || !f->directed && (*curJ).v2 > curI))
                        return true;
                    ++curJ;                
				}
                ++curI;
                if(curI < f->edgeList.size())
                    curJ = f->edgeList[curI].begin();
            }
            end = true;
            return true;        
		}
        EdgeT* getEdge() {
            if(end)
                throw 1;
            return (*curJ).edge;
        }
    };
    //Итератор исходящих рёбер
    class OutputEdgeIterator {
        int curI;
        typename list<typename GraphListForm<EdgeT>::Node>::iterator curJ;
        bool end;
        GraphListForm *f;
    public:
        OutputEdgeIterator(GraphListForm *f, int vIndex) {
            this->f = f;
            curI = vIndex;
            begin();
        }
        bool begin() {
            for(curJ = f->edgeList[curI].begin(); curJ != f->edgeList[curI].end(); ++curJ)
                if((*curJ).edge) {
                    end = false;
                    return true;                
				}
            end = true;
            return false;        
		}
        bool onEnd() { 
			return end; 
		}
		bool toend(){
			 typename list<typename GraphListForm<EdgeT>::Node>::iterator prev_curJ;
			 for(curJ = f->edgeList[curI].begin(); curJ != f->edgeList[curI].end(); ++curJ)
				 if ((*curJ).edge)
					 prev_curJ=curJ;
			 if (!(*prev_curJ).edge){
				 end=true;
				 return false;
			 }
			 else{
				 end=false;
				 curJ=prev_curJ;
				 return true;
			 };  
		}
        bool next() {
            if(end)
                return false;
            ++curJ;
            while(curJ != f->edgeList[curI].end()) {
                if((*curJ).edge)
                    return true;
                ++curJ;            
			}
            end = true;
            return true;
        }
        EdgeT* getEdge() {
            if(end)
                throw 1;
            return (*curJ).edge;        
		}    
	};

};
//АТД простой граф
template <class VertexT, class EdgeT> class Graph {
    friend class VertexIterator;  //дружественный класс
    vector<VertexT*> vertexVector;//Вектор вставленных
    GraphForm<EdgeT> *data;       //Данные графа
    bool directed;                //Направленный граф
    bool dense;                   //М-граф
    int edgeCounter;              //Число рёбер

    //Получение индекса вершины по дескриптору
	int getIndexFromName(string n){
		int index=0;
		for(int i=0; i<vertexVector.size(); ++i){
			if (vertexVector[index]->getName()==n)
				break;
			++index;
		}
		if(index==vertexVector.size())
			throw "Exeption#";
        return index;
	};

public:  
	//Создать пустой L-граф с нулевым числом вершин и рёбер
    Graph() {					
        data = new GraphListForm<EdgeT>(false);
        directed = false;
        dense = false;
        edgeCounter = 0;
    }
    //Создать граф с V вершинами, без ребер, типа D, формы представления F
    Graph(int vertexCount, bool directed, bool dense) {		
        if(dense)
            data = new GraphMatrixForm<EdgeT>(directed);//!!!!!!!!!!!!1
        else
            data = new GraphListForm<EdgeT>(directed);//!!!!!!!!!!!!!!!!!!!!
        //Создаём вершины и помещаем в структуру
        for(int i = 0; i < vertexCount; ++i)
            data->InsertV(i);
        for(int i = 0; i < vertexCount; ++i) {
            VertexT *v = new VertexT();
			v->setName(i_to_s(sz));
			sz++;
            vertexVector.push_back(v);
            data->InsertV(i);
        }
        this->directed = directed;
        this->dense = dense;
        edgeCounter = 0;
    }
    //Создать граф с V вершинами, с E случайными ребрами, типа D , формы F
    Graph(int vertexCount, int edgeCount, bool directed, bool dense) {
        if(dense)
            data = new GraphMatrixForm<EdgeT>(directed);
        else
            data = new GraphListForm<EdgeT>(directed);
        //Создаём вершины и помещаем в структуру
        for(int i = 0; i < vertexCount; ++i)
            data->InsertV(i);
        for(int i = 0; i < vertexCount; ++i) {
            VertexT *v = new VertexT(); 
			v->setName(i_to_s(sz));
			sz++;
            vertexVector.push_back(v);
            data->InsertV(i);
        } 
        this->directed = directed;
        this->dense = dense;
        edgeCounter = 0;

        //Вставляем рёбра
        if(edgeCount <= 0) return;
        //Корректируем число рёбер, чтобы не превышало максимумы
        if(directed) {
            if(edgeCount > vertexCount * (vertexCount - 1))
                edgeCount = vertexCount * (vertexCount - 1);
            srand(time(0));
        } else {
            if(edgeCount > (vertexCount * (vertexCount - 1)) / 2)
                edgeCount = (vertexCount * (vertexCount - 1)) / 2;
        }
        //Вставляем
        int v1, v2;

        while(edgeCounter < edgeCount) {
            v1 = rand() % vertexCount;
            v2 = rand() % vertexCount;
            if(v1 == v2)
                continue;
            if(data->hasEdge(v1, v2))
                continue;

            EdgeT *t = new EdgeT(vertexVector[v1], vertexVector[v2]);
            data->InsertE(v1, v2, t);
			t->setWeight((rand() % 9) +1);
            if(directed == false)
                data->InsertE(v2, v1, t);
            ++edgeCounter;
        }
    }
	//конструктор копирования
    Graph(const Graph<VertexT, EdgeT> &G) {
        delete data;
        if(G.Dense())
            data = new GraphMatrixForm<EdgeT>(*(G.data));
        else
            data = new GraphListForm<EdgeT>(*(G.data));
        directed = G.directed;
        dense = G.dense;
    }
	//деструктор
    ~Graph() {
		while (vertexVector.size()!=0)
			DeleteV(vertexVector.size()-1);
        delete data;
    }     
	//возвращает число вершин в графе
    int V() { 
		return vertexVector.size(); 
	}
	//возвращает число ребер в графе
    int E() { 
		return edgeCounter; 
	}
	//возвращает тип графа
    bool Directed() { 
		return directed; 
	}
	//возвращает форму представления графа
    bool Dense() { 
		return dense; 
	}
	//возвращает коэффициент насыщенности графа
    double K() {
        int max = vertexVector.size() * (vertexVector.size() - 1);
        if(!directed) {
            max /= 2;
		}
		if (max==0) {
			return -1;
		}
		else {
			return (double)edgeCounter / (double)max;
		}
    }
    //преобразует граф к L-графу
    void ToListGraph() {
        //Создаём структуру хранения L-графа
		GraphForm<EdgeT> *newData = new GraphListForm<EdgeT>(this->directed);

        //Создаём вершины
        for(int i = 0; i < vertexVector.size(); ++i)
            newData->InsertV(i);

        //Переносим рёбра
        for(int i = 0; i < vertexVector.size(); ++i)
            for(int j = 0; j < vertexVector.size(); ++j)
                if(data->hasEdge(i, j))
                    newData->InsertE(i, j, data->getEdge(i, j));
        delete data;
        data = newData;
        dense = false;
    }
	//преобразует граф к M-графу
    void ToMatrixGraph() {
        //Создаём структуру хранения М-графа
		GraphForm<EdgeT> *newData = new GraphMatrixForm<EdgeT>(this->directed);

        //Создаём вершины
        for(int i = 0; i < vertexVector.size(); ++i)
            newData->InsertV(i);

        //Переносим рёбра
        for(int i = 0; i < vertexVector.size(); ++i)
            for(int j = 0; j < vertexVector.size(); ++j)
                if(data->hasEdge(i, j))
                    newData->InsertE(i, j, data->getEdge(i, j));
        delete data;
        data = newData;
        dense = true;
    }
	//добавляет вершину к графу и возвращает адрес дескриптора вновь созданной вершины
    VertexT* InsertV() {
        VertexT *v = new VertexT;
        if(data->InsertV(vertexVector.size()) == false)
            throw 1;
		v->setName(i_to_s(sz));
		sz++;
        vertexVector.push_back(v);
        return v;
    }
	//удаляет вершину из графа, заданную адресом дескриптора v
    bool DeleteV(VertexT *v) {
        int index = getIndex(v);
        edgeCounter -= data->DeleteEsFromVertex(index, directed);
        if(data->DeleteV(index)) {
            vertexVector.erase(vertexVector.begin() + index);
            return true;
        }
        return false;
    }	
	bool DeleteV(int index) {
        edgeCounter -= data->DeleteEsFromVertex(index, directed);
        if(data->DeleteV(index)) {
            vertexVector.erase(vertexVector.begin() + index);
            return true;
        }
        return false;
    }
    bool DeleteV(string n) {
		VertexT *v=new VertexT;
		bool rez;
		try{
			v=getVertexFromName(n);
		}
		catch(char*){
			return false;
		}
		rez=DeleteV(v);
		return rez;
    }
    VertexT* getVertex(int index) {
        if(index < 0 || index >= vertexVector.size())
			throw "Exeption#";
        return vertexVector[index];
    }
	VertexT* getVertexFromName(string n){
		int i;
		for(i=0; i<vertexVector.size(); i++)
			if (vertexVector[i]->getName()==n)
				return vertexVector[i];
		if (i==vertexVector.size())
			throw "Exeption#";
	};
	int getIndex(VertexT *v) {
		int index = 0;
        for(int i = 0; i < vertexVector.size(); ++i) {
            if(vertexVector[index] == v)
                break;
            ++index;
        }
        if(index == vertexVector.size())
            throw "Exeption#";
        return index;
    }
    //добавляет ребро (v1, v2) к графу, соединяющую вершины, заданные адресами дескрипторов v1 и v2	
	bool InsertE(string v1, string v2){
		VertexT* _v1=new VertexT;
		VertexT* _v2=new VertexT;
		try{
			_v1=getVertexFromName(v1); 
		}
		catch(char* e){				
			return false;
		};
		try{
			_v2=getVertexFromName(v2);
		}
		catch(char* e){			
			return false;
		}
		try{
			InsertE(_v1, _v2);
		}
		catch (char* e){			
			return false;
		}
		return true;
	};
    EdgeT* InsertE(VertexT *v1, VertexT *v2) {
        EdgeT *e = new EdgeT(v1, v2);
		if(!data->InsertE(getIndex(v1), getIndex(v2), e)) throw "Exeption#";		
		if(directed == false) 
			data->InsertE(getIndex(v2), getIndex(v1), e);		
        ++edgeCounter;		
        return e;
    }
	//удаляет ребро, соединяющее вершины, заданные адресами дескрипторов v1 и v2
	bool DeleteE(string v1, string v2){
		VertexT* _v1=new VertexT;
		VertexT* _v2=new VertexT;
		try{
			_v1=getVertexFromName(v1);
		}
		catch(char*e){
			//cerr<<endl<<e<<endl;
			return false;
		};
		try{
			_v2=getVertexFromName(v2);
		}
		catch(char*e){
			//cerr<<endl<<e<<endl;
			return false;
		}
		if(!DeleteE(_v1, _v2)) return false;
		else return true;
	};
    bool DeleteE(VertexT *v1, VertexT *v2) {
        if(data->DeleteE(getIndex(v1), getIndex(v2))) {
            --edgeCounter;
            if(directed == false)
                data->DeleteE(getIndex(v2), getIndex(v1));
            return true;
        } else
            return false;
    }
    bool hasEdge(VertexT *v1, VertexT *v2) {
        int ind1, ind2;
        try { 
			ind1 = getIndex(v1); 
		} 
		catch(int &a) { 
			return false; 
		}
        try {
			ind2 = getIndex(v2); 
		}
		catch(int &a) 
		{ 
			return false; 
		}
        return data->hasEdge(getIndex(v1), getIndex(v2));
    }
    bool hasEdge(int v1, int v2) {
        if(v1 < 0 || v1 >= vertexVector.size()) 
			return false;
        if(v2 < 0 || v2 >= vertexVector.size()) 
			return false;
        return data->hasEdge(v1, v2);
    }
    EdgeT* getEdge(VertexT *v1, VertexT *v2) {
		EdgeT* e;		
		try{
			e=data->getEdge(getIndex(v1), getIndex(v2));
		}
		catch(int){
			throw "Exeption#";
		}
        return e;
    }
	//Записать данные о ребре
	bool write_data_edge(string _v1, string _v2, int data){
		EdgeT* e;
		VertexT *v1, *v2;
		try{
			v1=getVertexFromName(_v1);
		}
		catch(char*){
			return false;
		}
		try{
			v2=getVertexFromName(_v2);
		}
		catch(char*){
			return false;
		}
		try {
			e=getEdge(v1, v2);
			e->setData(data);
		}
		catch (char*e) {
			cerr<<endl<<e<<endl;
			return false;
		}
		return true;
	};
	//Записать вес ребра
	bool write_weight_edge(string _v1, string _v2, int w){
		EdgeT* e;
		VertexT *v1, *v2;
		try{
			v1=getVertexFromName(_v1);
		}
		catch(char*){
			return false;
		}
		try{
			v2=getVertexFromName(_v2);
		}
		catch(char*){
			return false;
		}
		try {
			e=getEdge(v1, v2);
			e->setWeight(w);
		}
		catch (char*e) {
			cerr<<endl<<e<<endl;
			return false;
		}
		return true;
	};
	//Прочитать данные о ребре
	int read_data_edge(string _v1, string _v2){
		EdgeT* e;
		VertexT *v1, *v2;
		try{
			v1=getVertexFromName(_v1);
			v2=getVertexFromName(_v2);
		}
		catch(char*e){
			cerr<<endl<<e<<endl;
			return -1;
		}
		try {
			e=getEdge(v1, v2);
		}
		catch (char*e) {
			cerr<<endl<<e<<endl;
			return -1;
		}
		try {
			if (e->isDataSet())
				return e->getData();
			else
				throw "Exeption#";
		}
		catch (char*e) {
			cerr<<endl<<e<<endl;
			return -1;
		}
	};
	//Прочитать вес ребра
	int read_weight_edge(string _v1, string _v2){
		EdgeT* e;
		VertexT *v1, *v2;
		try{
			v1=getVertexFromName(_v1);
			v2=getVertexFromName(_v2);
			e=getEdge(v1, v2);
		}
		catch(char*e){
			cerr<<endl<<e<<endl;
			return -1;
		}
		try {
			if (e->isWeightSet())
				return e->getWeight();
			else
				throw "Exeption#";
		}
		catch (char*e) {
			cerr<<endl<<e<<endl;
			return -1;
		}
	};
	//Вывод
	void print_graph(){
		int i, j;
		VertexT* v;
		EdgeT* e;
		if (Dense()){
			cout<<"  ";
			for (i=0; i<V(); i++){
				v=getVertex(i);
				cout<<setw(4)<<v->getName();
			}
			cout<<endl;
			for(i=0; i<5*V(); i++)
				cout<<"_";
			cout<<endl;
			for (i=0; i<V(); i++){
				v=getVertex(i);
				cout<<v->getName()<<"|";
				for (j=0; j<V(); j++)
					if (hasEdge(i,j)){
						e=getEdge(getVertex(i),getVertex(j));
						cout<<setw(4)<<e->getWeight();
					}
					else
						cout<<setw(4)<<"0";
				cout<<endl;
			}
		}
		else
		{
			for (i=0; i<V(); i++){
				v=getVertex(i);
				cout<<"*"<<v->getName()<<"->";
				for (j=0; j<V(); j++){
					v=getVertex(j);
					if (hasEdge(i,j))
						cout<<v->getName()<<"->";
				}
				cout<<endl;
			}
		}
	};
	//Прочитать данные о вершине
	int read_data_vertex(string v1){
		VertexT *v;
		try
		{
			v=getVertexFromName(v1);
		}
		catch(char* e){
			cerr<<endl<<e<<endl;
			return -1;
		};
		int d;
		d=v->getData();
		try {
			if (v->dataSet!=false)
				return d;
			else			
				throw invalid_argument ("Exeption#");
		}
		catch (invalid_argument &e) {
			cerr<<endl<<e.what()<<endl;
			return -1;
		}
	};
	//Записать данные о вершине
	bool write_data_vertex(string v1, int d){
		VertexT *v;
		try
		{
			v=getVertexFromName(v1);
		}
		catch(char*){
			return false;
		};
		v->setData(d);
		return true;
	};
	//Записать данные о вершине
	bool write_name_vertex(string v1, string str){
		VertexT *v;
		try
		{
			v=getVertexFromName(v1);
		}
		catch(char*){
			return false;
		};
		v->setName(str);
		return true;
	};
	  
    //Итератор вершин
    class VertexIterator {
        Graph<VertexT, EdgeT> *graph; 
        bool end;
        int current;
    public:
        VertexIterator(Graph<VertexT, EdgeT> &g) {
            graph = &g;
            //begin();
        }
        //возвращает итератор, установленный на первую вершину графа
        bool begin() {
            if(graph->vertexVector.size() == 0) {
                end = true;
                return true;				
            }
            current = 0;
            end = false;
			return true;
        }
        //Переход к следующей позиции
        bool operator++() {
            try {
				if(end){
					current=-1;
					throw "Exeption#";					
				}
			}
			catch (char*e) {
				cerr<<endl<<e<<endl;
				return false;
			}
            current++;
            if(graph->vertexVector.size()-1 == current)
                end = true;
            return true;
        };
		//Установить итератор вершин на конечную вершину
		bool toend(){
			current=graph->vertexVector.size()-1;
			end=true;
			return true;
		};
        //Проверка на выход
        bool onEnd() {
            return end;
        }
        //Получение вершины
        VertexT* operator*() {
            if(current >= graph->vertexVector.size() || current==-1)
				throw "Exeption#";
            return graph->vertexVector[current];
        }
		//Прочитать вершину по текущей позиции итератора
		string read_vertex() {
			VertexT *v;
			try
			{
				v=operator*();
			}
			catch(char*){
				throw "Exeption#";				
			};
			string d;
			d=v->getName();
			if (v->nameSet!=false)
				return d;
			else
				throw "Exeption#";
        }
		//Прочитать данные вершины по текущей позиции итератора
		int read_data_vertex(){
			VertexT *v;
			try
			{
				v=operator*();				
			}
			catch(char*e){
				cerr<<endl<<e<<endl;
				return -1;
			};
			try {
				if (v->dataSet!=false)
					return v->getData();					
				else throw "Exeption#";
			}
			catch (char*e) {
				cerr<<endl<<e<<endl;	
				return -1;
			}				
		};
		//Записать данные о вершины по текущей позиции итератора
		bool write_data_vertex(int d){
			VertexT *v;
			try
			{
				v=operator*();
			}
			catch(char*e){
				cerr<<endl<<e<<endl;
				return false;
			};
			v->setData(d);
			return true;
		};
		//Записать имя вершины по текущей позиции итератора
		bool write_name_vertex(string str){
			VertexT *v;
			try
			{
				v=operator*();
			}
			catch(char*e){
				cerr<<endl<<e<<endl;
				return false;
			};
			v->setName(str);
			return true;
		};
		
	};

    //Итератор рёбер
    class EdgeIterator {
        Graph<VertexT, EdgeT> *graph; //Обрабатываемый граф
        typename GraphMatrixForm<EdgeT>::EdgeIterator *mIt;
        typename GraphListForm<EdgeT>::EdgeIterator *lIt;
        bool useM;
    public:
		int curV1, curV2;
		string str1, str2, str3, str4;
		bool end;
        EdgeIterator(Graph<VertexT, EdgeT> &g) {
            graph = &g;
            if(graph->Dense()) {
                mIt = new typename GraphMatrixForm<EdgeT>::EdgeIterator(static_cast<GraphMatrixForm<EdgeT>*>(g.data));
                useM = true;
            } else {
                lIt = new typename GraphListForm<EdgeT>::EdgeIterator(static_cast<GraphListForm<EdgeT>*>(g.data));
                useM = false;
            }
            //begin();
        }
        //Установка в начало
        bool begin() {
            if(useM) return 
				mIt->begin();
            else 
				return lIt->begin();
        }
        //Сдвиг на следующий элемент
        bool operator++() {
            if(useM)
                return mIt->next();
            else
                return lIt->next();
        }
        //Проверка на выход
        bool onEnd() {            
            if(useM)
                return mIt->onEnd();
            else
                return lIt->onEnd();
        }
		//Установить итератор рёбер на конечное ребро
		bool toend(){			
			if (useM)
				return mIt->toend();
			else
				return lIt->toend();
		}
        //Получение ребра
        EdgeT* operator*() {
			if (onEnd())
				throw "Exeption#";
            if(useM)
                return mIt->getEdge();
            else
                return lIt->getEdge();
        }
		//Прочитать вершины ребра и вес по текущей позиции итератора
		string read_edge(){
			EdgeT* e;
			try{
				e=operator*();
			}
			catch(char*){
				throw "Exeption#";
			}
			VertexT *_v1, *_v2;								
			_v1=e->getVertex1();
			_v2=e->getVertex2();
			if (_v1->nameSet==false)
				str1="#data not available";
			else
				str1=_v1->getName();
			if (_v2->nameSet==false)
				str2="#data not available";
			else
				str2=_v2->getName();
			if (e->isWeightSet()==false)
				str3="#data not available";
			else
				str3=i_to_s(e->getWeight());
			str4="Исходящая вершина: "+str1+". Входящая вершина: "+str2+". Вес ребра: "+str3;
			return str4;
		}
		//Прочитать данные ребра по текущей позиции итератора
		int read_data_edge(){
			EdgeT* f;
			try{
				f=operator*();
			}
			catch(char*e){
				throw "Exeption#";
			}			
			if (f->isDataSet())
				return f->getData();
			else
				throw "#data not available";				
		};
		bool write_data_edge(int d){
			EdgeT* e;
			try{
				e=operator*();
			}
			catch(char*e){
				return false;
			}
			e->setData(d);
			return true;
		};
		bool write_weight_edge(int w){
			EdgeT* e;
			try{
				e=operator*();
			}
			catch(char* Error){
				return false;
			}
			e->setWeight(w);
			return true;
		}
    };

    //Итератор исходящих рёбер
    class OutputEdgeIterator {
        Graph<VertexT, EdgeT> *graph; //Обрабатываемый граф
        typename GraphMatrixForm<EdgeT>::OutputEdgeIterator *mIt;
        typename GraphListForm<EdgeT>::OutputEdgeIterator *lIt;
        bool useM;		
    public:
		string str1, str2, str3, str4;
		int curV1, curV2;
		bool end;
        OutputEdgeIterator(Graph<VertexT, EdgeT> &g, VertexT &v) {
            graph = &g;
            curV1 = graph->getIndex(&v);
            if(graph->Dense()) {
                mIt = new typename GraphMatrixForm<EdgeT>::OutputEdgeIterator(static_cast<GraphMatrixForm<EdgeT>*>(g.data), curV1);
                useM = true;
            } else {
                lIt = new typename GraphListForm<EdgeT>::OutputEdgeIterator(static_cast<GraphListForm<EdgeT>*>(g.data), curV1);
                useM = false;
            }            
        }
        //Установка в начало
        bool begin() {
            if(useM)
                return mIt->begin();
            else
                return lIt->begin();
        }
        //Сдвиг на следующий элемент
        bool operator++() {		
            if(useM)
                return mIt->next();
            else
                return lIt->next();
        }
		bool toend(){
			if(useM)
				return mIt->toend();
			else
				return lIt->toend();
		}
        //Проверка на выход
        bool onEnd() {
            if(useM)
                return mIt->onEnd();
            else
                return lIt->onEnd();
        }
        //Получение ребра
        EdgeT* operator*() {
			if (onEnd())
				throw "#Exeption";
            if(useM)
                return mIt->getEdge();
            else
                return lIt->getEdge();
        }
		string read_edge(){
			EdgeT* e;
			try{
				e=operator*();
			}
			catch(char*f){				
				throw "Exeption#";
			}
			VertexT *_v1, *_v2;
			_v1=e->getVertex1();
			_v2=e->getVertex2();
			if (_v1->nameSet==false)
				str1="#data not available";
			else
				str1=_v1->getName();
			if (_v2->nameSet==false)
				str2="#data not available";
			else
				str2=_v2->getName();
			if (e->isWeightSet()==false)
				str3="#data not available";
			else
				str3=i_to_s(e->getWeight());
			str4="Исходящая вершина: "+str1+". Входящая вершина: "+str2+". Вес ребра: "+str3;
			return str4;
		}
		int read_data_edge(){
			EdgeT* e;
			try{
				e=operator*();
			}
			catch(char* f){
				throw "Exeption#";
			}
			int a;
			if (e->isDataSet())
				return e->getData();
			else
				throw "#data not available";
		};
		bool write_data_edge(int d){
			EdgeT* e;
			try{
				e=operator*();
			}
			catch(char* Error){
				return false;
			}
			e->setData(d);
			return true;
		};
		bool write_weight_edge(int w){
			EdgeT* e;
			try{
				e=operator*();
			}
			catch(char* Error){
				return false;
			}
			e->setWeight(w);
			return true;
		}
    };
			
	//двухпроходной эйлеров цикл
    class Euler {
		int *color;			//цвет вершины 0-белый, 1-серый, 2-черный
		int *p;				//подграф предшествования		
		int **Adj;			//матрица смежных вершин
		int n;				//кол верш в графе		
		stack <int> st;
        Graph<VertexT, EdgeT> *graph; //Обрабатываемый граф 				
		
	public:		
		typename Graph<VertexT, EdgeT>::OutputEdgeIterator *out;

        Euler(Graph<VertexT, EdgeT> &g) {
            graph = &g;  			
        } 

		~Euler() {
			delete color,p,Adj;
		}
		bool DFS () {	
			if (graph->directed) {
				return false;
			}
			n=graph->V();			
			color=new int[n];			
			p=new int [n];			

			Adj = new int *[n];			
			for (int i=0; i<n; i++) {				
				Adj[i]=new int [n];			
			} 

			for (int u=0; u<n; u++) {
				color[u]=0;				
				p[u]=-1;				
			}
			search ();

			for (int u=0; u<n; u++) {
				if (color[u]==0) {
					DFS_Visit(u);
				}
			}		
			show();
			return true;
		}
		void DFS_Visit (int u) {			
			color[u]=1;				
			//------------------------------------------------			
			for (int i=0; i<n; i++) {
				if (Adj[u][i]==INT_MAX)  {
					break;
				}
				if (color[Adj[u][i]]==0) {						
					st.push(u);
					st.push(Adj[u][i]);
					//------------------------------------------------
					p[Adj[u][i]]=u;
					DFS_Visit(Adj[u][i]);
					//------------------------------------------------
					st.push(Adj[u][i]);////
					st.push(u);////
					//------------------------------------------------
				}
				else if (color[Adj[u][i]]==1) {
					if (p[u]!=Adj[u][i]) {						
						st.push(u);		
						st.push(Adj[u][i]);
						st.push(Adj[u][i]);
						st.push(u);
					}
				}
				color[u]=2;
			}			
		}
		void search () {			
			int vv1,vv2;
			for (int u=0; u<n; u++) {
				try {
					out=new Graph<VertexT, EdgeT>::OutputEdgeIterator(*graph,*graph->getVertex(u));
				}
				catch (char *) {
					continue;
				}
				if (!out->begin()) {
					continue;
				}

				for (int j=0; j<n; j++) {
					try {
						out->read_edge();
					}
					catch (char *) {
						Adj[u][j]=INT_MAX;
						break;
					}
					stringstream stream1(out->str1);
					stream1>>vv1;
					stringstream stream2(out->str2);
					stream2>>vv2;
					if (u==vv2) {
						Adj[u][j]=vv1;
					}
					else {
						Adj[u][j]=vv2;
					}				
					try {
						++*out;					
					}
					catch (char *) {					
						break;
					}									
				}	
			}
		}
		void show () {
			int *temp;
			temp=new int [st.size()];
			int st_size=st.size();
			for (int i=0; i<st_size; i++) {								
				temp[i]=st.top();				
				st.pop();
			}
			//-----------------------------------------------------
			//вывод попарно
			for (int i=(st_size-1); i>-1; i--) {								
				cout<<temp[i]<<"  ";				
			}
			//-----------------------------------------------------
			delete temp;			
		}
    };

	//алгоритм Беллмана - Форда
	class Bellman_ford {			
		int **mtr;

		struct edges {
			int a, b, cost;
		} edg;

		int n, m, v;
		vector<edges> e;
		int INF;
		
        Graph<VertexT, EdgeT> *graph; 				
	public:			
		typename Graph<VertexT, EdgeT>::OutputEdgeIterator *out;

        Bellman_ford(Graph<VertexT, EdgeT> &g) {
            graph = &g; 
			INF = 1000000000;
        }
		~Bellman_ford () {
			delete mtr;
		}
		
		void search () {
			int sz=graph->V();
			int vv1,vv2,w;
			for (int u=0; u<sz; u++) {
				try {
					out=new Graph<VertexT, EdgeT>::OutputEdgeIterator(*graph,*graph->getVertex(u));
				}
				catch (char *) {
					continue;
				}
				if (!out->begin()) {
					continue;
				}

				for (int j=0; j<sz; j++) {
					try {
						out->read_edge();
					}
					catch (char *) {						
						break;
					}
					stringstream stream1(out->str1);
					stream1>>vv1;
					stringstream stream2(out->str2);
					stream2>>vv2;
					stringstream stream3(out->str3);
					stream3>>w;
					if (u==vv2) {
						vv2=vv1;
					}
					
					edg.a=u;			//исходящая
					edg.b=vv2;			//входящая
					edg.cost=w;			//вес
					e.push_back(edg);

					try {
						++*out;					
					}
					catch (char *) {					
						break;
					}									
				}	
			}
		}
		
		string bellman_ford() {			
			search();
			n=m=graph->E();
						
			mtr = new int *[n];				
			for (int i=0; i<n; i++) {				
				mtr[i]=new int [n];				
				for (int j=0; j<n; j++) {
					mtr[i][j]=INF;					
				}				
			}

			for (int v=0; v<n; v++) {
				vector<int> d (n, INF);
				d[v] = 0;
				vector<int> p (n, -1);
				for (;;) {
					bool any = false;
					for (int j=0; j<m; ++j) {
						if (d[e[j].a] < INF) {
							if (d[e[j].b] > d[e[j].a] + e[j].cost) {
								d[e[j].b] = d[e[j].a] + e[j].cost;
								p[e[j].b] = e[j].a;
								any = true;
							}
						}
					}
					//-----------------------------------------------------
					int x;
					for (int i=0; i<n; ++i) {
						x = -1;
						for (int j=0; j<m; ++j)
							if (d[e[j].a] < INF)
								if (d[e[j].b] > d[e[j].a] + e[j].cost) {
									d[e[j].b] = max (-INF, d[e[j].a] + e[j].cost);
									p[e[j].b] = e[j].a;
									x = e[j].b;
								}
					}
					if (x!=-1) {
						return "отрицательный цикл";
					}
					//-----------------------------------------------------
					if (!any)  break;
				} 

				for (int h=0; h<n; h++) {
					mtr[v][h]=d[h];
				}				
			}
			return centr();
		}

		string centr () {
			int size=graph->V();
			int *temp;			//массив экстрентис.
			temp = new int [size];
			bool wr;
			for (int k=0; k<size; k++) {
				temp[k]=INF;
			}
			//поиск максимальных элементов
			for (int i=0; i<size; i++) {
				wr=false;
				for (int j=0; j<size; j++) {
					if ((mtr[i][j]==INF) || mtr[i][j]==0) {
						continue;
					}
					if (!wr) {
						temp[i]=mtr[i][j];
						wr=true;
					}
					else {						
						if (temp[i]<mtr[i][j]) {
							temp[i]=mtr[i][j];
						}						
					}
				}
			}
			//определение центра
			int result=0;
			int tmp=0;	
			tmp=temp[0];			
			for (int u=1; u<size; u++) {
				if (tmp>temp[u]) {
					tmp=temp[u];
					result=u;				
				}
			}	
			if (tmp==INF || tmp==0) {
				return "Центр не найден";
			}

			//-------------------------------------------------------------------------
			//показать матрицу
			cout<<endl<<"=============================================="<<endl<<"    ";
			for (int i=0; i<size; i++) {
				cout<<i<<"  ";
			}
			cout<<endl<<"-----------------------------------------------"<<endl;
			for (int i=0; i<size; i++) {
				cout<<i<<" | ";
				for (int y=0; y<size; y++) {
					if (mtr[i][y]==INF) {
						cout<<"-"<<"  ";
					}
					else {
						cout<<mtr[i][y]<<"  ";
					}
				}
				if ((temp[i]==INF) || (temp[i]==0)) {
					cout<<"| -"<<endl;
					continue;
				}
				else {
					cout<<"| "<<temp[i]<<endl;				
				}
			}
			delete temp;
			//-----------------------------------------------------------------------
			stringstream str;
			string result0;
			str << result;
			str >> result0;	
			return "Цент графа, узел: "+result0;		
		}
					
	};
	
};
