package async;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 组合式异步编程
 *
 * 什么是异步编程？
 * 假如你的程序需要与互联网上的多个Web服务通信。你并不希望因为等待某些服务的响应，阻塞应用程序的运行。
 * 比如说：不要因为等待qq消息的数据，暂停对来自腾讯新闻推过来的数据进行处理
 */

public class CompletableFutureDemo {

    public static final Random RANDOM = new Random();
    public static List<Shop> shops = Arrays.asList(new Shop("rongtao"),
            new Shop("liyi"),
            new Shop("family"),
            new Shop("can"),
            new Shop("we"));
    public static void main(String[] args) {
        test();
    }

    public static void test(){

        /**
         * 1. Future接口：在Future中执行耗时的操作，把线程解放出来，让他继续执行其他工作，不需要等待耗时的操作完成。
         */
//        futureTest();

//        以并发的方式，新起一个线程去执行耗时的动作，同时还可以执行一些其他动作。当你必须要得到异步的结果时，调用Feture的get方法获取结果。
//        如果异步操作完成了，get后得到异步执行结果。否则，它会阻塞线程，直到异步线程操作完成。
//        如果异步操作耗时较长，我们不可能无止境的等下去。使用.get(1, TimeUnit.SECONDS)定义超时时间：表示如果阻塞，最多等待1秒钟之后退出。

        /**
         * 2. Feture的局限性：当有多个Feture需要配合工作（A结果依赖B，A与B结果合并等），它就很鸡肋了
         */

        /**
         * 3. 使用CompletableFuture构建异步应用
         */
        long start = System.currentTimeMillis();
//        System.out.println(getPrices("myPhone"));
//        System.out.println(getPricesParallel("myPhone"));
        System.out.println(getPriceAsyncExecutor("myPhone"));
        System.out.println("耗时："+(System.currentTimeMillis()-start));


    }

    //串行
//    [rongtao price is 162.24388753441644, liyi price is 124.25168674134127, family price is 189.33664508120472, can price is 146.40423110971366, we price is 193.41041221052936]
//    耗时：5110
    public static List<String> getPrices(String product){
        return shops.stream().map(shop -> String.format("%s price is %s",shop.getName(), shop.getPrice(product))).collect(Collectors.toList());
    }

    //优化1：使用并行流
//    [rongtao price is 214.30908150815486, liyi price is 148.1433906563585, family price is 144.51582605529705, can price is 210.72839823921146, we price is 224.07197672145446]
//    耗时：2098
    public static List<String> getPricesParallel(String product){
        return shops.parallelStream().map(shop -> String.format("%s price is %s",shop.getName(), shop.getPrice(product))).collect(Collectors.toList());
    }

    //优化3：使用异步请求执行同步的API
//      [rongtao price is 142.92538080702096, liyi price is 154.35379147650573, family price is 216.1192597832072, can price is 222.1204448309338, we price is 186.467827182375]
//    耗时：2150
    /**
     * 使用2个不同的Stream流水线，而不是在同一个处理流的流水线上放两个Map操作。
     * 因为流操作之间 具有  延迟特性  ，如果在单一流水线中处理流，发向不同商家的请求只能以同步、顺序执行的方式才会成功！否则不会正确执行，比如向数据库中获取商家数据时，就会导致某些商家获取不到数据
     */
    public static List<String> getPriceAsync(String product){
        //以异步的方式计算价格
        List<CompletableFuture<String>> futures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %s", shop.getName(), shop.getPrice(product)))).collect(Collectors.toList());
//        等待所有一步操作结束
//        join方法同get相同，唯一不同的时join不会抛出任何受检异常。无需使用try/catch包围
        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }


