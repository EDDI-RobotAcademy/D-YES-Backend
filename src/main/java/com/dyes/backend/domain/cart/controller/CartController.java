package com.dyes.backend.domain.cart.controller;

import com.dyes.backend.domain.cart.controller.form.ContainProductDeleteRequestForm;
import com.dyes.backend.domain.cart.controller.form.ContainProductListRequestForm;
import com.dyes.backend.domain.cart.controller.form.ContainProductModifyRequestForm;
import com.dyes.backend.domain.cart.controller.form.ContainProductRequestForm;
import com.dyes.backend.domain.cart.service.CartService;
import com.dyes.backend.domain.cart.service.reponse.ContainProductCountChangeResponse;
import com.dyes.backend.domain.cart.service.reponse.ContainProductListResponse;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ContainProductCountChangeResponse productInCartChangeCount(@RequestBody ContainProductModifyRequestForm requestForm){
        return cartService.changeProductOptionCount(requestForm);
    }
    @DeleteMapping("/delete")
    public void productDeleteInCart(@RequestBody List<ContainProductDeleteRequestForm> requestFormList) {
        cartService.deleteProductOptionInCart(requestFormList);
    }
    @GetMapping("/list")
    public List<ContainProductListResponse> productListInCart(@RequestParam("userToken") String userToken ) {
        ContainProductListRequestForm requestForm = new ContainProductListRequestForm(userToken);
        return cartService.productListResponse(requestForm);
    }
}
