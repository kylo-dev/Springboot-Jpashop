package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.dto.order.OrderDto;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.jpa.OrderJpaRepository;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

//    private final OrderRepository orderRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderJpaRepository.search(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); // Lazy 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderJpaRepository.search(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o)) // 1차 Dto 변환
                .collect(Collectors.toList());
        return collect;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderJpaRepository.findAllWithItem(); // distinct로 order 4개 조회되는 것이 중복을 제외하고 2개 조회
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o)) // 1차 Dto 변환
                .collect(Collectors.toList());
        return collect;
    }

//    @GetMapping("/api/v3.1/orders")
//    public List<OrderDto> ordersV3_page(
//            @RequestParam(value = "offset", defaultValue = "0") int offset,
//            @RequestParam(value = "limit", defaultValue = "100") int limit
//    ) {
//        List<Order> orders = orderJpaRepository.findAllWithMemberDelivery(offset, limit);
//
//        List<OrderDto> collect = orders.stream()
//                .map(o -> new OrderDto(o)) // 1차 Dto 변환
//                .collect(Collectors.toList());
//        return collect;
//    }
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(Pageable pageable){
        List<Order> orders = orderJpaRepository.findAllWithMemberDelivery(pageable);

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

}
