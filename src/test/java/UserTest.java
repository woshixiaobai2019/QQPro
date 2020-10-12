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
        System.out.println("test");
    }
}
