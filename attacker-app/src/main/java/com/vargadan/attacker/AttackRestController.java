package com.vargadan.attacker;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Log
@RestController
public class AttackRestController {

    private static StringBuffer logBuffer = new StringBuffer();

    @SneakyThrows
    private static void handleLogItem(String s) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,String> msgMap = objectMapper.readValue(s, HashMap.class);
        log.info("msg = " + s);
        logBuffer.append(msgMap.get("k"));
    }

    @GetMapping(value = "/log", produces = "text/plain")
    @SneakyThrows
    public ResponseEntity log(@RequestParam String msg) {
        if (msg.startsWith("[")) {
            msg = msg.split("\\[")[1].split("\\]")[0];
        }
        if(msg.contains("},{")) {
            for (String smsg : msg.split("},")) {
                smsg = msg.endsWith("}") ? msg : msg.concat("}");
                handleLogItem(smsg);
            }
        } else {
            handleLogItem(msg);
        }
        return ResponseEntity.ok(msg);
    }

    @GetMapping(value = "/viewlog", produces = "text/plain")
    public ResponseEntity<String> viewLog() {
        return ResponseEntity.ok(logBuffer.toString());
    }
}