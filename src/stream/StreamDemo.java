package stream;

import domain.Apple;
import domain.Dish;
import domain.Trader;
import domain.Transaction;
import util.LambdaUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamDemo {
    //卡路里级别
    public enum CaloricLevel { DIET, NORMAL, FAT };

    private static List<Dish> menuList;//菜单列表

    static {
        menuList = Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH));
    }
    public static void main(String[] args) {
        tasteStream();
    }

    /**
     * Stream 流实践
     */
    public static void tasteStream(){

        /************************************************* 第 4 章 What is Stream？ ************************************************************/

        /**
         * 1.初体验
         * 注意：尽管filter和map是两个独立的操作，但他们合并到同一次遍历中了。
         */
        long start = System.currentTimeMillis();
        List<String> namesGt5 = menuList.stream()
                .filter(dish -> dish.getCalories() > 500)//筛选热量大于500
                .map(Dish::getName)//获取名字
                .limit(3)//前三个
                .collect(Collectors.toList());//转List
        out("非并行时间", System.currentTimeMillis()-start);
        LambdaUtils.out("[filter]热量>500 & [map]获取名字 & [limit]3个", namesGt5);

        /**
         * 2.利用多核CPU
         *      为了利用【多核】架构【并行】执行这段代码，把stream()换成parallelStream()
         * 注意：parallelStream()在正式环境中非常容易出现问题，具有一定的不稳定行，慎用！！！
         */
        long start2 = System.currentTimeMillis();
        List<String> namesGt5Parallel = menuList.parallelStream()
                .filter(dish -> dish.getCalories() > 500)//筛选热量大于500
                .map(Dish::getName)//获取名字
                .limit(3)//前三个
                .collect(Collectors.toList());//转List
        out("并行时间", System.currentTimeMillis()-start2);
        LambdaUtils.out("# parallelStream # [filter]热量>500 & [map]获取名字 & [limit]3个", namesGt5Parallel);

        /**
         * 3.注意：流与迭代器类似，只能遍历一次。使用完后，流就被消费掉了。
         *
         * 中间操作不会消耗流，终端操作会消耗流，以产生一个最终结果。
         *
         * 中间操作：filter、map、limit... （中间操作会返回一个流）
         * 终端操作：终端操作会从流的流水线生成结果。其结果是任何不是流的值。比如List、Integer，甚至void。例如menu.stream().forEach(System.out::println)
         *
         */


        /**
         * 4.外部迭代与内部迭代
         */

        /**
         * 4.1   传统外部迭代 for-each
         * for-each其实是一个语法糖，它背后使用Iterator来实现
         */
        List<String> names = new ArrayList<>();
        for(Dish d : menuList){
            names.add(d.getName());
        }

        /**
         * 4.2 用for-each背后的Iterator做外部迭代
         */
        Iterator<Dish> iterator = menuList.iterator();
        while(iterator.hasNext()){
            names.add(iterator.next().getName());
        }

        /**
         * 4.3 Stream 内部迭代
         */
        //使用map提取菜名，collect终端操作消费流
        names.addAll(menuList.stream().map(Dish::getName).collect(Collectors.toList()));

        /**
         * 4.4 内部迭代相比外部迭代的优势
         *      1）内部迭代时，项目可以透明的并行处理，或者用更优化的顺序进行处理
         */

        /**
         * 5. 流使用三部曲
         *      1）一个数据源（如集合）执行一个查询
         *      2）一些列的中间操作，形成一条中间操作流水线
         *      3）一个终端操作，执行流水线，并生成任意非流的结果
         */


        /************************************************* 第 5 章 使用流 ************************************************************/

        /**
         * 6.1 筛选流
         *      filter：接受boolean表达式
         *      distinct：元素去重
         *      limit(n)：截短流，获取前n个元素，注意并不会排序
         *      skip(n)：跳过前n个元素，即扔掉前n个元素返回剩下的元素。如果流中的数据不足n个，则返回一个空流。
         */
        menuList.stream()
                .filter(Dish::isVegetarian)//筛选所有素食菜
                .distinct()//并去重
                .skip(2)//跳过前2道菜
                .limit(3);//取前3道菜

        /**
         * 6.2 映射
         *      map:对流中每个元素执行相应操作，并形成一个新元素。map方法接受一个函数
         *      flatMap：把流中每一个值都换成另一个流，然后把所有流链接起来成为1个流。常用来将"数组流"转化成"基本流"（ Stream<String[]> -> Stream<String> ）
         */
        //从菜单中提取出菜单的名字 流中对象由 Dish -》 String
        menuList.stream().map(Dish::getName);

        //每道菜的名称多长
        List<Integer> nameLengths = menuList.stream()
                .map(Dish::getName)//Dish -》 String
                .map(String::length)//String -》 int
                .collect(Collectors.toList());//终端操作：收集流（将流转成集合 Stream -》 List）

        //问题:给定单词列表["Hello","World"]，你想要返回列表["H","e","l", "o","W","r","d"],列出里面各不相同的字符呢?
        List<String> words = Arrays.asList("hello", "world");
        //第一个版本,返回String[]
        List<String[]> collect = words.stream()
                .map(s -> s.split(""))
                .distinct()//去重
                .collect(Collectors.toList());

        //使用flatMap
        List<String> collect1 = words.stream()
                .map(s -> s.split(""))//Stream<String[]>
                .flatMap(Arrays::stream)//Stream<String>
                .distinct()//去重
                .collect(Collectors.toList());

        /**
         * 7 查找与匹配
         *      allMatch: 所有元素都匹配
         *      anyMatch: 是否有一个元素能匹配
         *      noneMatch: 没有任何元素匹配
         * allMatch|anyMatch|noneMatch：【终端操作】，接受boolean表达式。 而且allMatch & anyMatch & noneMatch都使用了★短路★的思想。
         *      findFirst: 查找第一个元素
         *      findAny: 返回流中任意元素
         */
        //anyMatch(流中是否有一个元素能匹配给定的谓词)
        boolean isVegetarian = menuList.stream().anyMatch(Dish::isVegetarian);

        //allMatch(流中的所有元素都能匹配给定的谓词)
        boolean isHealthy = menuList.stream().allMatch(d -> d.getCalories() < 1000);

        //noneMatch(流中没有任何元素与给定的谓词匹配)
        boolean noHealthy = menuList.stream().noneMatch(d -> d.getCalories() >= 1000);

        //获得任意的素食菜
        Optional<Dish> any = menuList.stream().filter(Dish::isVegetarian).findAny();
        //获得卡路里大于600的第一道菜
        Optional<Dish> first = menuList.stream().filter(dish -> dish.getCalories() > 600).findFirst();


        /**
         * 8 Optional 简介 显式地检查值是否存在或处理值不存在的情形
         *      isPresent()将在Optional包含值的时候返回true, 否则返回false。
         *      ifPresent(Consumer<T> block)会在值存在的时候执行给定的代码块。(Consumer接收T类型参数，并返回void的Lambda)
         *      T get()会在值存在时返回值，否则抛出一个NoSuchElement异常
         *      T orElse(T other)会在值存在时返回值，否则返回一个默认值
         */
        menuList.stream().filter(Dish::isVegetarian)
                .findAny()//findAny()
                .ifPresent(d -> out("筛选素食Optional.ifPresent", d.getName()));//如果结果集中存在，则输出名字，否则什么都不做，避免了null异常

        //举例
        List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5);
        Optional<Integer> first1 =
                someNumbers.stream()
                        .map(x -> x * x)
                        .filter(x -> x % 7 == 0)
                        .findFirst();
        out("Optional.orElse", first1.orElse(-1));//值存在的时候返回值，否则返回-1

        /**
         * 9. 归约：将六中的元素组合起来。
         * reduce:元素求和、最大最小值等
         * reduce(初始值, BinaryOperator<T> 函数):初始值就是设置一个初始值，函数就是Lambda形式的运算操作。而BinaryOperator用于同类型运算
         */

        /**
         * 9.1 元素求和
         */
        //传统for-each循环求和
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
        int sum = 0;
        for (int x : numbers) {
            sum += x;
        }

        //使用reduce求和
        Integer add = numbers.stream().reduce(0, (a, b) -> a + b);//初始值0，运算操作a+b
        out("使用reduce求和",add);
        //乘法
        Integer multiply = numbers.stream().reduce(0, (a, b) -> a * b);

        //使用方法引用
        //Integer有一个静态的sum方法对两个数求和
        Integer add2 = numbers.stream().reduce(0, Integer::sum);

        /**
         * reduce无初始值，使用Optional接收返回值，表明reduce值可能不存在
         */
        Optional<Integer> optional = numbers.stream().reduce((a, b) -> a + b);

        /**
         * 9.2 最大最小值
         */
        //最大值
        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        //最小值
        Optional<Integer> min = numbers.stream().reduce((a, b) -> a < b ? a : b);
        Optional<Integer> min2 = numbers.stream().reduce(Integer::min);
        System.out.println("min = " + min.get() + ", min2 = " + min2.get());//结果都是1

        //虽然都能得到最小值，但显然后者更易读

        //问题：使用map和reduce统计菜单中有多少个菜呢？
        Integer reduce = menuList.stream().map(m -> 1).reduce(0, Integer::sum);
        //使用map方法将集合中元素映射成1，然后使用reduce方法进行求和

        //但是怎么看这种实现方式都有点别扭，当然，流已经提供了相应的方法
        /**
         * count：统计流中元素个数
         */
        long count = menuList.stream().count();

        /**
         * 10. 几个问题强化理解
         */
        //Transaction：交易，Trader：交易员
        List<Transaction> transactions = Arrays.asList(
                new Transaction(new Trader("Brian","Cambridge"), 2011, 300),
                new Transaction(new Trader("Raoul", "Cambridge"), 2012, 1000),
                new Transaction(new Trader("Raoul", "Cambridge"), 2011, 400),
                new Transaction(new Trader("Mario","Milan"), 2012, 710),
                new Transaction(new Trader("Mario","Milan"), 2012, 700),
                new Transaction(new Trader("Alan","Cambridge"), 2012, 950) );

        //1.找出2011年的所有交易并按交易额排序（从低到高）
        List<Transaction> tr2011 = transactions.stream()
                .filter(d -> d.getYear() == 2011)
                .sorted(Comparator.comparing(Transaction::getValue))
                .collect(Collectors.toList());

        //2.交易员都在哪些不同的城市工作过
        List<String> cities = transactions.stream()
                .map(d -> d.getTrader().getCity())
                .distinct()
                .collect(Collectors.toList());

        //新招：你可以去掉distinct()，改用toSet()，这样就会把流转换为集合
        Set<String> cities1 = transactions.stream()
                .map(transaction -> transaction.getTrader().getCity())
                .collect(Collectors.toSet());

        //3.查找所有来自于剑桥的交易员，并按姓名排序
        List<Trader> traders = transactions.stream()
                .map(Transaction::getTrader)
                .filter(trader -> trader.getCity().equals("Cambridge"))
                .distinct()
                .sorted(Comparator.comparing(Trader::getName))
                .collect(Collectors.toList());

        //4.有没有交易员是在米兰工作的
        boolean anyMatch = transactions.stream()
                .anyMatch(d -> d.getTrader().getCity().equals("Milan"));

        //...多在工作中使用练习吧!

        /**
         * 11. 数值流：为了避免装箱操作
         *      IntStream、DoubleStream和LongStream分别将流中的元素特化为int、long和double
         *      将流转换为特化版本的常用方法是 mapToInt、mapToDouble 和 mapToLong。它们返回的是一个特化流，而不是Stream<T>
         */
        //使用reduce计算菜单中总热量
        Integer totalCalories = menuList.stream().map(Dish::getCalories).reduce(0, Integer::sum);
        //注：这段代码有一个问题就是，他有一个暗含的装箱成本。将上边代码拆开成 两 部分更为清晰
        //Dish::getCalories返回int，需要装箱操作才能存储在流中。而进行sum时，Stream流中的元素是Integer类型，需要先转为int，再进行求和。
        Stream<Integer> integerStream = menuList.stream().map(Dish::getCalories);
        Integer sum1 = integerStream.reduce(0, Integer::sum);

        //1.使用特化流计算总热量
        //a.通过mapToInt方法将流转为特化流。而不是Stream<Integer>
        IntStream intStream = menuList.stream().mapToInt(Dish::getCalories);
        //b.通过特化流intStream的方法进行求和
        int sum2 = intStream.sum();
        //c.intStream还支持其他方法max,min,average等

        //2.将特化流转为正常流 - boxed()
