package lambda;

import domain.Apple;
import util.LambdaUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * TODO Java8 Lambda Demo
 *
 * java8中常用函数式接口：
 * java.util.function.*
 *
 * 	函数式接口		描述符			原始类型特化                                     使用场景
 *
 * Predicate<T> 	T->boolean 		IntPredicate,LongPredicate, DoublePredicate		设计到类型T的布尔表达式时使用此函数式接口
 *
 * Consumer<T> 		T->void 		IntConsumer,LongConsumer, DoubleConsumer		定义了一个accept的抽象方法，用于接收T对象，并且没有返回值。
 * 																						如果你需要访问T对象，并且对其【执行某些操作】，即可使用此函数式接口。
 *
 * Function<T,R>	T->R 			IntFunction<R>,	IntToDoubleFunction, IntToLongFunction
 * 									LongFunction<R>, LongToDoubleFunction, LongToIntFunction,
 * 									DoubleFunction<R>,												接收T对象，返回R对象
 * 									ToIntFunction<T>, ToDoubleFunction<T>, ToLongFunction<T>
 *
 * Supplier<T> 		()->T 			BooleanSupplier, IntSupplier, LongSupplier, DoubleSupplier	返回T对象（常用于创建对象）
 *
 * UnaryOperator<T>  T->T 			IntUnaryOperator, LongUnaryOperator, DoubleUnaryOperator
 *
 * BinaryOperator<T> (T,T)->T 		IntBinaryOperator, LongBinaryOperator, DoubleBinaryOperator		同类型运算
 *
 * BiPredicate<L,R>  (L,R)->boolean
 *
 * BiConsumer<T,U>   (T,U)->void	ObjIntConsumer<T>, 	ObjLongConsumer<T>, ObjDoubleConsumer<T>
 *
 * BiFunction<T,U,R> (T,U)->R 		ToIntBiFunction<T,U>, ToLongBiFunction<T,U>,
 * 									ToDoubleBiFunction<T,U>
 **/
public class LambdaDemo {

    private static List<Apple> appleList;

    static {
        appleList = Arrays.asList(
                new Apple("red", 500),
                new Apple("blue", 20),
                new Apple("red", 30),
                new Apple("yellow", 20));
    }

    public static void main(String[] args) {

        filterApples();
        comsumerApples();
        functiuonApples();
        reference();//方法引用
        complexLambda();//复合Lambda表达式
    }

    public static void filterApples(){
        LambdaUtils.out("筛选红苹果：", LambdaUtils.filter(appleList, apple -> "red".equals(apple.getColor())));
        LambdaUtils.out("筛选红苹果且重量大于50的苹果：", LambdaUtils.filter(appleList, apple -> "red".equals(apple.getColor()) && apple.getHeight() > 50));
    }

    public static void comsumerApples(){
        LambdaUtils.out("改变苹果重量：", LambdaUtils.consumer(appleList, apple -> apple.setHeight(50), apple -> apple.setColor("ggg")));
    }

    public static void functiuonApples(){
        //int -> Integer 装箱操作
        List<Integer> integerFunction = LambdaUtils.function(appleList, apple -> apple.getHeight());
        LambdaUtils.out("Function T -> R : 集合由Apple List变为重量Integer List ：", integerFunction);
        /**
         * 问题注意:
         *      但这在性能上是要付出代价的。装箱后的值本质上就是把原始类型包裹起来，并保存在JVM堆中。
         *      因此，装箱后的值需要更多的内存进行存储，久而久之，就可能发生OOM问题
         */

        //如何解决？ 避免装箱就可以了，并且Function已经为我们提供了基本类型的函数式接口

        //举个栗子
        //装箱
        Predicate<Integer> p = i -> i>0;
        //避免装箱
        IntPredicate intP = i -> i>0;
//        p.test(-1);
//        intP.test(-1);

    }

