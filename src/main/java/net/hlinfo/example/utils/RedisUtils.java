package net.hlinfo.example.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * spring redis 工具类
 * 
 **/
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Component
public class RedisUtils {
	@Autowired
	public RedisTemplate redisTemplate;

	/**
	 * 缓存基本的对象，Integer、String、实体类等
	 *
	 * @param key   缓存的键值
	 * @param value 缓存的值
	 * @return 缓存的对象
	 */
	public <T> ValueOperations<String, T> setCacheObject(String key, T value) {
		ValueOperations<String, T> operation = redisTemplate.opsForValue();
		operation.set(key, value);
		return operation;
	}
	/**
	 * 缓存对象数据，指定时间
	 * @param <T>
	 * @param key 缓存的键值
	 * @param value 缓存的值
	 * @param minutes 分钟
	 * @return
	 */
	public <T> ValueOperations<String, T> setCacheData(String key, T value,long minutes) {
		ValueOperations<String, T> operation = redisTemplate.opsForValue();
		operation.set(key, value,Duration.ofMinutes(minutes));
		return operation;
	}
	
	/**
	 * 缓存对象数据，指定时间
	 * @param <T>
	 * @param key 缓存的键值
	 * @param value 缓存的值
	 * @param minutes 分钟
	 * @return
	 */
	public <T> ValueOperations<String, T> resetCacheData(String key, T value,long minutes) {
		if(this.hashKeys(key)) {
			this.deleteObject(key);
		}
		ValueOperations<String, T> operation = redisTemplate.opsForValue();
		operation.set(key, value,Duration.ofMinutes(minutes));
		return operation;
	}

	/**
	 * 缓存基本的对象，Integer、String、实体类等
	 *
	 * @param key      缓存的键值
	 * @param value    缓存的值
	 * @param timeout  时间
	 * @param timeUnit 时间颗粒度
	 * @return 缓存的对象
	 */
	public <T> ValueOperations<String, T> setCacheObject(String key, T value, Integer timeout, TimeUnit timeUnit) {
		ValueOperations<String, T> operation = redisTemplate.opsForValue();
		operation.set(key, value, timeout, timeUnit);
		return operation;
	}

	/**
	 * 获得缓存的基本对象。
	 *
	 * @param key 缓存键值
	 * @return 缓存键值对应的数据
	 */
	public <T> T getCacheObject(String key) {
		if(!this.hashKeys(key)) {return null;}
		ValueOperations<String, T> operation = redisTemplate.opsForValue();
		return operation.get(key);
	}
	
	public long getExpire(String key) {
		return redisTemplate.opsForValue().getOperations().getExpire(key);
	}
	/**
	 * 删除单个对象
	 *
	 * @param key
	 */
	public void deleteObject(String key) {
		if(this.hashKeys(key)) {
			redisTemplate.delete(key);
		}
	}
	
