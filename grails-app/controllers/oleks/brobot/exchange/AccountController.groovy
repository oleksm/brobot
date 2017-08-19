package oleks.brobot.exchange

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class AccountController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def exchangeService
    def brokerService


    def balance(Account account) {
        respond exchangeService.returnBalances(account)
    }

    def trade(Account account) {
        respond brokerService.handleTrade(account)
    }

    def tradeSmall(Account account) {
        respond brokerService.handleTradeSmall(account)
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Account.list(params), model:[accountCount: Account.count()]
    }

    def show(Account account) {
        respond account
    }

    def create() {
        respond new Account(params)
    }

    @Transactional
    def save(Account account) {
        if (account == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (account.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond account.errors, view:'create'
            return
        }

        account.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'account.label', default: 'Account'), account.id])
                redirect account
            }
            '*' { respond account, [status: CREATED] }
        }
    }

    def edit(Account account) {
        respond account
    }

    @Transactional
    def update(Account account) {
        if (account == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (account.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond account.errors, view:'edit'
            return
        }

        account.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'account.label', default: 'Account'), account.id])
                redirect account
            }
            '*'{ respond account, [status: OK] }
        }
    }

    @Transactional
    def delete(Account account) {

        if (account == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        account.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'account.label', default: 'Account'), account.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'account.label', default: 'Account'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
