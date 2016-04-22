package edu.ksu.canvas.aviation.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import edu.ksu.canvas.aviation.model.Day;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by allanjay808 on 4/22/16.
 */
public class JsonFileParseUtil {

    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    public List<Day> parseDaysFromJSON(String fileName) throws IOException {
        Type listType = new TypeToken<List<Day>>() {
        }.getType();
        List<Day> days = gson.fromJson(getStringBuilder(fileName).toString(), listType);
        return days;
    }

    private static StringBuilder getStringBuilder(String fileName) throws IOException {
        InputStream fileData = JsonFileParseUtil.class.getClassLoader().getResourceAsStream(fileName);
        InputStreamReader reader = new InputStreamReader(fileData);

        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder stringBuilder = new StringBuilder();
        while(bufferedReader.ready()){
            stringBuilder.append(bufferedReader.readLine());
        }
        bufferedReader.close();
        ;
        reader.close();
        fileData.close();
        return stringBuilder;
    }
}
