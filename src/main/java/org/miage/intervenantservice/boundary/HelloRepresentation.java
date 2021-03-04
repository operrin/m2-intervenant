package org.miage.intervenantservice.boundary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// la classe va comprendre et répondre aux méthodes HTTP conforme à REST
// GET, POST, PUT, DELETE, TRACE, OPTIONS
public class HelloRepresentation {

    @GetMapping("/hello")
    public String getHello() {
        return "Tim, Ann";
    }
}
