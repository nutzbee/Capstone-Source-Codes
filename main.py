from flask import Flask, request, render_template
import pickle
import pandas as pd
from pandas import DataFrame
import numpy as np
import seaborn as sns
from sklearn import metrics, datasets, cluster
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn import model_selection
from sklearn.neighbors import KNeighborsClassifier
from sklearn.datasets import make_blobs
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_samples, silhouette_score
import matplotlib.pyplot as plt
import matplotlib.cm as cm
from sklearn.preprocessing import MinMaxScaler
from sklearn.preprocessing import StandardScaler
from itertools import cycle, islice
from pandas.plotting import parallel_coordinates
from sklearn.preprocessing import LabelEncoder

app = Flask(__name__)
#model = pickle.load(open('example.pkl', 'rb'))
df1 = pd.read_excel('ABBREV.xlsx')

@app.route('/')
def home():
    return render_template('index.html')

@app.route('/predict',methods=['POST'])
def predict():
    """Grabs the input values and uses them to make prediction"""

    example_df = df1[['NDB_No','Water_(g)','Energ_Kcal','Protein_(g)','Lipid_Tot_(g)','Ash_(g)','Carbohydrt_(g)']]
    max = int(example_df.iloc[-1]['NDB_No'])
    foodId = max+1

    food_name = request.form["foodName"]
    data1 = int(request.form["nutrient1"])
    data2 = int(request.form["nutrient2"])
    data3 = int(request.form["nutrient3"])
    data4 = int(request.form["nutrient4"])
    data5 = int(request.form["nutrient5"])
    data6 = int(request.form["nutrient6"])
    #data0 = [data1,data2,data3,data4,data5]

    #prediction = model.fit_predict([[[data0]]])  
    # this returns a list e.g. [127.20488798], 
    # so pick first element [0]

    users_inputdf1 = [foodId,food_name,data1,data2,data3,data4,data5,data6]
    users_inputex = [foodId,data1,data2,data3,data4,data5,data6]

    example_df = example_df.append(pd.Series(users_inputex, index=example_df.columns[:len(users_inputex)]), ignore_index=True)
    df1 = df1.append(pd.Series(users_inputdf1, index=df1.columns[:len(users_inputdf1)]), ignore_index=True)

    example_df = example_df.fillna(0)

    ex = StandardScaler().fit_transform(example_df)

    kmeans = KMeans(n_clusters=4)
    kmeans.fit(ex)
    KMeans(n_clusters=4)

    y_predict = kmeans.fit_predict(example_df)
    example_df['cluster'] = y_predict
    example_df = example_df[['NDB_No','cluster']]
    example_df = pd.merge(df1, example_df)

    source = example_df[example_df['NDB_No'] == foodId]
    final_food_name = source.iloc[-1]['Shrt_Desc']
    cluster = source.iloc[-1]['cluster']
    final_foodId = source.iloc[-1]['NDB_No']


    output = cluster

    return render_template('index.html', 
    prediction_text=f'A food with food name of {final_food_name} that has and food database # of {final_foodId} is clustered in cluster {output}')

if __name__ == "__main__":
    app.run()
    