//        Stream<Integer> boxed = intStream.boxed();

        /**
         * 3. 生成数值范围
         *      IntStream和LongStream：（start,end）
         *      range和rangeClosed，区别是range不包含结束值，rangeClosed包含结束值
         */
        IntStream.range(0, 10).boxed().forEach(System.out::println);//0-9
        IntStream.rangeClosed(0, 10).boxed().forEach(System.out::println);//0-10

        /**
         * 12. 构建流
         */
        //1.由数值创建流、
        Stream<Integer> of = Stream.of(1, 2, 3, 4, 5, 3, 6, 7, 3, 2, 1);
        of.distinct().forEach(System.out::println);//求不重复数并打印
        //创建了一个字符串流
        Stream<String> of2 = Stream.of("Hello World","Java");
        of2.map(String::toUpperCase).forEach(System.out::println);//转成大写

        //2.由数组创建流
        int[] intArray = {1,2,3,4,5,6};
        String[] stringArray = {"l","y","w","a"};
        Apple[] appleArray = {new Apple("red", 10), new Apple("yellow", 100)};

        int sum3 = Arrays.stream(intArray).sum();//整数数组求和
        Arrays.stream(stringArray).sorted().forEach(System.out::println);//字符数组排序
        Arrays.stream(appleArray).map(Apple::getHeight).reduce(Integer::max).ifPresent(System.out::println);//获取苹果最大重量，如果存在，就输出这个元素


        /************************************************* 第 6 章 收集器 ************************************************************/

        /**
         * 13. 收集器Collector：使用流收集数据
         */

        //对交易员按照国家进行分组
        Map<Trader, List<Transaction>> collect2 = transactions.stream().collect(Collectors.groupingBy(Transaction::getTrader));

        //统计下菜单中菜的个数
        menuList.stream().collect(Collectors.counting());
        //当然，这样写更加直接
        menuList.stream().count();

        /**
         * 计算流中的最大或最小值: 接收一个Comparator参数来比较流中的元素。
         * Collectors.maxBy
         * Collectors.minBy
         */
        //查找流中热量最高的菜
        Optional<Dish> collect3 = menuList.stream().collect(Collectors.maxBy(Comparator.comparingInt(Dish::getCalories)));
        collect3.ifPresent(System.out::println);//如果存在就输出

        /**
         * 13.1 汇总
         *  求和：
         *      Collectors.summingInt：int型求和
         *      Collectors.summingDouble：double型求和
         *      Collectors.summingLong：long型求和
         *  求平均数：
         *      Collectors.averagingInt
         *      Collectors.averagingDouble
         *      Collectors.averagingLong
         *
         *  更全面：
         *      返回值包含：sum总和，average平均，max最大，min最小，count元素个数。
         *      Collectors.summarizingInt
         *      Collectors.summarizingDouble
         *      Collectors.summarizingLong
         *
         *  广义的汇总
         *      Collectors.reducing
         */

