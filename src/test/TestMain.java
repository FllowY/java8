package test;

import java.util.Arrays;
import java.util.regex.Pattern;

public class TestMain {

    public static void main(String[] args) {


//        boolean matches = Pattern.matches("^[0-9]|[1-9][0-9]|100$", "100");
        boolean matches = Pattern.matches("^([0-9]|[1-9][0-9])(\\.\\d{1,2})|[0-9]|[1-9][0-9]|100|100.00|0|0.00?$", "66.00");
//        boolean matches = Pattern.matches("^(\\d|[1-9]\\d|100)(\\.\\d{1,2})?$", "0.00");
        System.out.println(matches);


        System.out.println(Arrays.asList(0,1,2).stream().filter(a -> a>0).findAny().orElse(9));


//        List<Integer> list1 = Arrays.asList(2, 6, 5,7);
//        List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
//        list1.stream().filter(o -> !list2.contains(o)).forEach(System.out::println);
//        collect.stream().forEach();


//        Collectors.toSet去重
//        Set<Integer> collect = Arrays.asList(1, 2, 3, 4, 1, 2, 3, 0, 0, 4, 6, 7).stream().collect(Collectors.toSet());
//        for(Integer a: collect){
//            System.out.println(a);
//        }

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
