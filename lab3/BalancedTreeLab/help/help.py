"""
Итератор для 2-3 дерева (сбалансированное дерево поиска, где у узла 2 или 3 потомка) должен обходить его в симметричном (инфиксном) порядке (in-order traversal), так как это дерево является частным случаем B-дерева и хранит ключи в отсортированном порядке внутри узлов.
Основные правила обхода 2-3 дерева

Узел 2-3 дерева может содержать:

    2-узел: один ключ K1, два потомка: левый (ключи < K1) и правый (> K1)

    3-узел: два ключа K1 и K2 (K1 < K2), три потомка: левый (< K1), средний (между K1 и K2), правый (> K2)

Инфиксный обход (обход ключей по возрастанию):

    Для 2-узла:

        Обойти левого потомка

        Выдать K1

        Обойти правого потомка

    Для 3-узла:

        Обойти левого потомка

        Выдать K1

        Обойти среднего потомка

        Выдать K2

        Обойти правого потомка
"""

"""
        [10, 20]
       /   |    \
   [5]   [15]   [25, 30]

Обход:
5 → 10 → 15 → 20 → 25 → 30
Итератор может хранить стек узлов с позициями (какой ключ в узле следующий).
"""

class TwoThreeTreeIterator:
    def __init__(self, root):
        self.stack = []
        self._push_left(root)

    def _push_left(self, node):
        while node:
            self.stack.append((node, 0))  # 0 = еще не отдавали ключи
            if node.is_two_node():
                node = node.left
            else:  # 3-node
                node = node.left

    def __next__(self):
        while self.stack:
            node, state = self.stack.pop()
            
            if node.is_two_node():
                if state == 0:
                    # вернуть ключ K1, затем обработать правого потомка
                    self.stack.append((node, 1))
                    self._push_left(node.right)
                    return node.key1
                else:
                    continue  # узел закончен
                    
            else:  # three node
                if state == 0:
                    self.stack.append((node, 1))
                    self._push_left(node.mid)
                    return node.key1
                elif state == 1:
                    self.stack.append((node, 2))
                    self._push_left(node.right)
                    return node.key2
                else:
                    continue
        raise StopIteration