//        summingInt
        Integer collect5 = menuList.stream().collect(Collectors.summingInt(Dish::getCalories));
        out("Collectors.summingInt",collect5);

//        averagingInt
        Double collect6 = menuList.stream().collect(Collectors.averagingInt(Dish::getCalories));
        out("Collectors.averagingInt",collect6);

        //summarizingInt
        IntSummaryStatistics collect4 = menuList.stream().collect(Collectors.summarizingInt(Dish::getCalories));
        out("Sum", collect4.getSum());
        out("Average", collect4.getAverage());
        out("Count", collect4.getCount());
        out("Max", collect4.getMax());
        out("Min", collect4.getMin());

//        reducing
        menuList.stream().collect(Collectors.reducing(0, Dish::getCalories, Integer::sum));
        //其他写法
        menuList.stream().map(Dish::getCalories).reduce(0, Integer::sum);
        menuList.stream().mapToInt(Dish::getCalories).sum();
        /**
         * 根据实际情况，选择最优解决方案。此处选择第三种，它避免了拆箱操作，效率更高。上面解释了为什么它避免了拆箱
         * 在通用的方案里面，始终选择最专门化的一个.无论是从可读性还是性能上看，这一般都是最好的决定
         */

        /**
         * 13.2 连接字符串
         * Collectors.joining：支持使用分隔符
         */
        List<String> strings = Arrays.asList("hello", "world");
        String collect7 = strings.stream().collect(Collectors.joining());//helloworld
        out("Collectors.joining()", collect7);

        //支持分隔符：元素以分隔符分隔开
        String collect8 = strings.stream().collect(Collectors.joining(","));//hello,world
        out("Collectors.joining(\",\")", collect8);


        /**
         * 14. 分组
         * Collectors.groupingBy
         */

