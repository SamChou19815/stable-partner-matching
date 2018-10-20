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

import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.AnalyzeEntitySentimentRequest;
import com.google.cloud.language.v1.AnalyzeEntitySentimentResponse;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.AnalyzeSyntaxRequest;
import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import com.google.cloud.language.v1.ClassificationCategory;
import com.google.cloud.language.v1.ClassifyTextRequest;
import com.google.cloud.language.v1.ClassifyTextResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.EntityMention;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.Token;
import org.omg.SendingContext.RunTime;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.lang.AutoCloseable;
import java.lang.RuntimeException;
import java.io.IOException;

/**
 * A sample application that uses the Natural Language API to perform entity, sentiment and syntax
 * analysis.
 */
public class Analyze {


    /**
     * Identifies entities in the string {@code text}.
     */
    public static List<String> analyzeEntitiesText(String text) throws Exception {
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

        List<String> keywords = new ArrayList<String>();


        try {
            AnalyzeEntitiesResponse response = language.analyzeEntities(request);

            for (Entity entity : response.getEntitiesList()) {
                keywords.add(entity.getName().toLowerCase());
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
    public static List<String> analyzeCategoriesText(String text) throws IOException {
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
        List<String> categories = new ArrayList<String>();

        try {
            ClassifyTextResponse response = language.classifyText(request);

            for (ClassificationCategory category : response.getCategoriesList()) {
                categories.add(category.getName().substring(1).toLowerCase());
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
