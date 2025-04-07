package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.model.Address;
import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.io.output.FreightValueDTO;
import com.chairpick.ecommerce.repositories.AddressRepository;
import com.chairpick.ecommerce.repositories.ChairRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@Service
public class FreightCalculatorService {

    private static final Map<Predicate<String>, Double> cepMap = new HashMap<>();
    public static final double DEFAULT_DISTANCE_VALUE = 8.0;
    public static final double BASE_FREIGHT = 0.5;
    public static final double VOLUME_FACTOR = 0.4;
    public static final double WEIGHT_FACTOR = 0.2;
    public static final double DISTANCE_FACTOR = 0.3;

    static {
        cepMap.put(cep -> cep.startsWith("0"), 1.0);
        cepMap.put(cep -> cep.startsWith("1"), 2.0);
        cepMap.put(cep -> cep.startsWith("2"), 3.0);
        cepMap.put(cep -> cep.startsWith("3"), 4.0);
        cepMap.put(cep -> cep.startsWith("4"), 5.0);
        cepMap.put(cep -> cep.startsWith("5"), 6.0);
    }
    private final ChairRepository chairService;
    private final AddressRepository addressService;

    public FreightCalculatorService(ChairRepository chairService, AddressRepository addressService) {
        this.chairService = chairService;
        this.addressService = addressService;
    }

    public FreightValueDTO calculateFreight(Long chairId, Long deliveryAddressId) {
        Address deliveryAddress = addressService.findById(deliveryAddressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        Chair chair = chairService.findById(chairId)
                .orElseThrow(() -> new EntityNotFoundException("Chair not found"));

        // In m³
        double volume = (chair.getWidth() * chair.getHeight() * chair.getLength()) / 1_000_000_000.0;
        double freight = BASE_FREIGHT + (volume * VOLUME_FACTOR) + (chair.getWeight() * WEIGHT_FACTOR);

        double distance = calculateDistanceByCep(deliveryAddress.getCep());
        freight += distance * DISTANCE_FACTOR;
        return new FreightValueDTO(freight);
    }

    public FreightValueDTO calculateFreight(Chair chair, Address deliveryAddress) {
        // In m³
        double volume = (chair.getWidth() * chair.getHeight() * chair.getLength()) / 1_000_000_000.0;
        double freight = BASE_FREIGHT + (volume * VOLUME_FACTOR) + (chair.getWeight() * WEIGHT_FACTOR);

        double distance = calculateDistanceByCep(deliveryAddress.getCep());
        freight += distance * DISTANCE_FACTOR;
        return new FreightValueDTO(freight);
    }

    public double calculateDistanceByCep(String cep) {
        for (Map.Entry<Predicate<String>, Double> entry : cepMap.entrySet()) {
            if (entry.getKey().test(cep)) {
                return entry.getValue();
            }
        }
        return DEFAULT_DISTANCE_VALUE;
    }
}
