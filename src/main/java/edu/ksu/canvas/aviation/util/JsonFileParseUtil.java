package edu.ksu.canvas.aviation.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import edu.ksu.canvas.aviation.model.Attendance;
import edu.ksu.canvas.aviation.model.Day;
import org.apache.log4j.Logger;

import javax.naming.Context;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by allanjay808
 */
public class JsonFileParseUtil {
    private static final Logger LOG = Logger.getLogger(JsonFileParseUtil.class);

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    public List<Day> loadDaysFromJson(String fileName) throws IOException {
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

    public void writeDaysToJson(String filename, List<Day> list) throws IOException {
        FileOutputStream outputStream;
        String s = gson.toJson(list);
        try {
            outputStream = new FileOutputStream("src/main/resources/save.json");
            outputStream.write(s.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
