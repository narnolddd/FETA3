import warnings
warnings.filterwarnings("ignore")
from json import JSONEncoder

class FetaObject:
    def __init__(self,data,action,obms=None,operation=None):
        self.Data = data
        self.Action = action
        self.ObjectModel = obms
        self.OperationModel = operation

class DataObject:
    def __init__(self,infile, intype="NNT", outtype="NNT", outfile = None, directed=False):
        self.GraphInputFile=infile
        self.GraphInputType=intype
        self.GraphOutputType=outtype
        self.GraphOutputFile=outfile
        self.Directed=directed

class Action:
    def __init__(self,fmm=None,grow=None, measure=None):
        self.FitMixedModel = fmm
        self.Grow = grow
        self.Measure = measure

class Measure:
    def __init__(self, start, end, interval, fname, stats = ["NoNodes", "NoLinks", "AverageDegree", "MaxDegree","Singletons","MeanSquaredDegree", "Assortativity", "Clustering", "TriangleCount"]):
        self.Start = start
        self.MaxTime = end
        self.Interval = interval
        self.Statistics = stats
        self.FileName = fname

class Grow:
    def __init__(self, start, end, interval=10000):
        self.Start = start
        self.MaxTime = end
        self.Interval = interval


class FitMixedModel:
    def __init__(self, start, end, interval, granularity=100, debug = False):
        self.StartTime = start
        self.MaxTime = end
        self.Interval = interval
        self.Granularity = granularity
        self.DebugMode = debug

class OperationModel:
    def __init__(self, name, start, file):
        self.Name = name
        self.Start = start
        self.FileName = file

class ObjectModel:
    def __init__(self, start, end, components):
        self.Start = start
        self.End = end
        self.Components = components

class ObjectModelComponent:
    def __init__(self, name, weight=None):
        self.ComponentName = name
        self.Weight = weight

class FetaEncoder(JSONEncoder):
    def default(self,o):
        d = o.__dict__
        return del_none(d)

def del_none(d):
    """
    Delete keys with the value ``None`` in a dictionary, recursively.

    This alters the input so you may wish to ``copy`` the dict first.
    """
    # For Python 3, write `list(d.items())`; `d.items()` won’t work
    # For Python 2, write `d.items()`; `d.iteritems()` won’t work
    for key, value in list(d.items()):
        if value is None:
            del d[key]
        elif isinstance(value, dict):
            del_none(value)
    return d  # For convenience