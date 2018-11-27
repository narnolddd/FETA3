file = "tutorial/ArtificialTSDeg.dat"

set term postscript eps enhanced color 24
set font ",20"

set output "tutorial/plots/ArtificialTSDeg.eps"
set log xy
set title "Degree Distribution of Artificial Network"
set xlabel "Degree"
set ylabel "Frequency"
plot file matrix every :::299::299 pt 7 title 'Degree'