    /**
     * 示例：方法引用
     */
    public static void reference(){
//        appleList.sort((Apple o1, Apple o2) -> o1.getColor().compareToIgnoreCase(o2.getColor()));
        //Java编译器可以根据Lambda出现的上下文来推断Lambda表达式参数的类型
        appleList.sort((o1, o2) -> o1.getColor().compareToIgnoreCase(o2.getColor()));
        LambdaUtils.out("根据color排序后的Apple ： ", appleList);

        //使用方法引用
        List<String> collect = appleList.stream().map(apple -> apple.getColor()).collect(Collectors.toList());
        collect.sort(String::compareToIgnoreCase);
        LambdaUtils.out("【使用方法引用】对苹果的color进行排序 ： ", collect);

        //另一种实现方式
        appleList.sort(Comparator.comparing(apple -> apple.getColor()));
        LambdaUtils.out("使用【Comparator.comparing】根据color排序后的Apple ", appleList);
        //方法引用方式
        appleList.sort(Comparator.comparing(Apple::getColor));
        LambdaUtils.out("使用【Comparator.comparing】 & 使用【方法引用】根据color排序后的Apple ", appleList);

        //////////////////////////////////////////////////////////////////////////////////////////////////////

        //构造函数引用
        Supplier<Apple> appleNew = () ->  new Apple("yellow", 250);//创建一个apple对象
        Apple apple = appleNew.get();
        System.out.println("新创建的Apple对象 ： " + apple);

        //使用方法引用与Lambda比较
        Supplier<Apple> appleSupplier = Apple::new;
        BiFunction<String, Integer, Apple> biFunction = Apple::new;
        BiFunction<String, Integer, Apple> biFunction_Lambda = (color, height) -> new Apple(color, height);
        Apple green = biFunction.apply("green", 666);
        Apple green_lambda = biFunction_Lambda.apply("green", 777);
        System.out.println("BiFunction<String, Integer, Apple> biFunction = Apple::new 创建的对象 is " + green + ", 其Lambda形式创建的对象 is " + green_lambda);

    }

    /**
     * 复合lambda表达式
     */
    public static void complexLambda(){
        /**************************** ①.比较器复合 ***********************************/
        appleList.sort(Comparator.comparing(Apple::getHeight).reversed());
        LambdaUtils.out("比较器复合 - 重量逆序后的 Apple ：", appleList);

        //按照重量进行排序，当两个苹果重量一样时，按照颜色进行排序
        appleList.sort(Comparator.comparing(Apple::getHeight).thenComparing(Apple::getColor));
        LambdaUtils.out("比较器复合 - 重量排序 THEN Color排序 ： ", appleList);

        /**************************** ②.谓词复合 ***********************************/

        /**
         * negate	非
         * and		和
         * or		或
         */

        Apple apple = new Apple("yellow", 100);
        Predicate<Apple> predicate = a -> a.getHeight()>120;//苹果重量大于120的函数式表达式
        //100 > 120 返回false
        System.out.println("100 > 120 :" + predicate.test(apple));
        System.out.println("100 > 120 but negate : " + predicate.negate().test(apple));
        //在重量小于120的同时，再加上绿色的筛选条件    true
        System.out.println("100 > 120 negate! and color is yellow : " + predicate.negate().and(a -> "yellow".equals(a.getColor())).test(apple));
        //100 > 120 or color=yellow    true
        System.out.println("100 > 120 or color is yellow : " + predicate.or(a -> "yellow".equals(a.getColor())).test(apple));

        /**************************** ③.函数复合 ***********************************/

        //Function接口的表达式，andThen和compose两个默认方法
        //先加1，再乘以2
        Function<Integer, Integer> add = x -> x + 1;
        Function<Integer, Integer> multiply = x -> x * 2;
        Function<Integer, Integer> andThen = add.andThen(multiply);
        Integer result = andThen.apply(2);
        System.out.println("【andThen】 x + 1 => x * 2 = " + result);

        //先 * 2 再 + 1
        Function<Integer, Integer> compose = add.compose(multiply);
        System.out.println("【compose】 x * 2 => x + 1 = " + compose.apply(2));

        //总结：andThen [先] 执行调用者。 compose [后] 执行调用者


    }





}
