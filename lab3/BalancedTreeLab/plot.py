#!/usr/bin/env python3
import csv
import matplotlib.pyplot as plt
import numpy as np

def read_csv_data(filename):
    """Simple CSV parser without pandas dependency"""
    data = {'Insert': {'Random': [], 'Degenerate': []},
            'Search': {'Random': [], 'Degenerate': []},
            'Delete': {'Random': [], 'Degenerate': []}}
    sizes = []
    
    try:
        with open(filename, 'r') as f:
            reader = csv.DictReader(f)
            for row in reader:
                op = row['Operation']
                tree_type = row['Tree Type']
                size = int(row['Size'])
                time_us = float(row['Time (microseconds)'])
                
                if size not in sizes:
                    sizes.append(size)
                
                data[op][tree_type].append((size, time_us))
        
        sizes.sort()
        return data, sizes
    except Exception as e:
        print(f"Error reading CSV: {e}")
        return None, None

def plot_complexity(data, sizes):
    """Create simple plots"""
    
    if not data:
        print("No data to plot")
        return
    
    # Create figure
    fig, axes = plt.subplots(2, 2, figsize=(12, 10))
    fig.suptitle('2-3 Tree Complexity Analysis', fontsize=16, fontweight='bold')
    
    # Plot 1: Insert operations
    ax = axes[0, 0]
    for tree_type in ['Random', 'Degenerate']:
        points = data['Insert'][tree_type]
        if points:
            x = [p[0] for p in points]
            y = [p[1] for p in points]
            ax.plot(x, y, 'o-', linewidth=2, markersize=8, label=f'{tree_type} Tree')
    ax.set_xlabel('Number of Elements')
    ax.set_ylabel('Time (microseconds)')
    ax.set_title('Insert Operation')
    ax.grid(True, alpha=0.3)
    ax.legend()
    
    # Plot 2: Search operations
    ax = axes[0, 1]
    for tree_type in ['Random', 'Degenerate']:
        points = data['Search'][tree_type]
        if points:
            x = [p[0] for p in points]
            y = [p[1] for p in points]
            ax.plot(x, y, 'o-', linewidth=2, markersize=8, label=f'{tree_type} Tree')
    ax.set_xlabel('Number of Elements')
    ax.set_ylabel('Time (microseconds)')
    ax.set_title('Search Operation')
    ax.grid(True, alpha=0.3)
    ax.legend()
    
    # Plot 3: Delete operations
    ax = axes[1, 0]
    for tree_type in ['Random', 'Degenerate']:
        points = data['Delete'][tree_type]
        if points:
            x = [p[0] for p in points]
            y = [p[1] for p in points]
            ax.plot(x, y, 'o-', linewidth=2, markersize=8, label=f'{tree_type} Tree')
    ax.set_xlabel('Number of Elements')
    ax.set_ylabel('Time (microseconds)')
    ax.set_title('Delete Operation')
    ax.grid(True, alpha=0.3)
    ax.legend()
    
    # Plot 4: Log-log comparison
    ax = axes[1, 1]
    colors = {'Random': 'blue', 'Degenerate': 'red'}
    for tree_type in ['Random', 'Degenerate']:
        points = data['Insert'][tree_type]
        if points:
            x = [p[0] for p in points]
            y = [p[1] for p in points]
            ax.loglog(x, y, 'o-', linewidth=2, markersize=8, 
                     label=f'{tree_type} Tree', color=colors[tree_type])
    
    # Add theoretical lines
    if sizes:
        x_theory = np.array([sizes[0], sizes[-1]])
        # O(n) line
        if data['Insert']['Degenerate']:
            ref_point = data['Insert']['Degenerate'][0]
            slope = ref_point[1] / ref_point[0]
            y_linear = slope * x_theory
            ax.loglog(x_theory, y_linear, 'g--', linewidth=2, label='O(n) Theoretical')
        
        # O(log n) line
        if data['Insert']['Random']:
            ref_point = data['Insert']['Random'][0]
            slope = ref_point[1] / np.log2(ref_point[0])
            y_log = slope * np.log2(x_theory)
            ax.loglog(x_theory, y_log, 'm--', linewidth=2, label='O(log n) Theoretical')
    
    ax.set_xlabel('Number of Elements (log scale)')
    ax.set_ylabel('Time (microseconds) (log scale)')
    ax.set_title('Complexity Comparison (Log-Log)')
    ax.grid(True, alpha=0.3, which='both')
    ax.legend()
    
    plt.tight_layout()
    plt.savefig('tree23_complexity_plot.png', dpi=300, bbox_inches='tight')
    print("✓ Plot saved as: tree23_complexity_plot.png")
    plt.show()

def main():
    print("2-3 Tree Complexity Plotter")
    print("="*40)
    
    # Read data
    data, sizes = read_csv_data('tree23_complexity_results.csv')
    
    if data and sizes:
        print(f"✓ Loaded data for sizes: {sizes}")
        plot_complexity(data, sizes)
    else:
        print("Failed to load data. Please run Tree23ComplexityAnalyzer first.")

if __name__ == "__main__":
    main()