import pandas as pd
interactions=[]
from datetime import datetime

class Person:
  def __init__(self,name):
    self.name=name
    self.risk=0
    self.interactions=[]
    self.contacts={}
  def to_dict(self):
        return {
            'name': self.name,
            'risk':self.risk,
            'interactions':self.interactions,
            'contacts':self.contacts,
        }
  def test(self,result):
    if result==True:
      self.risk=100
    else:
      self.risk=0.0001
  def __str__(self):
    return str(str(self.name)) 
  def update(self):
    totalNonRisk=1
    for c in self.contacts:
      totalNonRisk=totalNonRisk*(1-c.risk)
      oldrisk=self.risk
      if oldrisk!=1-totalNonRisk:
        self.risk=1-totalNonRisk
        print (self.name,"'s risk changed to ",self.risk," from ",oldrisk)
        c.update()
    else:
      print(" no change to",self.name,"'s risk ")

class Interaction:
  def __init__(self,persons,time,distance):
    global data
    interactions.append(self)
    self.iid=len(interactions)-1
    self.time=time
    self.risks={}
    self.distance=distance
    self.persons=persons
    for p in persons:
      print ("=====for ",p.name)
      totalNonRisk=1
      for m in persons:
        if p==m:
          continue
        nonrisk=1-m.risk*distance
        print("nonrisk: ",nonrisk)
        totalNonRisk=totalNonRisk*nonrisk
        print("totalnonrisk",totalNonRisk)
      self.risks[p]=1-(totalNonRisk*(1-p.risk))
      p.interactions.append(self)
    for p in persons:
      p.risk=self.risks[p]
      for m in persons:
        if p==m:
          continue
        p.contacts[m]={"risk":m.risk,"time":self.time,"distance":self.distance}
    data=data.append({'time':self.time,'distance':self.distance},ignore_index=True)
    for p in persons:
      data.loc[data.time == self.time, p.name] = p.risk
  def __str__(self):
    return str(self.iid)
  def showRisks(self):
    string=""
    for name, score in self.risks.items():
      string= string+"Name: "+str(name)+", risk: "+str(score)+"\n"
    print(string)
  def to_dict(self):
      return {
          'time':self.time,
          'risks':self.risks,
        }

sam=Person("sam")
sam.risk=0.5
tim=Person("tim")
tim.risk=0.007
al=Person("al")
al.risk=0
amy=Person("amy")
amy.risk=0
joe=Person("joe")
joe.risk=0
gus=Person("gus")
gus.risk=0
liz=Person("liz")
liz.risk=0
sal=Person("sal")
sal.risk=1

people=[sam,tim,al,amy,joe,gus,liz,sal]
df=pd.DataFrame.from_records([s.to_dict() for s in people])
columns=["interaction"]
for p in people:
  columns.append(p.name)
data=pd.DataFrame(columns=columns)

s=Interaction([gus,tim],datetime.now().timestamp(),0.8)
df=pd.DataFrame.from_records([s.to_dict() for s in people])
dfi=pd.DataFrame.from_records([s.to_dict()for s in interactions])
print(data.head(7))


def update(someone):
  count=data.shape[0]
  for row in data.iterrows():
    if count>0:
      count=count-1
    if not pd.isna(data.at[count,someone.name]):
      print("=====row======",count," - ",data.at[count,someone.name])
      for p in people:
        nonrisk=1
        if p.name==someone.name:
          continue
        if not pd.isna(data.at[count,p.name]):
          print(" met with ", p.name," and now ",data.at[count,p.name])
            #reverse the effect of the interaction
        nonrisk=1-p.risk*data.at[count,'distance']
