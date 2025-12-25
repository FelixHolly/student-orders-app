package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.order.CreateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.OrderResponse;
import at.hollndonner.studentordersapp.dto.order.UpdateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.UpdateOrderStatusRequest;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    List<OrderResponse> getOrdersForStudent(Long studentId);

    OrderResponse updateOrder(Long id, UpdateOrderRequest request);

    OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request);

    void deleteOrder(Long id);
}

