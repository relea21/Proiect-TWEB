package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.service.BidService;
import com.mobylab.springbackend.service.dto.BidDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home/categories/{categoryName}/products/{productName}/bids")
public class BidController implements SecuredRestController {

    @Autowired
    private BidService bidService;

    private static final Logger logger = LoggerFactory.getLogger(BidController.class);

    @GetMapping
    public ResponseEntity<?> getBidsForProduct(@PathVariable String categoryName,
                                               @PathVariable String productName) {
        logger.info("Get bids for product {} in category {}", productName, categoryName);
        try {
            List<BidDto> bids = bidService.getBidsForProduct(categoryName, productName);
            return new ResponseEntity<>(bids, HttpStatus.OK);
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> addBid(@PathVariable String categoryName,
                                    @PathVariable String productName,
                                    @RequestBody Integer bidAmount) {
        logger.info("Add bid of {} to product {} in category {}", bidAmount, productName, categoryName);
        try {
            BidDto createdBid = bidService.addBid(categoryName, productName, bidAmount);
            return new ResponseEntity<>(createdBid, HttpStatus.CREATED);
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{bidId}")
    public ResponseEntity<?> takeBid(@PathVariable String categoryName,
                                       @PathVariable String productName,
                                       @PathVariable Integer bidId) {
        logger.info("Delete bid {} for product {} in category {}", bidId, productName, categoryName);
        try {
            bidService.takeBid(categoryName, productName, bidId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
