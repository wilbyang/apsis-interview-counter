package com.apsis.interview.counter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@SpringBootApplication
public class CounterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CounterApplication.class, args);
    }

    @RestController
    static class CounterController {

        ConcurrentHashMap<String, AtomicLong> store =  new ConcurrentHashMap<>();

        //increment a named counter by 1
        @RequestMapping(value = "/counter/increment/{key}", method = RequestMethod.PATCH)
        public ResponseEntity<Long> addOne(@PathVariable(name = "key") String key) {
            long value = store.putIfAbsent(key, new AtomicLong()).incrementAndGet();
            return new ResponseEntity<>(value, HttpStatus.OK);
        }

        // get the current value of a given counter
        @RequestMapping(value = "/counter/get/{key}", method = RequestMethod.GET)
        public ResponseEntity<Long> getBy(@PathVariable(name = "key") String key) {
            long value = store.get(key).get();
            return new ResponseEntity<>(value, HttpStatus.OK);
        }

        //create new counters
        @RequestMapping(value = "/counter", method = RequestMethod.POST)
        public ResponseEntity<Long> set(@RequestParam(name = "key") String key, @RequestParam(name = "value") Long value) {

            long ret = store.computeIfAbsent(key, k -> {
                return new AtomicLong(value);
            }).get();
            return new ResponseEntity<>(value, HttpStatus.CREATED);
        }

        //get a list of all counters and their current value
        @RequestMapping(value = "/counter/all", method = RequestMethod.GET)
        public ResponseEntity<Map<String, AtomicLong>> all() {

            return new ResponseEntity<>(store, HttpStatus.OK);
        }

    }

}
