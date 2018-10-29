package group.tonight.workbookhelper;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * 多文件解析
 * <p>
 * Description：李艺为(15897461476 @ 139.com) created at 2018/10/27 0027 15:39
 */
public abstract class MultiFileParser extends LiveData<BaseResponseBean<String>> implements Runnable {
    private String[] mFileName;
    private Context mContext;
    private OnParseResultListener onParseResultListener;

    public abstract Map<String, String> setKeyMap();

    public MultiFileParser(Context context) {
        mContext = context;
    }

    public void setFileName(String... fileName) {
        mFileName = fileName;
    }

    public void setOnParseResultListener(OnParseResultListener listener) {
        this.onParseResultListener = listener;
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
        String json = jsonArray.toString();
        response.setData(json);
        if (onParseResultListener != null) {
            onParseResultListener.onParseResult(response);
        }
        postValue(response);
    }

    public interface OnParseResultListener {
        /**
         * 工作线程
         *
         * @param response
         */
        void onParseResult(BaseResponseBean<String> response);
    }
}
