package org.tron.core.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j(topic = "DB")
@Component
public class Calculate {
   public Map<String, AtomicLong> mapCost =new ConcurrentHashMap<>();
   public Map<String, AtomicLong> mapTime = new ConcurrentHashMap<>();
  public Map<String, AtomicLong> names = new ConcurrentHashMap<>();

   public void addAction(String dbName ,String action, long cost) {
     if (mapCost.containsKey(action)) {
       mapCost.get(action).getAndAdd(cost);
       mapTime.get(action).incrementAndGet();
     } else {
       mapCost.put(action, new AtomicLong(cost));
       mapTime.put(action, new AtomicLong(1));
     }

     if (this.names.containsKey(dbName)) {
       names.get(dbName).incrementAndGet();
     } else {
       names.put(dbName, new AtomicLong(1));
     }
   }

   public void printCost() {
     for (Entry p : mapCost.entrySet()) {
       AtomicLong time = mapTime.get(p.getKey());
       logger.info("test performance name {}, cost {}, time {}", p.getKey(), p.getValue(), time);
     }
     for (Entry p : names.entrySet()) {
       logger.info("test performance db {}, time {}", p.getKey(), p.getValue());
     }
   }
   public void reset() {
     mapCost.clear();
     mapTime.clear();
     names.clear();
   }

}