	/**
	 * 模糊删除，*keys
	 * @param key
	 */
	public void deleteBySuffix(String key) {
		Set<String> keys=redisTemplate.keys("*"+key);
        redisTemplate.delete(keys);
	}
	/**
	 * 模糊删除，keys*
	 * @param key
	 */
	public void deleteByPrex(String key) {
        Set<String> keys = redisTemplate.keys(key + "*");
        redisTemplate.delete(keys);
    }
	/**
     *  获取指定前缀的一系列key
     *  使用scan命令代替keys, Redis是单线程处理，keys命令在KEY数量较多时，
     *  操作效率极低【时间复杂度为O(N)】，该命令一旦执行会严重阻塞线上其它命令的正常请求
     * @param keyPrefix
     * @return
     */
    public Set<String> keys(String keyPrefix) {
        String realKey = "*" + keyPrefix + "*";
        try {
            return (Set<String>) redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Set<String> binaryKeys = new HashSet<>();
                Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(realKey).count(Integer.MAX_VALUE).build());
                while (cursor.hasNext()) {
                    binaryKeys.add(new String(cursor.next()));
                }
                return binaryKeys;
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  删除指定前缀的一系列key
     * @param keyPrefix
     */
    public void removeAll(String keyPrefix) {
        try {
            Set<String> keys = keys(keyPrefix);
            if(keys!=null) {
                redisTemplate.delete(keys);
               }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

	/**
	 * 删除集合对象
	 *
	 * @param collection
	 */
	public void deleteObject(Collection collection) {
		redisTemplate.delete(collection);
	}

	/**
	 * 缓存List数据
	 *
	 * @param key      缓存的键值
	 * @param dataList 待缓存的List数据
	 * @return 缓存的对象
	 */
	public <T> ListOperations<String, T> setCacheList(String key, List<T> dataList) {
		ListOperations listOperation = redisTemplate.opsForList();
		if (null != dataList) {
			int size = dataList.size();
			for (int i = 0; i < size; i++) {
				listOperation.leftPush(key, dataList.get(i));
			}
		}
		return listOperation;
	}
	/**
	 * 缓存List数据
	 *
	 * @param key      缓存的键值
	 * @param dataList 待缓存的List数据
	 * @return 缓存的对象
	 */
	public <T> ListOperations<String, List<T>> setList(String key, List<T> dataList) {
		if(this.hashKeys(key)) {this.deleteObject(key);}
		ListOperations<String, List<T>> listOperation = redisTemplate.opsForList();
		if (null != dataList) {
			listOperation.rightPushAll(key, dataList);
		}
		return listOperation;
	}
	/**
	 * 缓存List数据
	 *
	 * @param key      缓存的键值
	 * @param list 待缓存的List数据
	 * @return 
	 * @return 缓存的对象
	 */
	public <T> Object setListString(String key, List<T> list) {
		if(this.hashKeys(key)) {this.deleteObject(key);}
		
		ValueOperations<String, Object> operation = redisTemplate.opsForValue();
		operation.set(key, JSON.toJSON(list).toString());
		return operation;
	}
	/**
	 * 缓存List数据
	 *
	 * @param key      缓存的键值
	 * @param dataList 待缓存的List数据
	 * @param savejson 是否将list转为json对象存储
	 * @return 缓存的对象
	 */
	public <T> Object resetCacheList(String key, List<T> dataList,boolean savejson) {
		this.deleteObject(key);
		if(savejson) {
			Object rs = this.setListString(key, dataList);
			return rs;
		}else {
			ListOperations<String, T> listOperation = this.setCacheList(key, dataList);
			return listOperation;
		}
	}

	/**
	 * 获得缓存的list对象
	 *
	 * @param key 缓存的键值
	 * @return 缓存键值对应的数据
	 */
	public <T> List<T> getCacheList(String key) {
		if(!this.hashKeys(key)) {return null;}
		List<T> dataList = new ArrayList<T>();
		ListOperations<String, T> listOperation = redisTemplate.opsForList();
		Long size = listOperation.size(key);

		for (int i = 0; i < size; i++) {
			dataList.add(listOperation.index(key, i));
		}
		return dataList;
	}
	
	/**
	 * 获得缓存的list对象
	 *
	 * @param key 缓存的键值
	 * @return 缓存键值对应的数据
	 */
	public <T> List<T> getList(String key) {
		if(!this.hashKeys(key)) {return null;}
		List<T> dataList = new ArrayList<T>();
		ListOperations<String, List<T>> listOperation = redisTemplate.opsForList();
		Long size = listOperation.size(key);
		if(size==1) {
			dataList = listOperation.rightPop(key);
		}else if(size>1) {
			for (int i = 0; i < size; i++) {
				dataList.addAll(listOperation.index(key, i));
			}
		}
		return dataList;
	}
	
	/**
	 * 获得缓存的list对象
	 *
	 * @param key 缓存的键值
	 * @return 缓存键值对应的数据
	 */
	public <T> List<T> getListString(String key,Class<T> clazz) {
		if(!this.hashKeys(key)) {return null;}
		ValueOperations<String, Object> operation = redisTemplate.opsForValue();
		Object rsdata = operation.get(key);
		List<T> list = JSONObject.parseArray(rsdata.toString(),clazz);
		return list;
	}

	/**
	 * 缓存Set
	 *
	 * @param key     缓存键值
	 * @param dataSet 缓存的数据
	 * @return 缓存数据的对象
	 */
	public <T> BoundSetOperations<String, T> setCacheSet(String key, Set<T> dataSet) {
		BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
		Iterator<T> it = dataSet.iterator();
		while (it.hasNext()) {
			setOperation.add(it.next());
		}
		return setOperation;
	}

	/**
	 * 获得缓存的set
	 *
	 * @param key
	 * @return
	 */
	public <T> Set<T> getCacheSet(String key) {
		Set<T> dataSet = new HashSet<T>();
		BoundSetOperations<String, T> operation = redisTemplate.boundSetOps(key);
		dataSet = operation.members();
		return dataSet;
	}

	/**
	 * 缓存Map
	 *
	 * @param key
	 * @param dataMap
	 * @return
	 */
	public <T> HashOperations<String, String, T> setCacheMap(String key, Map<String, T> dataMap) {
		HashOperations hashOperations = redisTemplate.opsForHash();
		if (null != dataMap) {
			for (Map.Entry<String, T> entry : dataMap.entrySet()) {
				hashOperations.put(key, entry.getKey(), entry.getValue());
			}
		}
		return hashOperations;
	}

	/**
	 * 获得缓存的Map
	 *
	 * @param key
	 * @return
	 */
	public <T> Map<String, T> getCacheMap(String key) {
		if(!this.hashKeys(key)) {return null;}
		Map<String, T> map = redisTemplate.opsForHash().entries(key);
		return map;
	}

	
	/**
	 * 判断key是否存在
	 * @param key
	 * @return
	 */
	public boolean hashKeys(String key) {
		return redisTemplate.hasKey(key);
	}
}
