package com.example.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;

public class TestUtil {

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        //ObjectMapper is used to translate object into JSON
        ObjectMapper mapper = new ObjectMapper();
        //take the object and return the JSON as a byte[]
        return mapper.writeValueAsBytes(object);
    }

    public static String convertObjectToJsonString(Object object) throws IOException {
        //ObjectMapper is again used to take an object and map it to JSON
        ObjectMapper mapper = new ObjectMapper();
        //write the JSON in the form of a String and return
        return mapper.writeValueAsString(object);
    }

    public static <T> T convertJsonBytesToObject(byte[] bytes, Class<T> clazz) throws IOException {
        //ObjectReader is used to translate JSON to a Java object.
        ObjectReader reader = new ObjectMapper()
                //indicate which model/class the reader needs to map the JSON onto
                .readerFor(clazz);

        //read the byte array containing the JSON and translate it into an object.
        return reader.readValue(bytes);
    }
}