import warnings
warnings.filterwarnings("ignore")
from json import JSONEncoder

class FetaObject:
    def __init__(self,data,action,obms,operation=None):
        self.Data = data
        self.Action = action
        self.ObjectModel = obms
        self.OperationModel = operation

class DataObject:
    def __init__(self,infile, intype="NNT", outtype="NNT", directed=False):
        self.GraphInputFile=infile
        self.GraphInputType=intype
        self.GraphOutputType=outtype
        self.Directed=directed

class Action:
    def __init__(self,fmm):
        self.FitMixedModel = fmm

class FitMixedModel:
    def __init__(self, start, end, interval, granularity=100):
        self.StartTime = start
        self.MaxTime = end
        self.Interval = interval
        self.Granularity = granularity

class ObjectModel:
    def __init__(self, start, end, components):
        self.Start = start
        self.End = end
        self.Components = components

class FetaEncoder(JSONEncoder):
    def default(self,o):
        return o.__dict__