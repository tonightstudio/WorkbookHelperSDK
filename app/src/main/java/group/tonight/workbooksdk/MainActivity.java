package group.tonight.workbooksdk;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import group.tonight.workbookhelper.BaseResponseBean;
import group.tonight.workbookhelper.MultiFileParser;
import group.tonight.workbookhelper.WorkbookHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void hahaha(View view) {
//        parseShenZhen();
//        parseUser();
        parseMultiFile();
    }

    /**
     * 处理7种情况：
     * 1.用户地址、用电地址                        -->750定.xls（已解决）
     * 2.多列抄表序号                              -->081定.xls（已解决）
     * 3.电能表号、电能表编号                      -->735定.xls（已解决）
     * 4.空数据行（完成无数据，仅做分隔作用）      -->081定.xls（已解决）
     * 5.无效数据行（部分单元格有无效数据）        -->081定.xls
     * 6.用户编号非纯数字判为无数数据              -->081定.xls
     * 7.数据不在sheet0                            -->750定.xls（已解决）
     */
    private void parseUser() {
        WorkbookHelper helper = new WorkbookHelper(this);

        String[] fileName = {
                "070定.xls"
                , "081定.xls"
                , "283定.xls"
                , "478定.xls"
                , "496定.xls"
                , "497定.xls"
                , "503定.xls"
                , "513定.xls"
                , "601定.xls"
                , "703定.xls"
                , "735定.xls"
                , "750定.xls"
        };

//        helper.setFileName("750定.xls");//设置assets中文件名
        helper.setFileName(fileName[1]);//设置assets中文件名

        //自定义key集合
//        helper.setKeyArray(new String[]{
//                "userId"
//                , "userName"
//                , "userPhone"
//                , "powerMeterId"
//                , "remarks"
//        });

        //如果未设置key的String数组，那么就会自动读取这里设置的key所在行的值做为key
//        helper.setKeyRowIndex(0);

        //设置key的map
        Map<String, String> map = new HashMap<>();
        map.put("用户编号", "userId");
        map.put("用户名称", "userName");
        map.put("联系方式", "userPhone");
        map.put("用户地址", "userAddress");
        map.put("电能表号", "powerMeterId");
        map.put("抄表序号", "meterReadingId");
        map.put("抄表段编号", "powerLineId");

        map.put("用电地址", "userAddress");
        map.put("电能表编号", "powerMeterId");
        helper.setKeyMap(map);
//        helper.setSheetIndex(1);
        helper.parse().observe(MainActivity.this, new Observer<BaseResponseBean<String>>() {
            @Override
            public void onChanged(BaseResponseBean<String> response) {
                if (response == null) {
                    return;
                }
                if (response.getMsg() != null) {
                    Toast.makeText(MainActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                    return;
                }
                String data = response.getData();
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void parseMultiFile() {
        String[] fileName = {
                "070定.xls"
                , "081定.xls"
                , "283定.xls"
                , "478定.xls"
                , "496定.xls"
                , "497定.xls"
                , "503定.xls"
                , "513定.xls"
                , "601定.xls"
                , "703定.xls"
                , "735定.xls"
                , "750定.xls"
        };

        MultiFileParser multiFileParser = new MultiFileParser(this) {
            @Override
            public Map<String, String> setKeyMap() {
                Map<String, String> map = new HashMap<>();
                map.put("用户编号", "userId");
                map.put("用户名称", "userName");
                map.put("联系方式", "userPhone");
                map.put("用户地址", "userAddress");
                map.put("电能表号", "powerMeterId");
                map.put("抄表序号", "meterReadingId");
                map.put("抄表段编号", "powerLineId");

                map.put("用电地址", "userAddress");
                map.put("电能表编号", "powerMeterId");
                return map;
            }
        };
        multiFileParser.setFileName(fileName);
        //工作线程
        multiFileParser.setOnParseResultListener(new MultiFileParser.OnParseResultListener() {
            @Override
            public void onParseResult(BaseResponseBean<String> response) {

            }
        });
        //主线程
        multiFileParser.observe(this, new Observer<BaseResponseBean<String>>() {
            @Override
            public void onChanged(BaseResponseBean<String> response) {
                if (response == null) {
                    return;
                }
                if (response.getMsg() != null) {
                    Toast.makeText(MainActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                    return;
                }
                String data = response.getData();
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();

                Type type = new TypeToken<List<User>>() {
                }.getType();
                List<User> dataList = new Gson().fromJson(data, type);
                System.out.println();
            }
        });
        new Thread(multiFileParser).start();
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
