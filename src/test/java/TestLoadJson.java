import edu.ksu.canvas.aviation.model.Attendance;
import edu.ksu.canvas.aviation.model.Day;
import edu.ksu.canvas.aviation.util.JsonFileParseUtil;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by allanjay808 on 4/22/16.
 */
public class TestLoadJson {
    private static final Logger LOG = Logger.getLogger(TestLoadJson.class);

    @Test
    public void testConvertJsonToPoJo() throws IOException {
        String fileName = "generated.json";
        JsonFileParseUtil jsonFileParseUtil = new JsonFileParseUtil();
        List<Day> days = jsonFileParseUtil.loadDaysFromJson(fileName);
        for(Day day : days) {
            LOG.info("DATE: " + day.getDate());
            assertNotNull("Day has data", day.getAttendances());
            for(Attendance a : day.getAttendances()) {
                LOG.info("Student ID: " + a.getId());
                LOG.info("On Time: " + a.isOnTime());
                LOG.info("Minutes missed: " + a.getMinutesMissed());
            }
        }

    }
}
