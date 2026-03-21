![alt text](list.png)

## Справка

current - элемент на который указывает итератор
next - следующий элемент
prev - предыдущий элемент
temp - новый элемент

## Задание 1 

```
iter1 : pos=2, elem=5
```

1) del by val 5

```
prev->next = current->next
current->next = nullptr
iter1 : pos=2, elem=4
```

2) insert 10 by index 3

```
temp->next = current
prev->next = temp
iter1 : pos=2, elem=4
```

## Задание 2

```
iter2 : pos=3, elem=12
```

1) insert 9 by index 3

```
temp->next = current
prev->next = temp
iter2 : pos=4, elem=12
```

2) del by index 4

```
prev->next = head;
next = nullptr; 
iter2 : not set
```