package stream;

import domain.Dish;
import util.LambdaUtils;

import java.util.*;
import java.util.stream.Collectors;

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
     * 体会 Stream 流
     */
    public static void tasteStream(){

        /**
         * 1.初体验
         * 注意：尽管filter和map是两个独立的操作，但他们合并到同一次遍历中了。
         */
        List<String> namesGt5 = menuList.stream()
                .filter(dish -> dish.getCalories() > 500)//筛选热量大于500
                .map(Dish::getName)//获取名字
                .limit(3)//前三个
                .collect(Collectors.toList());//转List
        LambdaUtils.out("[filter]热量>500 & [map]获取名字 & [limit]3个", namesGt5);

        /**
         * 2.利用多核CPU
         *      为了利用【多核】架构【并行】执行这段代码，把stream()换成parallelStream()
         * 注意：parallelStream()在正式环境中非常容易出现问题，具有一定的不稳定行，慎用！！！
         */
        List<String> namesGt5Parallel = menuList.parallelStream()
                .filter(dish -> dish.getCalories() > 500)//筛选热量大于500
                .map(Dish::getName)//获取名字
                .limit(3)//前三个
                .collect(Collectors.toList());//转List
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
                .ifPresent(d -> System.out.println(d.getName()));//如果结果集中存在，则输出名字，否则什么都不做，避免了null异常

        //举例
        List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5);
        Optional<Integer> first1 =
                someNumbers.stream()
                        .map(x -> x * x)
                        .filter(x -> x % 7 == 0)
                        .findFirst();
        System.out.println("Optional.orElse:" + first1.orElse(-1));//值存在的时候返回值，否则返回-1


    }





}
