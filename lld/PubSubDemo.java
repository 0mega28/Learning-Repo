/*
 * https://github.com/ashishps1/awesome-low-level-design/blob/main/problems/pub-sub-system.md
 */

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

record Message(String content) {}

class Topic {
    private final String name;
    private final Set<Subscriber> subscribers;

    public Topic(String name) {
        this.name = name;
        this.subscribers = new CopyOnWriteArraySet<>();
    }

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void broadcast(Message message) {
        this.subscribers.forEach(subscriber -> Dispatcher.dispatch(() -> subscriber.onMessage(message)));
    }
}

// @FunctionalInterface
interface Subscriber {
    void onMessage(Message message);
}

class PrintSubscriber implements Subscriber {
    private final String name;

    public PrintSubscriber(String name) {
        this.name = name;
    }

    @Override
    public void onMessage(Message message) {
        System.out.println(name + " received: " + message);
    }
}

class Publisher {
    private final String name;
    private final Broker broker;

    public Publisher(String name, Broker broker) {
        this.name = name;
        this.broker = broker;
    }

    public void publish(String topic, Message message) {
        broker.dispatch(topic, message);
    }
}

class TopicNotFoundException extends RuntimeException {
}

class Dispatcher {
    private final static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);

    public static void dispatch(Runnable runnable) {
        EXECUTOR_SERVICE.submit(runnable);
    }

    public static void shutdown() {
        try {
            EXECUTOR_SERVICE.awaitTermination(1, TimeUnit.SECONDS);
            EXECUTOR_SERVICE.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Broker implements AutoCloseable {
    Map<String, Topic> topicMap = new ConcurrentHashMap<>();

    boolean createTopic(String topicName) {
        return topicMap.putIfAbsent(topicName, new Topic(topicName)) != null;
    }

    void addSubscriber(String topicName, Subscriber subscriber) {
        Topic topic = topicMap.get(topicName);
        if (topic == null) {
            throw new TopicNotFoundException();
        }
        topic.addSubscriber(subscriber);
    }

    void unSubscriber(String topicName, Subscriber subscriber) {
        Topic topic = topicMap.get(topicName);
        if (topic == null) {
            throw new TopicNotFoundException();
        }
        topic.removeSubscriber(subscriber);
    }

    void dispatch(String topicName, Message message) {
        Topic topic = topicMap.get(topicName);
        if (topic == null) {
            throw new TopicNotFoundException();
        }

        Dispatcher.dispatch(() -> topic.broadcast(message));
    }

    @Override
    public void close() {
        Dispatcher.shutdown();
    }
}

public class PubSubDemo {
    public static void main(String[] args) {
        try (Broker broker = new Broker()) {
            Subscriber subscriber1 = new PrintSubscriber("Subscriber1");
            Subscriber subscriber2 = new PrintSubscriber("Subscriber1");

            broker.createTopic("topic1");
            broker.createTopic("topic2");

            Publisher publisher1 = new Publisher("publisher1", broker);
            Publisher publisher2 = new Publisher("publisher2", broker);

            broker.addSubscriber("topic1", subscriber1);
            broker.addSubscriber("topic2", subscriber2);

            publisher1.publish("topic1", new Message("Pubisher 1 on Topic 1"));
            publisher2.publish("topic2", new Message("Pubisher 2 on Topic 2"));
        }
    }
}