//        对交易员按照国家进行分组
        Map<Trader, List<Transaction>> groupingBymap = transactions.stream().collect(Collectors.groupingBy(Transaction::getTrader));

        //菜单中的菜按照类型进行分类，鱼类放在一起，肉类放在一起等。。
        Map<Dish.Type, List<Dish>> groupingBy = menuList.stream().collect(Collectors.groupingBy(Dish::getType));
        out("菜单中的菜按照类型进行分类，鱼类放在一起，肉类放在一起",groupingBy);//{OTHER=[french fries, rice, season fruit, pizza], MEAT=[pork, beef, chicken], FISH=[prawns, salmon]}

        //按照低热量和高热量进行分组。小于400低热量；大于400小于700普通，大于700高热量
        Map<CaloricLevel, List<Dish>> collect9 = menuList.stream().collect(Collectors.groupingBy(dish -> {
            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
            else return CaloricLevel.FAT;
        }));
        out("按照低热量和高热量进行分组。小于400低热量；大于400小于700普通，大于700高热量",collect9);//{FAT=[pork], NORMAL=[beef, french fries, pizza, salmon], DIET=[chicken, rice, season fruit, prawns]}

        /**
         * 14.2 多级分组：将GroupingBy分为多级，级嵌套型。
         */
        Map<Dish.Type, Map<CaloricLevel, List<Dish>>> collect10 = menuList.stream().collect(
                Collectors.groupingBy(Dish::getType, //先按照类型分组
                        Collectors.groupingBy(dish -> {//再根据热量分组
                            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                            else return CaloricLevel.FAT;
                        })
                )
        );
        out("多级分组,先按照类型分组,再根据热量分组",collect10);
        /**
         * {
         *  OTHER={
         *      NORMAL=[french fries, pizza],
         *      DIET=[rice, season fruit]},
         *  MEAT={
         *      FAT=[pork],
         *      NORMAL=[beef],
         *      DIET=[chicken]},
         * FISH={
         *      NORMAL=[salmon],
         *      DIET=[prawns]}}
         */

