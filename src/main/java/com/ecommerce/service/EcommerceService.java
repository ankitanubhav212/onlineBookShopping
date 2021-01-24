package com.ecommerce.service;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.ShopOrder;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ShopOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class EcommerceService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ShopOrderRepository shopOrderRepository;

    public Page<Product> getProducts(Integer pageNo, String sort, String search) {
        Sort sortBy = Sort.by(Sort.Direction.DESC, sort);
        Pageable pageable = PageRequest.of(pageNo, 20, sortBy);
        if(search.equals("") || Objects.isNull(search)) {
            return  productRepository.findAll(pageable);
        } else {
           return  productRepository.findByTitleContainingIgnoreCase(search,pageable);
        }
    }

    public ShopOrder addItem(Product product){
        ShopOrder order = new ShopOrder();
        Set<CartItem> cartItems = new HashSet<>();
        CartItem cartItem = new CartItem();
        List<ShopOrder> orders = shopOrderRepository.findAll();
        if(orders.size() == 0) {
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItems.add(cartItem);
            order.setCartItems(cartItems);
            order.setTotalPrice(product.getPrice());
        } else {
            order = orders.get(0);
            cartItems = order.getCartItems();

            Optional<CartItem> optionalCartItem = cartItems.stream().filter(item->item.getProduct().getBookID().equals(product.getBookID())).findAny();

            //if item already added to cart increase the quantity
            if(optionalCartItem.isPresent()) {
                cartItem = optionalCartItem.get();
                cartItem.setQuantity(cartItem.getQuantity()+1);
                cartItemRepository.save(cartItem);
            } else {
                cartItem.setProduct(product);
                cartItem.setQuantity(1);
                cartItems.add(cartItem);
            }
        }
        updatePrice(order);
        return shopOrderRepository.save(order);
    }

    public ShopOrder deleteItem(Long id,Long itemId) {
        Optional<ShopOrder> optionalShopOrder = shopOrderRepository.findById(id);
        ShopOrder shopOrder = null;
        if(optionalShopOrder.isPresent()) {
            shopOrder = optionalShopOrder.get();
            Set<CartItem> cartItems = shopOrder.getCartItems();
            Optional<CartItem> cartItem = cartItems.stream().filter(item->item.getId().equals(itemId)).findAny();
            cartItem.ifPresent(cartItems::remove);
            updatePrice(shopOrder);
            shopOrderRepository.save(shopOrder);
        }
        return  shopOrder;
    }


    public ShopOrder getOrder() {
         List<ShopOrder> order = shopOrderRepository.findAll();
         if (order.size()==0) {
             return new ShopOrder();
         } else {
             return order.get(0);
         }
    }

    public ShopOrder removeItem(Long id) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        if(cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            if(cartItem.getQuantity() > 0) {
                cartItem.setQuantity(cartItem.getQuantity()-1);
                cartItemRepository.save(cartItem);
            }
        }

        ShopOrder shopOrder =  shopOrderRepository.findAll().get(0);
        updatePrice(shopOrder);
        return shopOrderRepository.save(shopOrder);
    }

    void updatePrice(ShopOrder shopOrder) {
        Set<CartItem> cartItems = shopOrder.getCartItems();

        Double sum = cartItems.stream().mapToDouble((item)->item.getQuantity()*item.getProduct().getPrice()).sum();
        shopOrder.setTotalPrice(sum);
    }

    public void updateOrder(Long id) {
        shopOrderRepository.deleteById(id);
    }

    public Map<Object, Object> payment(Long id,String price) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("X-Api-Key", "test_465aaaff104bef4f010bea6a6cd");
        headers.add("X-Auth-Token", "test_3b05241f2fb9a35228a2b6aec3b");

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("purpose","online book shopping");
        map.add("amount",price);
        map.add("redirect_url","https://onlinebookshopping21.herokuapp.com");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity( "https://test.instamojo.com/api/1.1/payment-requests/", request , Map.class );
        shopOrderRepository.deleteById(id);
        return  response.getBody();
    }
}
