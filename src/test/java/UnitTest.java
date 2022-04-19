import com.jcorpse.beauty.constant.Constant;
import com.jcorpse.beauty.entity.WebPage;
import com.jcorpse.beauty.http.HttpManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class UnitTest {
    private HttpManager Manager = HttpManager.getInstance();

    @Test
    public void HttpTest(){
        WebPage StartPage =  Manager.getBody(Constant.START_URL);
        Assert.assertEquals(StartPage.getCode(),200);
    }

    @AfterClass
    public static void TestEnd(){
        System.out.printf("Test End");
    }
}