//        而，单个GroupingBuy又相当于
        //因为他是吧每个TYPE下的元素封装进LiST中
        Map<Dish.Type, List<Dish>> collect11 = menuList.stream().collect(Collectors.groupingBy(Dish::getType, Collectors.toList()));
        out("吧每个TYPE下的元素封装进LiST中",collect11);//{OTHER=[french fries, rice, season fruit, pizza], MEAT=[pork, beef, chicken], FISH=[prawns, salmon]}

        //也就是说，groupbying的第二个参数，不一定是多级分组，它可以是任何类型
        //统计没类菜有多少个
        Map<Dish.Type, Long> collect12 = menuList.stream().collect(Collectors.groupingBy(Dish::getType, Collectors.counting()));
        out("统计没类菜有多少个", collect12);//{OTHER=4, MEAT=3, FISH=2}

        //查找每个分类下热量最高的菜
        //使用Optional接收是为了处理没有值得情况
        Map<Dish.Type, Optional<Dish>> collect13 = menuList.stream().collect(Collectors.groupingBy(Dish::getType, Collectors.maxBy(Comparator.comparingInt(Dish::getCalories))));
        out("查找每个分类下热量最高的菜", collect13);//{OTHER=Optional[pizza], MEAT=Optional[pork], FISH=Optional[salmon]}

        /**
         * Collectors.collectingAndThen：转换函数返回的类型
         */
