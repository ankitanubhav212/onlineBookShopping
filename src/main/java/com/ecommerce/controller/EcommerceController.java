package com.ecommerce.controller;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.ShopOrder;
import com.ecommerce.model.Product;
import com.ecommerce.service.EcommerceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class EcommerceController {

    @Autowired
    EcommerceService ecommerceService;

    @GetMapping("/items")
    Page<Product> getItems(@RequestParam("pageNo") Integer pageNo, @RequestParam("sort") String sort, @RequestParam(value = "search",required = false) String search) {
        return ecommerceService.getProducts(pageNo,sort,search);
    }

    @DeleteMapping("/delete/{id}/{itemId}")
    ShopOrder deleteItem(@PathVariable("id") Long id,@PathVariable("itemId") Long itemId) {
        return ecommerceService.deleteItem(id,itemId);
    }

    @PostMapping("/additem")
    ShopOrder addItem(@RequestBody Product product) {
       return ecommerceService.addItem(product);
    }

    @GetMapping("/order")
    ShopOrder getOrder() {
       return ecommerceService.getOrder();
    }

    @PutMapping("/remove/{id}")
    ShopOrder removerItem(@PathVariable("id") Long id) {
        return ecommerceService.removeItem(id);
    }

    @PutMapping("/order/{id}")
    void updateOrder(@PathVariable("id") Long id) {
         ecommerceService.updateOrder(id);
    }

    @GetMapping("/payment/{id}")
    Map<Object,Object> payment(@PathVariable("id") Long id, @RequestParam("price") String price) {
        return ecommerceService.payment(id,price);
    }
}
