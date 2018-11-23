set term postscript eps enhanced color 24
unset key
set font ",20"

set xrange [0:13000]
set xlabel 'Timestamp'
set font ",20"

# Network density
set output "tutorial/plots/cit_dense.eps"
set yrange [0:0.2]
set title 'Density'
set ylabel 'NoLinks/NoPossibleLinks'
plot "tutorial/CitationsTS.dat" using 1:5 with lines lw 3

# Maximum degree
set output "tutorial/plots/cit_maxdeg.eps"
set yrange [0:200]
set title 'Maximum degree'
set ylabel 'kmax'
plot "tutorial/CitationsTS.dat" using 1:6 with lines lw 3

# Clusterinf coeff
set output "tutorial/plots/cit_cluster.eps"
set yrange [0:0.3]
set title 'Average clustering coefficient'
set ylabel 'C'
plot "tutorial/CitationsTS.dat" using 1:7 with lines lw 3

# Mean squared degree
set output "tutorial/plots/cit_meandegsq.eps"
set yrange [0:330]
set title 'Mean squared degree'
set ylabel '<k^2>'
plot "tutorial/CitationsTS.dat" using 1:8 with lines lw 3

# Degree assortativity
set output "tutorial/plots/cit_assort.eps"
set yrange [-0.2:0.2]
set title 'Degree Assortativity'
set ylabel 'r'
plot "tutorial/CitationsTS.dat" using 1:9 with lines lw 3

# Average degree
set output "tutorial/plots/cit_avgdeg.eps"
set yrange [0:12]
set title 'Average Degree'
set ylabel '<k>'
plot "tutorial/CitationsTS.dat" using 1:4 with lines lw 3