//        把groupingBy收集器返回的结果转换成另一种类型
        //同样是：查找每个分类下热量最高的菜,不返回Optional
        Map<Dish.Type, Dish> collect14 = menuList.stream()
                .collect(Collectors.groupingBy(
                        Dish::getType,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingInt(Dish::getCalories)),
                                Optional::get)));
        out("查找每个分类下热量最高的菜,不返回Optional", collect14);//{OTHER=pizza, MEAT=pork, FISH=salmon}


        //求出每种类型菜的总热量
        Map<Dish.Type, Integer> collect15 = menuList.stream().collect(Collectors.groupingBy(Dish::getType, Collectors.summingInt(Dish::getCalories)));
        out("求出每种类型菜的总热量",collect15);//{OTHER=1550, MEAT=1900, FISH=750}

        //mapping收集器
        Map<Dish.Type, Set<String>> collect16 = menuList.stream().collect(Collectors.groupingBy(
                Dish::getType,
                Collectors.mapping(
                        Dish::getName, //将Dish -> name
                        Collectors.toSet()//转Set
                )));
        out("mapping收集器", collect16);//{OTHER=[season fruit, pizza, rice, french fries], MEAT=[chicken, beef, pork], FISH=[salmon, prawns]}


        /**
         * 15. 分区：分区是分组的特殊情况。
         *      分区函数返回一个Boolean值，所以分组Map的键类型是Boolean,即分组之后true为一组；false为一组
         *
         *     Collectors.partitioningBy（boolean flag）
         */
        //菜单按照素数和非素食分开
        Map<Boolean, List<Dish>> collect17 = menuList.stream().collect(Collectors.partitioningBy(Dish::isVegetarian));
        out("菜单按照素数和非素食分开", collect17);//{false=[pork, beef, chicken, prawns, salmon], true=[french fries, rice, season fruit, pizza]}
        collect17.get(true);
        collect17.get(false);
        //当然，使用filter也可以实现
        menuList.stream().filter(Dish::isVegetarian).collect(Collectors.toList());

        //partitioningBy的一个重载版本，传递第 2 个收集器
        Map<Boolean, Map<Dish.Type, List<Dish>>> collect18 = menuList.stream().collect(
                Collectors.partitioningBy(
                        Dish::isVegetarian,
                        Collectors.groupingBy(Dish::getType)));
        out("partitioningBy的一个重载版本，传递第 2 个收集器", collect18);//{false={MEAT=[pork, beef, chicken], FISH=[prawns, salmon]}, true={OTHER=[french fries, rice, season fruit, pizza]}}

        //统计素数菜几个，非素食菜几个
        Map<Boolean, Long> collect19 = menuList.stream().collect(Collectors.partitioningBy(Dish::isVegetarian, Collectors.counting()));
        out("统计素数菜几个，非素食菜几个", collect19);//{false=5, true=4}

        /**
         * 16 收集器分析
         *
         * public interface Collector<T, A, R> {
         *      Supplier<A> supplier(); 创建一个集合，供数据收集过程使用
         *      BiConsumer<A, T> accumulator(); 将元素添加到集合当中
         *      Function<A, R> finisher();  将accumulator中的对象转换为整个集合的最终结果
         *      BinaryOperator<A> combiner();   同类型操作，合并两个结果容器
         *      Set<Characteristics> characteristics(); 返回一个Set，它定义了收集器的行为。尤其是流是否可以进行并行归约，以及可以使用哪些优化的提示。
         * }
         *
         *
         */
