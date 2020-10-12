import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.Const.SingleFileObj;
import com.me.domain.User;
import com.me.utils.IOGUIUtils;
import com.me.utils.Logger;
import com.me.utils.SQLPoolHelper;
import org.junit.Test;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UserTest {
    @Test
    public void test1() throws IOException {
////        SingleFileObj singleFileObj = new SingleFileObj();
////        singleFileObj.setFile(new File("C:\\Users\\86198\\Pictures\\Camera Roll\\WIN_20191115_23_28_37_Pro.jpg"));
////        singleFileObj.setTo("hello");
////        singleFileObj.setFrom("world");
////        ObjectMapper mapper = new ObjectMapper();
////        String s = mapper.writeValueAsString(singleFileObj);
////        SingleFileObj obj = mapper.readValue(s, SingleFileObj.class);
////        System.out.println(singleFileObj.getFile().length());
////        System.out.println(obj.getFile().length());
////        IOGUIUtils.recv(obj);
//        byte[] content = new byte[1024 * 500];
//        System.out.println(content.length);
//        System.out.println((65536 / 1024));
//        DataSource ds = SQLPoolHelper.getDs();
        System.out.println(SQLPoolHelper.class.getClassLoader().getResourceAsStream("/properties/druid.properties"));
    }
    private void showThread(){
        ThreadGroup currentGroup =
                Thread.currentThread().getThreadGroup();
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);
        for (int i = 0; i < noThreads; i++) {
            Logger.deBug("线程号：" + i + " = " + lstThreads[i].getName());
        }
    }
    @Test
    public void test2() throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (true){
            }
        },"test");
        System.out.println("开始前:");
        showThread();
        thread.start();
        System.out.println("开始后:");
        showThread();
        thread.stop();
        System.out.println("停止后:");
        showThread();
    }
}
