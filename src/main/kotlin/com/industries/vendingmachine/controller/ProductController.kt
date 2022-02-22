package com.industries.vendingmachine.controller

import com.industries.vendingmachine.exception.ProductException
import com.industries.vendingmachine.exception.SellerNotAllowedToPerformOperationException
import com.industries.vendingmachine.exception.UserNotAllowedException
import com.industries.vendingmachine.model.ProductModel
import com.industries.vendingmachine.model.Role
import com.industries.vendingmachine.service.ProductService
import com.industries.vendingmachine.service.UserService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("product")
class ProductController(val productService: ProductService, val userService: UserService) {

    companion object {
        private val klogger = KotlinLogging.logger { }
        const val APPLICATION_JSON = "application/json"
    }

    @GetMapping(produces = [APPLICATION_JSON])
    fun getProducts(): ResponseEntity<List<ProductModel>> {
        val products = productService.getProducts()
        return ResponseEntity(products, HttpStatus.OK)
    }

    @PostMapping(consumes = [APPLICATION_JSON])
    fun createProduct(@RequestBody productModel: ProductModel): ResponseEntity<ProductModel> {
        if (isBuyer(productModel.sellerid.toInt())) {
            klogger.info { "Buyer with id ${productModel.id} tried to create a product $productModel" }
            throw UserNotAllowedException("Buyer not allowed to create product")
        }

        val product = productService.createProduct(productModel)
        return ResponseEntity(product, HttpStatus.CREATED)
    }

    @PutMapping(consumes = [APPLICATION_JSON], produces = [APPLICATION_JSON])
    fun updateProduct(@RequestBody productModel: ProductModel): ResponseEntity<ProductModel> {

        if (productService.isUnAuthorizeSeller(productModel.id))
            throw SellerNotAllowedToPerformOperationException("Seller not allowed to update product $productModel")

        if (productModel.cost.toInt() % 5 != 0)
            throw ProductException("Product cost must be in multiples of 5")

        val productModelFromDB = productService.getProduct(productModel.id)

        val newModel = productModelFromDB.copy(
            productname = productModel.productname,
            quantityavailable = productModel.quantityavailable,
            sellerid = productModel.sellerid,
            cost = productModel.cost
        )
        val updatedProduct = productService.updateProduct(newModel)
        return ResponseEntity(updatedProduct, HttpStatus.OK)
    }

    @DeleteMapping(path = ["/{id}/{sellerId}"])
    fun deleteProduct(@PathVariable id: Long, @PathVariable sellerId: Long): ResponseEntity<HttpStatus> {
        if (productService.isUnAuthorizeSeller(sellerId))
            throw SellerNotAllowedToPerformOperationException("Seller not allowed to delete product with id $id")

        productService.deleteProduct(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    private fun isBuyer(id: Int) = userService.getUser(id.toLong()).role == Role.BUYER
}