Запускать из ./RGR

# 1. Скомпилировать

javac -d out src/main/java/com/rgr/model/*.java src/main/java/com/rgr/tasks/*.java src/main/java/com/rgr/ui/*.java

# 2. Запустить

java -cp out com.rgr.ui.Main