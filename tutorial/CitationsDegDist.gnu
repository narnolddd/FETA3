file = "tutorial/CitationsTSDeg.dat"
fileIn = "tutorial/CitationsTSDirectedInDeg.dat"
fileOut = "tutorial/CitationsTSDirectedOutDeg.dat"

set term postscript eps enhanced color 24
set font ",20"

set output "tutorial/plots/CitationsTSDeg.eps"
set log xy
set title "Degree Distribution of Citation Network"
set xlabel "Degree"
set ylabel "Frequency"
plot file matrix every :::129::129 pt 7 title 'Total Degree', fileIn matrix every :::129::129 pt 7 title 'In-Degree', \
fileOut matrix every :::129::129 pt 7 title 'Out-Degree'