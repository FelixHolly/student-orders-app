package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.order.CreateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    List<OrderResponse> getOrdersForStudent(Long studentId);

    void deleteOrder(Long id);
}

