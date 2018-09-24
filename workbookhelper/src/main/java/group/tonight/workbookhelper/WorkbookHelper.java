package group.tonight.workbookhelper;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class WorkbookHelper implements Runnable {
    private final AssetManager assetManager;
    private String fileName;
    private String[] keyArray;
    private int sheetIndex;
    private int keyRowIndex;
    private OnParseResultListener onParseResultListener;

    public WorkbookHelper(Context context) {
        assetManager = context.getApplicationContext().getAssets();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setKeyArray(String[] keyArray) {
        this.keyArray = keyArray;
    }

    public void parse(OnParseResultListener onParseResultListener) {
        this.onParseResultListener = onParseResultListener;
        new Thread(this).start();
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public void setKeyRowIndex(int keyRowIndex) {
        this.keyRowIndex = keyRowIndex;
    }

    @Override
    public void run() {
        try {
            Workbook workbook = Workbook.getWorkbook(assetManager.open(fileName));
            int numberOfSheets = workbook.getNumberOfSheets();
            if (numberOfSheets == 0) {
                return;
            }
            Sheet sheet = workbook.getSheet(this.sheetIndex);
            int rows = sheet.getRows();//获取总共多少行
            int columns = sheet.getColumns();//获取总共多少列

            List<String> keyList;
            if (keyArray == null) {
                keyList = new ArrayList<>();
            } else {
                keyList = Arrays.asList(keyArray);
            }
            if (keyList.size() == 0) {
                //如果没有设置key的集合，就用表的指定行的数据做为key
                Cell[] cells = sheet.getRow(keyRowIndex);
                for (Cell cell : cells) {
                    keyList.add(cell.getContents());
                }
            }
            JSONArray jsonArray = new JSONArray();
            for (int m = 0; m < rows; m++) {
                if (m <= keyRowIndex) {
                    continue;
                }
                //循环读取第i行的所有列的数据
                JSONObject jsonObject = new JSONObject();
                for (int n = 0; n < columns; n++) {
                    //sheet.getCell(列，行);
                    Cell cell = sheet.getCell(n, m);
                    String contents = cell.getContents();
                    jsonObject.put(keyList.get(n), contents);
                }
                jsonArray.put(jsonObject);
            }
            String json = jsonArray.toString();

            if (onParseResultListener != null) {
                onParseResultListener.onResult(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (onParseResultListener != null) {
                onParseResultListener.onError(e);
            }
        } catch (BiffException e) {
            e.printStackTrace();
            if (onParseResultListener != null) {
                onParseResultListener.onError(e);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (onParseResultListener != null) {
                onParseResultListener.onError(e);
            }
        }
    }

    public interface OnParseResultListener {
        void onError(Exception e);

        void onResult(String json);
    }
}
