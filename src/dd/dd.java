package dd;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JSONUtils {

	/**
	 * 
	 * @Title: mergeListJson   
	 * @Description:    合并list中的数据成jsonarray
	 * @param: @param list
	 * @param: @param children
	 * @param: @return      
	 * @return: JSONArray      
	 * @throws
	 */
	public static JSONArray mergeListJson(List<JSONObject> list, String children) {

		JSONObject res = null;
		for (JSONObject obj : list) {
			if (res == null) {
				res = newJsonArray(obj, null, children);
				continue;
			}
			res = mergeJsonFromSub(res, obj, children);
		}
		return res.getJSONArray(children);
	}

	/**
	 * 
	 * @Title:  mergeTwoJsonSameLevel   
	 *
	 * @Description:  merge two json
	 *
	 * @param:   @param obj
	 * @param:   @param obj2
	 * @param:   @param children
	 * @param:   @return
	 *
	 * @return:  JSONObject
	 *
	 * @throws
	 */
	public static JSONObject mergeTwoJsonSameLevel(JSONObject obj, JSONObject obj2, String children) {
		JSONObject res = null;
		if (!checkJsonIsSame(obj, obj2, children)) {
			obj = newJsonArray(obj, null, children);
		}
		res = mergeJsonFromSub(obj, obj2, children);
		return res;
	}

	/**
	 * 
	 * @Title:  mergeJsonFromSub   
	 *
	 * @Description:  比较obj2 是否在obj1的子节点, 并返回合并后的结果
	 *
	 * @param:   @param obj
	 * @param:   @param obj2
	 * @param:   @param children
	 *
	 * @return:  JSONObject
	 *
	 * @throws
	 */
	public static JSONObject mergeJsonFromSub(JSONObject obj, JSONObject obj2, String children) {
		// JSONObject obj = obj1;
		// 此时不比较根结点， 默认根结点相同
		// 不在子节点中
		// checkSubJsonByObj2(obj,obj2,children)
		int re = checkSubJsonByObj2(obj, obj2, children);
		if (re == -1) {
			obj.getJSONArray(children).add(obj2);
		} else if (re == -2) {
			return obj;
		} else if (re == -3) {
			JSONArray ja = new JSONArray();
			ja.add(obj2);
			obj.put(children, ja);
			return obj;
		} else {
			// 在子节点中， 比较obj1 的对应子节点和obj2的关系
			// 此时re大于0， 说明在obj子节点中存在相同的节点。
			JSONArray array = obj.getJSONArray(children);
			JSONObject res = objectToJSONObject(array.get(re));
			if (!obj2.containsKey(children))
				return obj;
			for (Object o : obj2.getJSONArray(children)) {
				JSONObject sub = mergeJsonFromSub(res, objectToJSONObject(o), children);
				array.remove(re);
				array.add(sub);
				return obj;
			}
		}
		return obj;
	}

	/**
	 * 
	 * @Title:  checkSubJsonByObj2   
	 *
	 * @Description:  判断obj2是否在obj的子节点中
	 *
	 * @param:   @param obj
	 * @param:   @param obj2
	 * @param:   @param children
	 * @param:   @return
	 *
	 * @return:  int
	 *
	 * @throws
	 */
	private static int checkSubJsonByObj2(JSONObject obj, JSONObject obj2, String children) {
		if (obj2 == null) {
			return -2;
		}
		if (!obj.containsKey(children)) {
			return -3;
		}
		JSONArray array = obj.getJSONArray(children);
		for (int k = 0; k < array.size(); k++) {
			if (checkJsonIsSame(objectToJSONObject(array.get(k)), obj2, children)) {
				return k;
			}
		}

		return -1;

	}

	/**
	 * 
	 * @Title:  newJsonArray   
	 *
	 * @Description:  结果封装
	 *
	 * @param:   @param obj
	 * @param:   @param obj1
	 * @param:   @param children
	 * @param:   @return
	 *
	 * @return:  JSONObject
	 *
	 * @throws
	 */
	private static JSONObject newJsonArray(JSONObject obj, JSONObject obj1, String children) {
		JSONArray array = new JSONArray();

		if (obj1 != null) {
			array.add(obj1);
		}
		array.add(obj);
		JSONObject o = new JSONObject();
		o.put(children, array);

		return o;
	}

	/**
	 * 
	 * @Title:  stringToJSONObject   
	 *
	 * @Description: stringToJSONObject
	 *
	 * @param:   @param str
	 * @param:   @param num
	 * @param:   @param children
	 * @param:   @param reg
	 * @param:   @param strs
	 * @param:   @return
	 *
	 * @return:  JSONObject
	 *
	 * @throws
	 */

	public static JSONObject stringToJSONObject(String str, int num, String children, String reg, String... strs) {
		String[] ss = str.split(reg);
		if (ss.length % num != 0) {
			return null;
		}
		JSONObject obj = null;
		for (int i = ss.length - 1; i > 0; i -= num) {
			if (ss[i - num + 1].equals("")) {
				continue;
			}
			JSONObject obj1 = new JSONObject();
			for (int k = 0; k < strs.length; k++) {
				obj1.put(strs[k], ss[i - num + 1 + k]);
			}
			if (i == ss.length - 1) {
				obj = obj1;
				continue;
			}
			JSONArray ja = new JSONArray();
			ja.add(obj);
			obj1.put(children, ja);
			obj = obj1;
		}
		return obj;
	}

	/**
	 * 
	 * @Title:  objectToJSONObject   
	 *
	 * @Description:  对象转jsonobject
	 *
	 * @param:   @param obj
	 * @param:   @return
	 *
	 * @return:  JSONObject
	 *
	 * @throws
	 */
	public static JSONObject objectToJSONObject(Object obj) {
		return JSONObject.parseObject(JSON.toJSONString(obj));
	}

	/**
	 * 
	 * @Title:  checkJsonIsSame   
	 *
	 * @Description:  比较两个对象节点是否相同, 除子节点外 false 不等， true 等于
	 *
	 * @param:   @param obj0
	 * @param:   @param obj1
	 * @param:   @param children
	 * @param:   @return
	 *
	 * @return:  boolean
	 *
	 * @throws
	 */
	private static boolean checkJsonIsSame(JSONObject obj0, JSONObject obj1, String children) {
		if (obj0 == null && obj1 == null) {
			return true;
		} else if (obj0 == null || obj1 == null) {
			return false;
		}

		return checkJson(obj0, obj1, children);

	}

	private static boolean checkJson(JSONObject obj0, JSONObject obj1, String children) {
		Set<String> set = obj0.keySet();
		int k = 0;
		for (String key : set) {
			if (key.startsWith(children)) {
				k++;
				continue;
			}
				
			if (obj1.containsKey(key)) {
				if (obj0.getString(key).equalsIgnoreCase(obj1.getString(key))) {
					k++;
				}
			} else {
				return false;
			}
		}
		if (k == set.size()) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 
	 * @Title: checkSubJSONSub   
	 * @Description:    判断当前json的子串是否包含对应值
	 * @param: @param obj0
	 * @param: @param children
	 * @param: @param key
	 * @param: @param value
	 * @param: @param isSuper 是否返回上一级节点
	 * @param: @return      
	 * @return: JSONObject      
	 * @throws
	 */
	public static JSONObject checkSubJSONSub(JSONObject obj0, String children, String key, String value, boolean isSuper) {
		if (obj0 == null) {
			return null;
		}
		if (!obj0.containsKey(key) || !obj0.containsKey(children)) {
			return null;
		}
		
		if(!isSuper) {
			if(String.valueOf(obj0.get(key)).equalsIgnoreCase(value)) {
				return obj0;
			}
		}
		JSONObject obj = obj0;
		// System.out.println(obj0+"2");
		JSONArray jsonArray = obj.getJSONArray(children);
		for (Object json : jsonArray) {
			JSONObject job = objectToJSONObject(json);

			if (String.valueOf(job.get(key)).equalsIgnoreCase(value)) {
				// System.out.println(job.getInteger("value"));
				
				if(!isSuper) {
					return job;
				}else {
					return obj;
				}
			} else if (job.containsKey(children)&& !(job.getJSONArray(children).size() == 1 && job.getJSONArray(children).get(0) == null)) {
				JSONObject res = checkSubJSONSub(job, children, key, value, isSuper);
				// break;
				if (res != null) {
					return res;
				}
			}
		}
		return null;
	}
	
	
	/**
	 * 
	 * @Title:  toCSV   
	 *
	 * @Description:  JSON转csv
	 *
	 * @param:   @param json
	 * @param:   @param children
	 * @param:   @return
	 *
	 * @return:  List<String>
	 *
	 * @throws
	 */
	public static List<String> toCSV(JSONObject json, String children) {
		// Set<String> keySet = json.keySet();
		List<String> list1 = new ArrayList<String>();
		if (!json.containsKey(children)
				|| (json.getJSONArray(children).size() == 1 && json.getJSONArray(children).get(0) == null)) {
			list1.add(jsonToString(json, children, keysToArray(json, children)));
		} else {
			for (Object obj : json.getJSONArray(children)) {
				JSONObject o = objectToJSONObject(obj);
				List<String> csv = toCSV(o, children);
				for (String ss : csv) {
					list1.add(jsonToString(json, children, keysToArray(json, children)) + "," + ss);
				}
			}
		}

		return list1;
	}

	/**
	 * 
	 * @Title:  keysToArray   
	 *
	 * @Description:  获取有序key
	 *
	 * @param:   @param json
	 * @param:   @param children
	 * @param:   @return
	 *
	 * @return:  String[]
	 *
	 * @throws
	 */
	public static String[] keysToArray(JSONObject json, String children) {
		if (json == null || !json.containsKey(children)) {
			return null;
		}
		Set<String> keySet = json.keySet();
		StringBuffer sb = new StringBuffer();
		for (String s : keySet) {
			if (!s.startsWith(children)) {
				sb.append(s).append(",");
			}
		}
		String ss = sb.toString().substring(0, sb.toString().length() - 1);
		return ss.split(",");
	}

	/**
	 * 
	 * @Title:  jsonToString   
	 *
	 * @Description:  依据有序key生成csv
	 *
	 * @param:   @param json
	 * @param:   @param children
	 * @param:   @param keySet
	 * @param:   @return
	 *
	 * @return:  String
	 *
	 * @throws
	 */
	public static String jsonToString(JSONObject json, String children, String[] keySet) {
		if (json == null || !json.containsKey(children) || keySet == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (String s : keySet) {
			if (!s.startsWith(children)) {
				sb.append(json.get(s)).append(",");
			}
		}
		String ss = sb.toString().substring(0, sb.toString().length() - 1);
		return ss;
	}

}
