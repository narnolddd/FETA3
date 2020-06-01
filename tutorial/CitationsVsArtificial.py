import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib as mpl
import datetime as dt

fig, ax = plt.subplots(nrows=2, ncols=4, figsize=(15,5))

# Get time axes set up
x_major_lct = mpl.dates.AutoDateLocator(minticks=2,maxticks=10, interval_multiples=True)
x_fmt = mpl.dates.AutoDateFormatter(x_major_lct)

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
files = ["tutorial/CitationsTS.dat", "tutorial/CitationsTSArtificial.dat"]
labels = ["Real Data", "Model"]

for i in range(2):
    # Read stats from datafile
    with open(files[i],'r') as f:
        f.readline()
        rawdata = f.read().splitlines()
        times = [dt.datetime.fromtimestamp(int(l.split()[0])) for l in rawdata]
        times = times[:39]
        matrix = np.array([[int(row.split()[0])]+[float(num) for num in row.split()[1:]] for row in rawdata])
        matrix = matrix[:39]
        df = pd.DataFrame(matrix)
        f.close()

    df.columns = ['timestamp', 'nodes', 'links', 'avgdeg', 'maxdeg', 'singletons', 'doubletons', 'meandegsq',
                  'assortativity', 'clustercoeff', 'triangles']

    if i == 1:
        start, end = df.iloc[0]['timestamp'], df.iloc[-1]['timestamp']

    ax[0,0].plot(times, df['avgdeg'])
    ax[0,1].plot(times, df['maxdeg'], label = labels[i])
    ax[0,2].plot(times, df['clustercoeff'], label = labels[i])
    ax[0,3].plot(times, df['meandegsq'], label = labels[i])
    ax[1,0].plot(times, df['assortativity'], label = labels[i])
    ax[1,1].plot(times, df['singletons'], label = labels[i])
    ax[1,2].plot(times, df['doubletons'], label = labels[i])
    ax[1,3].plot(times, df['triangles'], label = labels[i])

for row in range(2):
    for col in range(4):
        ax[row,col].xaxis.set_major_locator(x_major_lct)
        ax[row,col].xaxis.set_major_formatter(x_fmt)
        for label in ax[row,col].get_xmajorticklabels():
            label.set_rotation(30)
        for label in ax[row,col].get_ymajorticklabels():
            label.set_rotation(30)

plt.legend(loc = 'upper left')
plt.tight_layout()
fig.savefig("tutorial/plots/CitationsVsArtificial.png")
plt.show()