    //并行流与CompletavleFuture效率差不错，是因为他们内部使用的是通用线程池，默认使用固定数目的线程
//    具体线程数取决于Runtime.getRuntime().availableProcessors()的返回值
//    而CompletavleFuture可以配置线程池的大小，充分发挥CPU的性能
/**
 * 如何选择合适的线程数目？
 * Nthreads = NCPU * UCPU * (1 + W/C)
 * ❑NCPU是处理器的核的数目，可以通过Runtime.getRuntime().availableProcessors()得到
 * ❑UCPU是期望的CPU利用率（该值应该介于0和1之间）
 * ❑W/C是等待时间与计算时间的比率
 *
 * 你的应用99%的时间都在等待商店的响应，所以估算出的W/C比率为100。这意味着如果你期望的CPU利用率是100%，
 *
 * 你需要创建一个拥有400个线程的线程池。实际操作中，如果你创建的线程数比商店的数目更多，反而是一种浪费，因为这样做之后，你线程池中的有些线程根本没有机会被使用。
 * 出于这种考虑，我们建议你将执行器使用的线程数，与你需要查询的商店数目设定为同一个值，这样每个商店都应该对应一个服务线程。不过，为了避免发生由于商店的数目过
 * 多导致服务器超负荷而崩溃，你还是需要设置一个上限，比如100个线程。
 */
    //优化4：定制线程池
    private static final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);//使用守护线程——这种方式不会阻止程序的关停
            return t;
        }
    });

    public static List<String> getPriceAsyncExecutor(String product){
        //以异步的方式计算价格
        List<CompletableFuture<String>> futures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %s", shop.getName(), shop.getPrice(product), executor))).collect(Collectors.toList());
//        等待所有一步操作结束
//        join方法同get相同，唯一不同的时join不会抛出任何受检异常。无需使用try/catch包围
        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    /**
     * 总结：什么时候使用并行流？什么时候使用CompletableFuture？
     *
     * 并行流：适用于计算密集型操作，并且没有I/O，使用并行流效率是最高的
     * CompletableFuture：如果涉及等待I/O的操作，包括网络连接。这种比并行流更灵活，你可以定制线程池。并且例如查询数据库等，使用并行流程序很可能不正常！
     */

    /**
     * 4.多个异步任务进行流水线操作
     *
     * thenCompose：方法允许你对两个异步操作进行流水线，第一个操作完成时，将其结果作为参数传递给第二个操作
     *
     * thenComposeAsync：
     *
     * 通常而言，名称中不带Async的方法和它的前一个任务一样，在同一个线程中运行；
     * 而名称以Async结尾的方法会将后续的任务提交到一个线程池，所以每个任务是由不同的线程处理的。
     *
     * *******************************************************************************************************
     * 就这个例子而言，第二个CompletableFuture对象的结果取决于第一个CompletableFuture，
     * 所以无论你使用哪个版本的方法来处理CompletableFuture对象，对于最终的结果，或者大致的时间而言都没有多少差别。
     * 我们选择thenCompose方法的原因是因为它更高效一些，因为少了很多线程切换的开销。
     */
//    public static List<String> getPriceAsyncExecutorMany(String product){
//        List<CompletableFuture<Object>> collect = shops.stream()
//                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product)))
//                .map(future -> future.thenApply(Double::intValue))//如果值存在，对其进行操作，不会阻塞
//                //使用另一个异步任务获取折扣价
//                //thenCompose：、依赖于上一个Future
//                .map(future -> future.thenCompose(integer -> CompletableFuture.supplyAsync(() -> selectDiscount(integer))))
//                .collect(Collectors.toList());
//
//        return collect.stream().map(CompletableFuture::join).collect(Collectors.toList());//等待流中所有的Future执行完毕，提取各自的返回值
//    }

    /**
     * 2.thenCombine:不依赖于任何Future
     * 它接收名为BiFunction的第二参数，这个参数定义了当两个CompletableFuture对象完成计算后，结果如何合并
     *
     * 提供有一个Async的版本。这里，如果使用thenCombineAsync会导致BiFunction中定义的合并操作被提交到线程池中，由另一个任务以异步的方式执行。
     */
    public void testCombine(String product){
        shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                        .thenCombine(
                                CompletableFuture.supplyAsync(() -> shop.getPrice(product)), //假设获取美元和人民币的转换汇率
                                (price, rate) -> price * rate))//接收BiFunction，将两个结果“合并”成新结果
                .collect(Collectors.toList());

    }

    /**
     * 由于这里的整合操作只是简单的乘法操作，用另一个线程执行有些资源浪费。即不需要使用thenCombine的异步版本thenCombineAsync
     */

    /**
     * 5. allOf:等待所有future执行完毕
     *
     *    anyOf：任意一个future执行完毕就返回
     */
    public void test002(){
        List<CompletableFuture> cfs = new ArrayList<>(1);
        CompletableFuture<Void> all = CompletableFuture.allOf(cfs.toArray(new CompletableFuture[cfs.size()]));
        //线程阻塞在这里，等待所有future执行完毕
        all.join();
    }

    public static void futureTest(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        //以异步的方式 开启一个新线程执行耗时的操作
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Shop.delay();
                int a = RANDOM.nextInt();
                System.out.println("异步线程执行耗时操作ING...返回结果为："+a);
                return a;
            }
        });
        System.out.println("异步操作进行的同时，我可以做其他事情");//异步操作进行的同时，你可以做其他事情
        try {
            Integer result = future.get(1, TimeUnit.SECONDS);//获取异步操作的结果，如果最终被阻塞，无法获取到结果，最终等待1S之后退出
            System.out.println("取得future结果："+result);
        } catch (ExecutionException ee) {
            // 计算抛出一个异常
        } catch (InterruptedException ie) {
            // 当前线程在等待过程中被中断
        } catch (TimeoutException te) {
            // 在Future对象完成之前超过已过期
        }
    }


    private class ThreadFactoryBuilder {
    }
}
