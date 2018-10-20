/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.cloud.language.v1.*;
import com.google.cloud.language.v1.Document.Type;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A sample application that uses the Natural Language API to perform entity, sentiment and syntax
 * analysis.
 */
public class Analyze {


    /**
     * Identifies entities in the string {@code text}.
     */
    public static Map<String, Double> analyzeEntitiesText(String text) throws Exception {
        // [START language_entities_text]
        // Instantiate the Language client com.google.cloud.language.v1.LanguageServiceClient
        LanguageServiceClient language = LanguageServiceClient.create();
        Document doc = Document.newBuilder()
                .setContent(text)
                .setType(Type.PLAIN_TEXT)
                .build();
        AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder()
                .setDocument(doc)
                .setEncodingType(EncodingType.UTF16)
                .build();

        Map<String, Double> keywords = new HashMap<String, Double>();


        try {
            AnalyzeEntitiesResponse response = language.analyzeEntities(request);
            for (Entity entity : response.getEntitiesList()) {
                keywords.put(entity.getName().toLowerCase(), new Double(entity.getSalience()));
            }
        } catch (RuntimeException e) {
            System.out.println("Too few tokens to analyze categories for.");
        } finally {
            language.close();
            return keywords;
        }

        // [END language_entities_text]
    }

    /**
     * Detects categories in text using the Language Beta API.
     */
    public static Map<String, Double> analyzeCategoriesText(String text) throws IOException {
        // [START language_classify_text]
        // Instantiate the Language client com.google.cloud.language.v1.LanguageServiceClient
        LanguageServiceClient language = LanguageServiceClient.create();

        // set content to the text string
        Document doc = Document.newBuilder()
                .setContent(text)
                .setType(Type.PLAIN_TEXT)
                .build();
        ClassifyTextRequest request = ClassifyTextRequest.newBuilder()
                .setDocument(doc)
                .build();
        // detect categories in the given text
        Map<String, Double> categories = new HashMap<String, Double>();

        try {
            ClassifyTextResponse response = language.classifyText(request);

            for (ClassificationCategory category : response.getCategoriesList()) {
                double conf = category.getConfidence();
                String[] catNames = category.getName().substring(1).split(" ");
                for (int i = 0; i < catNames.length; i++) {
                    categories.put(catNames[i], conf);
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Too few tokens to analyze categories for.");
        } finally {
            language.close();
            return categories;
        }

        // [END language_classify_text]
    }


}
