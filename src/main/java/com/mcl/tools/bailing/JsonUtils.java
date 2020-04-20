package com.mcl.tools.bailing;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @auth caiguowei
 * @date 2020/4/14
 */
public class JsonUtils {

    public static String toJsonString(Object obj){
        return new Gson().toJson(obj);
    }

    public static <T> T parse(String jsonStr, Class<T> clazz){
        return new Gson().fromJson(jsonStr, clazz);
    }
    public static <T> List<T> parseList(String jsonStr, Class<T> cls) {
        Gson gson = new Gson();
        List<T> list = new ArrayList<T>();
        JsonArray array = JsonParser.parseString(jsonStr).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }



    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        JsonObject jsonObject = new JsonObject();
        public Builder add(String key, String val){
            jsonObject.addProperty(key,val);
            return this;
        }
        public Builder add(String key, Number val){
            jsonObject.addProperty(key,val);
            return this;
        }
        public Builder add(String key, Boolean val){
            jsonObject.addProperty(key,val);
            return this;
        }
        public Builder add(String key, Character val){
            jsonObject.addProperty(key,val);
            return this;
        }

        public Builder remove(String key){
            jsonObject.remove(key);
            return  this;
        }

        public JsonObject build(){
            return jsonObject;
        }

        public String toString(){
            return jsonObject.toString();
        }
    }

    public static void main(String[] args) {

        JsonObject build = JsonUtils.builder().add("a", "b").add("b", 123).add("c", true).remove("c").build();
        System.out.println(build);
    }
}
