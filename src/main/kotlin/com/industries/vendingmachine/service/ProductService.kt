package com.industries.vendingmachine.service

import com.industries.vendingmachine.dto.Product
import com.industries.vendingmachine.exception.ProductException
import com.industries.vendingmachine.exception.ProductUnavailableException
import com.industries.vendingmachine.model.ProductModel
import com.industries.vendingmachine.model.Role
import com.industries.vendingmachine.repo.ProductRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class ProductService(
    val db: ProductRepository,
    val userService: UserService
) {
    companion object {
        private val klogger = KotlinLogging.logger { }
    }

    fun getProducts(): List<ProductModel> {
        try {
            val usersFromDB = db.findProducts()
            return usersFromDB.map {
                it.toProductModel()
            }
        } catch (exception: Exception) {
            klogger.error(exception) { "Error getting data from db" }
            throw ProductException("Error getting data from db", exception);
        }
    }

    fun createProduct(productModel: ProductModel): ProductModel {
        try {
            val product = Product(
                productname = productModel.productname,
                cost = productModel.cost,
                quantityavailable = productModel.quantityavailable,
                sellerid = productModel.sellerid
            )
            return db.save<Product>(product).toProductModel()
        } catch (exception: Exception) {
            klogger.error(exception) { "Error creating data from db for details: $productModel" }
            throw ProductException("Error creating data from db for details $productModel", exception);
        }
    }


    fun updateProduct(productModel: ProductModel): ProductModel {
        try {
            val product = productModel.toProduct()
            return db.save(product).toProductModel()
        } catch (exception: Exception) {
            klogger.warn(exception) { "Product with id ${productModel.id} could not be updated" }
            throw ProductException("Product with id ${productModel.id} could not be updated", exception)
        }
    }

    fun getProduct(id: Long): ProductModel {
        val product = db.findById(id)
        if (product.isPresent)
            return product.get().toProductModel()
        throw ProductUnavailableException("Product with $id does not exist")
    }

    fun deleteProduct(id: Long) {
        db.deleteById(id)
    }

    fun isUnAuthorizeSeller(productId: Long): Boolean {
        val product = getProduct(productId)
        val user = userService.getUser(product.sellerid)

        return (product.sellerid != productId) || (user.role == Role.BUYER)
    }

}