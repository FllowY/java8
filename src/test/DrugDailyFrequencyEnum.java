package test;

import java.util.HashMap;
import java.util.Map;

/**
 * 用药频次枚举:药品库侧提供
 */
public enum DrugDailyFrequencyEnum {

    NEEDED(0, 0, "必要时"),
    ONE_PER_DAY(1 * 24, 1, "一日一次"),
    TWO_PER_DAY(1 * 24, 2, "一日两次"),
    THREE_PER_DAY(1 * 24, 3, "一日三次"),
    FOUR_PER_DAY(1 * 24, 4, "一日四次"),
    HALF_PER_DAY(2 * 24, 1, "隔日一次"),
    ONE_PER_WEEK(7 * 24, 1, "每周一次"),
    TWO_PER_WEK(7 * 24, 2, "每周两次"),
    THREE_PER_WEK(7 * 24, 3, "每周三次"),
    HALF_PER_WEEK(14 * 24, 1, "隔周一次"),
    HALF_PER_WEEK2(14 * 24, 1, "每两周一次"),
    THREE_WEEK_PER_TIME(21 * 24, 1, "每三周一次"),
    FOUR_WEEK_PER_TIME(28 * 24, 1, "每四周一次"),
    THIRTY_MINUTE_PER_TIME(0.5, 1, "三十分钟一次"),
    ONE_HOUR_PER_TIME(1, 1, "每小时一次"),
    TWO_HOUR_PER_TIME(2, 1, "二小时一次"),
    FOUR_HOUR_PER_TIME(4, 1, "四小时一次"),
    SIX_HOUR_PER_TIME(6, 1, "六小时一次"),
    EIGHT_HOUR_PER_TIME(8, 1, "八小时一次"),
    TWELVE_HOUR_PER_TIME(12, 1, "十二小时一次");

    /**
     * 小时
     */
    private double hours;
    /**
     * 次数
     */
    private int times;
    /**
     * 描述
     */
    private String desc;

    public static final Map<String, String> map = new HashMap<>();

    static {
        map.put("必要时", "NEEDED");
        map.put("一日一次", "ONE_PER_DAY");
        map.put("一日两次", "TWO_PER_DAY");
        map.put("一日三次", "THREE_PER_DAY");
        map.put("一日四次", "FOUR_PER_DAY");
        map.put("隔日一次", "HALF_PER_DAY");
        map.put("每周一次", "ONE_PER_WEEK");
        map.put("每周两次", "TWO_PER_WEK");
        map.put("每周三次", "THREE_PER_WEK");
        map.put("隔周一次", "HALF_PER_WEEK");
        map.put("每两周一次", "HALF_PER_WEEK2");
        map.put("每三周一次", "THREE_WEEK_PER_TIME");
        map.put("每四周一次", "FOUR_WEEK_PER_TIME");
        map.put("三十分钟一次", "THIRTY_MINUTE_PER_TIME");
        map.put("每小时一次", "ONE_HOUR_PER_TIME");
        map.put("二小时一次", "TWO_HOUR_PER_TIME");
        map.put("四小时一次", "FOUR_HOUR_PER_TIME");
        map.put("六小时一次", "SIX_HOUR_PER_TIME");
        map.put("八小时一次", "EIGHT_HOUR_PER_TIME");
        map.put("十二小时一次", "TWELVE_HOUR_PER_TIME");
    }

    DrugDailyFrequencyEnum(double hours, int times, String desc){
        this.hours = hours;
        this.times = times;
        this.desc = desc;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
