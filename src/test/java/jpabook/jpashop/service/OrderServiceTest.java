package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        Member member = new Member();
        member.setName("member1");
        member.setAddress(new Address("seoul", "강가", "1000"));
        em.persist(member);

        Book book = new Book();
        book.setName("book1");
        book.setPrice(10000);
        book.setStockQuantity(10);
        em.persist(book);

        int orderCount=2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        Order getOrder = orderRepository.findOne(orderId);

        assertThat(OrderStatus.ORDER).isEqualTo(getOrder.getStatus());
        assertThat(getOrder.getTotalPrice()).isEqualTo(book.getPrice()*orderCount);
        assertThat(book.getStockQuantity()).isEqualTo(8);
    }

    @Test
    public void 주문취소() throws Exception {
        Member member = new Member();
        member.setName("member1");
        member.setAddress(new Address("seoul", "강가", "1000"));
        em.persist(member);

        Book book = new Book();
        book.setName("book1");
        book.setPrice(10000);
        book.setStockQuantity(10);
        em.persist(book);

        int orderCount=2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        orderService.cancelOrder(orderId);

        Order getOrder = orderRepository.findOne(orderId);

        assertThat(OrderStatus.CANCEL).isEqualTo(getOrder.getStatus());
        assertThat(book.getStockQuantity()).isEqualTo(10);
    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품_재고수량초과() throws Exception {
        Member member = new Member();
        member.setName("member1");
        member.setAddress(new Address("seoul", "강가", "1000"));
        em.persist(member);

        Book book = new Book();
        book.setName("book1");
        book.setPrice(10000);
        book.setStockQuantity(10);
        em.persist(book);

        int orderCount=12;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

    }

}