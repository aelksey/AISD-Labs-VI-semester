===========================================================
     BST Operations Complexity Benchmark
===========================================================

Benchmark Configuration:
  Start size: 0
  Max size: 5000
  Step: 500
  Iterations per size: 10

Running benchmarks...

=== RANDOM BST ===

Size      Insert(μs)    Search(μs)    Remove(μs)    Height      Balanced    log2(n)     n           
-------------------------------------------------------------------------------------------------------
Testing size: 0 ... 0         0.00           0.00           0.00           0.00        Yes         0.00        0.00        
Testing size: 500 ... 500       42.80          20.20          910.10         20.00       No          12.46       500.00      
Testing size: 1000 ... 1000      93.70          47.60          3301.30        23.00       No          13.85       1000.00     
Testing size: 1500 ... 1500      142.90         76.70          7297.60        23.00       No          14.67       1500.00     
Testing size: 2000 ... 2000      202.30         115.90         13015.20       25.00       No          15.24       2000.00     
Testing size: 2500 ... 2500      267.40         150.40         24606.70       24.00       No          15.69       2500.00     
Testing size: 3000 ... 3000      336.90         191.80         32418.00       27.00       No          16.06       3000.00     
Testing size: 3500 ... 3500      407.40         242.40         68336.90       27.00       No          16.36       3500.00     
Testing size: 4000 ... 4000      480.00         285.10         68157.70       28.00       No          16.63       4000.00     
Testing size: 4500 ... 4500      563.70         341.20         104868.30      28.00       No          16.87       4500.00     
Testing size: 5000 ... 5000      650.20         416.70         135290.20      29.00       No          17.08       5000.00     

=== DEGENERATE BST ===

Size      Insert(μs)    Search(μs)    Remove(μs)    Height      Balanced    log2(n)     n           
-------------------------------------------------------------------------------------------------------
Testing size: 0 ... 0         0.00           0.00           0.00           0.00        Yes         0.00        0.00        
Testing size: 500 ... 500       406.90         377.90         1121.50        500.00      No          12.46       500.00      
Testing size: 1000 ... 1000      1607.20        1515.50        4241.90        1000.00     No          13.85       1000.00     
Testing size: 1500 ... 1500      3654.60        3471.00        9374.30        1500.00     No          14.67       1500.00     
Testing size: 2000 ... 2000      6512.60        6186.40        16350.00       2000.00     No          15.24       2000.00     
Testing size: 2500 ... 2500      10171.10       9657.90        25306.60       2500.00     No          15.69       2500.00     
Testing size: 3000 ... 3000      14636.00       13898.20       36153.60       3000.00     No          16.06       3000.00     
Testing size: 3500 ... 3500      19903.80       18899.50       48862.90       3500.00     No          16.36       3500.00     
Testing size: 4000 ... 4000      25968.60       24708.00       63383.30       4000.00     No          16.63       4000.00     
Testing size: 4500 ... 4500      32824.70       31220.80       80425.80       4500.00     No          16.87       4500.00     
Testing size: 5000 ... 5000      40483.70       38495.90       99377.40       5000.00     No          17.08       5000.00     

Results saved to: ../benchmark/benchmark_random.csv

Results saved to: ../benchmark/benchmark_degenerate.csv

=== Time Complexity Visualization (ASCII Chart) ===

Size    Insert  Search  Remove
----    ------  ------  ------
0        (0.00μs)
         (0.00μs)
         (0.00μs)

500      (42.80μs)
         (20.20μs)
         (910.10μs)

1000     (93.70μs)
         (47.60μs)
        # (3301.30μs)

1500     (142.90μs)
         (76.70μs)
        ## (7297.60μs)

2000     (202.30μs)
         (115.90μs)
        #### (13015.20μs)

2500     (267.40μs)
         (150.40μs)
        ######### (24606.70μs)

3000     (336.90μs)
         (191.80μs)
        ########### (32418.00μs)

3500     (407.40μs)
         (242.40μs)
        ######################### (68336.90μs)

4000     (480.00μs)
         (285.10μs)
        ######################### (68157.70μs)

4500     (563.70μs)
         (341.20μs)
        ###################################### (104868.30μs)

