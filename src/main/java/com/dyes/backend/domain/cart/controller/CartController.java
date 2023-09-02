package com.dyes.backend.domain.cart.controller;

import com.dyes.backend.domain.cart.controller.form.ContainProductModifyRequestForm;
import com.dyes.backend.domain.cart.controller.form.ContainProductRequestForm;
import com.dyes.backend.domain.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@ToString
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    final private CartService cartService;
    @PostMapping("/contain")
    public void productContainIntoCart(@RequestBody ContainProductRequestForm requestForm) {
        cartService.containProductIntoCart(requestForm);
    }
    @PutMapping("/change")
    public void productInCartChangeCount(@RequestBody ContainProductModifyRequestForm requestForm){
        cartService.changeProductOptionCount(requestForm);
    }
}
