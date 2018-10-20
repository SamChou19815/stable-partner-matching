import pandas as pd
import re
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfTransformer
import json
import sys

# file = sys.argv[1]
frame = pd.read_json('entryData.json')
text = []
for chunks in frame['keywords']:
    acc = ''
    for word in chunks:
        acc += (word + ' ')
    text.append(acc[:-1])

def pre_process(text):

    text=text.lower()

    text=re.sub("&lt;/?.*?&gt;"," &lt;&gt; ",text)

    text=re.sub("(\\d|\\W)+"," ",text)

    return text
text = [pre_process(t) for t in text]

cv=CountVectorizer(max_df=0.85, stop_words='english', max_features=10000)
word_count_vector=cv.fit_transform(text)
tfidf_transformer=TfidfTransformer(smooth_idf=True,use_idf=True)
tfidf_transformer.fit(word_count_vector)

feature_names=cv.get_feature_names()
doc = ''
for descriptions in text:
    doc += descriptions
tf_idf_vector=tfidf_transformer.transform(cv.transform([doc]))

def sort_coo(coo_matrix):
    tuples = zip(coo_matrix.col, coo_matrix.data)
    return sorted(tuples, key=lambda x: (x[1], x[0]), reverse=True)

def extract_topn_from_vector(feature_names, sorted_items, topn=10):
    sorted_items = sorted_items[:topn]

    score_vals = []
    feature_vals = []

    for idx, score in sorted_items:
        score_vals.append(round(score, 3))
        feature_vals.append(feature_names[idx])
    results= {}
    for idx in range(len(feature_vals)):
        results[feature_vals[idx]]=score_vals[idx]

    return results

sorted_items=sort_coo(tf_idf_vector.tocoo())
keywords=extract_topn_from_vector(feature_names,sorted_items,round(0.5*len(text)))

result = [k for k in keywords]

with open('keywords.json', 'w') as outfile:
    json.dump(result, outfile)
