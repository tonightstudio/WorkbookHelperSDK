package group.tonight.workbookhelper;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class WorkbookHelper extends LiveData<BaseResponseBean<String>> implements Runnable {
    private Map<String, String> mKeyMap;
    private AssetManager assetManager;
    private String fileName;
    private String[] keyArray;
    private int sheetIndex;
    private int keyRowIndex;
    private OnParseResultListener onParseResultListener;
    private InputStream mInputSream;

    public WorkbookHelper() {
    }

    public WorkbookHelper(Context context) {
        assetManager = context.getApplicationContext().getAssets();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDataSource(InputStream inputStream) {
        mInputSream = inputStream;
    }

    public void setDataSource(Context context, String fileName) throws IOException {
        mInputSream = context.getAssets().open(fileName);
    }

    /**
     * 手动设置key集合
     *
     * @param keyArray
     */
    public void setKeyArray(String[] keyArray) {
        this.keyArray = keyArray;
    }

    public void parse(OnParseResultListener onParseResultListener) {
        this.onParseResultListener = onParseResultListener;
        new Thread(this).start();
    }

    public WorkbookHelper parse() {
        new Thread(this).start();
        return this;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    /**
     * 指定key所在的行数
     *
     * @param keyRowIndex
     */
    public void setKeyRowIndex(int keyRowIndex) {
        this.keyRowIndex = keyRowIndex;
    }

    /**
     * 设置自定义key
     *
     * @param keyMap key：excel原key，value：自定义key
     */
    public void setKeyMap(Map<String, String> keyMap) {
        mKeyMap = keyMap;
    }


    private List<String> getKeyList(Workbook workbook, BaseResponseBean data) {
        Sheet[] sheets = workbook.getSheets();//多少个工作表
        Sheet sheet = workbook.getSheet(this.sheetIndex);
        int rows = sheet.getRows();//获取总共多少行
        int columns = sheet.getColumns();//获取总共多少列

        List<String> keyList = new ArrayList<>();
        if (keyArray == null) {
            keyList = new ArrayList<>();
        } else {
            keyList = Arrays.asList(keyArray);
            if (columns != keyArray.length) {
                data.setMsg("key的个数应该为：" + columns);
                postValue(data);
                return null;
            }
        }
        if (keyList.size() == 0) {
            //如果没有设置key的集合，就用表的指定行的数据做为key
            Cell[] cells = sheet.getRow(keyRowIndex);

            for (Cell cell : cells) {
                String contents = cell.getContents();
                if (mKeyMap != null) {
                    if (mKeyMap.containsKey(contents)) {
                        keyList.add(mKeyMap.get(contents));
                    } else {
                        data.setMsg("缺少对应解析参数：" + contents);
                        postValue(data);
                        sheetIndex++;
                        if (sheetIndex <= sheets.length - 1) {
                            return getKeyList(workbook, data);
                        } else {
                            return null;
                        }
                    }
                } else {
                    keyList.add(contents);
                }
            }
        }
        return keyList;
    }

    @Override
    public void run() {
        BaseResponseBean<String> data = new BaseResponseBean<>();
        try {
            Workbook workbook;
            if (mInputSream != null) {
                workbook = Workbook.getWorkbook(mInputSream);
            } else {
                workbook = Workbook.getWorkbook(assetManager.open(fileName));
            }
            int numberOfSheets = workbook.getNumberOfSheets();
            if (numberOfSheets == 0) {
                data.setMsg("excel文档工作表数量为0");
                postValue(data);
                return;
            }


            Sheet[] sheets = workbook.getSheets();//多少个工作表
            Sheet sheet = workbook.getSheet(this.sheetIndex);

            int columns;//获取总共多少列

            List<String> keyList;
            if (keyArray == null) {
                keyList = new ArrayList<>();
            } else {
                keyList = Arrays.asList(keyArray);
                columns = sheet.getColumns();//获取总共多少列
                if (columns != keyArray.length) {
                    data.setMsg("key的个数应该为：" + columns);
                    postValue(data);
                    return;
                }
            }
            if (keyList.size() == 0) {
                //用keyMap中的值循环解析excel中的各个表，直到解析成功，或者超出工作表数量为止
                boolean success = false;
                while (!success) {
                    success = true;
                    sheet = workbook.getSheet(sheetIndex);
                    //如果没有设置key的集合，就用表的指定行的数据做为key
                    Cell[] cells = sheet.getRow(keyRowIndex);

                    for (Cell cell : cells) {
                        String contents = cell.getContents();
                        if (mKeyMap != null) {
                            if (mKeyMap.containsKey(contents)) {
                                keyList.add(mKeyMap.get(contents));
                            } else {
                                success = false;
                                keyList.clear();
                                sheetIndex++;
                                if (sheetIndex == sheets.length) {
                                    data.setMsg("缺少对应解析参数：" + contents);
                                    postValue(data);
                                    return;
                                }
                                break;
                            }
                        } else {
                            keyList.add(contents);
                        }
                    }
                }
            }

            int rows = sheet.getRows();//获取总共多少行
            columns = sheet.getColumns();//获取总共多少列
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
                    if (n < keyList.size()) {
                        String key = keyList.get(n);
                        if (jsonObject.has(key)) {
                            if (!TextUtils.isEmpty(contents)) {
                                jsonObject.put(key, contents);
                            }
                        } else {
                            jsonObject.put(key, contents);
                        }
                    }
                }
                jsonArray.put(jsonObject);
            }
            //过滤空数据
            removeInvalidData(jsonArray, keyList);

            String json = jsonArray.toString();
            if (onParseResultListener != null) {
                onParseResultListener.onResult(json);
            }
            data.setData(json);
        } catch (BiffException | JSONException | IOException e) {
            e.printStackTrace();
            if (onParseResultListener != null) {
                onParseResultListener.onError(e);
            }
            data.setMsg(e.toString());
        }
        postValue(data);
    }

    /**
     * 过滤空数据
     *
     * @param jsonArray
     * @param keyList
     * @throws JSONException
     */
    private void removeInvalidData(JSONArray jsonArray, List<String> keyList) throws JSONException {
        List<Integer> invalidDataIndexList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int length = jsonObject.length();
            int emptyCount = 0;
            for (String key : keyList) {
                String string = jsonObject.getString(key);
                if (TextUtils.isEmpty(string)) {
                    emptyCount++;
                    if (emptyCount == length) {
                        invalidDataIndexList.add(i);
                    }
                }
            }
            System.out.println();
        }
        Collections.reverse(invalidDataIndexList);
        for (Integer integer : invalidDataIndexList) {
            if (integer != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    jsonArray.remove(integer);
                }
            }
        }
    }


    public interface OnParseResultListener {
        void onError(Exception e);

        void onResult(String json);
    }
}
