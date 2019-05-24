package com.vargadan.attacker;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;

@Log
@RestController
public class VictimRestController {

    @GetMapping(value = "/victim", produces = "text/plain")
    @SneakyThrows
    public String victim(@RequestParam(required = false) String outOfBand) {
        String msg = "Here is your confidential data from the victim service! " +
                "Please note, this service could be behind a firewall and inaccessible by the client calling the XML parser.";
        if (Boolean.parseBoolean(outOfBand)) {
            msg = msg.concat(" (you fall victim to an Out-of-Band XXE attack!)");
        } else {
            msg = msg.concat(" (you fall victim to a standard XXE attack!)");
        }
        return msg;
    }
}
