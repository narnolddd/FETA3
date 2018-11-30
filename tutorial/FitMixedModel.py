import os

likeFile = "tutorial/MixedModelLike.json"
inputNet = "data/cit-HepPh-ordered.txt"

tmp1 = "tutorial/MixedModelLikeTMP1.json"
fin = "tutorial/CitationsLike.txt"

# Models

rand = "RandomAttachment"
deg = "DegreeModelComponent"
degAge = "DegreeWithAgeing"
rankPref = "RankPreferentialAttachment"

def fitModel(model1, model2, input, intervals):
    os.system('rm '+fin)
    with open(likeFile,'r') as f:
        jsonfile = f.read()
    f.close()

    jsonfile = jsonfile.replace("MODEL1",str(model1))
    jsonfile = jsonfile.replace("MODEL2",str(model2))
    jsonfile = jsonfile.replace("INPUTFILE",str(input))

    likeVals = []

    for i in range(intervals+1):
        jsontmp = jsonfile
        a = float(i) / intervals
        print(a)
        b = 1-a
        jsontmp = jsontmp.replace("AAA",str(a))
        jsontmp = jsontmp.replace("BBB",str(b))
        with open(tmp1,'w') as f:
            f.write(jsontmp)
            f.close()
        os.system('java -jar feta3-1.0.0.jar '+tmp1+' >> '+fin)


fitModel(rankPref,deg,inputNet,10)