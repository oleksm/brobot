package oleks.brobot.exchange

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class TickerController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Ticker.list(params), model:[tickerCount: Ticker.count()]
    }

    def show(Ticker ticker) {
        respond ticker
    }

    def create() {
        respond new Ticker(params)
    }

    @Transactional
    def save(Ticker ticker) {
        if (ticker == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (ticker.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond ticker.errors, view:'create'
            return
        }

        ticker.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'ticker.label', default: 'Ticker'), ticker.id])
                redirect ticker
            }
            '*' { respond ticker, [status: CREATED] }
        }
    }

    def edit(Ticker ticker) {
        respond ticker
    }

    @Transactional
    def update(Ticker ticker) {
        if (ticker == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (ticker.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond ticker.errors, view:'edit'
            return
        }

        ticker.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'ticker.label', default: 'Ticker'), ticker.id])
                redirect ticker
            }
            '*'{ respond ticker, [status: OK] }
        }
    }

    @Transactional
    def delete(Ticker ticker) {

        if (ticker == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        ticker.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'ticker.label', default: 'Ticker'), ticker.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'ticker.label', default: 'Ticker'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
