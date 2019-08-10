package test;

public class TestMain {

    public static void main(String[] args) {
//        AtomicInteger counter = new AtomicInteger(0);
//
////        System.out.println("counter.incrementAndGet():" + counter.incrementAndGet());//i++;
//
//        if(1 == counter.incrementAndGet()){
//            System.out.println("counter.getAndIncrement():" + counter.getAndIncrement());//++i;
//        }

//        ^([1]?\d{1,2})$
//        ^[0-9][0-9]{0,1}$ 0-99
//        System.out.println("101".matches("^[0-9]|[1-9][0-9]|100$"));




//        枚举测试
//        String 一日三次 = DrugDailyFrequencyEnum.map.get("一日三次");
//        System.out.println(一日三次);
//
//
//        DrugDailyFrequencyEnum frequencyEnum = DrugDailyFrequencyEnum.valueOf(DrugDailyFrequencyEnum.map.get("一日三次"));
//        System.out.println(frequencyEnum.getTimes());
//        System.out.println(frequencyEnum.getHours());
//        System.out.println(frequencyEnum.getDesc());

    }
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
}
