#include <iostream>
#include <string>
#include <limits>
#include "circular_list.h"

using namespace std;

template <typename T>
void showMenu(CircularList<T>& list) {
    int choice;
    T value;
    int pos;
    auto iter = list.end();

    do {
        cout << "\n========== МЕНЮ ОПЕРАЦИЙ СО СПИСКОМ ==========" << endl;
        cout << "1. Вывести список на экран" << endl;
        cout << "2. Получить размер списка" << endl;
        cout << "3. Проверить, пуст ли список" << endl;
        cout << "4. Добавить элемент в конец (push_back)" << endl;
        cout << "5. Вставить элемент по позиции (insert)" << endl;
        cout << "6. Прочитать элемент по позиции (at)" << endl;
        cout << "7. Изменить элемент по позиции (set)" << endl;
        cout << "8. Найти позицию элемента по значению (indexOf)" << endl;
        cout << "9. Проверить наличие значения (contains)" << endl;
        cout << "10. Удалить элемент по значению (remove_value)" << endl;
        cout << "11. Удалить элемент по позиции (remove_at)" << endl;
        cout << "12. Очистить список (clear)" << endl;
        cout << "14. Получить итератор(*)" << endl;
        cout << "15. Установить итератор" << endl;
        cout << "16. Сдвинуть итератор(++)" << endl;
        cout << "17. Установить итератор в начало(begin)" << endl;
        cout << "18. Установить итератор в конец(end)" << endl;
        cout << "0. Выход" << endl;
        cout << "================================================" << endl;
        cout << "Ваш выбор: ";

        while (!(cin >> choice)) {
            cin.clear();
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            cout << "Неверный ввод. Пожалуйста, введите число: ";
        }

        try {
            

            switch (choice) {
                case 1:
                    list.print();
                    break;
                case 2:
                    cout << "Размер списка: " << list.size() << endl;
                    break;
                case 3:
                    cout << (list.empty() ? "Список пуст." : "Список не пуст.") << endl;
                    break;
                case 4:
                    cout << "Введите значение для добавления в конец: ";
                    cin >> value;
                    list.push_back(value);
                    cout << "Элемент добавлен." << endl;
                    list.print();
                    break;
                case 5:
                    cout << "Введите позицию для вставки (начиная с 0): ";
                    cin >> pos;
                    cout << "Введите значение: ";
                    cin >> value;
                    list.insert(pos, value);
                    cout << "Элемент вставлен." << endl;
                    list.print();
                    break;
                case 6:
                    cout << "Введите позицию для чтения: ";
                    cin >> pos;
                    cout << "Значение на позиции " << pos << ": " << list.at(pos) << endl;
                    break;
                case 7:
                    cout << "Введите позицию для изменения: ";
                    cin >> pos;
                    cout << "Введите новое значение: ";
                    cin >> value;
                    list.set(pos, value);
                    cout << "Значение изменено." << endl;
                    list.print();
                    break;
                case 8:
                    cout << "Введите значение для поиска: ";
                    cin >> value;
                    pos = list.indexOf(value);
                    if (pos != -1)
                        cout << "Первое вхождение значения на позиции: " << pos << endl;
                    else
                        cout << "Значение не найдено." << endl;
                    break;
                case 9:
                    cout << "Введите значение для проверки: ";
                    cin >> value;
                    cout << (list.contains(value) ? "Значение найдено." : "Значение не найдено.") << endl;
                    break;
                case 10:
                    cout << "Введите значение для удаления: ";
                    cin >> value;
                    if (list.remove_value(value)){
                        cout << "Значение удалено." << endl;
                    }else
                        cout << "Значение не найдено." << endl;
                    
                    list.print();
                    break;
                case 11:
                    cout << "Введите позицию для удаления: ";
                    cin >> pos;
                    if (list.remove_at(pos)){
                        cout << "Элемент удален." << endl;
                    }else
                        cout << "Неверная позиция." << endl;
                    list.print();
                    break;
                case 12:
                    list.clear();
                    cout << "Список очищен." << endl;
                    break;
                case 14: {
                    cout << "Текущее значение: " << *iter << endl;
                    break;
                }
                case 15:{
                    cout << "Текущее значение: " << *iter << endl;
                    cout << "Новое значение: ";
                    cin >> value;
                    *iter = value;
                    break;
                }
                case 16:{
                    ++iter;
                    cout << "Текущее значение: " << *iter << endl;
                    break;
                }
                case 17:{
                    iter = list.begin();
                    break;
                }
                case 18:{
                    iter = list.end();
                    break;
                }
                

                case 0:
                    cout << "Выход из программы." << endl;
                    break;
                default:
                    cout << "Неверный выбор. Попробуйте снова." << endl;
            }
        } catch (const std::exception& e) {
            cerr << "Ошибка: " << e.what() << endl;
        }

    } while (choice != 0);
}

int main() {
    cout << "Демонстрация работы кольцевого односвязного списка." << endl;
    cout << "Создан пустой список для целых чисел." << endl;

    CircularList<int> myList;
    showMenu(myList);

    return 0;
}


// Установить итератор в элемент (по позиции)
// Удалить элемент на котором находится итератор(выбросить исключение)
// Сдвинуть итератор (++ --)