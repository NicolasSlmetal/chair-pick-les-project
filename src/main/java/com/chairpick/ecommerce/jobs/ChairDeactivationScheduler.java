package com.chairpick.ecommerce.jobs;

import com.chairpick.ecommerce.model.*;
import com.chairpick.ecommerce.repositories.ChairRepository;
import com.chairpick.ecommerce.repositories.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ChairDeactivationScheduler {

    private final static Integer MIN_SALES = 2;

    private final ChairRepository chairRepository;
    private final OrderRepository orderRepository;

    public ChairDeactivationScheduler(ChairRepository chairRepository, OrderRepository orderRepository) {
        this.chairRepository = chairRepository;
        this.orderRepository = orderRepository;
    }

    @Scheduled(cron = "0 0 15 * * *")
    public void deactivateChairs() {
        System.out.println("Desativando cadeiras fora de mercado");
        List<Chair> chairs = chairRepository.findAllChairs();
        Map<Long, Integer> chairsWithNoStock = chairs
                .stream()
                .collect(
                        Collectors.toMap(Chair::getId,
                                chair -> chair.getItems().stream()
                                        .map(Item::getAmount)
                                        .reduce( 0, Integer::sum)
                ));
        List<Chair> chairsToDeactivate = chairs.stream()
                .filter(chair -> chairsWithNoStock.getOrDefault(chair.getId(), 0) == 0)
                .collect(Collectors.toCollection(ArrayList::new));

        List<Chair> chairsWithStock = chairs
                .stream()
                .filter(chair -> chairsWithNoStock.getOrDefault(chair.getId(), 0) > 0)
                .toList();
        for (Chair chair : chairsWithStock) {
            List<Order> orders = orderRepository.findAllOrdersWithChair(chair);

            Map<Chair, Integer> chairAmountSell = orders
                    .stream()
                    .flatMap(order -> order.getItems().stream())
                    .filter(item -> item.getItem().getChair().getId().equals(chair.getId()))
                    .collect(Collectors.toMap(orderItem -> chair, OrderItem::getAmount, Integer::sum));

            if (chairAmountSell.getOrDefault(chair, 0) < MIN_SALES) {
                chairsToDeactivate.add(chair);
            }
        }
        chairsToDeactivate.forEach(chair -> {

            ChairStatusChange chairStatusChange = ChairStatusChange
                    .builder()
                    .chair(chair)
                    .status(false)
                    .reason("Fora de mercado")
                    .build();
            chairRepository.deactivate(chair, chairStatusChange);

        });
    }
}
