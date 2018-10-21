# WorkbookHelperSDK
解析excel文档工具类

引用方法：

    repositories {
        maven { url "https://github.com/tonightstudio/WorkbookHelperSDK/raw/master" }
    }
    dependencies {
        implementation 'com.github.tonightstudio:workbookhelper:1.0'
    }
    
使用方法：
    
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
       
