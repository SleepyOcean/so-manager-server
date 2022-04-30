package com.sleepy.manager.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.*;

import java.util.List;

/**
 * @author Captain1920
 * @classname XMLUtils
 * @description TODO
 * @date 2022/4/30 11:18
 */
public class XmlUtils {
    /**
     * xml转json
     *
     * @param xmlStr xml字符串
     * @return
     */
    public static String xml2Json(String xmlStr) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        JSONObject json = new JSONObject();
        dom4j2Json(doc.getRootElement(), json);
        return JSONObject.toJSONString(json);
    }

    /**
     * xml转json
     *
     * @param element
     * @param json
     */
    private static void dom4j2Json(Element element, JSONObject json) {
        //如果是属性
        for (Object o : element.attributes()) {
            Attribute attr = (Attribute) o;
            if (!StringUtils.isBlank(attr.getValue())) {
                json.put("@" + attr.getName(), attr.getValue());
            }
        }
        List<Element> chdEl = element.elements();
        //如果没有子元素,只有一个值
        if (chdEl.isEmpty() && !StringUtils.isBlank(element.getText())) {
            json.put(element.getName(), element.getText());
        }
        //有子元素
        for (Element e : chdEl) {
            //子元素也有子元素
            if (!e.elements().isEmpty()) {
                JSONObject chdjson = new JSONObject();
                dom4j2Json(e, chdjson);
                Object o = json.get(e.getName());
                if (o != null) {
                    JSONArray jsona = null;
                    //如果此元素已存在,则转为jsonArray
                    if (o instanceof JSONObject) {
                        JSONObject jsono = (JSONObject) o;
                        json.remove(e.getName());
                        jsona = new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if (o instanceof JSONArray) {
                        jsona = (JSONArray) o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                } else {
                    if (!chdjson.isEmpty()) {
                        json.put(e.getName(), chdjson);
                    }
                }
            } else {
                //子元素没有子元素
                for (Object o : element.attributes()) {
                    Attribute attr = (Attribute) o;
                    if (!StringUtils.isBlank(attr.getValue())) {
                        json.put("@" + attr.getName(), attr.getValue());
                    }
                }
                if (!e.getText().isEmpty()) {
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }

    /**
     * json2xml
     *
     * @param json
     * @param element
     */
    public static void json2Xml(JSONObject json, Element element) {
        for (String key : json.keySet()) {
            if (key.startsWith("-")) {
                element.addAttribute(key.replace("-", ""), json.getString(key));
            } else if (key.equals("#text")) {
                element.addText(json.getString(key));
            } else {
                Object o = json.get(key);
                if (o != null) {
                    JSONArray jsona = null;
                    if (o instanceof JSONObject) {
                        Element child = element.addElement(key);
                        json2Xml(json.getJSONObject(key), child);

                    } else if (o instanceof JSONArray) {
                        if (!json.getJSONArray(key).isEmpty()) {
                            jsonArray2Xml(json.getJSONArray(key), element, key);
                        }
                    } else if (o instanceof String) {
                        element.addText(json.getString(key));
                    }

                }
            }

        }
    }

    /***
     * jsonArray2Element
     * @param jsonArray
     * @param element
     * @param rootName
     */
    public static void jsonArray2Xml(JSONArray jsonArray, Element element, String rootName) {

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Element element1 = element.addElement(rootName);
            json2Xml(jsonObject, element1);
        }
    }
}
