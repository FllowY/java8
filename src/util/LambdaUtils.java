package util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class LambdaUtils {

    /**
     * 筛选集合
     * @param list  集合
     * @param predicate boolean条件
     * @param <T>
     * @return
     */
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate){
        List<T> result = new ArrayList<>();
        for(T t : list){
            if(predicate.test(t)){
                result.add(t);
            }
        }
        return result;
    }

    /**
     * 输出集合元素
     * @param str
     * @param list
     * @param <T>
     */
    public static<T> void out(String str, List<T> list){
        System.out.println(str + " ========START========");

        for(T t : list){
            System.out.println(t);
        }

        System.out.println(str + " =========END=========");
    }

    /**
     * 改变集合中元素
     * @param list
     * @param consumer
     * @param <T>
     */
    public static <T> List<T> consumer(List<T> list, Consumer<T> consumer){
        for(T t : list){
            consumer.accept(t);
        }
        return list;
    }

    /**
     * Function<T, R>:由 T 对象转 R 对象
     * @param list
     * @param function
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> function(List<T> list, Function<T, R> function){
        List<R> result = new ArrayList<>();
        for(T t : list){
            result.add(function.apply(t));
        }
        return result;
    }
}
