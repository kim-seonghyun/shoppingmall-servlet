package com.nhnacademy.shoppingmall.controller.auth;

import com.nhnacademy.shoppingmall.common.mvc.annotation.RequestMapping;
import com.nhnacademy.shoppingmall.common.mvc.annotation.RequestMapping.Method;
import com.nhnacademy.shoppingmall.common.mvc.controller.BaseController;
import com.nhnacademy.shoppingmall.order.domain.Orders;
import com.nhnacademy.shoppingmall.order.repository.OrderRepository;
import com.nhnacademy.shoppingmall.order.repository.impl.OrderRepositoryImpl;
import com.nhnacademy.shoppingmall.order.services.OrderServices;
import com.nhnacademy.shoppingmall.order.services.impl.OrderServicesImpl;
import com.nhnacademy.shoppingmall.orderdetail.repository.OrderDetailRepository;
import com.nhnacademy.shoppingmall.orderdetail.repository.impl.OrderDetailRepositoryImpl;
import com.nhnacademy.shoppingmall.products.repository.ProductsRepository;
import com.nhnacademy.shoppingmall.products.repository.impl.ProductRepositoryImpl;
import com.nhnacademy.shoppingmall.shoppingCart.repository.impl.ShoppingCartRepositoryImpl;
import com.nhnacademy.shoppingmall.shoppingCart.service.ShoppingCartService;
import com.nhnacademy.shoppingmall.shoppingCart.service.impl.ShoppingCartServiceImpl;
import com.nhnacademy.shoppingmall.user.domain.User;
import com.nhnacademy.shoppingmall.user.repository.UserRepository;
import com.nhnacademy.shoppingmall.user.repository.impl.UserRepositoryImpl;
import com.nhnacademy.shoppingmall.user.service.UserService;
import com.nhnacademy.shoppingmall.user.service.impl.UserServiceImpl;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping(method = Method.GET, value = "/order.do")
public class OrderController implements BaseController {
    OrderRepository orderRepository = new OrderRepositoryImpl(new UserRepositoryImpl());
    OrderDetailRepository orderDetailRepository = new OrderDetailRepositoryImpl(new ProductRepositoryImpl(),
            new OrderRepositoryImpl(new UserRepositoryImpl()));

    ShoppingCartService shoppingCartService = new ShoppingCartServiceImpl(new ShoppingCartRepositoryImpl());
    ProductsRepository productsRepository = new ProductRepositoryImpl();
    UserRepository userRepository = new UserRepositoryImpl();
    UserService userService = new UserServiceImpl(userRepository);
    OrderServices orderServices = new OrderServicesImpl(orderRepository, orderDetailRepository, shoppingCartService,
            productsRepository, userService);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("OrderController 실행됨");
        HttpSession session = req.getSession(false);
        if (Objects.isNull(session) || Objects.isNull(session.getAttribute("user"))) {
            throw new RuntimeException("로그인 후 사용해 주세요");
        }
        User user = (User) session.getAttribute("user");
        String userId = user.getUserId();
        LocalDateTime shipDate = LocalDateTime.now().plusDays(2);
        Orders orders = new Orders(userId, LocalDateTime.now(), shipDate);
        log.debug("user가 Order하면 왜 안되는 걸까?");
        orderServices.order(orders);
        user.setUserPoint(userService.getPoint(userId));
        //쓰레드 호출.
        return "shop/main/order_result";
        // 주문 성공 띄우기
    }
}