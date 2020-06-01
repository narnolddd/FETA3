import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib as mpl
import datetime as dt

fig, ax = plt.subplots(nrows=2, ncols=4, figsize=(15,5))

# Get time axes set up

plt.xlabel("Timestamp")
ax[0,0].set_title("Average Degree")
ax[0,1].set_title('Maximum Degree')
ax[0,2].set_title('Average Clustering Coefficient')
ax[0,3].set_title('Mean squared degree')
ax[1,0].set_title('Degree Assortativity')
ax[1,1].set_title('Singleton Nodes')
ax[1,2].set_title('Doubleton Nodes')
ax[1,3].set_title('Number of Triangles')

# File with all the time series of measurements
file = "tutorial/ArtificialTS.dat"

# Read stats from datafile
with open(file,'r') as f:
    f.readline()
    rawdata = f.read().splitlines()
    times = [int(l.split()[0]) for l in rawdata]
    matrix = np.array([[int(row.split()[0])]+[float(num) for num in row.split()[1:]] for row in rawdata])
    df = pd.DataFrame(matrix)
    f.close()

df.columns = ['timestamp', 'nodes', 'links', 'avgdeg', 'maxdeg', 'singletons', 'doubletons', 'meandegsq',
              'assortativity', 'clustercoeff', 'triangles']

ax[0,0].plot(times, df['avgdeg'])
ax[0,1].plot(times, df['maxdeg'])
ax[0,2].plot(times, df['clustercoeff'])
ax[0,3].plot(times, df['meandegsq'])
ax[1,0].plot(times, df['assortativity'])
ax[1,1].plot(times, df['singletons'])
ax[1,2].plot(times, df['doubletons'])
ax[1,3].plot(times, df['triangles'])

for row in range(2):
    for col in range(4):
        for label in ax[row,col].get_xmajorticklabels():
            label.set_rotation(30)
        for label in ax[row,col].get_ymajorticklabels():
            label.set_rotation(30)

plt.tight_layout()
fig.savefig("tutorial/plots/ArtificialTS.png")
plt.show()