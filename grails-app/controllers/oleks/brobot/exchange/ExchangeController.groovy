package oleks.brobot.exchange

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ExchangeController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def exchangeService

    def ticker(Exchange exchange) {
        respond exchangeService.returnTicker(exchange)
    }

    def currency(Exchange exchange) {
        def result = exchangeService.returnCurrencies(exchange)

        respond result
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Exchange.list(params), model:[exchangeCount: Exchange.count()]
    }

    def show(Exchange exchange) {
        respond exchange
    }

    def create() {
        respond new Exchange(params)
    }

    @Transactional
    def save(Exchange exchange) {
        if (exchange == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (exchange.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond exchange.errors, view:'/oleks/brobot/exchange/create'
            return
        }

        exchange.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'exchange.label', default: 'Exchange'), exchange.id])
                redirect exchange
            }
            '*' { respond exchange, [status: CREATED] }
        }
    }

    def edit(Exchange exchange) {
        respond exchange
    }

    @Transactional
    def update(Exchange exchange) {
        if (exchange == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (exchange.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond exchange.errors, view:'/oleks/brobot/exchange/edit'
            return
        }

        exchange.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'exchange.label', default: 'Exchange'), exchange.id])
                redirect exchange
            }
            '*'{ respond exchange, [status: OK] }
        }
    }

    @Transactional
    def delete(Exchange exchange) {

        if (exchange == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        exchange.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'exchange.label', default: 'Exchange'), exchange.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'exchange.label', default: 'Exchange'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
