package optional;

import domain.Female;
import domain.Male;

import java.util.Optional;

public class OptionalDemo {

    public static void main(String[] args) {
        test();
    }

    public static void test(){

        /**
         * 1. 使用Optional的几种形式
         */
        //声明一个空的Optional
        Optional<Male> male = Optional.empty();

        Female female = new Female("LiYi");
        //依据一个非空值创建Optional,如果car是一个null，这段代码会立即抛出一个NullPointerException，
        // 而不是等到你试图访问car的属性值时才返回一个错误。
        Optional<Female> femaleOptional = Optional.of(female);

//        可接受null的Optional;如果car是null，那么得到的Optional对象就是个空对象。
        Female female2 = new Female();
        Optional<Female> optCar = Optional.ofNullable(female2);

        /**
         * 2. 使用map 从Optional 对象中提取和转换值
         *  同流的map方法相差无几。只不过Optional中只有一个元素
         */
        Optional<String> feName = femaleOptional.map(Female::getName);

        /**
         * 3. 使用flatMap 链接Optional 对象
         */
        Optional<Male> male1 = Optional.of(new Male("Rt", Optional.of(female)));
        Optional<String> s = male1.map(Male::getGirlfriend).map(female1 -> female.getName());
        s.ifPresent(System.out::println);

        /**
         * 4. 使用filter
         */
        male1.filter(male2 -> male2.getGirlfriend()!=null).ifPresent(x ->System.out.println("ok"));



// get()是这些方法中最简单但又最不安全的方法。如果变量存在，它直接返回封装的变量
//        值，否则就抛出一个NoSuchElementException异常。所以，除非你非常确定Optional
//        变量一定包含值，否则使用这个方法是个相当糟糕的主意。此外，这种方式即便相对于
//        嵌套式的null检查，也并未体现出多大的改进。
// orElse(T other)是我们在代码清单10-5中使用的方法，正如之前提到的，它允许你在
//        Optional对象不包含值时提供一个默认值。
// orElseGet(Supplier<? extends T> other)是orElse方法的延迟调用版，Supplier
//        方法只有在Optional对象不含值时才执行调用。如果创建默认值是件耗时费力的工作，
//        你应该考虑采用这种方式（借此提升程序的性能），或者你需要非常确定某个方法仅在
//        Optional为空时才进行调用，也可以考虑该方式（这种情况有严格的限制条件）。
// orElseThrow(Supplier<? extends X> exceptionSupplier)和get方法非常类似，
//        它们遭遇Optional对象为空时都会抛出一个异常，但是使用orElseThrow你可以定制希
//        望抛出的异常类型。
// ifPresent(Consumer<? super T>)让你能在变量值存在时执行一个作为参数传入的
//        方法，否则就不进行任何操作。

    }

}