//        1. Supplier<A> supplier()    创建一个集合，供数据收集过程使用
//        public Supplier<List<T>> supplier(){
//            return ()->new ArrayList<T>();
//            return ArrayList::new;
//        }

//        2. BiConsumer<A, T> accumulator(); 将元素添加到集合当中
//        public BiConsumer<List<T>, T> accumulator(){
//            return (list, item)->list.add(item);
//            return List::add;
//        }

//        3. Set<Characteristics> characteristics(); 返回一个Set，它定义了收集器的行为。尤其是流是否可以进行并行归约，以及可以使用哪些优化的提示。
//        Characteristics是一个包含三个项目的枚举。
//            UNORDERED—归约结果 不受流中项目的遍历顺序和累积顺序的影响
//            CONCURRENT—accumulator函数可以从多个线程同时调用，且该收集器可以并行归约流。如果收集器没有标为UNORDERED，那它仅在用于无序数据源时才可以并行归约。
//            IDENTITY_FINISH—累加器对象将会直接用作归约过程的最终结果。这也意味着，将累加器A不加检查地转换为结果R是安全的。

        /**
         * 17. 自定义收集器
         */

        /************************************************* 第 7 章 并行流 ************************************************************/

        //并行流就是把一个内容分成多个数据块，并用不同的线程分别处理每个数据块的流。充分利用CPU

        //Java7之前，并行处理非常麻烦。你需要吧一个“集合”进行分段，每段数据分配一个独立的线程，可能还会引入同步避免由于竞争引发的线程安全问题。最后，等待所有线程完成，把结果合并成最终结果。

        //Java 7  中引入了 分支/合并框架，让这些操作更稳定，不易出错、

        //Java 8 中使用parallelStream可以声明一个并行流，其幕后是使用 分支/合并 框架实现的。
        //了解并行流内部是如何工作的是很重要的。工作中也有很多坑，很可能会因为误用并行流导致线上重大BUG问题


        /**
         * 1. 并行与顺序执行
         * parallel():之后进行的所有操作都并行执行
         * sequential()：之后的操作顺序执行
         */
        //控制那些操作顺序执行，那些操作并行执行
        menuList.stream()
                .filter(dish -> dish.getCalories()>0)
                .sequential()
                .mapToInt(Dish::getCalories)
                .parallel()
                .sum();
//        最后一次parallel或sequential调用会影响整个流水线。例如上个例子中，最后调用的是parallel(),所以整个流水线最后会并行执行
        //使用mapToInt避免了装箱操作。

        /**
         * 并行流使用的线程池？线程从哪里来的？有多少个？
         * 并行流内部使用了默认ForkJoinPool（分支/合并框架），默认的线程数量是你机器的处理器的数量
         *      得到处理器数量值：Runtime.getRuntime().availableProcessors()
         *
         * 可以通过系统属性改变线程池大小
         *      System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "12")
         * 但它是一个全局的设置，也就是说，它将影响代码中所有的并行流。反过来说，目前无法为某个特定的并行流指定这个值。
         * 【不建议修改这个值】让ForkJoinPool的大小等于处理器数量是个不错的默认值。
         *
         */
        out("处理器数量值",Runtime.getRuntime().availableProcessors());
