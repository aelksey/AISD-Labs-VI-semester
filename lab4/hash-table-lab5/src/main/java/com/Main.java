package com;
/*
 *
 * Главный класс для запуска приложения
 * Практическая работа №5: Хеш-таблица с открытой адресацией
 * Вариант №6
 *
 * @author Student
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        // Запуск GUI приложения в потоке обработки событий
        javax.swing.SwingUtilities.invokeLater(() -> {
            HashTableGUI gui = new HashTableGUI();
            gui.setVisible(true);
        });
    }
}