package oleks.brobot.exchange

import grails.gorm.transactions.Transactional

@Transactional
class ExchangeService {

    def poloneixExchangeService


    def returnTicker(Exchange exchange) {
        getService(exchange).returnTicker()
    }

    def returnCurrencies(Exchange exchange) {
        getService(exchange).returnCurrencies()
    }

    def returnBalances(Account account) {
        getService(account.exchange).returnBalances(account)
    }

    def buy (Account account, String pair, double rate, double amount) {
        getService(account.exchange).buy(account, pair, rate, amount)
    }


    def getService(Exchange exchange) {
        switch (exchange.name) {
            case "Poloneix" : return poloneixExchangeService
        }
        log.warn "No Exchange Service Implementation for ${exchange.name}"
    }
}
