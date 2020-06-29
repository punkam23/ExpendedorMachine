package expendedor.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ObjectMapperWrapper extends ObjectMapper {
    @Override
    public void writeValue(File resultFile, Object value){
        try {
            super.writeValue(resultFile, value);
            writeValueCloud(resultFile, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeValueCloud(File resultFile, Object value){
        //call s3 restAPI to update the json file or use fireBase to update the json

    }
}
