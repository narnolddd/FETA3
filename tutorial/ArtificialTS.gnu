set term postscript eps enhanced color 24
unset key
set font ",20"

set xrange [0:3000]
set xlabel 'Timestamp'
set font ",20"

file = "tutorial/ArtificialTS.dat"

# Network density
set output "tutorial/plots/artificial_dense.eps"
set yrange [0:0.2]
set title 'Density'
set ylabel 'NoLinks/NoPossibleLinks'
plot file using 1:5 with lines lw 3

# Maximum degree
set output "tutorial/plots/artificial_maxdeg.eps"
set yrange [0:40]
set title 'Maximum degree'
set ylabel 'kmax'
plot file using 1:6 with lines lw 3

# Clustering coeff
set output "tutorial/plots/artificial_cluster.eps"
set yrange [0:0.5]
set title 'Average clustering coefficient'
set ylabel 'C'
plot file using 1:7 with lines lw 3

# Mean squared degree
set output "tutorial/plots/artificial_meandegsq.eps"
set yrange [0:100]
set title 'Mean squared degree'
set ylabel '<k^2>'
plot file using 1:8 with lines lw 3

# Degree assortativity
set output "tutorial/plots/artificial_assort.eps"
set yrange [-0.5:0.5]
set title 'Degree Assortativity'
set ylabel 'r'
plot file using 1:9 with lines lw 3

# Average degree
set output "tutorial/plots/artificial_avgdeg.eps"
set yrange [0:8]
set title 'Average Degree'
set ylabel '<k>'
plot file using 1:4 with lines lw 3
