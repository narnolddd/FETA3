set term postscript eps enhanced color 24
unset key
set font ",20"
file = "tutorial_scripts/CitationsTSDirected.dat"

set xrange [0:13000]
set xlabel 'Timestamp'
set font ",20"

# Maximum degrees

set output "tutorial_scripts/plots/cit_maxdeg_in.eps"
set yrange [0:200]
set title 'Maximum in-degree'
set ylabel 'kmax'
plot file using 1:6 with lines lw 3

set output "tutorial_scripts/plots/cit_maxdeg_out.eps"
set yrange [0:200]
set title 'Maximum out-degree'
set ylabel 'kmax'
plot file using 1:7 with lines lw 3

# Mean squared degrees

set output "tutorial_scripts/plots/cit_meandegsq_in.eps"
set yrange [0:330]
set title 'Mean squared in-degree'
set ylabel '<k_in^2>'
plot file using 1:8 with lines lw 3

set output "tutorial_scripts/plots/cit_meandegsq_out.eps"
set yrange [0:330]
set title 'Mean squared out-degree'
set ylabel '<k_out^2>'
plot file using 1:9 with lines lw 3

# Degree assortativity

set output "tutorial_scripts/plots/cit_assort_inin.eps"
set yrange [-0.5:0.5]
set title 'Degree Assortativity In-In'
set ylabel 'r'
plot file using 1:10 with lines lw 3

set output "tutorial_scripts/plots/cit_assort_inout.eps"
set yrange [-0.5:0.5]
set title 'Degree Assortativity In-Out'
set ylabel 'r'
plot file using 1:11 with lines lw 3

set output "tutorial_scripts/plots/cit_assort_outin.eps"
set yrange [-0.5:0.5]
set title 'Degree Assortativity Out-In'
set ylabel 'r'
plot file using 1:12 with lines lw 3

set output "tutorial_scripts/plots/cit_assort_outout.eps"
set yrange [-0.5:0.5]
set title 'Degree Assortativity Out-Out'
set ylabel 'r'
plot file using 1:13 with lines lw 3

# Average in/outdegree

set output "tutorial_scripts/plots/cit_avgdeg_inout.eps"
set yrange [0:12]
set title 'Average In/Out Degree'
set ylabel '<k>'
plot file using 1:4 with lines lw 3