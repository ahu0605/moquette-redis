package sk.broker.uitls;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
	private final static JedisPool pool ;
    
    /**
     * 构建redis连接池
     * 
     * @param ip
     * @param port
     * @return JedisPool
     */
	static{
		JedisPoolConfig config = new JedisPoolConfig();
        //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        config.setMaxIdle(500);
        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(5);
        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(1000 * 100);
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(true);
        pool = new JedisPool(config, "ott.tserv.4kgarden.com", 1111);
	}
   
    /**
     * 返还到连接池
     * 
     * @param pool 
     * @param redis
     */
    @SuppressWarnings("deprecation")
	public static void returnResource(Jedis redis) {
        if (redis != null) {
        	redis.close();
        }
    }
     
    /**
     * 获取数据
     * 
     * @param key
     * @return
     */
    public static String get(String key){
        String value = null;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            //释放redis对象
        	jedis.close();
            e.printStackTrace();
        } finally {
            //返还到连接池
            returnResource(jedis);
        }
         
        return value;
    }
    
    /**
     * 获取数据
     * 
     * @param key
     * @return
     */
    public static String set(String key,String value){
     
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            value = jedis.set(key,value);
        } catch (Exception e) {
            //释放redis对象
        	jedis.close();
            e.printStackTrace();
        } finally {
            //返还到连接池
            returnResource(jedis);
        }
         
        return value;
    }
    
    public static void main(String[] args) {
    	set("name","test");
    	System.out.println(get("name"));
	}
}
