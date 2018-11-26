import os

likeFile = "experiments/MixedModelLike.json"
inputNet = "data/cit-HepPh-ordered.txt"

tmp1 = "experiments/MixedModelLikeTMP1.json"
fin = "experiments/CitationsLike.txt"

# Models

rand = "RandomAttachment"
deg = "DegreeModelComponent"
degAge = "DegreeWithAgeing"
rankPref = "RankPreferentialAttachment"

def fitModel(model1, model2, input):
    with open(likeFile,'r') as f:
        jsonfile = f.read()
    f.close()

    jsonfile = jsonfile.replace("MODEL1",str(model1))
    jsonfile = jsonfile.replace("MODEL2",str(model2))
    jsonfile = jsonfile.replace("INPUTFILE",str(input))

    likeVals = []

    for i in range(101):
        jsontmp = jsonfile
        a = float(i) / 100
        print(a)
        b = 1-a
        jsontmp = jsontmp.replace("AAA",str(a))
        jsontmp = jsontmp.replace("BBB",str(b))
        with open(tmp1,'w') as f:
            f.write(jsontmp)
            f.close()
        like = os.system('java -jar feta3-1.0.0.jar '+tmp1)
        likeVals.append([like, a])
    with open(fin,'w') as f:
        f.write(likeVals)
    f.close

fitModel(rand,deg,inputNet)