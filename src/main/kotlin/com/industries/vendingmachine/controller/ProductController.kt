package com.industries.vendingmachine.controller

import com.industries.vendingmachine.exception.ProductException
import com.industries.vendingmachine.exception.ProductUnavailableException
import com.industries.vendingmachine.exception.SellerNotAllowedToPerformOperationException
import com.industries.vendingmachine.model.ProductModel
import com.industries.vendingmachine.service.ProductService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("product")
class ProductController(val productService: ProductService) {

    companion object {
        private val klogger = KotlinLogging.logger { }
        const val JSON = "application/json"
    }

    @GetMapping(produces = [JSON])
    fun getProducts(): ResponseEntity<List<ProductModel>> {
        val products = productService.getProducts()
        if (products.isEmpty())
            throw ProductUnavailableException("Products unavailable")

        return ResponseEntity(products, HttpStatus.OK)
    }

    @PostMapping(consumes = [JSON])
    fun createProduct(@RequestBody productModel: ProductModel): ResponseEntity<ProductModel> {
        val product = productService.createProduct(productModel)
        return ResponseEntity(product, HttpStatus.CREATED)
    }

    @PutMapping(consumes = [JSON], produces = [JSON])
    fun updateUser(@RequestBody productModel: ProductModel): ResponseEntity<ProductModel> {

        if (productService.isUnAuthorizeSeller(productModel.id))
            throw SellerNotAllowedToPerformOperationException("Seller not allowed to update product $productModel")

        if(productModel.cost.toInt() % 5 != 0)
            throw ProductException("Product cost must be in multiples of 5")

        val productModelFromDB = productService.getProduct(productModel.id)

        val newModel = productModelFromDB.copy(
            productname = productModel.productname,
            quantityavailable = productModel.quantityavailable,
            sellerid = productModel.sellerid,
            cost = productModel.cost
        )
        productService.updateProduct(newModel)
        return ResponseEntity(newModel, HttpStatus.OK)
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteAllProduct(@PathVariable id: Long) {
        if (productService.isUnAuthorizeSeller(id))
            throw SellerNotAllowedToPerformOperationException("Seller not allowed to delete product with id $id")

        productService.deleteProduct(id)
    }

    @DeleteMapping
    fun deleteAllProducts() {
        productService.deleteAll()
    }
}