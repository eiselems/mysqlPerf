package com.example.demo;

import com.example.demo.dto.Event;
import com.example.demo.repository.EventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.UUID;

@SpringBootApplication
public class DemoApplication {

    public static int numberOfEntriesToGenerate = 50;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Component
    public static class Test {


        private int batchSize;

        private EntityManager entityManager;

        private EventRepository eventRepository;
        private ArrayList<String> ids = new ArrayList<>();

        public Test(@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}") int batchSize, EntityManagerFactory emf, EventRepository eventRepository) {
            this.batchSize = batchSize;
            this.eventRepository = eventRepository;
            entityManager = emf.createEntityManager();
        }

        @PostConstruct
        public void test() throws InterruptedException {
            //INIT Testdata so that they can be re-used without affecting timings
            for (int i = 0; i < numberOfEntriesToGenerate; i++) {
                ids.add(UUID.randomUUID().toString());
            }


            numberIndexAsBatch(); //this uses save(Collection) to INSERT ALL NEW ENTRIES
            numberIndexAsBatch(); //use also same IDs but calling .save(Collection) but this time as updates

            numberIndex(); //call .save(SingleItem) in a forEach Lambda
        }

        private void numberIndexAsBatch() {
            long before;
            long after;

            before = System.currentTimeMillis();
            ArrayList<Event> events = new ArrayList<>();
            for (int i = 0; i < numberOfEntriesToGenerate; i++) {

                Event one = eventRepository.findOne(ids.get(i));
                if(one==null){
                    Event e = new Event(ids.get(i));
                    e.setFieldOne("numberIndex");
                    e.setFieldTwo("AsBatch");
                    events.add(e);
                }else{
                    one.setFieldOne("numberIndex");
                    one.setFieldTwo("AsBatch");
                    events.add(one);
                }
            }

            eventRepository.save(events);
            after = System.currentTimeMillis();
            System.out.println("Took " + (after - before) + "ms to write entries as batch .save(allEvents)");
        }

        private void numberIndex() {
            ArrayList<Event> events = new ArrayList<>();
            for (int i = 0; i < numberOfEntriesToGenerate; i++) {
                events.add(new Event(ids.get(i)));
            }
            long before = System.currentTimeMillis();
            events.stream().forEach(a -> eventRepository.save(a));
            long after = System.currentTimeMillis();
            System.out.println("Took " + (after - before) + "ms to write entries in lambda for each with .save(singleEvent)");
        }


    }
}