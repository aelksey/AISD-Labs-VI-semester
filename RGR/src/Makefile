CXX = g++
CXXFLAGS = -std=c++11 -Wall -Wextra -O2

all: tests graph_gui

tests: tests.cpp Graph.h
	$(CXX) $(CXXFLAGS) -o tests tests.cpp

graph_gui: graphviz_gui.cpp Graph.h
	$(CXX) $(CXXFLAGS) -o graph_gui graphviz_gui.cpp

run: tests
	./tests

run-gui: graph_gui
	./graph_gui

test: tests
	./tests

clean:
	rm -f tests graph_gui *.o *.dot *.png

.PHONY: all run run-gui test clean