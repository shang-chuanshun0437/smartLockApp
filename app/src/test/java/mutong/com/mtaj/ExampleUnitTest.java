package mutong.com.mtaj;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import mutong.com.mtaj.utils.DateUtil;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void date()
    {
        String str = DateUtil.convert2String("201807061701",1);
        System.out.println(str);
        String temp = DateUtil.dateToWeek("20180710");
        System.out.println(temp);
    }

}