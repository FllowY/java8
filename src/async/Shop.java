package async;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @Description: 最佳价格查询器
 * @Author: rongtao7
 * @Date: 2019/9/3
 */
public class Shop {

    private String name;

    public Shop(){}
    public Shop(String name) {
        this.name = name;
    }

    public static final Random RANDOM = new Random();

    /**
     * 使用工厂方法supplyAsync创建CompletableFuture
     *
     *  supplyAsync支持传入自己的线程池参数
     *  同样提供错误管理机制，上面代码中更繁琐
     *
     *  runAsync方法不支持返回值。
     *  supplyAsync可以支持返回值。
     */
    public Future<Double> getPriceAsync(String product){
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
    }

    /**
     * 模拟延迟
     */
    public static void delay(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    public static double calculatePrice(String product){
        delay();
        return RANDOM.nextDouble() * product.charAt(0) + product.charAt(1);
    }

    public double getPrice(String product){
        return calculatePrice(product);
    }

    public Future<Double> getPriceFutureAsync(String prouct){
        CompletableFuture<Double> future = new CompletableFuture<>();
        new Thread(() -> {
            try {
                double price = calculatePrice(prouct);
                future.complete(price);//任务结束时，设置future返回值
            } catch (Exception e) {
                future.completeExceptionally(e);//确定抛出了什么异常信息，方便定位问题
            }
        }).start();
        return future;//不等待还没结束的计算，直接返回
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
