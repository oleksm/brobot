package oleks.brobot.exchange

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class OrderController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Order.list(params), model:[orderCount: Order.count()]
    }

    def show(Order order) {
        respond order
    }

    def create() {
        respond new Order(params)
    }

    @Transactional
    def save(Order order) {
        if (order == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (order.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond order.errors, view:'create'
            return
        }

        order.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'order.label', default: 'Order'), order.id])
                redirect order
            }
            '*' { respond order, [status: CREATED] }
        }
    }

    def edit(Order order) {
        respond order
    }

    @Transactional
    def update(Order order) {
        if (order == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (order.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond order.errors, view:'edit'
            return
        }

        order.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'order.label', default: 'Order'), order.id])
                redirect order
            }
            '*'{ respond order, [status: OK] }
        }
    }

    @Transactional
    def delete(Order order) {

        if (order == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        order.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'order.label', default: 'Order'), order.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'order.label', default: 'Order'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
