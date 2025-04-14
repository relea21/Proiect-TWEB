package com.mobylab.springbackend.service;

import com.mobylab.springbackend.entity.Bid;
import com.mobylab.springbackend.entity.Category;
import com.mobylab.springbackend.entity.Product;
import com.mobylab.springbackend.entity.User;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.BidRepository;
import com.mobylab.springbackend.repository.CategoryRepository;
import com.mobylab.springbackend.repository.ProductRepository;
import com.mobylab.springbackend.repository.UserRepository;
import com.mobylab.springbackend.service.dto.BidDto;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BidService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    public BidService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            BidRepository bidRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    public List<BidDto> getBidsForProduct(String categoryName, String productName) {
        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new BadRequestException("Category not found");
        }
        Product product = productRepository.findByName(productName);
        if (product == null) {
            throw new BadRequestException("Product not found");
        }
        return product.getBidList().stream().map(BidDto::new).collect(Collectors.toList());
    }

    public BidDto addBid(String categoryName, String productName, Integer bidAmount) {
        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new BadRequestException("Category not found");
        }
        Product product = productRepository.findByName(productName);
        if (product == null) {
            throw new BadRequestException("Product not found");
        }
        User user = findUserLogged();
        if (user.equals(product.getUser())) {
            throw new BadRequestException("You cant bid for your own product");
        }
        if (bidAmount < product.getStartingPrice() ) {
            throw new BadRequestException("Bid amount too low");
        }
        Bid bid = new Bid();
        bid.setAmount(bidAmount);
        bid.setTimestamp(LocalDateTime.now());
        product.addBid(bid);
        bidRepository.save(bid);
        return new BidDto(bid);
    }

    public void takeBid(String categoryName, String productName, Integer bidId) {
        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new BadRequestException("Category not found");
        }
        Product product = productRepository.findByName(productName);
        if (product == null) {
            throw new BadRequestException("Product not found");
        }
        Optional<Bid> bid = bidRepository.findById(bidId);
        if (bid.isEmpty()) {
            throw new BadRequestException("Bid not found");
        }
        User user = findUserLogged();
        if (!user.equals(product.getUser())) {
            throw new BadRequestException("You dont have permission to take bid");
        }

        List<Bid> bidsToDelete = new ArrayList<>(product.getBidList());
        for (Bid b : bidsToDelete) {
            b.setProduct(null);
            bidRepository.delete(b);
        }
        bidRepository.flush();
        product.getBidList().clear();

        if (product.getUser() != null) {
            product.getUser().getProductsList().remove(product);
            product.setUser(null);
        }
        if (product.getCategory() != null) {
            product.getCategory().getProducts().remove(product);
            product.setCategory(null);
        }

        productRepository.delete(product);
        productRepository.flush();
    }
    private User findUserLogged() {
        UserDetails userLogged = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userLogged.getUsername();
        return userRepository.findUserByEmail(email).get();
    }

}
