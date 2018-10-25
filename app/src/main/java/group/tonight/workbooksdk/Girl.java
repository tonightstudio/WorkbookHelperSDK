package group.tonight.workbooksdk;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Girl {
    @PrimaryKey(autoGenerate = true)
    private int girlId;
    private String title;//信息标题
    private String type;//信息分类
    private String area;//所属地区
    private String address;//详细地址
    private String from;//信息来源
    private String count;//小姐数量
    private String ageRange;//小姐年龄
    private String suZhi;//小姐素质
    private String score;//小姐外形
    private String serviceItems;//服务项目
    private String price;//价格一览
    private String openTime;//营业时间
    private String surroundings;//环境设备
    private String safety;//安全评估
    private String contactInfo;//联系方式
    private String evaluate;//综合评价
    private String description;//详细介绍

    private long createTime;
    private long updateTime;

    public int getGirlId() {
        return girlId;
    }

    public void setGirlId(int girlId) {
        this.girlId = girlId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public String getSuZhi() {
        return suZhi;
    }

    public void setSuZhi(String suZhi) {
        this.suZhi = suZhi;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getServiceItems() {
        return serviceItems;
    }

    public void setServiceItems(String serviceItems) {
        this.serviceItems = serviceItems;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getSurroundings() {
        return surroundings;
    }

    public void setSurroundings(String surroundings) {
        this.surroundings = surroundings;
    }

    public String getSafety() {
        return safety;
    }

    public void setSafety(String safety) {
        this.safety = safety;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