//        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "12");


//       1. 有时候并行流甚至比串行流速度慢很多
        /**
         * 原因有 2：
         * 其一，iterator生成的是装箱的对象，必须拆箱才能进行求和等操作
         * 其二，很难把iterator分成多个独立块来并行执行。因为每次应用这个函数都要依赖前一次应用的结果（iterator在本质上是顺序的）。
         */
//        所以在调用那个看似神奇的parallel操作时，了解背后到底发生了什么是很有必要的。否则可能适得其反。

        //使用正确的数据结构 然后使其 并行 工作能够保证 最佳 的性能。（避免拆装箱）

        //2. 正确的使用并行流：确保结果正确
//        错误使用并行流的一个常用场景就是：就是改变了共享变量。因为并行过程会分段，每段独立一个线程。这样就会产生线程安全的问题。不要尝试使用同步解决这类问题，那完全失去了并行的意义。

        /**
         * 总结：如何高效的利用并行流
         * 1.避免自动装箱和拆箱，它会降低性能。Java8中有原始类型流（IntStream,LongStream,DoubleStream）来避免拆装箱。
         * 2.有些操作本身在并行流上的性能就比顺序流差。特别是 limit 和 findFirst 等依赖于于元素顺序的操作，他们在并行流上执行的代价非常大。
         *      例如：findAny要比findFirst性能好，因为它不需要按顺序来执行。
         *      也可以调用unordered方法把 有序流 变成 无序流 。
         *          那么如果你拿流中的 n个元素 而不是 前n个元素 。对于无序的并行流调用 limit 可能会比 单个有序流（比如数据源是List） 更高效。
         * 3.如果数据量较小，不建议使用并行流。因为，并行处理少数几个元素的好处还 抵不过 并行化造成的额外开销。
         * 4.考虑流背后的数据结构是否易于分解。因为并行流需要将数据分段。例如ArrayLIst的拆分效率比LinkedList高的多。因为前者不需要遍历就可以平均拆分。而后者则必须遍历。
         * 5.使用filter的流不建议使用并行流。例如：本来List大小是确定的，并行流可以拆分成多段数据处理。但是筛选操作可能丢弃的元素个数是不确定的，导致流本身的大小也是未知的。
         * 6.考虑收集器Collectors收集过程的代价大小。如果收集代价很大，那么组合每个子流所付出的代价很可能超过并行流带来的性能提升。
         */
//        流数据源是否适用于并行总结。

//        源             可分解性
//        ArrayList         极佳
//        LinkedList        差
//        IntStream.range   极佳
//        Stream.iterate    差
//        HashSet           好
//        TreeSet           好

        long start3 = System.currentTimeMillis();
        IntStream.rangeClosed(0, 1000000).filter(value -> value%2==0).boxed().findFirst();
        out("非并行时间", System.currentTimeMillis()-start3);

        long start4 = System.currentTimeMillis();
        IntStream.rangeClosed(0, 1000000).parallel().filter(value -> value%2==0).boxed().findFirst();
        out("并行时间", System.currentTimeMillis()-start4);

//        上面这段代码使用并行流效率非常高
        //上面说法也不一定准确！！！！！！！！！！！！！所以要看具体场景吧
//        但是并行流具有一定的不稳定性，不要一遇到流就使用并行流。它可能给你带来灾难。




        /**
         * 并行流后盾：分支/合并框架
         *      以递归的方式将可以并行的任务拆分成更小的任务，然后将每个子任务的结果合并起来生成最终结果。
         */









    }

    public static void out(String msg, Object o){
        System.out.println(msg+" : "+o);
    }



}
