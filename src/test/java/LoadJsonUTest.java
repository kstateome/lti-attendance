import edu.ksu.canvas.aviation.model.Attendance;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by allanjay808
 */
//public class LoadJsonUTest {
//    private static final Logger LOG = Logger.getLogger(LoadJsonUTest.class);
//
//    @Test
//    public void testConvertJsonToPoJo() throws IOException {
//        String fileName = "generated.json";
//        JsonFileParseUtil jsonFileParseUtil = new JsonFileParseUtil();
//        List<Day> days = jsonFileParseUtil.loadDaysFromJson(fileName);
//        for(Day day : days) {
//            assertNotNull("Day has data", day.getAttendances());
//        }
//
//    }
//}
