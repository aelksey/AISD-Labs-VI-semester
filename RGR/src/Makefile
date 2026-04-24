CXX = g++
CXXFLAGS = -std=c++11 -Wall -Wextra -O2
GRAPHSRC = $(wildcard *.cpp)
HEADERS = $(wildcard *.h)

all: graph_tests graph_gui

graph_tests: Menu.cpp Graph.h
	$(CXX) $(CXXFLAGS) -o graph_tests Menu.cpp

graph_gui: graphviz_gui.cpp Graph.h
	$(CXX) $(CXXFLAGS) -o graph_gui graphviz_gui.cpp -lgraphviz `pkg-config --cflags --libs gtk+-2.0` 2>/dev/null || \
	$(CXX) $(CXXFLAGS) -o graph_gui graphviz_gui.cpp -lgvc -lgraphviz

graphviz_gui: graphviz_gui.cpp
	$(CXX) $(CXXFLAGS) -o graph_gui graphviz_gui.cpp -lgvc -lgraphviz

run: graph_tests
	./graph_tests

run-gui: graph_gui
	./graph_gui

test: graph_tests
	./graph_tests

clean:
	rm -f graph_tests graph_gui *.o

.PHONY: all run run-gui test clean