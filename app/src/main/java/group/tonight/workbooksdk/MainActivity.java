package group.tonight.workbooksdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
        WorkbookHelper helper = new WorkbookHelper(this);
        helper.setFileName("深圳.xls");
        helper.setKeyRowIndex(1);
        helper.setKeyArray(getResources().getStringArray(R.array.work_book_key_entries));
        helper.parse(new WorkbookHelper.OnParseResultListener() {
            @Override
            public void onError(Exception e) {
//                KLog.e(e);
            }

            @Override
            public void onResult(String json) {
//                KLog.e(json);

                Type type = new TypeToken<List<Girl>>() {
                }.getType();
                List<Girl> beanList = new Gson().fromJson(json, type);
//                KLog.e();
                System.out.println();

//                UserDatabase.get().getGirlDao().insert(beanList);
            }
        });
    }
}
