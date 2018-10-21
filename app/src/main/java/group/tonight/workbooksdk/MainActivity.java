package group.tonight.workbooksdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import group.tonight.workbookhelper.WorkbookHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void hahaha(View view) {
//        parseShenZhen();
        parseUser();
    }

    private void parseUser() {
        WorkbookHelper helper = new WorkbookHelper(this);
        helper.setFileName("513_1021.xls");//设置assets中文件名
        helper.setKeyArray(new String[]{
                "userId"
                , "userName"
                , "userPhone"
                , "powerMeterId"
                , "remarks"
        });//自定义key集合
        //如果未设置key的String数组，那么就会自动读取这里设置的key所在行的值做为key
        helper.setKeyRowIndex(0);
        helper.parse(new WorkbookHelper.OnParseResultListener() {
            @Override
            public void onError(Exception e) {
                System.out.println();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(String json) {
                System.out.println();
            }
        });
    }

    private void parseShenZhen() {
        WorkbookHelper helper = new WorkbookHelper(this);
        helper.setFileName("深圳.xls");
        helper.setKeyRowIndex(1);
//        helper.setKeyArray(getResources().getStringArray(R.array.work_book_key_entries));
        helper.parse(new WorkbookHelper.OnParseResultListener() {
            @Override
            public void onError(Exception e) {
                System.out.println();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(String json) {
                System.out.println();
                Type type = new TypeToken<List<Girl>>() {
                }.getType();
                List<Girl> beanList = new Gson().fromJson(json, type);
                System.out.println();
//                UserDatabase.get().getGirlDao().insert(beanList);
            }
        });
    }
}
