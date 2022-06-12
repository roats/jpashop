package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {

        // given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus()); // 상품 주문 시 주문 상태 검증 (ORDER)
        assertEquals(1, getOrder.getOrderItems().size()); // 주문한 상품 종류 수 정확성 검증
        assertEquals(10000 * orderCount, getOrder.getTotalPrice()); // 주문 가격 검증 (가격 * 수량)
        assertEquals(8, book.getStockQuantity()); // 주문 수량만큼 재고 반영 검증

    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {

        // given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;

        // when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.order(member.getId(), book.getId(), orderCount));

        // then
        assertEquals(NotEnoughStockException.class, exception.getClass()); // 재고 수량 부족 시 예외 발생 검증

    }

    @Test
    public void 주문취소() throws Exception {

        // given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL, getOrder.getStatus()); // 주문 취소 시 주문 상태 검증 (CANCEL)
        assertEquals(10, book.getStockQuantity());  // 주문 취소된 상품 재고 증가 검증

    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

}