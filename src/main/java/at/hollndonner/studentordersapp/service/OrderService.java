package at.hollndonner.studentordersapp.service;

import at.hollndonner.studentordersapp.dto.order.CreateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.OrderFilterRequest;
import at.hollndonner.studentordersapp.dto.order.OrderResponse;
import at.hollndonner.studentordersapp.dto.order.UpdateOrderRequest;
import at.hollndonner.studentordersapp.dto.order.UpdateOrderStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrderById(Long id);

    Page<OrderResponse> getOrders(OrderFilterRequest filter, Pageable pageable);

    OrderResponse updateOrder(Long id, UpdateOrderRequest request);

    OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request);

    void deleteOrder(Long id);
}

