package com.intellibill.service;

import com.intellibill.database.ConsumerDAO;
import com.intellibill.exception.ConsumerNotFoundException;
import com.intellibill.model.CommercialConsumer;
import com.intellibill.model.Consumer;
import com.intellibill.model.ConsumerType;
import com.intellibill.model.IndustrialConsumer;
import com.intellibill.model.ResidentialConsumer;
import com.intellibill.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

public class ConsumerService {
    private final InMemoryStore store = InMemoryStore.getInstance();
    private final ConsumerDAO consumerDAO = new ConsumerDAO();
    private final FileService fileService = new FileService();

    public Consumer addConsumer(String name, String address, ConsumerType type) throws Exception {
        int consumerId = IdGenerator.nextConsumerId();
        Consumer consumer = createConsumerByType(consumerId, name, address, type);
        store.getConsumers().add(consumer);
        store.getConsumerMap().put(consumer.getConsumerId(), consumer);
        persistConsumer(consumer);
        return consumer;
    }

    public void addConsumer(Consumer consumer) {
        store.getConsumers().add(consumer);
        store.getConsumerMap().put(consumer.getConsumerId(), consumer);
        IdGenerator.syncConsumerCounter(consumer.getConsumerId());
    }

    public void updateConsumer(int consumerId, String name, String address) throws Exception {
        Consumer consumer = getConsumerById(consumerId);
        consumer.setName(name);
        consumer.setAddress(address);
        persistConsumer(consumer);
    }

    public void deleteConsumer(int consumerId) throws Exception {
        Consumer consumer = getConsumerById(consumerId);
        store.getConsumers().remove(consumer);
        store.getConsumerMap().remove(consumerId);
        store.getBills().removeIf(b -> b.getConsumerId() == consumerId);
        store.getBillMap().entrySet().removeIf(entry -> entry.getValue().getConsumerId() == consumerId);
        store.getPayments().removeIf(p -> !store.getBillMap().containsKey(p.getBillId()));
        store.getPaymentMap().entrySet().removeIf(entry -> !store.getBillMap().containsKey(entry.getValue().getBillId()));

        if (ServiceRegistry.ensureDatabaseMode()) {
            consumerDAO.deleteById(consumerId);
        } else {
            fileService.saveAll();
        }
    }

    public Consumer getConsumerById(int consumerId) throws ConsumerNotFoundException {
        Consumer consumer = store.getConsumerMap().get(consumerId);
        if (consumer == null) {
            throw new ConsumerNotFoundException("Consumer not found with ID: " + consumerId);
        }
        return consumer;
    }

    public List<Consumer> getAllConsumers() {
        return new ArrayList<>(store.getConsumers());
    }

    private Consumer createConsumerByType(int consumerId, String name, String address, ConsumerType type) {
        return switch (type) {
            case RESIDENTIAL -> new ResidentialConsumer(consumerId, name, address);
            case COMMERCIAL -> new CommercialConsumer(consumerId, name, address);
            case INDUSTRIAL -> new IndustrialConsumer(consumerId, name, address);
        };
    }

    private void persistConsumer(Consumer consumer) throws Exception {
        if (ServiceRegistry.ensureDatabaseMode()) {
            consumerDAO.upsert(consumer);
        } else {
            fileService.saveAll();
        }
    }
}
