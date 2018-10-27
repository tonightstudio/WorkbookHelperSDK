package group.tonight.workbookhelper;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import androidx.lifecycle.LiveData;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * String[] fileName = {
 *                 "070定.xls"
 *                 , "081定.xls"
 *                 , "283定.xls"
 *                 , "478定.xls"
 *                 , "496定.xls"
 *                 , "497定.xls"
 *                 , "503定.xls"
 *                 , "513定.xls"
 *                 , "601定.xls"
 *                 , "703定.xls"
 *                 , "735定.xls"
 *                 , "750定.xls"
 *         };
 *
 *         MultiFileParser multiFileParser = new MultiFileParser(this) {
 *             @Override
 *             public Map<String, String> setKeyMap() {
 *                 Map<String, String> map = new HashMap<>();
 *                 map.put("用户编号", "userId");
 *                 map.put("用户名称", "userName");
 *                 map.put("联系方式", "userPhone");
 *                 map.put("用户地址", "userAddress");
 *                 map.put("电能表号", "powerMeterId");
 *                 map.put("抄表序号", "meterReadingId");
 *                 map.put("抄表段编号", "powerLineId");
 *
 *                 map.put("用电地址", "userAddress");
 *                 map.put("电能表编号", "powerMeterId");
 *                 return map;
 *             }
 *         };
 *         multiFileParser.setFileName(fileName);
 *         multiFileParser.observe(this, new Observer<BaseResponseBean<String>>() {
 *             @Override
 *             public void onChanged(BaseResponseBean<String> response) {
 *                 if (response == null) {
 *                     return;
 *                 }
 *                 if (response.getMsg() != null) {
 *                     Toast.makeText(MainActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
 *                     return;
 *                 }
 *                 String data = response.getData();
 *                 Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
 *
 *                 Type type = new TypeToken<List<User>>() {
 *                 }.getType();
 *                 List<User> dataList = new Gson().fromJson(data, type);
 *                 System.out.println();
 *             }
 *         });
 *         new Thread(multiFileParser).start();
 *
 * Description：李艺为(15897461476 @ 139.com) created at 2018/10/27 0027 15:39
 */
public abstract class MultiFileParser extends LiveData<BaseResponseBean<String>> implements Runnable {
    private String[] mFileName;
    private Context mContext;

    public abstract Map<String, String> setKeyMap();

    public MultiFileParser(Context context) {
        mContext = context;
    }

    public void setFileName(String... fileName) {
        mFileName = fileName;
    }

    @Override
    public void run() {
        BaseResponseBean<String> response = new BaseResponseBean<>();
        AssetManager manager = mContext.getAssets();

        JSONArray jsonArray = new JSONArray();
        Map<String, String> keyMap = setKeyMap();
        for (String fileName : mFileName) {
            try {
                Workbook workbook = Workbook.getWorkbook(manager.open(fileName));
                Sheet sheet = workbook.getSheet(0);//获取第一个工作表
                int rows = sheet.getRows();//获取总共多少行
                int columns = sheet.getColumns();//获取总共多少列
                for (int m = 0; m < rows; m++) {//处理行
                    if (m == 0) {
                        continue;
                    }
                    JSONObject object = new JSONObject();
                    boolean validData = true;
                    for (int n = 0; n < columns; n++) {//处理列
                        //sheet.getCell(列，行);
                        String key = sheet.getCell(n, 0).getContents();//第1行第n列单元格内的数据
                        String value = sheet.getCell(n, m).getContents();//第m行第n列单元格内的数据
                        if (n == 0) {
                            if (TextUtils.isEmpty(value)) {
                                validData = false;
                                break;
                            }
                        }
                        if (TextUtils.isEmpty(key)) {
                            continue;
                        }
                        if (keyMap != null) {
                            object.put(keyMap.get(key), value);
                        } else {
                            object.put(key, value);
                        }
                    }
                    if (validData) {
                        jsonArray.put(object);
                    }
                }
            } catch (IOException | BiffException | JSONException e) {
                e.printStackTrace();
                response.setMsg(e.getMessage());
            }
        }
        response.setData(jsonArray.toString());
        postValue(response);
    }
}