5000     (650.20μs)
         (416.70μs)
        ################################################## (135290.20μs)


=== Time Complexity Visualization (ASCII Chart) ===

Size    Insert  Search  Remove
----    ------  ------  ------
0        (0.00μs)
         (0.00μs)
         (0.00μs)

500      (406.90μs)
         (377.90μs)
         (1121.50μs)

1000     (1607.20μs)
         (1515.50μs)
        ## (4241.90μs)

1500    # (3654.60μs)
        # (3471.00μs)
        #### (9374.30μs)

2000    ### (6512.60μs)
        ### (6186.40μs)
        ######## (16350.00μs)

2500    ##### (10171.10μs)
        #### (9657.90μs)
        ############ (25306.60μs)

3000    ####### (14636.00μs)
        ###### (13898.20μs)
        ################## (36153.60μs)

3500    ########## (19903.80μs)
        ######### (18899.50μs)
        ######################## (48862.90μs)

4000    ############# (25968.60μs)
        ############ (24708.00μs)
        ############################### (63383.30μs)

4500    ################ (32824.70μs)
        ############### (31220.80μs)
        ######################################## (80425.80μs)

5000    #################### (40483.70μs)
        ################### (38495.90μs)
        ################################################## (99377.40μs)


=== SUMMARY ===

Random BST (size=5000):
  Insert time: 650.20 μs
  Search time: 416.70 μs
  Remove time: 135290.20 μs
  Height: 29.00
  Theoretical O(log n): 17.08

Degenerate BST (size=5000):
  Insert time: 40483.70 μs
  Search time: 38495.90 μs
  Remove time: 99377.40 μs
  Height: 5000.00
  Theoretical O(n): 5000.00

=== COMPLEXITY ANALYSIS ===

Degenerate tree is 92.38x slower than random tree for search operations.
This matches theoretical expectation: O(n) vs O(log n)

Benchmark completed successfully!

## Обьяснение

**A degenerate BST is slower than a random BST.**  

### What each tree looks like  
- **Random (balanced‑ish) BST**: nodes are roughly evenly spread, so the tree is “bushy” and the height is about \(O(\log n)\). [webdocs.cs.ualberta](https://webdocs.cs.ualberta.ca/~holte/T26/pb-with-bst.html)
- **Degenerate BST**: it turns into a long chain (like a linked list), with height \(O(n)\). [inc.ucsd](https://inc.ucsd.edu/mplab/users/jake/CSE12_2011/Lectures/L13.pdf)

### Why degenerate is slower  
- **Search / insert / delete** all take time proportional to the **height** of the tree. [scribd](https://www.scribd.com/document/984819346/BST-2)
- In a **random BST** height ≈ \(O(\log n)\), so each operation is about \(O(\log n)\). [webdocs.cs.ualberta](https://webdocs.cs.ualberta.ca/~holte/T26/pb-with-bst.html)
- In a **degenerate BST** height ≈ \(O(n)\), so each operation drops to \(O(n)\) (same as a linked list). [inc.ucsd](https://inc.ucsd.edu/mplab/users/jake/CSE12_2011/Lectures/L13.pdf)

In short: a degenerate BST is slower because it becomes a straight line, and you may have to walk through almost all nodes just to find or insert one element.

## РУС

**Вырожденное BST медленнее, чем случайное («рандомное») BST.**

### Какие это деревья  
- **Случайное (сбалансированное) BST**: узлы примерно равномерно распределены, дерево «кустистое», высота около \(O(\log n)\).  
- **Вырожденное BST**: дерево превращается в длинную цепочку, как связный список, высота становится \(O(n)\).  

### Почему вырожденное медленнее  
- **Поиск, вставка, удаление** работают за время, пропорциональное **высоте дерева**.  
- В **случайном BST** высота ≈ \(O(\log n)\), поэтому операции работают примерно за \(O(\log n)\).  
- В **вырожденном BST** высота ≈ \(O(n)\), поэтому операции падают до \(O(n)\) (то есть почти как обычный список).  

Проще говоря: вырожденное BST — это почти прямая линия, и чтобы найти или вставить элемент, тебе может понадобиться пройтись почти по всем узлам.