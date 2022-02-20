package com.industries.vendingmachine.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


sealed class VendingMachineException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}


@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class FundsUnavailableException : VendingMachineException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class InvalidDenominationException : VendingMachineException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}


@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class NotEnoughItemsInVendingMachineException : VendingMachineException {
    constructor(message: String) : super(message)
}