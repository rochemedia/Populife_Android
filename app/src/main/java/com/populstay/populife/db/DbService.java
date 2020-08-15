//package com.populstay.populife.db;
//
//
//import com.populstay.populife.entity.Key;
//
//import org.litepal.LitePal;
//
//import java.util.List;
//
//
///**
// * Created by Jerry
// */
//public class DbService {
//
//	/**
//	 * get keys by userId
//	 *
//	 * @param userId 用户id
//	 */
//	public static List<Key> getKeysByUserId(String userId) {
//		return LitePal.where("userid = ?", userId).find(Key.class);
//	}
//
//	/**
//	 * get key by lockmac
//	 *
//	 * @param lockmac 锁地址
//	 */
//	public static Key getKeyByLockmac(String lockmac) {
//		List<Key> keys = LitePal.where("lockmac = ?", lockmac).find(Key.class);
//		if (keys.size() > 0) {
////            LogUtil.d("keys.size():" + keys.size(), DBG);
////            LogUtil.d("key:" + keys.get(0).toString(), DBG);
//			return keys.get(0);
//		} else {
//			return null;
//		}
//	}
//
//	/**
//	 * save key
//	 *
//	 * @param key
//	 */
//	public static boolean saveKey(Key key) {
//		return key.save();
//	}
//
//	/**
//	 * delete key
//	 *
//	 * @param key
//	 */
//	public static void deleteKey(Key key) {
//		if (key.isSaved()) {
//			key.delete();
//		}
//	}
//
//	/**
//	 * save key list
//	 */
//	public static void saveKeyList(List<Key> keys) {
//		LitePal.saveAll(keys);
//	}
//
//	/**
//	 * clear all keys
//	 */
//	public static void deleteAllKeys() {
//		LitePal.deleteAll(Key.class);
//	}
//
//	/**
//	 * update key
//	 *
//	 * @param key
//	 */
//	public static void updateKey(Key key) {
//		key.updateAll();
//	}
//
//}
